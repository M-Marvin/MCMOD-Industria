package de.m_marvin.industria.util.unifiedvectors;

import net.minecraft.util.Mth;

public class Vec2i {
	
	public int x;
	public int y;
	
	public Vec2i(net.minecraft.world.phys.Vec2 vector) {
		this.x = (int) vector.x;
		this.y = (int) vector.y;
	}

	public Vec2i(javax.vecmath.Vector2f vector) {
		this.x = (int) vector.x;
		this.y = (int) vector.y;
	}
	public Vec2i(javax.vecmath.Vector2d vector) {
		this.x = (int) vector.x;
		this.y = (int) vector.y;
	}
	
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
	
	public Vec2f toFloat() {
		return new Vec2f(x, y);
	}
	
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	public double lengthSqr() {
		return this.x * this.x + this.y * this.y;
	}
	
	public Vec2i add(Vec2i vec) {
		this.x += vec.x;
		this.y += vec.y;
		return this;
	}
	
	public Vec2i sub(Vec2i vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		return this;
	}
	
	public Vec2i mul(Vec2i vec) {
		this.x *= vec.x;
		this.y *= vec.y;
		return this;
	}
	
	public Vec2i mul(int m) {
		this.x *= m;
		this.y *= m;
		return this;
	}
	
	public Vec2i div(Vec2i vec) {
		this.x /= vec.x;
		this.y /= vec.y;
		return this;
	}
	
	public int dot(Vec2i vec) {
		return this.x * vec.x + this.y * vec.y;
	}
	
	public void clamp(Vec2i min, Vec2i max) {
		this.x = Mth.clamp(this.x, min.getX(), max.getX());
		this.y = Mth.clamp(this.y, min.getY(), max.getY());
	}
	
	public void clamp(int min, int max) {
		this.x = Mth.clamp(this.x, min, max);
		this.y = Mth.clamp(this.y, min, max);
	}
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2i copy() {
		return new Vec2i(this.x, this.y);
	}
	
	public String toString() {
		return "[" + this.x + ", " + this.y + "]";
	}
	
	public int hashCode() {
		long j = Double.doubleToLongBits(this.x);
		int i = (int)(j ^ j >>> 32);
		j = Double.doubleToLongBits(this.y);
		i = 31 * i + (int)(j ^ j >>> 32);
		return 31 * i + (int)(j ^ j >>> 32);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec2i) {
			return ((Vec2i) obj).x == x && ((Vec2i) obj).y == y;
		}
		return false;
	}
	
}
