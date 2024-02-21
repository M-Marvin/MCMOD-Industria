package de.m_marvin.industria.core.magnetism.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import de.m_marvin.industria.core.Config;
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
	
	public static final double DEFAULT_ANGULAR_FORCE_MULTIPLIER = 100.0;
	public static final double DEFAULT_LINEAR_FORCE_MULTIPLIER = 4000.0;
	public static final double DEFAULT_MAGNETIC_FIELD_RANGE_PER_STRENGTH = 5.0;
	public static final double DEFAULT_FIELD_CHANGE_NOTIFY_LIMIT = 1.0;
	
	static double angularForceMultiplier = DEFAULT_ANGULAR_FORCE_MULTIPLIER;
	static double linearForceMultiplier = DEFAULT_LINEAR_FORCE_MULTIPLIER;
	static double fieldRangePerStrength = DEFAULT_MAGNETIC_FIELD_RANGE_PER_STRENGTH;
	static double fieldChangeNotifyLimit = DEFAULT_FIELD_CHANGE_NOTIFY_LIMIT;
	
	public static void reloadConfig() {
		angularForceMultiplier = Config.MAGNETIC_FORCE_MULTIPLIER_ANGULAR.get();
		linearForceMultiplier = Config.MAGNETIC_FORCE_MULTIPLIER_LINEAR.get();
		fieldRangePerStrength = Config.MAGNETIC_FIELD_RANGE.get();
		fieldChangeNotifyLimit = Config.MAGNETIC_FIELD_CHANGE_NOTIFY_LIMIT.get();
	}
	
	protected final long id;
	protected HashSet<MagneticFieldInfluence> magneticInfluences = new HashSet<>();

	protected Vec3d magneticCenterOffset = new Vec3d();
	protected double inductionCoefficient = 1.0;
	protected Vec3d lastInducedFieldVector = new Vec3d();
	protected Vec3d inducedFieldVector = new Vec3d();
	protected Vec3d fieldVectorLinear = new Vec3d();
	protected Vec3d lastFieldVectorLinear = new Vec3d();
	protected BlockPos minPos = new BlockPos(0, 0, 0);
	protected BlockPos maxPos = new BlockPos(0, 0, 0);
	
	public static enum FieldVectorType {
		EMITTED, INDUCED;
	}
	
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
		tag.put("LastLinearField", NBTUtility.writeVector3d(this.lastFieldVectorLinear));
		tag.put("LinearField", NBTUtility.writeVector3d(this.fieldVectorLinear));
		tag.put("MagneticCenter", NBTUtility.writeVector3d(this.magneticCenterOffset));
		tag.putDouble("InductionCoefficient", this.inductionCoefficient);
		tag.put("InducedFieldVector", NBTUtility.writeVector3d(this.lastInducedFieldVector));
		return tag;
	}
	
	public static MagneticField deserialize(CompoundTag tag) {
		MagneticField field = new MagneticField(tag.getLong("Id"));
		ListTag influenceList = tag.getList("Influences", 10);
		for (int i = 0; i < influenceList.size(); i++) {
			field.addInfluence(MagneticFieldInfluence.deserialize(influenceList.getCompound(i)));
		}
		field.lastFieldVectorLinear = NBTUtility.loadVector3d(tag.getCompound("LastLinearField"));
		field.fieldVectorLinear = NBTUtility.loadVector3d(tag.getCompound("LinearField"));
		field.magneticCenterOffset = NBTUtility.loadVector3d(tag.getCompound("MagneticCenter"));
		field.inductionCoefficient = tag.getDouble("InductionCoefficient");
		field.lastInducedFieldVector = NBTUtility.loadVector3d(tag.getCompound("InducedFieldVector"));
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
	
	public Vec3d getGeometricCenter() {
		return MathUtility.getMiddle(getMinPos(), getMaxPos());
	}

	public Vec3d getMagneticCenterOffset() {
		return magneticCenterOffset;
	}

	public double getInductionCoefficient() {
		return inductionCoefficient;
	}
	
	public Vec3d getInducedFieldVector() {
		return inducedFieldVector;
	}
	
	public Vec3d getMagneticCenter() {
		return this.getGeometricCenter().add(getMagneticCenterOffset());
	}
	
	public double getEffectiveRangeLinear() {
		return Math.max(this.fieldVectorLinear.length(), this.inducedFieldVector.length()) * fieldRangePerStrength;
	}

	public double getIntensityLinearAt(double distance) {
		return Mth.clamp(1 - (distance - getEffectiveRangeLinear()), 0, 1);
	}
	
	public boolean isInEffectiveLinearRange(Level level, MagneticField other) {
		Vec3d center1 = PhysicUtility.ensureWorldCoordinates(level, other.getAnyInfluencePos(), other.getMagneticCenter());
		Vec3d center2 = PhysicUtility.ensureWorldCoordinates(level, this.getAnyInfluencePos(), this.getMagneticCenter());
		double centerDistance = center1.dist(center2);
		return centerDistance <= this.getEffectiveRangeLinear() || centerDistance <= other.getEffectiveRangeLinear();
	}
	
	public Vec3d getWorldFieldVector(Level level, FieldVectorType type) {
		BlockPos thisBlockPos = this.getAnyInfluencePos();
		return PhysicUtility.ensureWorldVector(level, thisBlockPos, type == FieldVectorType.EMITTED ? this.fieldVectorLinear : this.inducedFieldVector);	
	}
	
	public Vec3d getWorldFieldVectorInteracting(Level level, MagneticField other, FieldVectorType type) {
		Vec3d thisFieldVector = getWorldFieldVector(level, type);
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

	// Called when block state changes in the field occur
	public void updateField(Level level) {
		this.fieldVectorLinear = new Vec3d();
		this.magneticCenterOffset = new Vec3d();
		
		// Calculate field strength and direction
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			influence.update(level);
			
			this.fieldVectorLinear.addI(influence.getVector());
		}

		// Calculate field center point and induction coefficient
		double d = 0;
		Vec3d geometricCenter = getGeometricCenter();
		this.inductionCoefficient = 0;
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			double strength = influence.getMagneticCoefficient() + influence.getVector().length();
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

		// Detect induction field change and notify blocks
		double fieldChange = this.lastFieldVectorLinear.sub(this.fieldVectorLinear).length();
		if (fieldChange > fieldChangeNotifyLimit) {
			this.lastFieldVectorLinear = this.fieldVectorLinear;
			
			for (MagneticFieldInfluence influence : this.magneticInfluences) {
				influence.notifyInductionChange(level);
			}
		}
		
	}
	
	// Called every game tick to update induction vectors
	public void updateInduction(Level level, Collection<MagneticField> fields) {
		
		Vec3d newVector = new Vec3d(0, 0, 0);
		
		for (MagneticField field : fields) {
			
			if (field == this || !this.isInEffectiveLinearRange(level, field)) continue;
			
			Vec3d interactingFieldVector = PhysicUtility.ensureContraptionVector(level, this.getAnyInfluencePos(), field.getWorldFieldVectorInteracting(level, this, FieldVectorType.EMITTED));
			newVector.addI(interactingFieldVector);
			
		}
		
		this.inducedFieldVector = (newVector.mul(this.inductionCoefficient));
		
		// Calculate individual induction vectors
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			double d = influence.getMagneticCoefficient() / this.inductionCoefficient / this.magneticInfluences.size();
			Vec3d externalInduction = this.inducedFieldVector.mul(d);
			//Vec3d internalInduction = this.fieldVectorLinear.mul(d); TODO internal induction
			influence.getInducedVector().setI(externalInduction);	
		}
		
		// Detect induction field change and notify blocks
		double fieldChange = this.lastInducedFieldVector.sub(this.inducedFieldVector).length();
		if (fieldChange > fieldChangeNotifyLimit) {
			this.lastInducedFieldVector = this.inducedFieldVector;
			
			for (MagneticFieldInfluence influence : this.magneticInfluences) {
				influence.notifyInductionChange(level);
			}
		}
		
	}
	
	// Called every physics tick to update applied forces if part of contraption
	public void accumulateForces(Level level, PhysShipImpl contraptionPhysics, ServerShip contraption, MagneticField field) {
		
		Vec3d interactingMagneticCenter = PhysicUtility.ensureWorldCoordinates(level, field.getAnyInfluencePos(), field.getMagneticCenter());
		Vec3d magneticCenter = PhysicUtility.toWorldPos(contraption.getTransform(), getMagneticCenter());
		
		Vec3d offset = interactingMagneticCenter.sub(magneticCenter);
		double distance = offset.length();
		
		// Check if fields are on effective range
		if (distance > this.getEffectiveRangeLinear() && distance > field.getEffectiveRangeLinear()) return;
		
		Vec3d interactingFieldVectorE = field.getWorldFieldVectorInteracting(level, this, FieldVectorType.EMITTED);
		Vec3d interactingFieldVectorI = field.getWorldFieldVectorInteracting(level, this, FieldVectorType.INDUCED);
		Vec3d currentFieldVectorE = getWorldFieldVector(level, FieldVectorType.EMITTED);
		Vec3d currentFieldVectorI = getWorldFieldVector(level, FieldVectorType.INDUCED);
		
		/* Angular force */
		
		// Calculate angular force applied
		double strengthAngular = interactingFieldVectorE.length() * currentFieldVectorE.length() * angularForceMultiplier;
		
		Vec3d currentOmega = Vec3d.fromVec(contraption.getOmega());
		Vec3d dampeningForce = currentOmega.mul(strengthAngular * 2.0);
		
		Vec3d axis = currentFieldVectorE.cross(interactingFieldVectorE);
		Vec3d torque = axis.mul(strengthAngular).sub(dampeningForce);
		
		// Apply angular force
		if (torque.isFinite()) contraptionPhysics.applyInvariantTorque(torque.writeTo(new Vector3d()));
		
		/* Linear force */
		
		// Calculate linear force applied between direct emitted fields (from magnets)
		Vec3d v1 = interactingFieldVectorE; //.add(interactingFieldVectorI);
		Vec3d v2 = currentFieldVectorE; //.add(currentFieldVectorI);
		double strengthLinear = v1.length() * v2.length() * linearForceMultiplier;
		double alignmentAngle = strengthLinear > 0 ? v1.angle(v2) : 0;
		
		Vec3d attractingVector = offset.normalize();
		Vec3d attractionForce = attractingVector.mul(strengthLinear).mul(Math.cos(alignmentAngle));
		
		// Calculate linear force applied between induced fields
		double strengthLinearInduced = (
				(interactingFieldVectorE.length() * currentFieldVectorI.length()) +
				(interactingFieldVectorI.length() * currentFieldVectorE.length())
				)* linearForceMultiplier;
		
		attractionForce.addI(attractingVector.mul(strengthLinearInduced));
		
		// Apply linear force on magnetic center
		Vec3d relativeMagneticCenter = getMagneticCenter().sub(Vec3d.fromVec(contraption.getTransform().getPositionInShip()));
		
		if (attractionForce.isFinite()) contraptionPhysics.applyInvariantForceToPos(attractionForce.writeTo(new Vector3d()), relativeMagneticCenter.writeTo(new Vector3d()));
		
	}
	
}
