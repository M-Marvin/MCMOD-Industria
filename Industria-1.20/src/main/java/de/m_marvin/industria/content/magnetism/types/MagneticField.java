package de.m_marvin.industria.content.magnetism.types;

import java.util.HashSet;

import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.util.Mth;

public class MagneticField {

	public Vec3d getCenter();
	public Vec3d getVektor();
	public double getStrength();
	public boolean isAlternating();
	public void applyForce(Vec3d linear, Quaternion angular);
	
	protected HashSet<MagneticFieldInfluence> magneticInfluences = new HashSet<>();
	protected Vec3d linearForceAccumulated = new Vec3d();
	protected Quaternion angularForceAccumulated = new Quaternion(1, 0, 0, 0);
	
	public double getEffectiveRange() {
		return this.getStrength();
	}
	
	public double getIntensityAt(double distance) {
		return Mth.clamp(1 - distance / getEffectiveRange(), 0, 1);
	}
	
	public boolean isInEffectiveRange(MagneticField other) {
		double centerDistance = other.getCenter().dist(this.getCenter());
		return centerDistance <= this.getEffectiveRange() || centerDistance <= other.getEffectiveRange();
	}
	
	public void accumulate(MagneticField other) {
		if (other.isAlternating() || this.isAlternating()) return;
		
		Vec3d offset = other.getCenter().sub(this.getCenter());
		Quaternion angle = this.getVektor().normalize().relativeRotationQuat(other.getVektor().normalize());
		double intensity = getStrength() * getIntensityAt(offset.length());
		Vec3d linear = offset.normalize().mul(intensity).transform(angle);
		Quaternion angular = angle.mul((float) intensity);
		
		other.linearForceAccumulated.addI(linear);
		other.angularForceAccumulated.addI(angular);
		this.linearForceAccumulated.subI(linear);
		this.angularForceAccumulated.subI(angular);
	}
	
	public void applyAccumulated() {
		applyForce(linearForceAccumulated, angularForceAccumulated);
		linearForceAccumulated = new Vec3d();
		angularForceAccumulated = new Quaternion(1, 0, 0, 0);
	}
	
}
