package de.m_marvin.industria.util;

import java.util.stream.Stream;

import com.jozufozu.flywheel.repack.joml.Vector3i;
import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UtilityHelper {
	
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
	
	public static BlockPos getMinCorner(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(
				Math.min(pos1.getX(), pos2.getX()),
				Math.min(pos1.getY(), pos2.getY()),
				Math.min(pos1.getZ(), pos2.getZ())
			);
	}
	public static BlockPos getMaxCorner(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(
				Math.max(pos1.getX(), pos2.getX()),
				Math.max(pos1.getY(), pos2.getY()),
				Math.max(pos1.getZ(), pos2.getZ())
			);
	}
	
	public static BlockPos getMiddle(BlockPos pos1, BlockPos pos2) {
		int middleX = Math.min(pos1.getX(), pos2.getX()) + (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX())) / 2;
		int middleY = Math.min(pos1.getY(), pos2.getY()) + (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY())) / 2;
		int middleZ = Math.min(pos1.getZ(), pos2.getZ()) + (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ())) / 2;
		return new BlockPos(middleX, middleY, middleZ);
	}
	
	public static double directionAngle(Direction direction) {
		switch (direction) {
		case NORTH: return 0;
		case SOUTH: return 180;
		case EAST: return -90;
		case WEST: return 90;
		case UP: return 90;
		case DOWN: return -90;
		default: return 0;
		}
	}
	
	public static Vector3f rotatePoint(Vector3f point, double angle, Axis axis) {
		Vector3f rotationVec = null;
		switch (axis) {
		case X: rotationVec = new Vector3f(1, 0, 0); break;
		case Y: rotationVec = new Vector3f(0, 1, 0); break;
		case Z: rotationVec = new Vector3f(0, 0, 1); break;
		}
		return rotateVectorCC(point, rotationVec, angle);
	}
	
	public static Vector3f rotateVectorCC(Vector3f vec, Vector3f axis, double theta){
	    float x, y, z;
	    float u, v, w;
	    x=vec.x();y=vec.y();z=vec.z();
	    u=axis.x();v=axis.y();w=axis.z();
	    float xPrime = (float) (u*(u*x + v*y + w*z)*(1d - Math.cos(theta)) 
	            + x*Math.cos(theta)
	            + (-w*y + v*z)*Math.sin(theta));
	    float yPrime = (float) (v*(u*x + v*y + w*z)*(1d - Math.cos(theta))
	            + y*Math.cos(theta)
	            + (w*x - u*z)*Math.sin(theta));
	    float zPrime = (float) (w*(u*x + v*y + w*z)*(1d - Math.cos(theta))
	            + z*Math.cos(theta)
	            + (-v*x + u*y)*Math.sin(theta));
	    return new Vector3f(xPrime, yPrime, zPrime);
	}
	
	public static VoxelShape rotateShape(VoxelShape shape, Vector3f rotationPoint, Direction direction, Axis axis) {
		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
			Vector3f vecMin = new Vector3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
			vecMin.sub(rotationPoint);
			Vector3f vecMax = new Vector3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
			vecMax.sub(rotationPoint);	
			Vector3f rvec1 = rotatePoint(vecMin, Math.toRadians(directionAngle(direction)), axis);
			rvec1.add(rotationPoint);
			Vector3f rvec2 = rotatePoint(vecMax, Math.toRadians(directionAngle(direction)), axis);
			rvec2.add(rotationPoint);
			vecMin = new Vector3f(Math.min(rvec1.x(), rvec2.x()), Math.min(rvec1.y(), rvec2.y()), Math.min(rvec1.z(), rvec2.z()));
			vecMax = new Vector3f(Math.max(rvec1.x(), rvec2.x()), Math.max(rvec1.y(), rvec2.y()), Math.max(rvec1.z(), rvec2.z()));
			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
	}
	
	public static VoxelShape rotateShape(VoxelShape shape, Vector3f rotationPoint, double angle, Axis axis) {
		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
			Vector3f vecMin = new Vector3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
			vecMin.sub(rotationPoint);
			Vector3f vecMax = new Vector3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
			vecMax.sub(rotationPoint);	
			Vector3f rvec1 = rotatePoint(vecMin, angle, axis);
			rvec1.add(rotationPoint);
			Vector3f rvec2 = rotatePoint(vecMax, angle, axis);
			rvec2.add(rotationPoint);
			vecMin = new Vector3f(Math.min(rvec1.x(), rvec2.x()), Math.min(rvec1.y(), rvec2.y()), Math.min(rvec1.z(), rvec2.z()));
			vecMax = new Vector3f(Math.max(rvec1.x(), rvec2.x()), Math.max(rvec1.y(), rvec2.y()), Math.max(rvec1.z(), rvec2.z()));
			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
	}
	
	public static VoxelShape offsetShape(VoxelShape shape, Vector3f offset) {
		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
			Vector3f vecMin = new Vector3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
			vecMin.add(offset);
			Vector3f vecMax = new Vector3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
			vecMax.add(offset);	
			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
	}
	
}
