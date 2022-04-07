package de.m_marvin.industria.util.unifiedvectors;

import net.minecraft.util.Mth;

public class Vec2f {
	
	public float x;
	public float y;
	
	public Vec2f(net.minecraft.world.phys.Vec2 vector) {
		this.x = (float) vector.x;
		this.y = (float) vector.y;
	}

	public Vec2f(javax.vecmath.Vector2f vector) {
		this.x = (float) vector.x;
		this.y = (float) vector.y;
	}
	public Vec2f(javax.vecmath.Vector2d vector) {
		this.x = (float) vector.x;
		this.y = (float) vector.y;
	}
	
	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float x() {
		return x;
	}
	
	public float y() {
		return y;
	}

	public Vec2i toInt() {
		return new Vec2i((int) x, (int) y);
	}
	
	public double angle(Vec2f vec) {
		double f1 = this.scalar(vec);
		double f2 = this.length() * vec.length();
		return Math.acos(f1 / f2);
	}
	
	public float scalar(Vec2f vec) {
		return this.x * vec.x + this.y * vec.y;
	}
	
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	public double lengthSqr() {
		return this.x * this.x + this.y * this.y;
	}
	
	public Vec2f add(Vec2f vec) {
		this.x += vec.x;
		this.y += vec.y;
		return this;
	}
	
	public Vec2f sub(Vec2f vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		return this;
	}
	
	public Vec2f mul(Vec2f vec) {
		this.x *= vec.x;
		this.y *= vec.y;
		return this;
	}
	
	public Vec2f mul(float m) {
		this.x *= m;
		this.y *= m;
		return this;
	}
	
	public Vec2f div(Vec2f vec) {
		this.x /= vec.x;
		this.y /= vec.y;
		return this;
	}
	
	public float dot(Vec2f vec) {
		return this.x * vec.x + this.y * vec.y;
	}
	
	public void clamp(Vec2f min, Vec2f max) {
		this.x = Mth.clamp(this.x, min.getX(), max.getX());
		this.y = Mth.clamp(this.y, min.getY(), max.getY());
	}
	
	public void clamp(float min, float max) {
		this.x = Mth.clamp(this.x, min, max);
		this.y = Mth.clamp(this.y, min, max);
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void lerp(Vec2f vec, float delta) {
		float f = 1.0F - delta;
		this.x = this.x * f + vec.x * delta;
		this.y = this.y * f + vec.y * delta;
	}
	
	public Vec2f copy() {
		return new Vec2f(this.x, this.y);
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
		if (obj instanceof Vec2f) {
			return ((Vec2f) obj).x == x && ((Vec2f) obj).y == y;
		}
		return false;
	}
	
}
