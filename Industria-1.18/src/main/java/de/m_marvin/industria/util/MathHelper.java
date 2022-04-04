package de.m_marvin.industria.util;

import com.jozufozu.flywheel.repack.joml.Vector3i;

import net.minecraft.nbt.CompoundTag;

public class MathHelper {
	
	public static float clampToDegree(float angle) {
		return angle % 360;
	}
	
	public static CompoundTag writeVector(Vector3i vec) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("x", vec.x);
		tag.putInt("y", vec.y);
		tag.putInt("z", vec.z);
		return tag;
	}
	
}
