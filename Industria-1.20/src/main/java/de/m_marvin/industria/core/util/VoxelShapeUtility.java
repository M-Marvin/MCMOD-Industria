package de.m_marvin.industria.core.util;

import java.util.stream.Stream;

import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeUtility {
	
	public static VoxelShape rotateShape(VoxelShape shape, Vec3f rotationPoint, Direction direction, Axis axis) {
		return rotateShape(shape, rotationPoint, MathUtility.directionHoriziontalAngleDegrees(direction), true, axis); 
	}
	
	public static VoxelShape rotateShape(VoxelShape shape, Vec3f rotationPoint, double angle, boolean degrees, Axis axis) {
		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
			Vec3f vecMin = new Vec3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
			vecMin.subI(rotationPoint);
			Vec3f vecMax = new Vec3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
			vecMax.subI(rotationPoint);	
			Vec3f rvec1 = MathUtility.rotatePoint(vecMin, (float) angle, degrees, axis);
			rvec1.addI(rotationPoint);
			Vec3f rvec2 = MathUtility.rotatePoint(vecMax, (float) angle, degrees, axis);
			rvec2.addI(rotationPoint);
			vecMin = new Vec3f(Math.min(rvec1.x(), rvec2.x()), Math.min(rvec1.y(), rvec2.y()), Math.min(rvec1.z(), rvec2.z()));
			vecMax = new Vec3f(Math.max(rvec1.x(), rvec2.x()), Math.max(rvec1.y(), rvec2.y()), Math.max(rvec1.z(), rvec2.z()));
			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
	}
	
	public static VoxelShape offsetShape(VoxelShape shape, Vec3f offset) {
		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
			Vec3f vecMin = new Vec3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
			vecMin.addI(offset);
			Vec3f vecMax = new Vec3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
			vecMax.addI(offset);	
			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
	}
	
}
