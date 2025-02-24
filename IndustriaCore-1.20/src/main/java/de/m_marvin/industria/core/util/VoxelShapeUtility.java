package de.m_marvin.industria.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import de.m_marvin.unimat.api.IQuaternionMath.EulerOrder;
import de.m_marvin.unimat.impl.Matrix4f;
import de.m_marvin.unimat.impl.Quaternionf;
import de.m_marvin.univec.api.IVector4;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.univec.impl.Vec4f;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeUtility {
	
	private VoxelShapeUtility() {}
	
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
	
	public static enum ShapeType {
		MISC(),
		COLLISION(),
		VISUAL(),
		INTERACTION(),
		BLOCK_SUPPORT();

		private final Map<BlockState, VoxelShape> shapeStateCache = new ConcurrentHashMap<BlockState, VoxelShape>();
	}
	
	public static VoxelShape stateCachedShape(ShapeType type, BlockState state, Supplier<VoxelShape> shapeSource) {
		VoxelShape shape = type.shapeStateCache.get(state);
		if (shape == null) {
			shape = shapeSource.get();
			type.shapeStateCache.put(state, shape);
		}
		return shape;
	}
	
	public static VoxelShapeRotationBuilder transformation() {
		return new VoxelShapeRotationBuilder();
	}
	
	public static class VoxelShapeRotationBuilder {
		
		private Matrix4f matrix = new Matrix4f();
		private VoxelShapeRotationBuilder() {}
		
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
				return rotateY((direction.get2DDataValue() - 2) * -90);
			}
		}
		
		public VoxelShapeRotationBuilder rotateFromAxisX(Axis axis) {
			switch (axis) {
			case Y: return rotateZ(+90);
			case Z: return rotateY(-90);
			default: return this;
			}
		}

		public VoxelShapeRotationBuilder rotateFromAxisY(Axis axis) {
			switch (axis) {
			case X: return rotateZ(-90);
			case Z: return rotateX(+90);
			default: return this;
			}
		}
		
		public VoxelShapeRotationBuilder rotateFromAxisZ(Axis axis) {
			switch (axis) {
			case X: return rotateY(+90);
			case Y: return rotateX(-90);
			default: return this;
			}
		}
		
		public VoxelShapeRotationBuilder rotateAround(Axis axis, int angle) {
			if (angle == 0) return this;
			switch (axis) {
			case X: return rotateX(angle);
			case Y: return rotateY(angle);
			case Z: return rotateZ(angle);
			default: return this;
			}
		}
		
		public VoxelShapeRotationBuilder rotateX(int degrees) {
			return rotate(degrees, 0, 0);
		}

		public VoxelShapeRotationBuilder rotateY(int degrees) {
			return rotate(0, degrees, 0);
		}

		public VoxelShapeRotationBuilder rotateZ(int degrees) {
			return rotate(0, 0, degrees);
		}
		
		public VoxelShapeRotationBuilder rotate(int x, int y, int z) {
			this.matrix = new Matrix4f(new Quaternionf(new Vec3i(x, y, z), EulerOrder.XYZ, true)).mul(this.matrix);
			return this;
		}

		public VoxelShapeRotationBuilder offset(int x, int y, int z) {
			this.matrix = Matrix4f.translateMatrix(x * 0.0625F, y * 0.0625F, z * 0.0625F).mul(this.matrix);
			return this;
		}
		
		public VoxelShapeRotationBuilder scale(float x, float y, float z) {
			this.matrix = Matrix4f.scaleMatrix(x, y, z).mul(this.matrix);
			return this;
		}
		
		public VoxelShape transform(VoxelShape shape) {
			if (shape.isEmpty()) return shape;
			return shape.toAabbs().stream().map(aabb -> {
				IVector4<Float> av = this.matrix.translate(new Vec4f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, 1F));
				IVector4<Float> bv = this.matrix.translate(new Vec4f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ, 1F));
				return VoxelShapeUtility.create(av.x(), av.y(), av.z(), bv.x(), bv.y(), bv.z());
			}).reduce(Shapes::or).get();
		}
		
	}
	
}
