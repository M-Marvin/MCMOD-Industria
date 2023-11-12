package de.m_marvin.industria.core.magnetism.types;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import de.m_marvin.industria.core.magnetism.engine.MagneticForceInducer;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.NBTUtility;
import de.m_marvin.unimat.api.IQuaternionMath.EulerOrder;
import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class MagneticField {
	
	public static final double MAGNETIC_FIELD_RANGE_MODIFIER = 6.0;
	public static final double MAGNETIC_FORCE_MODIFIER = 400.0;
	
	protected final long id;
	protected HashSet<MagneticFieldInfluence> magneticInfluences = new HashSet<>();
	protected Vec3d fieldVectorAlternating = new Vec3d();
	protected Vec3d fieldVectorLinear = new Vec3d();
	protected Vec3d magneticCenter = new Vec3d();
	
	protected BlockPos minPos = new BlockPos(0, 0, 0);
	protected BlockPos maxPos = new BlockPos(0, 0, 0);
	protected Vec3d linearForceAccumulated = new Vec3d();
	protected Vec3d angularForceAccumulated = new Vec3d(0, 0, 0);
	
	public MagneticField(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public Set<MagneticFieldInfluence> getInfluences() {
		return this.magneticInfluences;
	}
	
	public void addInfluence(MagneticFieldInfluence influence) {
		assert influence != null : "influnce can not be null!";
		this.magneticInfluences.add(influence);
		minPos = null;
		maxPos = null;
	}
	
	public void removeInfluence(MagneticFieldInfluence influence) {
		assert influence != null : "influnce can not be null!";
		this.magneticInfluences.remove(influence);
		minPos = null;
		maxPos = null;
	}
	
	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		ListTag influenceList = new ListTag();
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			influenceList.add(influence.serialize());
		}
		tag.put("Influences", influenceList);
		tag.put("LinearField", NBTUtility.writeVector3d(this.fieldVectorLinear));
		tag.put("AlternatingField", NBTUtility.writeVector3d(this.fieldVectorAlternating));
		tag.put("MagneticCenter", NBTUtility.writeVector3d(this.magneticCenter));
		return tag;
	}
	
	public static MagneticField deserialize(CompoundTag tag) {
		MagneticField field = new MagneticField(tag.getLong("Id"));
		ListTag influenceList = tag.getList("Influences", 10);
		for (int i = 0; i < influenceList.size(); i++) {
			field.addInfluence(MagneticFieldInfluence.deserialize(influenceList.getCompound(i)));
		}
		field.fieldVectorLinear = NBTUtility.loadVector3d(tag.getCompound("LinearField"));
		field.fieldVectorAlternating = NBTUtility.loadVector3d(tag.getCompound("AlternatingField"));
		field.magneticCenter = NBTUtility.loadVector3d(tag.getCompound("MagneticCenter"));
		return field;
	}
	
	private void updateBounds() {
		
		if (this.magneticInfluences.isEmpty()) {
			this.minPos = new BlockPos(0, 0, 0);
			this.maxPos = new BlockPos(0, 0, 0);
		}
		
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			BlockPos pos = influence.getPos();
			
			if (minPos == null || maxPos == null) {
				minPos = pos;
				maxPos = pos;
			} else {
				minPos = new BlockPos(Math.min(minPos.getX(), pos.getX()), Math.min(minPos.getY(), pos.getY()), Math.min(minPos.getZ(), pos.getZ()));
				maxPos = new BlockPos(Math.max(maxPos.getX(), pos.getX()), Math.max(maxPos.getY(), pos.getY()), Math.max(maxPos.getZ(), pos.getZ()));
			}
		}
		
	}
	
	public BlockPos getMinPos() {
		if (this.minPos == null) updateBounds();
		return minPos;
	}
	
	public BlockPos getMaxPos() {
		if (this.maxPos == null) updateBounds();
		return maxPos;
	}
	
	public BlockPos getAnyInfluencePos() {
		return getInfluences().isEmpty() ? BlockPos.ZERO : getInfluences().iterator().next().getPos();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MagneticField other) {
			return Objects.equals(this.minPos, other.minPos) && Objects.equals(this.maxPos, other.maxPos);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// WARNING: Don't implement hashCode(), it undefined behavior in HashSet<> for some reason!
		return super.hashCode();
	}

	public void updateField(Level level) {
		this.fieldVectorLinear = new Vec3d();
		this.fieldVectorAlternating = new Vec3d();
		this.magneticCenter = new Vec3d();
		
		// Calculate field strength and direction
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			influence.update(level);
			
			if (influence.isAlternating()) {
				this.fieldVectorAlternating.addI(influence.getVector());
			} else {
				this.fieldVectorLinear.addI(influence.getVector());
			}
		}
		
		// Calculate induced fields
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			Vec3d fieldApplied = influence.isAlternating() ? this.fieldVectorAlternating : this.fieldVectorLinear;
			influence.getInducedVector().setI(fieldApplied.mul(influence.getMagneticCoefficient() - 1));
		}
		
		// Calculate field center point
		double d = 0;
		Vec3d geometricCenter = getGeometricCenter();
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			double strength = influence.getVector().add(influence.getInducedVector()).length();
			this.magneticCenter.addI(Vec3d.fromVec(influence.getPos()).add(0.5, 0.5, 0.5).sub(geometricCenter).mul(strength));
			d += strength;
		}
		this.magneticCenter.divI(d);

		// If part of contraption, create force inducer
		if (!level.isClientSide()) {
			BlockPos pos = getAnyInfluencePos();
			Ship contraption = PhysicUtility.getContraptionOfBlock(level, pos);
			if (contraption != null) {
				MagneticForceInducer forceInducer = PhysicUtility.getOrCreateForceInducer((ServerLevel) level, (ServerShip) contraption, MagneticForceInducer.class);
				forceInducer.addField(this.getId());
			}
		}
		
	}
	
	public void removeInducer(ServerLevel level) {
		
		// If part of contraption, remove from force inducer
		BlockPos pos = getAnyInfluencePos();
		Ship contraption = PhysicUtility.getContraptionOfBlock(level, pos);
		if (contraption != null) {
			MagneticForceInducer forceInducer = PhysicUtility.getAttachment((ServerShip) contraption, MagneticForceInducer.class);
			if (forceInducer != null) {
				forceInducer.removeField(this.getId());
				if (forceInducer.getFields().isEmpty()) {
					PhysicUtility.removeAttachment((ServerShip) contraption, MagneticForceInducer.class);
				}
			}
		}
		
	}
	
	public Vec3d getFieldVectorLinear() {
		return fieldVectorLinear;
	}
	
	public Vec3d getFieldVectorAlternating() {
		return fieldVectorAlternating;
	}
	
	public Vec3d getGeometricCenter() {
		return MathUtility.getMiddle(getMinPos(), getMaxPos());
	}
	
	public Vec3d getMagneticCenter() {
		return magneticCenter;
	}
	
	public double getEffectiveRangeLinear() {
		return this.fieldVectorLinear.length() * MAGNETIC_FIELD_RANGE_MODIFIER;
	}

	public double getEffectiveRangeAlternating() {
		return this.fieldVectorAlternating.length() * MAGNETIC_FIELD_RANGE_MODIFIER;
	}
	
	public double getIntensityLinearAt(double distance) {
		return Mth.clamp(1 - distance / getEffectiveRangeLinear(), 0, 1);
	}

	public double getIntensityAlternatingAt(double distance) {
		return Mth.clamp(1 - distance / getEffectiveRangeLinear(), 0, 1);
	}
	
	public boolean isInEffectiveLinearRange(Level level, MagneticField other) {
		Vec3d center1 = PhysicUtility.ensureWorldCoordinates(level, other.getAnyInfluencePos(), other.getMagneticCenter().add(other.getGeometricCenter()));
		Vec3d center2 = PhysicUtility.ensureWorldCoordinates(level, this.getAnyInfluencePos(), this.getMagneticCenter().add(this.getGeometricCenter()));
		double centerDistance = center1.dist(center2);
		return centerDistance <= this.getEffectiveRangeLinear() || centerDistance <= other.getEffectiveRangeLinear();
	}
	
	public void accumulate(Level level, MagneticField other) {
		BlockPos thisBlockPos = this.getAnyInfluencePos();
		BlockPos otherBlockPos = other.getAnyInfluencePos();
		Vec3d thisCenter = PhysicUtility.ensureWorldCoordinates(level, thisBlockPos, this.getMagneticCenter().add(this.getGeometricCenter()));
		Vec3d otherCenter = PhysicUtility.ensureWorldCoordinates(level, otherBlockPos, other.getMagneticCenter().add(other.getGeometricCenter()));
		Vec3d thisFieldVector = PhysicUtility.ensureWorldVector(level, thisBlockPos, this.fieldVectorLinear);
		Vec3d otherFieldVector = PhysicUtility.ensureWorldVector(level, otherBlockPos, other.fieldVectorLinear);
		
		Vec3d offset = otherCenter.sub(thisCenter);
		//Quaterniond angle = thisFieldVector.normalize().relativeRotationQuat(otherFieldVector.normalize());

//		double angle2 = Math.acos(thisFieldVector.normalize().dot(otherFieldVector.normalize()));
//		Vec3d axis = this.fieldVectorLinear.cross(otherFieldVector).normalize();
//		Quaterniond angle = new Quaterniond(axis, angle2);
		
		double thisStrength = this.fieldVectorLinear.length() * this.getIntensityLinearAt(offset.length());
		double otherStrength = other.fieldVectorLinear.length() * other.getIntensityLinearAt(offset.length());
		double strength = thisStrength + otherStrength;
		double strengthLinear = Math.cos(thisFieldVector.angle(otherFieldVector)) * (strength);
		
		Vec3d relativeForce = offset.normalize().mul(strengthLinear);//.transform(angle);
		Vec3d linearForce = offset.normalize().mul(relativeForce.dot(offset));
		
		Vec3d linearForce2 = PhysicUtility.ensureContraptionVector(level, thisBlockPos, linearForce);
		
		Vec3d targetVecWorld = PhysicUtility.ensureWorldVector(level, otherBlockPos, other.fieldVectorLinear);
		Vec3d targetVecThis = PhysicUtility.ensureContraptionVector(level, thisBlockPos, targetVecWorld);
		
		double angle = Math.acos(this.fieldVectorLinear.normalize().dot(targetVecThis.normalize()));
		Vec3d axis = this.fieldVectorLinear.cross(targetVecThis).normalize();
		Quaterniond rot = new Quaterniond(axis, angle);
		
		double roll = Math.atan2(2*rot.j*rot.r - 2*rot.i*rot.k, 1 - 2*rot.j*rot.j - 2*rot.k*rot.k); 
		double pitch = Math.atan2(2*rot.i*rot.r - 2*rot.j*rot.k, 1 - 2*rot.i*rot.i - 2*rot.k*rot.k); 
		double yaw = Math.asin(2*rot.k*rot.j + 2*rot.k*rot.r);
		
		Vec3d angular = new Vec3d(0, 0, 0).mul(50.0);
		
//		Vec3d thisFieldVectorN = otherFieldVector.normalize();
//		Vec3d otherFieldVectorN = thisFieldVector.normalize();
//		double x_angle = 0; // Math.acos( new Vec2d( thisFieldVectorN.y, thisFieldVectorN.z).dot( new Vec2d( otherFieldVectorN.y, otherFieldVectorN.z ) ) );
//		double y_angle = 0 ; //Math.acos( new Vec2d( thisFieldVectorN.x, thisFieldVectorN.z).dot( new Vec2d( otherFieldVectorN.x, otherFieldVectorN.z ) ) );
//		double z_angle = 0; //Math.acos( new Vec2d( thisFieldVectorN.x, thisFieldVectorN.y).dot( new Vec2d( otherFieldVectorN.x, otherFieldVectorN.y ) ) );
//		
//		//Vec3d angular = new Vec3d(x_angle, y_angle, z_angle).mul(50.0);
//		
//		double angle2 = Math.acos(thisFieldVectorN.dot(otherFieldVectorN));
//		Vec3d axis2 = thisFieldVectorN.cross(otherFieldVectorN).normalize();
//		Quaterniond rot = new Quaterniond(axis2, angle2);
//		Vec3d angular = rot.euler(EulerOrder.XYZ, false).mul(50.0);
//		
//		
//		double roll = Math.atan2(2*rot.j*rot.r - 2*rot.i*rot.k, 1 - 2*rot.j*rot.j - 2*rot.k*rot.k); 
//		double pitch = Math.atan2(2*rot.i*rot.r - 2*rot.j*rot.k, 1 - 2*rot.i*rot.i - 2*rot.k*rot.k); 
//		double yaw = Math.asin(2*rot.k*rot.j + 2*rot.k*rot.r);
//		
//		Vec3d angularVec = new Vec3d(0, pitch, roll).mul(50.0);
//		
//		Vec3d targetVector = thisFieldVector.transform(rot);
//		System.out.println("Target: " + angularVec);
		
		Ship contraption = PhysicUtility.getContraptionOfBlock(level, thisBlockPos);
		if (contraption != null) {
			
//			org.joml.Quaterniondc q = contraption.getTransform().getShipToWorldRotation();
//			Quaterniond quat = new Quaterniond(q.x(), q.y(), q.z(), q.w());
//			
//			angle = quat.mul(angle);
			
		}
		
		//Vec3d angular = angle.euler(EulerOrder.XYZ, false).mul(strength * 20);
		
//		other.linearForceAccumulated.addI(linearForce);
//		other.angularForceAccumulated.addI(angular);
		this.linearForceAccumulated.addI(linearForce2);
		this.angularForceAccumulated.addI(angular);
	}
	
	public void applyAccumulated(ServerLevel level) {
		BlockPos pos = getInfluences().isEmpty() ? BlockPos.ZERO : getInfluences().iterator().next().getPos();
		Ship contraption = PhysicUtility.getContraptionOfBlock(level, pos);
		if (contraption != null) {
			Vec3d massCenter = PhysicUtility.toContraptionPos(contraption.getTransform(), Vec3d.fromVec(contraption.getTransform().getPositionInWorld()));
			MagneticForceInducer forceInducer = PhysicUtility.getOrCreateForceInducer(level, (ServerShip) contraption, MagneticForceInducer.class);
			forceInducer.updateForces(this.getId(), this.getGeometricCenter().add(this.magneticCenter).sub(massCenter), this.linearForceAccumulated.mul(MAGNETIC_FORCE_MODIFIER), this.angularForceAccumulated);
		}
		linearForceAccumulated = new Vec3d();
		angularForceAccumulated = new Vec3d(0, 0, 0);
	}
	
}
