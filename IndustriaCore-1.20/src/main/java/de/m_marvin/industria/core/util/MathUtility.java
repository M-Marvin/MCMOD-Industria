package de.m_marvin.industria.core.util;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.unimat.impl.Quaternionf;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.univec.impl.Vec4f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MathUtility {

	private MathUtility() {}
	
	public static final double ANGULAR_VELOCITY_TO_ROTATIONS_PER_SECOND = (180.0 / Math.PI) / 360.0;
	public static final double ROTATIONS_PER_SECOND_TO_ANGULAR_VELOCITY = 360 / (180.0 / Math.PI);

	public static Direction.Axis rotate(Rotation rotation, Direction.Axis axis) {
		switch (rotation) {
		  case COUNTERCLOCKWISE_90:
		  case CLOCKWISE_90:
			  switch (axis) {
				  case X:
					  return Direction.Axis.Z;
				  case Z:
					  return Direction.Axis.X;
				  default:
					  return axis;
			  }
		  default:
			  return axis;
		}
	}
	
	public static int toIntegerColor(int r, int g, int b, int a) {
		return new Color(r, g, b, a).getRGB();
	}
	
	public static int toIntegerColor(float r, float g, float b, float a) {
		return new Color(r, g, b, a).getRGB();
	}
	
	public static int toIntegerColor(Vec4f color) {
		return new Color(color.x, color.y, color.z, color.w).getRGB();
	}
	
	public static Vec4f toVecColor(int color) {
		Color colorc = new Color(color);
		return new Vec4f(colorc.getRed() / 255F, colorc.getGreen() / 255F, colorc.getBlue() / 255F, colorc.getAlpha() / 255F);
	}
	
	public static Direction getFacingDirection(Entity entity) {
		Vec3d viewVec = Vec3d.fromVec(entity.getViewVector(1));
		return MathUtility.getVecDirection(viewVec);
	}
	
	public static Direction getPosRelativeFacing(BlockPos pos1, BlockPos pos2) {
		return getVecDirection(Vec3i.fromVec(pos2).sub(Vec3i.fromVec(pos1)));
	}
	
	public static int rasterDistance(BlockPos pos1, BlockPos pos2) {
		return  Math.abs(pos1.getX() - pos2.getX()) +
				Math.abs(pos1.getY() - pos2.getY()) +
				Math.abs(pos1.getZ() - pos2.getZ());
	}
	
	public static BlockPos toBlockPos(double x, double y, double z) {
		return new BlockPos((int) Mth.floor(x), (int) Math.floor(y), (int) Math.floor(z));
	}
	
	public static BlockPos toBlockPos(Vec3f vec) {
		return toBlockPos(vec.x, vec.y, vec.z);
	}

	public static BlockPos toBlockPos(Vec3i vec) {
		return new BlockPos(vec.x, vec.y, vec.z);
	}

	public static BlockPos toBlockPos(Vec3d vec) {
		return toBlockPos(vec.x, vec.y, vec.z);
	}
	
	public static int clamp(int v, int min, int max) {
		if (v < min) return min;
		if (v > max) return max;
		return v;
	}
	
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
	
	public static boolean isBetweenInclusive(BlockPos minPos, BlockPos maxPos, BlockPos testPos) {
		return	minPos.getX() <= testPos.getX() && maxPos.getX() >= testPos.getX() &&
				minPos.getY() <= testPos.getY() && maxPos.getY() >= testPos.getY() &&
				minPos.getZ() <= testPos.getZ() && maxPos.getZ() >= testPos.getZ();
	}
	
	public static float clampToDegree(float angle) {
		return angle % 360;
	}

	public static Direction[] getDirectionsOrthogonal(Axis axis) {
		return Stream.of(Direction.values()).filter(d -> d.getAxis() != axis).toArray(Direction[]::new);
	}
	
	public static BlockPos[] getPositionsAroundAxis(BlockPos pos, Axis axis) {
		return Stream.of(getDirectionsOrthogonal(axis)).map(pos::relative).toArray(BlockPos[]::new);
	}
	
	public static BlockPos[] getDiagonalPositionsAroundAxis(BlockPos pos, Axis axis) {
		switch (axis) {
		case X: return new BlockPos[] {
				pos.offset(0, -1, -1),
				pos.offset(0, +1, -1),
				pos.offset(0, +1, +1),
				pos.offset(0, -1, +1)
		};
		case Z: return new BlockPos[] {
				pos.offset(-1, -1, 0),
				pos.offset(+1, -1, 0),
				pos.offset(+1, +1, 0),
				pos.offset(-1, +1, 0)
		};
		default:
		case Y: return new BlockPos[] {
				pos.offset(-1, 0, -1),
				pos.offset(+1, 0, -1),
				pos.offset(+1, 0, +1),
				pos.offset(-1, 0, +1)
		};
		}
	}
	
	public static Direction getPosDirection(BlockPos pos1, BlockPos pos2) {
		if (pos1.equals(pos2)) return null;
		return getVecDirection(Vec3d.fromVec(pos1.subtract(pos2)).normalize());
	}
	
	public static Direction getVecDirection(Vec3i v) {
		Axis axis = Axis.X;
		if (v.y() != 0) axis = Axis.Y;
		if (v.z() != 0) axis = Axis.Z;
		AxisDirection direction = (v.x + v.y + v.z) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
		return Direction.fromAxisAndDirection(axis, direction);
	}
	
	public static Direction getVecDirection(Vec3d v) {
		v = v.normalize();
		Vec3d v2 = v.abs();
		if (v2.x > v2.y && v2.x > v2.z) {
			return v.x > 0 ? Direction.EAST : Direction.WEST;
		} else if (v2.y > v2.x && v2.y > v2.z) {
			return v.y > 0 ? Direction.UP : Direction.DOWN;
		} else if (v2.z > v2.y && v2.z > v2.y) {
			return v.z > 0 ? Direction.SOUTH : Direction.NORTH;
		} else {
			return Direction.NORTH;
		}
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

	public static Vec3d getMinCorner(Vec3d pos1, Vec3d pos2) {
		return new Vec3d(
				Math.min(pos1.getX(), pos2.getX()),
				Math.min(pos1.getY(), pos2.getY()),
				Math.min(pos1.getZ(), pos2.getZ())
			);
	}
	
	public static Vec3d getMaxCorner(Vec3d pos1, Vec3d pos2) {
		return new Vec3d(
				Math.max(pos1.getX(), pos2.getX()),
				Math.max(pos1.getY(), pos2.getY()),
				Math.max(pos1.getZ(), pos2.getZ())
			);
	}
	
	public static BlockPos getMiddleBlock(BlockPos pos1, BlockPos pos2) {
		int middleX = Math.min(pos1.getX(), pos2.getX()) + (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX())) / 2;
		int middleY = Math.min(pos1.getY(), pos2.getY()) + (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY())) / 2;
		int middleZ = Math.min(pos1.getZ(), pos2.getZ()) + (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ())) / 2;
		return new BlockPos(middleX, middleY, middleZ);
	}

	public static Vec3d getMiddle(BlockPos pos1, BlockPos pos2) {
		double middleX = (double) Math.min(pos1.getX(), pos2.getX()) + (double) (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) / 2.0;
		double middleY = (double) Math.min(pos1.getY(), pos2.getY()) + (double) (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) / 2.0;
		double middleZ = (double) Math.min(pos1.getZ(), pos2.getZ()) + (double) (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1) / 2.0;
		return new Vec3d(middleX, middleY, middleZ);
	}

	public static Vec3d getMiddle(Vec3d pos1, Vec3d pos2) {
		double middleX = Math.min(pos1.getX(), pos2.getX()) + (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX())) / 2.0;
		double middleY = Math.min(pos1.getY(), pos2.getY()) + (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY())) / 2.0;
		double middleZ = Math.min(pos1.getZ(), pos2.getZ()) + (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ())) / 2.0;
		return new Vec3d(middleX, middleY, middleZ);
	}

	public static Vec3i getMiddle(Vec3i pos1, Vec3i pos2) {
		int middleX = (int) (Math.min(pos1.getX(), pos2.getX()) + (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX())) / 2.0);
		int middleY = (int) (Math.min(pos1.getY(), pos2.getY()) + (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY())) / 2.0);
		int middleZ = (int) (Math.min(pos1.getZ(), pos2.getZ()) + (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ())) / 2.0);
		return new Vec3i(middleX, middleY, middleZ);
	}
	
	public static double directionHoriziontalAngleDegrees(Direction direction) {
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
	
	public static Vec3i directionVector(Direction direction) {
		Vec3i vector = new Vec3i();
		switch (direction.getAxis()) {
		case X: vector.setX(1); break;
		case Y: vector.setY(1); break;
		case Z: vector.setZ(1); break;
		}
		return direction.getAxisDirection() == AxisDirection.POSITIVE ? vector : vector.mul(-1);
	}
	
	public static double directionRelativeAngleDegrees(Direction direction, Direction origin) {
		return directionVector(direction).angle(directionVector(direction));
	}
	
	public static Vec3d rotatePoint(Vec3d point, float angle, boolean degrees, Axis axis) {
		Vec3d rotationAxis = null;
		switch (axis) {
		case X: rotationAxis = new Vec3d(1, 0, 0); break;
		case Y: rotationAxis = new Vec3d(0, 1, 0); break;
		case Z: rotationAxis = new Vec3d(0, 0, 1); break;
		}
		return rotatePoint(point, rotationAxis, angle, degrees);
	}
	
	public static Vec3f rotatePoint(Vec3f point, float angle, boolean degrees, Axis axis) {
		Vec3f rotationAxis = null;
		switch (axis) {
		case X: rotationAxis = new Vec3f(1, 0, 0); break;
		case Y: rotationAxis = new Vec3f(0, 1, 0); break;
		case Z: rotationAxis = new Vec3f(0, 0, 1); break;
		}
		return rotatePoint(point, rotationAxis, angle, degrees);
	}
	
	public static Vec3i rotatePoint(Vec3i point, float angle, boolean degrees, Axis axis) {
		Vec3f rotationAxis = null;
		switch (axis) {
		case X: rotationAxis = new Vec3f(1, 0, 0); break;
		case Y: rotationAxis = new Vec3f(0, 1, 0); break;
		case Z: rotationAxis = new Vec3f(0, 0, 1); break;
		}
		return rotatePoint(point, rotationAxis, angle, degrees);
	}

	public static Vec3d rotatePoint(Vec3d point, Vec3d axis, float angle, boolean degrees) {
		if (degrees) angle = (float) Math.toRadians(angle);
		Quaterniond quat = new Quaterniond(axis, angle);
		return point.transform(quat);
	}
	
	public static Vec3f rotatePoint(Vec3f point, Vec3f axis, float angle, boolean degrees) {
		if (degrees) angle = (float) Math.toRadians(angle);
		Quaternionf quat = new Quaternionf(axis, angle);
		return point.transform(quat);
	}
	
	public static Vec3i rotatePoint(Vec3i point, Vec3f axis, float angle, boolean degrees) {
		if (degrees) angle = (float) Math.toRadians(angle);
		Quaternionf quat = new Quaternionf(axis, angle);
		Vec3f transform = new Vec3f(point).transform(quat);
		return new Vec3i(Math.round(transform.x), Math.round(transform.y), Math.round(transform.z));
	}
	
	public static boolean isInChunk(ChunkPos chunk, BlockPos block) {
		return 	chunk.getMinBlockX() <= block.getX() && chunk.getMaxBlockX() >= block.getX() &&
				chunk.getMinBlockZ() <= block.getZ() && chunk.getMaxBlockZ() >= block.getZ();
	}
	
	public static Set<ChunkPos> getChunksOnLine(Vec2f from, Vec2f to) {	
		Vec2f lineVec = to.copy().sub(from);
		Vec2f chunkOff = from.copy().module(16F);
		chunkOff.x = lineVec.x() < 0 ? -(16 - chunkOff.x()) : chunkOff.x();
		chunkOff.y = lineVec.y() < 0 ? -(16 - chunkOff.y()) : chunkOff.y();
		Vec2f worldOff = from.copy().sub(chunkOff);
		Vec2f lineRlativeTarget = to.copy().sub(worldOff);
		
		int insecsX = (int) Math.floor(Math.abs(lineRlativeTarget.x()) / 16);
		int insecsZ = (int) Math.floor(Math.abs(lineRlativeTarget.y()) / 16);
		
		Set<ChunkPos> chunks = new HashSet<ChunkPos>();
		chunks.add(new ChunkPos(toBlockPos(from.x, 0, from.y)));
		
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
	
	public static List<ChunkPos> getChunksBetweenBounds(ChunkPos a, ChunkPos b) {
		int lx = Math.min(a.x, b.x);
		int lz = Math.min(a.z, b.z);
		int hx = Math.max(a.x, b.x);
		int hz = Math.max(a.z, b.z);
		return IntStream.range(lx, hx).mapToObj(Integer::valueOf)
				.flatMap(x -> IntStream.range(lz, hz)
				.mapToObj(z -> new ChunkPos(x, z)))
				.toList();
	}
	
	public static Vec3d[] lineInfinityIntersection(Vec3d lineA1, Vec3d lineA2, Vec3d lineB1, Vec3d lineB2) {
		Vec3d p43 = new Vec3d(lineB2.x - lineB1.x, lineB2.y - lineB1.y, lineB2.z - lineB1.z);
		Vec3d p21 = new Vec3d(lineA2.x - lineA1.x, lineA2.y - lineA1.y, lineA2.z - lineA1.z);
		Vec3d p13 = new Vec3d(lineA1.x - lineB1.x, lineA1.y - lineB1.y, lineA1.z - lineB1.z);
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
		
		return new Vec3d[] {Vec3d.fromVec(cl1), Vec3d.fromVec(cl2)};
	}
	
	public static boolean isOnLine(Vec3d point, Vec3d line1, Vec3d line2, double t) {
		return line1.copy().sub(point).length() + line2.copy().sub(point).length() <= line1.copy().sub(line2).length() + t;
	}
	
	public static Optional<Vec3d> getHitPoint(Vec3d lineA1, Vec3d lineA2, Vec3d lineB1, Vec3d lineB2, double tolerance) {
		Vec3d[] shortesLine = lineInfinityIntersection(lineA1, lineA2, lineB1, lineB2);
		if (isOnLine(shortesLine[0], lineA1, lineA2, 0.1F) && isOnLine(shortesLine[1], lineB1, lineB2, 0.1F)) {
			if (shortesLine[0].copy().sub(shortesLine[1]).length() <= tolerance) return Optional.of(shortesLine[0]);
		}
		return Optional.empty();
	}
	
	public static boolean doLinesCross(Vec3d lineA1, Vec3d lineA2, Vec3d lineB1, Vec3d lineB2, double tolerance) {
		Vec3d[] shortesLine = lineInfinityIntersection(lineA1, lineA2, lineB1, lineB2);
		if (isOnLine(shortesLine[0], lineA1, lineA2, 0.1F) && isOnLine(shortesLine[1], lineB1, lineB2, 0.1F)) {
			return shortesLine[0].copy().sub(shortesLine[1]).length() <= tolerance;
		}
		return false;
	}
	
	private static final Predicate<Entity> ENTITY_PREDICATE_CLICKEABLE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
	
	public static HitResult getPlayerPOVHitResultWithEntitites(Level level, Player player, Fluid fluidMode, double reachDistance) {
		HitResult hitresult = getPlayerPOVHitResult(level, player, fluidMode, reachDistance);
		
		if (hitresult.getType() != HitResult.Type.MISS) {
			Vec3 vec3 = player.getViewVector(1.0F);
			List<Entity> list = level.getEntities(player,  player.getBoundingBox().expandTowards(vec3.scale(reachDistance)).inflate(1.0), ENTITY_PREDICATE_CLICKEABLE);
			if (!list.isEmpty()) {
				Vec3 eyePos = player.getEyePosition();
				
				for (Entity entity : list) {
					AABB aabb = entity.getBoundingBox().inflate((double) entity.getPickRadius());
					if (aabb.contains(eyePos)) {
						return new EntityHitResult(entity);
					}
				}
			}
		}
		return hitresult;
	}
	
	public static BlockHitResult getPlayerPOVHitResult(BlockGetter pLevel, Player pPlayer, ClipContext.Fluid pFluidMode, double reachDistance) {
		return pLevel.clip(getPlayerPOVClipContext(pLevel, pPlayer, pFluidMode, reachDistance));
	}
	
	public static ClipContext getPlayerPOVClipContext(BlockGetter pLevel, Player pPlayer, ClipContext.Fluid pFluidMode, double reachDistance) {
		float f = pPlayer.getXRot();
		float f1 = pPlayer.getYRot();
		Vec3 vec3 = pPlayer.getEyePosition();
		float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
		float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = reachDistance;
		Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
		return new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, pFluidMode, pPlayer);
	}
	
	public static Vec2f calculateBezier2D(Vec2f[] points, float s) {
		while (points.length > 1) {
			Vec2f[] p = new Vec2f[points.length - 1];
			for (int i = 0; i < p.length; i++) {
				p[i] = points[i + 1].sub(points[i]).mul(s).add(points[i]);
			}
			points = p;
		}
		return points[0];
	}
	
	public static Vec2f[] makeBezierVectors2D(Vec2f p1, Vec2f v1, Vec2f p2, Vec2f v2, float vecmaxlen) {
		float dist = p1.dist(p2);
		
		Vec2f p1b = p1.add(v1.mul(dist / 2)); // p2.sub(p1).mul(v1.abs()).add(p1);
		Vec2f p2b = p2.add(v2.mul(dist / 2)); // p1.sub(p2).mul(v2.abs()).add(p2);
		Vec2f[] points = new Vec2f[] {p1, p1b, p2b, p2};
		float distance = p1.dist(p2);
		Vec2f[] vecs = new Vec2f[Math.round(distance / vecmaxlen)];
		
		Vec2f lp = p1;
		for (int i = 0; i < vecs.length; i++) {
			float s = (i + 1) / (float) vecs.length;
			Vec2f p = calculateBezier2D(points, s);
			vecs[i] = p.sub(lp);
			lp = p;
		}
		
		return vecs;
	}
	
}
