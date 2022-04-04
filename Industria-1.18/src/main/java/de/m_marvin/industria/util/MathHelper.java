package de.m_marvin.industria.util;

import com.jozufozu.flywheel.repack.joml.Vector3i;

import net.minecraft.core.BlockPos;
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
	
	public static Vector3i loadVector(CompoundTag tag) {
		return new Vector3i(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}
	
	public static BlockPos getMiddle(BlockPos pos1, BlockPos pos2) {
		int middleX = Math.min(pos1.getX(), pos2.getX()) + (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX())) / 2;
		int middleY = Math.min(pos1.getY(), pos2.getY()) + (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY())) / 2;
		int middleZ = Math.min(pos1.getZ(), pos2.getZ()) + (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ())) / 2;
		return new BlockPos(middleX, middleY, middleZ);
	}
	
}
