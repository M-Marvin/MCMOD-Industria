package de.m_marvin.industria.core.util;

import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class NBTUtility {

	private NBTUtility() {}
	
	public static CompoundTag writeVector3i(Vec3i vec) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("x", vec.x());
		tag.putInt("y", vec.y());
		tag.putInt("z", vec.z());
		return tag;
	}
	
	public static Vec3i loadVector3i(CompoundTag tag) {
		return new Vec3i(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}

	public static CompoundTag writeVector3f(Vec3f vec) {
		CompoundTag tag = new CompoundTag();
		tag.putFloat("x", vec.x());
		tag.putFloat("y", vec.y());
		tag.putFloat("z", vec.z());
		return tag;
	}
	
	public static Vec3f loadVector3f(CompoundTag tag) {
		return new Vec3f(tag.getFloat("x"), tag.getFloat("y"), tag.getFloat("z"));
	}

	public static CompoundTag writeVector3d(Vec3d vec) {
		CompoundTag tag = new CompoundTag();
		tag.putDouble("x", vec.x());
		tag.putDouble("y", vec.y());
		tag.putDouble("z", vec.z());
		return tag;
	}
	
	public static Vec3d loadVector3d(CompoundTag tag) {
		return new Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
	}

	public static Vec3i readVector3i(FriendlyByteBuf buff) {
		return new Vec3i(buff.readInt(), buff.readInt(), buff.readInt());
	}
	
	public static void writeVector3i(Vec3i vec, FriendlyByteBuf buff) {
		buff.writeInt(vec.x());
		buff.writeInt(vec.y());
		buff.writeInt(vec.z());
	}

	public static Vec3f readVector3f(FriendlyByteBuf buff) {
		return new Vec3f(buff.readFloat(), buff.readFloat(), buff.readFloat());
	}
	
	public static void writeVector3f(Vec3f vec, FriendlyByteBuf buff) {
		buff.writeFloat(vec.x());
		buff.writeFloat(vec.y());
		buff.writeFloat(vec.z());
	}

	public static Vec3d readVector3d(FriendlyByteBuf buff) {
		return new Vec3d(buff.readDouble(), buff.readDouble(), buff.readDouble());
	}
	
	public static void writeVector3d(Vec3d vec, FriendlyByteBuf buff) {
		buff.writeDouble(vec.x());
		buff.writeDouble(vec.y());
		buff.writeDouble(vec.z());
	}
	
}
