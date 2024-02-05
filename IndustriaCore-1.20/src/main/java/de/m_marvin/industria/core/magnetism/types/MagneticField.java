package de.m_marvin.industria.core.magnetism.types;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import de.m_marvin.industria.core.magnetism.engine.MagneticForceInducer;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.NBTUtility;
import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class MagneticField {
	
	public static final double MAGNETIC_FIELD_RANGE_PER_STRENGTH = 6.0;
	
	protected final long id;
	protected HashSet<MagneticFieldInfluence> magneticInfluences = new HashSet<>();
	protected Vec3d fieldVectorAlternating = new Vec3d();
	protected Vec3d fieldVectorLinear = new Vec3d();
	protected Vec3d magneticCenterOffset = new Vec3d();
	
	protected BlockPos minPos = new BlockPos(0, 0, 0);
	protected BlockPos maxPos = new BlockPos(0, 0, 0);
	
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
		tag.putLong("Id", this.id);
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
		return this.fieldVectorLinear.length() * MAGNETIC_FIELD_RANGE_PER_STRENGTH;
	}

	public double getEffectiveRangeAlternating() {
		return this.fieldVectorAlternating.length() * MAGNETIC_FIELD_RANGE_PER_STRENGTH;
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
	
	public static final double OMEGA_COEFFICIENT = 30.0;
	//public static final double OMEGA_RADIAL_FORCE_COEFFICIENT = 100.0;
	//
	
	public void applyFieldForces(Level level, PhysShipImpl contraptionPhysics, ServerShip contraption, Vec3d targetFieldVector) {
		
		Vec3d currentFieldVector = getWorldFieldVector(level);													// World Vector of this field (length = strength)
		Vec3d contraptionMassCenter = Vec3d.fromVec(contraptionPhysics.getTransform().getPositionInWorld());	// Center of mass position in world coordinates
		Vec3d relativeMagneticCenter = getMagneticCenter().sub(contraptionMassCenter);							// Center of magnetic field relative to mass center
		Vec3d linearVelocity = Vec3d.fromVec(contraption.getVelocity());										// Linear velocity in m/s
		Vec3d angularVelocity = Vec3d.fromVec(contraption.getOmega());											// Angular velocity in rad/s
		double mass = contraption.getInertiaData().getMass();													// Mass in g
		
		// TODO
		double ANGULAR_FORCE_MULTIPLIER = 20000.0;
		double LINEAR_FORCE_MULTIPLIER = 1.0;
		
		/* Angular force */
		
		double strengthAngular = targetFieldVector.length() * currentFieldVector.length() * ANGULAR_FORCE_MULTIPLIER;
		Quaterniond angularError = currentFieldVector.relativeRotationQuat(targetFieldVector);
		
		double torqueMultiplier = Math.min(mass * OMEGA_COEFFICIENT, strengthAngular);
		Vector3d initialOmega = new Vector3d(angularError.i * OMEGA_COEFFICIENT, angularError.j * OMEGA_COEFFICIENT, angularError.k * OMEGA_COEFFICIENT); 
		if (angularError.r > 0) initialOmega.mul(-1.0);
		Vector3d torque = initialOmega.sub(contraptionPhysics.getPoseVel().getOmega()).mul(torqueMultiplier);
		
		if (torque.isFinite()) contraptionPhysics.applyInvariantTorque(torque);
		
		/* Linear force */
		
		
		
	}
	
}
