package de.m_marvin.industria.core.magnetism.types;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.magnetism.engine.MagneticForceInducer;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.NBTUtility;
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
	protected Vec3d magneticCenterOffset = new Vec3d();
	
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
		tag.put("MagneticCenter", NBTUtility.writeVector3d(this.magneticCenterOffset));
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
		field.magneticCenterOffset = NBTUtility.loadVector3d(tag.getCompound("MagneticCenter"));
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
		// WARNING: Don't implement hashCode(), it causes undefined behavior in HashSet<> for some reason!
		return super.hashCode();
	}

	public void updateField(Level level) {
		this.fieldVectorLinear = new Vec3d();
		this.fieldVectorAlternating = new Vec3d();
		this.magneticCenterOffset = new Vec3d();
		
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
			this.magneticCenterOffset.addI(Vec3d.fromVec(influence.getPos()).add(0.5, 0.5, 0.5).sub(geometricCenter).mul(strength));
			d += strength;
		}
		this.magneticCenterOffset.divI(d);

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

	public Vec3d getMagneticCenterOffset() {
		return magneticCenterOffset;
	}
	
	public Vec3d getMagneticCenter() {
		return this.getGeometricCenter().add(getMagneticCenterOffset());
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
		Vec3d center1 = PhysicUtility.ensureWorldCoordinates(level, other.getAnyInfluencePos(), other.getMagneticCenter());
		Vec3d center2 = PhysicUtility.ensureWorldCoordinates(level, this.getAnyInfluencePos(), this.getMagneticCenter());
		double centerDistance = center1.dist(center2);
		return centerDistance <= this.getEffectiveRangeLinear() || centerDistance <= other.getEffectiveRangeLinear();
	}
	
	public Vec3d getWorldFieldVector(Level level) {
		BlockPos thisBlockPos = this.getAnyInfluencePos();
		return PhysicUtility.ensureWorldVector(level, thisBlockPos, this.fieldVectorLinear);	
	}
	
	public Vec3d getWorldFieldVectorInteracting(Level level, MagneticField other) {
		BlockPos thisBlockPos = this.getAnyInfluencePos();
		
		BlockPos otherBlockPos = other.getAnyInfluencePos();
		Vec3d thisCenter = PhysicUtility.ensureWorldCoordinates(level, thisBlockPos, this.getMagneticCenter());
		Vec3d otherCenter = PhysicUtility.ensureWorldCoordinates(level, otherBlockPos, other.getMagneticCenter());
		Vec3d thisFieldVector = getWorldFieldVector(level);
		
		Vec3d offset = otherCenter.sub(thisCenter);
		
		return thisFieldVector.mul(this.getIntensityLinearAt(offset.length()));	
	}
	
	public static final double MAX_ANGULAR_VELOCITY = 4 * Math.PI;
	
	public void applyFieldForces(Level level, PhysShip contraptionPhysics, ServerShip contraption, Vec3d targetFieldVector) {
		
		Vec3d currentFieldVector = getWorldFieldVector(level);													// World Vector of this field (length = strength)
		Vec3d contraptionMassCenter = Vec3d.fromVec(contraptionPhysics.getTransform().getPositionInWorld());	// Center of mass position in world coordinates
		Vec3d relativeMagneticCenter = getMagneticCenter().sub(contraptionMassCenter);							// Center of magnetic field relative to mass center
		Vec3d linearVelocity = Vec3d.fromVec(contraption.getVelocity());										// Linear velocity in m/s
		Vec3d angularVelocity = Vec3d.fromVec(contraption.getOmega());											// Angular velocity in rad/s
		double mass = contraption.getInertiaData().getMass();													// Mass in g
		
		// Calculate angular error in rad
		Vec3d current = currentFieldVector.normalize();
		Vec3d target = targetFieldVector.normalize();
		
		Vec2d cavX = new Vec2d(current.y, current.z);
		Vec2d tavX = new Vec2d(target.y, target.z);
		double factX = cavX.length() * tavX.length();
		double dotX = cavX.dot(tavX);
		double angleX = cavX.angle(tavX);
		

		Vec2d cavY = new Vec2d(current.x, current.z);
		Vec2d tavY = new Vec2d(target.x, target.z);
		double factY = cavY.length() * tavY.length();
		double dotY = cavY.dot(tavY);
		double angleY = cavY.angle(tavY);
		
		Vec2d cavZ = new Vec2d(current.x, current.y);
		Vec2d tavZ = new Vec2d(target.x, target.y);
		double factZ = cavZ.length() * tavZ.length();
		double dotZ = cavZ.dot(tavZ);
		double angleZ = cavZ.angle(tavZ);
		
//		double dotY = new Vec3d(current.x, 0.0, current.z).dot(new Vec3d(target.x, 0.0, target.z));
//		double dotZ = new Vec3d(current.x, current.y, 0.0).dot(new Vec3d(target.x, target.y, 0.0));
		double crossX = new Vec3d(0.0, current.y, current.z).cross(new Vec3d(0.0, target.y, target.z)).normalize().x;
		double crossY = new Vec3d(current.x, 0.0, current.z).cross(new Vec3d(target.x, 0.0, target.z)).normalize().y;
		double crossZ = new Vec3d(current.x, current.y, 0.0).cross(new Vec3d(target.x, target.y, 0.0)).normalize().z;

//		double crossX = cavX.cross(tavX);
//		double crossY = cavY.cross(tavY);
//		double crossZ = cavZ.cross(tavZ);
		
//		double angleX = Math.acos(dotX) * (dotX / (dotX + dotY + dotZ)) * crossX;
//		double angleY = Math.acos(dotY) * (dotY / (dotX + dotY + dotZ)) * crossY;
//		double angleZ = Math.acos(dotZ) * (dotZ / (dotX + dotY + dotZ)) * crossZ;
		double angleX2 = angleX * (factX / (factX + factY + factZ));// * crossX;
		double angleY2 = angleY * (factY / (factX + factY + factZ));// * crossY;
		double angleZ2 = angleZ * (factZ / (factX + factY + factZ));// * crossZ;
		
		Vec3d angularError = new Vec3d(
				Double.isFinite(angleX2) ? angleX2 : 0.0, 
				Double.isFinite(angleY2) ? angleY2 : 0.0, 
				Double.isFinite(angleZ2) ? angleZ2 : 0.0
		);
		
		System.out.println("TEST");
		

		
//		// Calculate resulting magnetic force applied
//		Vec3d angularForce = angularError.mul(targetFieldVector.length() * currentFieldVector.length() * 50000);
//		
//		// Limit velocity
////		Vec3d resultingVelocity = angularForce.div(mass).add(angularVelocity);
////		Vec3d forceOvershot = resultingVelocity.sub(MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY).max(0.0).mul(mass);
////		angularForce.subI(forceOvershot);
//		
//		System.out.println(angularForce);
//		
//		if (angularForce.isFinite()) {
//
//			contraptionPhysics.applyInvariantTorque(new Vector3d(angularError.x, angularForce.y, angularForce.z));
//				
//		}
		
	}
	
	public void accumulate(Level level, MagneticField other, Vec3d angularVelocity, Vec3d linearVelocity, Vec3d linearForceVec, Vec3d angularForceVec) {
		BlockPos thisBlockPos = this.getAnyInfluencePos();
		
		if (PhysicUtility.getContraptionOfBlock(level, thisBlockPos) == null) return;
		
		BlockPos otherBlockPos = other.getAnyInfluencePos();
		Vec3d thisCenter = PhysicUtility.ensureWorldCoordinates(level, thisBlockPos, this.getMagneticCenter());
		Vec3d otherCenter = PhysicUtility.ensureWorldCoordinates(level, otherBlockPos, other.getMagneticCenter());
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
		
		Vec3d current = this.fieldVectorLinear.normalize(); //thisFieldVector;
		Vec3d target = targetVecThis.normalize(); //.normalize(); // otherFieldVector;
		
		double dotX = new Vec3d(0.0, current.y, current.z).dot(new Vec3d(0.0, target.y, target.z));
		double dotY = new Vec3d(current.x, 0.0, current.z).dot(new Vec3d(target.x, 0.0, target.z));
		double dotZ = new Vec3d(current.x, current.y, 0.0).dot(new Vec3d(target.x, target.y, 0.0));
		double crossX = new Vec3d(0.0, current.y, current.z).cross(new Vec3d(0.0, target.y, target.z)).normalize().x;
		double crossY = new Vec3d(current.x, 0.0, current.z).cross(new Vec3d(target.x, 0.0, target.z)).normalize().y;
		double crossZ = new Vec3d(current.x, current.y, 0.0).cross(new Vec3d(target.x, target.y, 0.0)).normalize().z;
		double angleX = Math.acos(dotX) * (dotX / (dotX + dotY + dotZ)) * crossX;
		double angleY = Math.acos(dotY) * (dotY / (dotX + dotY + dotZ)) * crossY;
		double angleZ = Math.acos(dotZ) * (dotZ / (dotX + dotY + dotZ)) * crossZ;
		
		//System.out.println(new Vec3d(current.x, current.y, 0.0).normalize() + "\n" + (new Vec3d(target.x, target.y, 0.0).normalize()) + "\n= " + Math.acos(dotZ));
		
		Vec3d angularError = new Vec3d(
				Double.isFinite(angleX) ? angleX : 0.0, 
				Double.isFinite(angleY) ? angleY : 0.0, 
				Double.isFinite(angleZ) ? angleZ : 0.0
		);//.mul(strengthLinear * 200);
		
		Vec3d angularForce = angularError.sub(angularVelocity);
		
//		Vec3d angular = new Vec3d(
//				Double.isFinite(angleX) ? angleX : 0.0, 
//				Double.isFinite(angleY) ? angleY : 0.0, 
//				Double.isFinite(angleZ) ? angleZ : 0.0
//		).mul(strengthLinear * 200);
		
//		other.linearForceAccumulated.addI(linearForce);
		//other.angularForceAccumulated.addI(angular);
		//this.linearForceAccumulated.addI(linearForce2);
		//System.out.println("\n\n\n\n\n\n" + angular);
		//angularForceVec.addI(angularForce);
	}
	
//	public void applyAccumulated(PhysShip contraption) {
////		BlockPos pos = getInfluences().isEmpty() ? BlockPos.ZERO : getInfluences().iterator().next().getPos();
////		Ship contraption = PhysicUtility.getContraptionOfBlock(level, pos);
////		if (contraption != null) {
////			Vec3d massCenter = PhysicUtility.toContraptionPos(contraption.getTransform(), Vec3d.fromVec(contraption.getTransform().getPositionInWorld()));
////			MagneticForceInducer forceInducer = PhysicUtility.getOrCreateForceInducer(level, (ServerShip) contraption, MagneticForceInducer.class);
////			forceInducer.updateForces(this.getId(), this.getGeometricCenter().add(this.magneticCenter).sub(massCenter), this.linearForceAccumulated.mul(200.0), this.angularForceAccumulated.mul(200.0));
////		}
//		
//		linearForceAccumulated = new Vec3d();
//		angularForceAccumulated = new Vec3d(0, 0, 0);
//	}
	
}
