package de.m_marvin.industria.util.unifiedvectors;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class Vec3i {
	
	public int x;
	public int y;
	public int z;
	
	public Vec3i(net.minecraft.world.phys.Vec3 vector) {
		this.x = (int) vector.x;
		this.y = (int) vector.y;
		this.z = (int) vector.z;
	}
	public Vec3i(com.mojang.math.Vector3f vector) {
		this.x = (int) vector.x();
		this.y = (int) vector.y();
		this.z = (int) vector.z();
	}
	public Vec3i(com.mojang.math.Vector3d vector) {
		this.x = (int) vector.x;
		this.y = (int) vector.y;
		this.z = (int) vector.z;
	}
	public Vec3i(net.minecraft.core.Vec3i vector) {
		this.x = (int) vector.getX();
		this.y = (int) vector.getY();
		this.z = (int) vector.getZ();
	}
	
	public Vec3i(BlockPos pos) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}
	
	public Vec3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}

	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
	
	public int z() {
		return z;
	}
	
	public Vec3f toFloat() {
		return new Vec3f(x, y, z);
	}
	
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
	public double lengthSqr() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}
	
	public Vec3i add(Vec3i vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
		return this;
	}
	
	public Vec3i sub(Vec3i vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
		return this;
	}
	
	public Vec3i mul(Vec3i vec) {
		this.x *= vec.x;
		this.y *= vec.y;
		this.z *= vec.z;
		return this;
	}
	
	public Vec3i mul(int m) {
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return this;
	}
	
	public Vec3i div(Vec3i vec) {
		this.x /= vec.x;
		this.y /= vec.y;
		this.z /= vec.z;
		return this;
	}
	
	public int dot(Vec3i vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}
	
	public void clamp(Vec3i min, Vec3i max) {
		this.x = Mth.clamp(this.x, min.getX(), max.getX());
		this.y = Mth.clamp(this.y, min.getY(), max.getY());
		this.z = Mth.clamp(this.z, min.getZ(), max.getZ());
	}
	
	public void clamp(int min, int max) {
		this.x = Mth.clamp(this.x, min, max);
		this.y = Mth.clamp(this.y, min, max);
		this.z = Mth.clamp(this.z, min, max);
	}
	
	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3i copy() {
		return new Vec3i(this.x, this.y, this.z);
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
		if (obj instanceof Vec3i) {
			return ((Vec3i) obj).x == x && ((Vec3i) obj).y == y && ((Vec3i) obj).z == z;
		}
		return false;
	}
	
}
