package de.m_marvin.industria.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import cpw.mods.modlauncher.api.INameMappingService.Domain;
import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.network.CBreakConduitPackage;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.conduit.ConduitHandlerCapability;
import de.m_marvin.industria.util.conduit.ConduitHitResult;
import de.m_marvin.industria.util.conduit.ConduitPos;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import de.m_marvin.industria.util.unifiedvectors.Vec2f;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import de.m_marvin.industria.util.unifiedvectors.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class UtilityHelper {
	
	public static float clamp(float v, float min, float max) {
		if (v < min) return min;
		if (v > max) return max;
		return v;
	}
	
	public static double clamp(double v, double min, double max) {
		if (v < min) return min;
		if (v > max) return max;
		return v;
	}
	
	public static float clampToDegree(float angle) {
		return angle % 360;
	}
	
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
	
	public static Vec3f rotatePoint(Vec3f point, double angle, Axis axis) {
		Vec3f rotationVec = null;
		switch (axis) {
		case X: rotationVec = new Vec3f(1, 0, 0); break;
		case Y: rotationVec = new Vec3f(0, 1, 0); break;
		case Z: rotationVec = new Vec3f(0, 0, 1); break;
		}
		return rotateVectorCC(point, rotationVec, angle);
	}
	
	public static Vec3i rotatePoint(Vec3i point, double angle, Axis axis) {
		Vec3f rotationVec = null;
		switch (axis) {
		case X: rotationVec = new Vec3f(1, 0, 0); break;
		case Y: rotationVec = new Vec3f(0, 1, 0); break;
		case Z: rotationVec = new Vec3f(0, 0, 1); break;
		}
		return rotateVectorCC(point, rotationVec, angle);
	}
	
	public static Vec3i rotateVectorCC(Vec3i vec, Vec3f axis, double theta){
		float x, y, z;
		float u, v, w;
		x=vec.x();y=vec.y();z=vec.z();
		u=axis.x();v=axis.y();w=axis.z();
		int xPrime = (int) Math.round( (u*(u*x + v*y + w*z)*(1d - Math.cos(theta)) 
				+ x*Math.cos(theta)
				+ (-w*y + v*z)*Math.sin(theta)));
		int yPrime = (int) Math.round(  (v*(u*x + v*y + w*z)*(1d - Math.cos(theta))
				+ y*Math.cos(theta)
				+ (w*x - u*z)*Math.sin(theta)));
		int zPrime = (int) Math.round(  (w*(u*x + v*y + w*z)*(1d - Math.cos(theta))
				+ z*Math.cos(theta)
				+ (-v*x + u*y)*Math.sin(theta)));
		return new Vec3i(xPrime, yPrime, zPrime);
	}
	
	public static Vec3f rotateVectorCC(Vec3f vec, Vec3f axis, double theta){
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
		return new Vec3f(xPrime, yPrime, zPrime);
	}
	
	public static VoxelShape rotateShape(VoxelShape shape, Vec3f rotationPoint, Direction direction, Axis axis) {
		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
			Vec3f vecMin = new Vec3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
			vecMin.sub(rotationPoint);
			Vec3f vecMax = new Vec3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
			vecMax.sub(rotationPoint);	
			Vec3f rvec1 = rotatePoint(vecMin, Math.toRadians(directionAngle(direction)), axis);
			rvec1.add(rotationPoint);
			Vec3f rvec2 = rotatePoint(vecMax, Math.toRadians(directionAngle(direction)), axis);
			rvec2.add(rotationPoint);
			vecMin = new Vec3f(Math.min(rvec1.x(), rvec2.x()), Math.min(rvec1.y(), rvec2.y()), Math.min(rvec1.z(), rvec2.z()));
			vecMax = new Vec3f(Math.max(rvec1.x(), rvec2.x()), Math.max(rvec1.y(), rvec2.y()), Math.max(rvec1.z(), rvec2.z()));
			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
	}
	
	public static VoxelShape rotateShape(VoxelShape shape, Vec3f rotationPoint, double angle, Axis axis) {
		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
			Vec3f vecMin = new Vec3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
			vecMin.sub(rotationPoint);
			Vec3f vecMax = new Vec3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
			vecMax.sub(rotationPoint);	
			Vec3f rvec1 = rotatePoint(vecMin, angle, axis);
			rvec1.add(rotationPoint);
			Vec3f rvec2 = rotatePoint(vecMax, angle, axis);
			rvec2.add(rotationPoint);
			vecMin = new Vec3f(Math.min(rvec1.x(), rvec2.x()), Math.min(rvec1.y(), rvec2.y()), Math.min(rvec1.z(), rvec2.z()));
			vecMax = new Vec3f(Math.max(rvec1.x(), rvec2.x()), Math.max(rvec1.y(), rvec2.y()), Math.max(rvec1.z(), rvec2.z()));
			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
	}
	
	public static VoxelShape offsetShape(VoxelShape shape, Vec3f offset) {
		return Stream.of(shape.toAabbs().toArray(new AABB[] {})).map((aabb) -> {
			Vec3f vecMin = new Vec3f((float) aabb.minX * 16F, (float) aabb.minY * 16F, (float) aabb.minZ * 16F);
			vecMin.add(offset);
			Vec3f vecMax = new Vec3f((float) aabb.maxX * 16F, (float) aabb.maxY * 16F, (float) aabb.maxZ * 16F);
			vecMax.add(offset);	
			return Block.box(vecMin.x(), vecMin.y(), vecMin.z(), vecMax.x(), vecMax.y(), vecMax.z());
		}).reduce((shape1, shape2) -> Shapes.or(shape1, shape2)).get(); 
	}
	
	public static Vec3f rotationFromFaceAndAngle(Direction face, float angle) {
		return rotationFromAxisAndAngle(face.getAxis(), face.getAxisDirection() == AxisDirection.POSITIVE ? angle : -angle);
	}
	
	public static Vec3f rotationFromAxisAndAngle(Axis axis, float angle) {
		switch (axis) {
			case X:
				return new Vec3f(angle, 0, 0);
			case Y:
				return new Vec3f(0, angle, 0);
			case Z:
				return new Vec3f(0, 0, angle);
		};
		return new Vec3f(0, 0, 0);
	}
	
	public static boolean setConduit(Level level, ConduitPos position, Conduit conduit, int nodesPerBlock) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			return conduitHolder.resolve().get().placeConduit(position, conduit, nodesPerBlock);
		}
		return false;
	}
	
	public static boolean removeConduit(Level level, ConduitPos position, boolean dropItems) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			return conduitHolder.resolve().get().breakConduit(position, dropItems);
		}
		return false;
	}

	public static Optional<PlacedConduit> getConduit(Level level, ConduitPos position) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			return conduitHolder.resolve().get().getConduit(position);
		}
		return Optional.empty();
	}

	public static Optional<PlacedConduit> getConduitAtNode(Level level, ConnectionPoint node) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			return conduitHolder.resolve().get().getConduitAtNode(node);
		}
		return Optional.empty();
	}
	
	public static List<PlacedConduit> getConduitsAtBlock(Level level, BlockPos position) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			return conduitHolder.resolve().get().getConduitsAtBlock(position);
		}
		return new ArrayList<>();
	}
	
	public static List<PlacedConduit> getConduitsInChunk(Level level, ChunkPos chunk) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			return conduitHolder.resolve().get().getConduitsInChunk(chunk);
		}
		return new ArrayList<>();
	}
	
	public static <T> void accessReflective(Object target, String name, T value) {
		try {
			Field field = target.getClass().getDeclaredField(ObfuscationReflectionHelper.remapName(Domain.FIELD, name));
			field.setAccessible(true);
			field.set(target, value);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			Industria.LOGGER.log(org.apache.logging.log4j.Level.WARN, "Failed to write field " + name + " vie reflection on " + target.getClass().getName());
		} catch (ClassCastException e) {
			Industria.LOGGER.log(org.apache.logging.log4j.Level.WARN, "Failed to cast reflection-field " + name + " from " + target.getClass().getName());
		} catch (NoSuchFieldException | SecurityException e) {
			Industria.LOGGER.log(org.apache.logging.log4j.Level.WARN, "Failed to access field " + name + " vie reflection on " + target.getClass().getName());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T accessReflective(Object source, String name) {
		try {
			Field value = source.getClass().getDeclaredField(ObfuscationReflectionHelper.remapName(Domain.FIELD, name));
			value.setAccessible(true);
			return (T) value.get(source);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			Industria.LOGGER.log(org.apache.logging.log4j.Level.WARN, "Failed to read field " + name + " vie reflection on " + source.getClass().getName());
			return null;
		} catch (ClassCastException e) {
			Industria.LOGGER.log(org.apache.logging.log4j.Level.WARN, "Failed to cast reflection-field " + name + " from " + source.getClass().getName());
			return null;
		} catch (NoSuchFieldException | SecurityException e) {
			Industria.LOGGER.log(org.apache.logging.log4j.Level.WARN, "Failed to access field " + name + " vie reflection on " + source.getClass().getName());
			return null;
		}
	}
	
	public static ConduitHitResult clipConduits(Level level, ClipContext context, boolean skipBlockClip) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			ConduitHitResult cResult = conduitHolder.resolve().get().clipConduits(context);
			if (cResult.isHit() && !skipBlockClip) {
				Vec3f newTarget = cResult.getHitPos().copy();
				Vec3f blockDistance = new Vec3f(context.getTo().subtract(context.getFrom()));
				blockDistance.normalize();
				newTarget.add(blockDistance.mul(-0.1F));
				
				accessReflective(context, "to", newTarget.getVec3());
				BlockHitResult bResult = level.clip(context);
				if (bResult.getType() == Type.BLOCK) {
					return ConduitHitResult.block(bResult);
				}
			}
			return cResult;
		}
		return ConduitHitResult.miss();
	}
	
	public static Vec3f getWorldGravity(BlockGetter level) {
		return new Vec3f(0, 0.1F, 0); // TODO
	}

	public static boolean isInChunk(ChunkPos chunk, BlockPos block) {
		return 	chunk.getMinBlockX() <= block.getX() && chunk.getMaxBlockX() >= block.getX() &&
				chunk.getMinBlockZ() <= block.getZ() && chunk.getMaxBlockZ() >= block.getZ();
	}
	
	public static Set<ChunkPos> getChunksOnLine(Vec2f from, Vec2f to) {	
		Vec2f lineVec = to.copy().sub(from);
		Vec2f chunkOff = from.copy().module(16);
		chunkOff.x = lineVec.x() < 0 ? -(16 - chunkOff.x()) : chunkOff.x();
		chunkOff.y = lineVec.y() < 0 ? -(16 - chunkOff.y()) : chunkOff.y();
		Vec2f worldOff = from.copy().sub(chunkOff);
		Vec2f lineRlativeTarget = to.copy().sub(worldOff);
		
		int insecsX = (int) Math.floor(Math.abs(lineRlativeTarget.x()) / 16);
		int insecsZ = (int) Math.floor(Math.abs(lineRlativeTarget.y()) / 16);
		
		Set<ChunkPos> chunks = new HashSet<ChunkPos>();
		chunks.add(new ChunkPos(new BlockPos(from.x, 0, from.y)));
		
		for (int insecX = 1; insecX <= insecsX; insecX++) {
			int chunkX = (int) (worldOff.x + insecX * (lineVec.x() < 0 ? -16 : 16));
			if (lineVec.x() < 0) chunkX -= 1;
			int chunkZ = (int) ((Math.abs(chunkX - from.x()) / Math.abs(lineVec.x())) * lineVec.y() + from.y());
			chunks.add(new ChunkPos(new BlockPos(chunkX, 0, chunkZ)));
		}
		for (int insecZ = 1; insecZ <= insecsZ; insecZ++) {
			int chunkZ = (int) (worldOff.y + insecZ * (lineVec.y() < 0 ? -16 : 16));
			if (lineVec.y() < 0) chunkZ -= 1;
			int chunkX = (int) ((Math.abs(chunkZ - from.y()) / Math.abs(lineVec.y())) * lineVec.x() + from.x());
			chunks.add(new ChunkPos(new BlockPos(chunkX, 0, chunkZ)));
		}
		
		return chunks;
	}
	
	public static Vec3f[] lineInfinityIntersection(Vec3f lineA1, Vec3f lineA2, Vec3f lineB1, Vec3f lineB2) {
		Vec3f p43 = new Vec3f(lineB2.x - lineB1.x, lineB2.y - lineB1.y, lineB2.z - lineB1.z);
		Vec3f p21 = new Vec3f(lineA2.x - lineA1.x, lineA2.y - lineA1.y, lineA2.z - lineA1.z);
		Vec3f p13 = new Vec3f(lineA1.x - lineB1.x, lineA1.y - lineB1.y, lineA1.z - lineB1.z);
		double d1343 = p13.x * p43.x + p13.y * p43.y + p13.z * p43.z;
		double d4321 = p43.x * p21.x + p43.y * p21.y + p43.z * p21.z;
		double d4343 = p43.x * p43.x + p43.y * p43.y + p43.z * p43.z;
		double d2121 = p21.x * p21.x + p21.y * p21.y + p21.z * p21.z;
		double denom = d2121 * d4343 - d4321 * d4321;
		double d1321 = p13.x * p21.x + p13.y * p21.y + p13.z * p21.z;
		double numer = d1343 * d4321 - d1321 * d4343;
		
		double mua = numer / denom;
		double mub = (d1343 + d4321 * mua) / d4343;
		
		Vec3 cl1 = new Vec3(lineA1.x+mua*p21.x, lineA1.y+mua*p21.y, lineA1.z+mua*p21.z);
		Vec3 cl2 = new Vec3(lineB1.x+mub*p43.x, lineB1.y+mub*p43.y, lineB1.z+mub*p43.z);
		
		return new Vec3f[] {new Vec3f(cl1), new Vec3f(cl2)};
	}
	
	public static boolean isOnLine(Vec3f point, Vec3f line1, Vec3f line2, float t) {
		return line1.copy().sub(point).length() + line2.copy().sub(point).length() <= line1.copy().sub(line2).length() + t;
	}

	public static Optional<Vec3f> getHitPoint(Vec3f lineA1, Vec3f lineA2, Vec3f lineB1, Vec3f lineB2, float tolerance) {
		Vec3f[] shortesLine = lineInfinityIntersection(lineA1, lineA2, lineB1, lineB2);
		if (isOnLine(shortesLine[0], lineA1, lineA2, 0.1F) && isOnLine(shortesLine[1], lineB1, lineB2, 0.1F)) {
			if (shortesLine[0].copy().sub(shortesLine[1]).length() <= tolerance) return Optional.of(shortesLine[0]);
		}
		return Optional.empty();
	}
	
	public static boolean doLinesCross(Vec3f lineA1, Vec3f lineA2, Vec3f lineB1, Vec3f lineB2, float tolerance) {
		Vec3f[] shortesLine = lineInfinityIntersection(lineA1, lineA2, lineB1, lineB2);
		if (isOnLine(shortesLine[0], lineA1, lineA2, 0.1F) && isOnLine(shortesLine[1], lineB1, lineB2, 0.1F)) {
			return shortesLine[0].copy().sub(shortesLine[1]).length() <= tolerance;
		}
		return false;
	}

	public static void dropItem(Level level, ItemStack stack, Vec3f position, float spreadFactH, float spreadFactV) {
		ItemEntity drop = new ItemEntity(level, position.x, position.y, position.z, stack);
		Vec3f spread = new Vec3f(
				(level.random.nextFloat() - 0.5F) * spreadFactH,
				level.random.nextFloat() * spreadFactV,
				(level.random.nextFloat() - 0.5F) * spreadFactH
				);
		drop.setDeltaMovement(spread.getVec3());
		level.addFreshEntity(drop);
	}

	public static void removeConduitFromClient(Level level, ConduitPos conduitPosition, boolean dropItems) {
		UtilityHelper.removeConduit(level, conduitPosition, dropItems);
		Industria.NETWORK.sendToServer(new CBreakConduitPackage(conduitPosition, dropItems));
	}
	
}
