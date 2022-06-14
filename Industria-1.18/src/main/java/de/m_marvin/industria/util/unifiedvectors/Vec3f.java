package de.m_marvin.industria.util.unifiedvectors;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class Vec3f {
	
	public float x;
	public float y;
	public float z;
	
	public Vec3f(net.minecraft.world.phys.Vec3 vector) {
		this.x = (float) vector.x;
		this.y = (float) vector.y;
		this.z = (float) vector.z;
	}
	public Vec3f(com.mojang.math.Vector3f vector) {
		this.x = (float) vector.x();
		this.y = (float) vector.y();
		this.z = (float) vector.z();
	}
	public Vec3f(com.mojang.math.Vector3d vector) {
		this.x = (float) vector.x;
		this.y = (float) vector.y;
		this.z = (float) vector.z;
	}
	public Vec3f(net.minecraft.core.Vec3i vector) {
		this.x = (float) vector.getX();
		this.y = (float) vector.getY();
		this.z = (float) vector.getZ();
	}
	
	public Vec3f(BlockPos pos) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}
	
	public Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}

	public float x() {
		return x;
	}
	
	public float y() {
		return y;
	}
	
	public float z() {
		return z;
	}
	
	public Vec3i toInt() {
		return new Vec3i((int) x, (int) y, (int) z);
	}
	
	public double angle(Vec3f vec) {
		double f1 = this.scalar(vec);
		double f2 = this.length() * vec.length();
		return Math.acos(f1 / f2);
	}
	
	public float scalar(Vec3f vec) {
		return this.x * vec.x + this.y * vec.y + this.z* vec.z;
	}
	
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
	public double lengthSqr() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}
	
	public Vec3f add(Vec3f vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
		return this;
	}
	
	public Vec3f sub(Vec3f vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
		return this;
	}
	
	public Vec3f mul(Vec3f vec) {
		this.x *= vec.x;
		this.y *= vec.y;
		this.z *= vec.z;
		return this;
	}
	
	public Vec3f mul(float m) {
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return this;
	}
	
	public Vec3f div(Vec3f vec) {
		this.x /= vec.x;
		this.y /= vec.y;
		this.z /= vec.z;
		return this;
	}
	
	public float dot(Vec3f vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}
	
	public void clamp(Vec3f min, Vec3f max) {
		this.x = Mth.clamp(this.x, min.getX(), max.getX());
		this.y = Mth.clamp(this.y, min.getY(), max.getY());
		this.z = Mth.clamp(this.z, min.getZ(), max.getZ());
	}
	
	public void clamp(float min, float max) {
		this.x = Mth.clamp(this.x, min, max);
		this.y = Mth.clamp(this.y, min, max);
		this.z = Mth.clamp(this.z, min, max);
	}
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3f cross(Vec3f vec) {
		float f = this.x;
		float f1 = this.y;
		float f2 = this.z;
		float f3 = vec.x();
		float f4 = vec.y();
		float f5 = vec.z();
		this.x = f1 * f5 - f2 * f4;
		this.y = f2 * f3 - f * f5;
		this.z = f * f4 - f1 * f3;
		return this;
	}
	
	public boolean normalize() {
		float f = this.x * this.x + this.y * this.y + this.z * this.z;
		if (f < Float.MIN_NORMAL) { //Forge: Fix MC-239212
			return false;
		} else {
			float f1 = Mth.fastInvSqrt(f);
			this.x *= f1;
			this.y *= f1;
			this.z *= f1;
			return true;
		}
	}
	
	public Quaternion rotationQuatFromDirection(Vec3f reference) {
		Vec3f v = reference.copy().cross(this);
		v.normalize();
		float angle = (float) Math.acos(this.copy().dot(reference) / reference.length() * this.length());
		return new Quaternion(new Vector3f(v.x, v.y, v.z), angle, false);
	}
	
	public void transform(Quaternion pQuaternion) {
		Quaternion quaternion = new Quaternion(pQuaternion);
		quaternion.mul(new Quaternion(this.x, this.y, this.z, 0.0F));
		Quaternion quaternion1 = new Quaternion(pQuaternion);
		quaternion1.conj();
		quaternion.mul(quaternion1);
		this.set(quaternion.i(), quaternion.j(), quaternion.k());
	}

	public void lerp(Vec3f vec, float delta) {
		float f = 1.0F - delta;
		this.x = this.x * f + vec.x * delta;
		this.y = this.y * f + vec.y * delta;
		this.z = this.z * f + vec.z * delta;
	}
	
	public Quaternion rotation(float pValue) {
		return new Quaternion(new Vector3f(x, y, z), pValue, false);
	}

	public Quaternion rotationDegrees(float pValue) {
		return new Quaternion(new Vector3f(x, y, z), pValue, true);
	}

	public Vec3f copy() {
		return new Vec3f(this.x, this.y, this.z);
	}
	
	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + "]";
	}
	
	public int hashCode() {
		long j = Double.doubleToLongBits(this.x);
		int i = (int)(j ^ j >>> 32);
		j = Double.doubleToLongBits(this.y);
		i = 31 * i + (int)(j ^ j >>> 32);
		j = Double.doubleToLongBits(this.z);
		return 31 * i + (int)(j ^ j >>> 32);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec3f) {
			return ((Vec3f) obj).x == x && ((Vec3f) obj).y == y && ((Vec3f) obj).z == z;
		}
		return false;
	}
	
}
