package de.m_marvin.industria.core.util;

import java.util.stream.Stream;

import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.api.IVector4;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeUtility {
	
//	public static VoxelShape rotateShape(VoxelShape shape, Vec3f rotationPoint, Direction direction, Axis axis) {
//		return rotateShape(shape, rotationPoint, MathUtility.directionHoriziontalAngleDegrees(direction), true, axis); 
//	}
//	
//	public static VoxelShape rotateShape(VoxelShape shape, Vec3f rotationPoint, double angle, boolean degrees, Axis axis) {
//		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
//			Vec3f vecMin = new Vec3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
//			vecMin.subI(rotationPoint);
//			Vec3f vecMax = new Vec3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
//			vecMax.subI(rotationPoint);	
//			Vec3f rvec1 = MathUtility.rotatePoint(vecMin, (float) angle, degrees, axis);
//			rvec1.addI(rotationPoint);
//			Vec3f rvec2 = MathUtility.rotatePoint(vecMax, (float) angle, degrees, axis);
//			rvec2.addI(rotationPoint);
//			vecMin = new Vec3f(Math.min(rvec1.x(), rvec2.x()), Math.min(rvec1.y(), rvec2.y()), Math.min(rvec1.z(), rvec2.z()));
//			vecMax = new Vec3f(Math.max(rvec1.x(), rvec2.x()), Math.max(rvec1.y(), rvec2.y()), Math.max(rvec1.z(), rvec2.z()));
//			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
//		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
//	}
//	
//	public static VoxelShape offsetShape(VoxelShape shape, Vec3f offset) {
//		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
//			Vec3f vecMin = new Vec3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
//			vecMin.addI(offset);
//			Vec3f vecMax = new Vec3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
//			vecMax.addI(offset);	
//			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
//		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
//	}
	
	public static VoxelShape box(float ax, float ay, float az, float bx, float by, float bz) {
		return Shapes.create(
				(ax > bx ? bx : ax) * 0.0625F, 
				(ay > by ? by : ay) * 0.0625F, 
				(az > bz ? bz : az) * 0.0625F, 
				(ax <= bx ? bx : ax) * 0.0625F, 
				(ay <= by ? by : ay) * 0.0625F, 
				(az <= bz ? bz : az) * 0.0625F
		);
	}

	public static VoxelShape create(float ax, float ay, float az, float bx, float by, float bz) {
		return Shapes.create(
				(ax > bx ? bx : ax), 
				(ay > by ? by : ay), 
				(az > bz ? bz : az), 
				(ax <= bx ? bx : ax), 
				(ay <= by ? by : ay), 
				(az <= bz ? bz : az)
		);
	}
	
	public static VoxelShapeRotationBuilder transformation() {
		return new VoxelShapeRotationBuilder();
	}
	
	public static class VoxelShapeRotationBuilder {
		
		private Matrix4f matrix = new Matrix4f();
		private VoxelShapeRotationBuilder() {}
		
		public VoxelShapeRotationBuilder offset(int x, int y, int z) {
			this.matrix.mulI(Matrix4f.translateMatrix(x * 0.0625F, y * 0.0625F, z * 0.0625F));
			return this;
		}
		
		public VoxelShapeRotationBuilder centered() {
			return this.offset(8, 8, 8);
		}
		
		public VoxelShapeRotationBuilder uncentered() {
			return this.offset(-8, -8, -8);
		}
		
		public VoxelShapeRotationBuilder rotateX(int degrees) {
			this.matrix.mulI(Matrix4f.rotationMatrixX((float) Math.toRadians(degrees)));
			return this;
		}

		public VoxelShapeRotationBuilder rotateY(int degrees) {
			this.matrix.mulI(Matrix4f.rotationMatrixY((float) Math.toRadians(degrees)));
			return this;
		}

		public VoxelShapeRotationBuilder rotateZ(int degrees) {
			this.matrix.mulI(Matrix4f.rotationMatrixZ((float) Math.toRadians(degrees)));
			return this;
		}
		
		public VoxelShapeRotationBuilder scale(float x, float y, float z) {
			this.matrix.mulI(Matrix4f.scaleMatrix(x, y, z));
			return this;
		}
		
		public VoxelShape transform(VoxelShape shape) {
			return shape.toAabbs().stream().map(aabb -> {
				IVector4<Float> av = this.matrix.translate(new Vec4f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, 1F));
				IVector4<Float> bv = this.matrix.translate(new Vec4f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ, 1F));
				return VoxelShapeUtility.create(av.x(), av.y(), av.z(), bv.x(), bv.y(), bv.z());
			}).reduce(Shapes::or).get();
		}
		
	}
	
}
