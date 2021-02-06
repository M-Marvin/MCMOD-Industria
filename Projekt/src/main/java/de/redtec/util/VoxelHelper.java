package de.redtec.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class VoxelHelper {
	
	public static VoxelShape rotateShape(VoxelShape shape, Direction side) {
		
		VoxelShape newShape = VoxelShapes.empty();
		
		int rad = 0;
		switch(side) {
		default:
		case NORTH:
			rad = 0;
			break;
		case EAST:
			rad = 90;
			break;
		case SOUTH:
			rad = 180;
			break;
		case WEST:
			rad = 270;
			break;
		}
		
		for (AxisAlignedBB box : shape.toBoundingBoxList()) {
			float[] rotMin = rotatePoint((float) (box.minX - 0.5F), (float) (box.minZ - 0.5F), rad);
			float[] rotMax = rotatePoint((float) (box.maxX - 0.5F), (float) (box.maxZ - 0.5F), rad);
			
			VoxelShape rotatedBox = VoxelShapes.create(new AxisAlignedBB(rotMin[0] + 0.5F, box.minY, rotMin[1] + 0.5F, rotMax[0] + 0.5F, box.maxY, rotMax[1] + 0.5F));
			newShape = VoxelShapes.or(newShape, rotatedBox);
		}
		
		return newShape;
		
	}
	
	public static float[] rotatePoint(float x, float y, int rad) {
		
		float[] cord = new float[] {x, y};
		float i = (int) (rad / 90);
		for (int i0 = 0; i0 < i; i0++) {
			cord = rotateRight(cord[0], cord[1]);
		}
		
		return cord;
		
	}
	
	public static  float[] rotateRight(float x, float y) {
		float i1 = y;
		float i2 = x;
		return new float[] {i1 * -1, i2};
	}
	
}
