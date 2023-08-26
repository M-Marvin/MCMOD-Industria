package de.m_marvin.industria.core.util;

import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.univec.api.IVector4;
import de.m_marvin.univec.impl.Vec4f;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeUtility {
	
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
			this.matrix = Matrix4f.translateMatrix(x * 0.0625F, y * 0.0625F, z * 0.0625F).mul(this.matrix);
			return this;
		}
		
		public VoxelShapeRotationBuilder centered() {
			return this.offset(-8, -8, -8);
		}
		
		public VoxelShapeRotationBuilder uncentered() {
			return this.offset(8, 8, 8);
		}
		
		public VoxelShapeRotationBuilder rotateFromNorth(Direction direction) {
			if (direction.getAxis() == Axis.Y) {
				return rotateX(direction.getAxisDirection() == AxisDirection.POSITIVE ? 90 : -90);
			} else {
				return rotateY(direction.get2DDataValue() * 90);
			}
		}
		
		public VoxelShapeRotationBuilder rotateX(int degrees) {
			this.matrix = Matrix4f.rotationMatrixX((float) Math.toRadians(degrees)).mul(this.matrix);
			return this;
		}

		public VoxelShapeRotationBuilder rotateY(int degrees) {
			this.matrix = Matrix4f.rotationMatrixY((float) Math.toRadians(degrees)).mul(this.matrix);
			return this;
		}

		public VoxelShapeRotationBuilder rotateZ(int degrees) {
			this.matrix = Matrix4f.rotationMatrixZ((float) Math.toRadians(degrees)).mul(this.matrix);
			return this;
		}
		
		public VoxelShapeRotationBuilder scale(float x, float y, float z) {
			this.matrix = Matrix4f.scaleMatrix(x, y, z).mul(this.matrix);
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
