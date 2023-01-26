package de.m_marvin.industria.core;

import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.nbt.CompoundTag;

public class NBTUtility {

	public static CompoundTag writeVector(Vec3i vec) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("x", vec.x());
		tag.putInt("y", vec.y());
		tag.putInt("z", vec.z());
		return tag;
	}
	
	public static Vec3i loadVector(CompoundTag tag) {
		return new Vec3i(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}

	public static CompoundTag writeVectorf(Vec3f vec) {
		CompoundTag tag = new CompoundTag();
		tag.putFloat("x", vec.x());
		tag.putFloat("y", vec.y());
		tag.putFloat("z", vec.z());
		return tag;
	}
	
	public static Vec3f loadVectorf(CompoundTag tag) {
		return new Vec3f(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"));
	}
	
}
