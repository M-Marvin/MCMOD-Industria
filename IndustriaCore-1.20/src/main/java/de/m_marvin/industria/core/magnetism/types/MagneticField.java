package de.m_marvin.industria.core.magnetism.types;

import java.util.HashSet;
import java.util.Iterator;
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
import net.minecraft.world.level.block.state.BlockState;

public class MagneticField {
	
	public static final double MAGNETIC_FIELD_RANGE_PER_STRENGTH = 6.0;
	public static final double INITIAL_OMEGA_MULTIPLIER = 30.0;
	public static final double ANGULAR_FORCE_MULTIPLIER = 100.0;
	public static final double LINEAR_FORCE_MULTIPLIER = 80000.0;
	
	protected final long id;
	protected HashSet<MagneticFieldInfluence> magneticInfluences = new HashSet<>();
	protected Vec3d fieldVectorAlternating = new Vec3d();
	protected Vec3d fieldVectorLinear = new Vec3d();
	protected Vec3d magneticCenterOffset = new Vec3d();
	protected double inductionCoefficient = 1.0;
	
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
	
	public boolean clearInvalidInfluences(Level level) {
		Iterator<MagneticFieldInfluence> itr = this.magneticInfluences.iterator();
		while (itr.hasNext()) {
			MagneticFieldInfluence influence = itr.next();
			BlockState state = level.getBlockState(influence.getPos());
			if (state.isAir()) itr.remove();
		}
		return this.magneticInfluences.isEmpty();
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
			return this.id == other.id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(this.id);
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
		
		// Calculate field center point and induction coefficient
		double d = 0;
		Vec3d geometricCenter = getGeometricCenter();
		this.inductionCoefficient = 0;
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			double strength = influence.getVector().add(influence.getInducedVector()).length();
			if (strength == 0) strength = influence.getMagneticCoefficient();
			this.magneticCenterOffset.addI(Vec3d.fromVec(influence.getPos()).add(0.5, 0.5, 0.5).sub(geometricCenter).mul(strength));
			this.inductionCoefficient += influence.getMagneticCoefficient();
			d += strength;
		}
		this.magneticCenterOffset.divI(d);
		this.inductionCoefficient /= this.magneticInfluences.size();
		
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

	public double getInductionCoefficient() {
		return inductionCoefficient;
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
		return Mth.clamp(Math.pow(getEffectiveRangeLinear() - distance, 2) / Math.pow(getEffectiveRangeLinear(), 2), 0, 1);
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
		Vec3d thisFieldVector = getWorldFieldVector(level);
		if (thisFieldVector.length() == 0) return new Vec3d(0, 0, 0);
		
		BlockPos thisBlockPos = this.getAnyInfluencePos();
		BlockPos otherBlockPos = other.getAnyInfluencePos();
		Vec3d thisCenter = PhysicUtility.ensureWorldCoordinates(level, thisBlockPos, this.getMagneticCenter());
		Vec3d otherCenter = PhysicUtility.ensureWorldCoordinates(level, otherBlockPos, other.getMagneticCenter());
		
		Vec3d offset = otherCenter.sub(thisCenter);
		double angle = offset.angle(thisFieldVector);
		Vec3d angleVec = offset.cross(thisFieldVector).normalize();
		Quaterniond interactionVecRot = new Quaterniond(angleVec, angle * -2.0);
		
		return thisFieldVector.mul(this.getIntensityLinearAt(offset.length())).transform(interactionVecRot);
	}
	
	public void applyFieldForces(Level level, PhysShipImpl contraptionPhysics, ServerShip contraption, MagneticField field) {
		
		Vec3d interactingMagneticCenter = PhysicUtility.ensureWorldCoordinates(level, field.getAnyInfluencePos(), field.getMagneticCenter());
		Vec3d magneticCenter = PhysicUtility.toWorldPos(contraption.getTransform(), getMagneticCenter());
		Vec3d offset = interactingMagneticCenter.sub(magneticCenter);
		double distance = offset.length();
		
		if (distance > this.getEffectiveRangeLinear() && distance > field.getEffectiveRangeLinear()) return;
		
		Vec3d interactingFieldVector = field.getWorldFieldVectorInteracting(level, this);
		double interactingInductionCoefficient = field.getInductionCoefficient();
		Vec3d currentFieldVector = getWorldFieldVector(level);
		double inductionCoefficient = getInductionCoefficient();
		double mass = contraption.getInertiaData().getMass();
		
		// TODO adjust magnetic field multipliers
		double ANGULAR_FORCE_MULTIPLIER = 2000.0;
		double LINEAR_FORCE_MULTIPLIER = 40000.0;
		
		/* Angular force */
		
		double strengthAngular = interactingFieldVector.length() * currentFieldVector.length() * ANGULAR_FORCE_MULTIPLIER;
		Quaterniond angularError = currentFieldVector.relativeRotationQuat(interactingFieldVector);
		
		double torqueMultiplier = Math.min(mass * INITIAL_OMEGA_MULTIPLIER, strengthAngular);
		Vector3d initialOmega = new Vector3d(angularError.i * INITIAL_OMEGA_MULTIPLIER, angularError.j * INITIAL_OMEGA_MULTIPLIER, angularError.k * INITIAL_OMEGA_MULTIPLIER); 
		if (angularError.r > 0) initialOmega.mul(-1.0);
		Vector3d torque = initialOmega.sub(contraptionPhysics.getPoseVel().getOmega()).mul(torqueMultiplier);
		
		if (torque.isFinite()) contraptionPhysics.applyInvariantTorque(torque);
		
		/* Linear force */
		
		double strengthLinear = interactingFieldVector.length() * currentFieldVector.length() * LINEAR_FORCE_MULTIPLIER;
		double alignmentAngle = strengthLinear > 0 ? interactingFieldVector.angle(currentFieldVector) : 0;
		
		Vec3d attractingVector = offset.normalize();
		Vec3d attractionForce = attractingVector.mul(strengthLinear).mul(Math.cos(alignmentAngle));
		
		
		// Force of vector induced on other field
		Vec3d interactingInducedFieldVector = currentFieldVector.mul(this.getIntensityLinearAt(distance) * interactingInductionCoefficient);
		double strengthProjected = Mth.clamp(Math.pow(interactingInducedFieldVector.length() * MAGNETIC_FIELD_RANGE_PER_STRENGTH - distance, 2) / Math.pow(interactingInducedFieldVector.length() * MAGNETIC_FIELD_RANGE_PER_STRENGTH, 2), 0, 1);
		interactingInducedFieldVector.mulI(strengthProjected);
		double strengthLinearInduced1 = interactingInducedFieldVector.length() * currentFieldVector.length() * LINEAR_FORCE_MULTIPLIER;
		
		attractionForce.addI(attractingVector.mul(strengthLinearInduced1));
		
		// Force of vector induced in this field
		Vec3d infucedFieldVectpr = interactingFieldVector.mul(inductionCoefficient);
		double strengthLinearInduced2 = infucedFieldVectpr.length() * interactingFieldVector.length() * LINEAR_FORCE_MULTIPLIER;

		attractionForce.addI(attractingVector.mul(strengthLinearInduced2));
		
		Vec3d relativeMagneticCenter = getMagneticCenter().sub(Vec3d.fromVec(contraption.getTransform().getPositionInShip()));
		
		// TODO 
		
		if (attractionForce.isFinite()) contraptionPhysics.applyInvariantForceToPos(attractionForce.writeTo(new Vector3d()), relativeMagneticCenter.writeTo(new Vector3d()));
		
	}
	
}
