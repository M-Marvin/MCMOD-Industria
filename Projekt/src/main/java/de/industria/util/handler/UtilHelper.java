package de.industria.util.handler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fluids.FluidStack;

public class UtilHelper {
	
	@SuppressWarnings("unchecked")
	public static <E> E[] sortRandom(Class<E> clazz, E[] array, Random rand) {
		E[] randArr = (E[]) Array.newInstance(clazz, array.length);
		
		for (E entry : array) {
			int randOffset = rand.nextInt(array.length - 1);
			
			boolean flag = false;
			while (randArr[randOffset] != null) {
				if (flag && randOffset == array.length - 1) {
					throw new RuntimeException("Error on randomize genric Array!");
				}
				if (randOffset == array.length - 1) flag = true;
				randOffset++;
				randOffset %= array.length;
			}
			
			randArr[randOffset] = entry;
		}
		
		return randArr;
	}
	
	@SafeVarargs
	public static <T> List<T> toCollection(T... items) {
		List<T> list = new ArrayList<T>();
		for (T item : items) {
			if (item instanceof ItemStack && ((ItemStack) item).isEmpty()) continue;
			if (item instanceof FluidStack && ((FluidStack) item).isEmpty()) continue;
			if (item == null) continue;
			list.add(item);
		}
		return list;
	}
	
	public static <T> List<T> toCollection(T[] items, T item) {
		List<T> list = new ArrayList<T>();
		for (T item1 : items) list.add(item1);
		list.add(item);
		return list;
	}
	
	public static BlockPos rotateBlockPos(BlockPos pos, Direction facing) {
		switch (facing) {
		case NORTH: return pos;
		case SOUTH: return new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
		case EAST: return new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
		case WEST: return new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
		default: return pos;
		}
	}
	
	public static Rotation directionToRotation(Direction facing) {
		switch (facing) {
		case NORTH: return Rotation.NONE;
		case SOUTH: return Rotation.CLOCKWISE_180;
		case EAST: return Rotation.CLOCKWISE_90;
		case WEST: return Rotation.COUNTERCLOCKWISE_90;
		default: return Rotation.NONE;
		}
	}

	public static Ingredient toIngredient(ItemStack... stack) {
		return Ingredient.of(stack);
	}
	
	public static boolean containsArray(Object[] array, Object object) {
		for (Object o : array) if (o.equals(object)) return true;
		return false;
	}
	
	public static List<Direction> getDirectionsOutOfAxis(Axis axis) {
		List<Direction> directions = new ArrayList<Direction>();
		for (Direction d : Direction.values()) if (d.getAxis() != axis) directions.add(d);
		return directions;
	}

	public static Direction getDirectionOutOfAxis(Axis axis) {
		for (Direction d : Direction.values()) if (d.getAxis() != axis) return d;
		return Direction.NORTH;
	}
	
	public static Direction rotateOnAxis(Direction d1, int i, Axis axis) {
		Vector3f vec = axis == Axis.X ? Vector3f.XP : axis == Axis.Y ? Vector3f.YP : Vector3f.ZP;
		Matrix4f matrix = new Matrix4f(vec.rotationDegrees(i));
		return Direction.rotate(matrix, d1);
	}
	
	public static byte[] makeArrayFromBuffer(PacketBuffer buf) {
		int arrayLength = buf.writerIndex();
		return Arrays.copyOf(buf.array(), arrayLength);
	}
	
}
