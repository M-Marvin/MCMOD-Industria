package de.m_marvin.industria.content.magnetism.types;

import java.util.HashSet;
import java.util.Set;

import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class MagneticField {

//	public Vec3d getCenter() {
//		return null;
//	}
//	public Vec3d getVektor() {
//		return null;
//	}
//	public double getStrength() {
//		return 0;
//	}
//	public boolean isAlternating() {
//		return false;
//	}
//	public void applyForce(Vec3d linear, Quaternion angular) {
//	}
	
	protected HashSet<MagneticFieldInfluence> magneticInfluences = new HashSet<>();
	protected Vec3d linearForceAccumulated = new Vec3d();
	protected Quaternion angularForceAccumulated = new Quaternion(1, 0, 0, 0);
	
	protected BlockPos minPos = new BlockPos(0, 0, 0);
	protected BlockPos maxPos = new BlockPos(0, 0, 0);
	
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
		// TODO
		return tag;
	}
	
	public static MagneticField deserialize(CompoundTag tag) {
		MagneticField field = new MagneticField();
		ListTag influenceList = tag.getList("Influences", 10);
		for (int i = 0; i < influenceList.size(); i++) {
			field.addInfluence(MagneticFieldInfluence.deserialize(influenceList.getCompound(i)));
		}
		//
		return field;
	}
	
	public void update() {
		
		if (this.magneticInfluences.isEmpty()) {
			this.minPos = new BlockPos(0, 0, 0);
			this.maxPos = new BlockPos(0, 0, 0);
		}
		
		for (MagneticFieldInfluence influence : this.magneticInfluences) {
			
			if (influence == null) continue; // TODO
			
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
		if (this.minPos == null) update();
		return minPos;
	}
	
	public BlockPos getMaxPos() {
		if (this.maxPos == null) update();
		return maxPos;
	}
	
//	public double getEffectiveRange() {
//		return this.getStrength();
//	}
//	
//	public double getIntensityAt(double distance) {
//		return Mth.clamp(1 - distance / getEffectiveRange(), 0, 1);
//	}
//	
//	public boolean isInEffectiveRange(MagneticField other) {
//		double centerDistance = other.getCenter().dist(this.getCenter());
//		return centerDistance <= this.getEffectiveRange() || centerDistance <= other.getEffectiveRange();
//	}
//	
//	public void accumulate(MagneticField other) {
//		if (other.isAlternating() || this.isAlternating()) return;
//		
//		Vec3d offset = other.getCenter().sub(this.getCenter());
//		Quaternion angle = this.getVektor().normalize().relativeRotationQuat(other.getVektor().normalize());
//		double intensity = getStrength() * getIntensityAt(offset.length());
//		Vec3d linear = offset.normalize().mul(intensity).transform(angle);
//		Quaternion angular = angle.mul((float) intensity);
//		
//		other.linearForceAccumulated.addI(linear);
//		other.angularForceAccumulated.addI(angular);
//		this.linearForceAccumulated.subI(linear);
//		this.angularForceAccumulated.subI(angular);
//	}
//	
//	public void applyAccumulated() {
//		applyForce(linearForceAccumulated, angularForceAccumulated);
//		linearForceAccumulated = new Vec3d();
//		angularForceAccumulated = new Quaternion(1, 0, 0, 0);
//	}
	
}
