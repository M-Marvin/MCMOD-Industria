package de.m_marvin.industria.core.contraptions;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.internal.joints.VSJoint;
import org.valkyrienskies.mod.common.BlockStateInfo;

import de.m_marvin.industria.core.contraptions.engine.ContraptionHandlerCapability;
import de.m_marvin.industria.core.contraptions.engine.VS2MassSyncPatch;
import de.m_marvin.industria.core.contraptions.engine.types.ContraptionHitResult;
import de.m_marvin.industria.core.contraptions.engine.types.ContraptionPosition;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.ClientContraption;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.Contraption;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.ServerContraption;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ContraptionUtility {

	private ContraptionUtility() {}
	
	/* Naming and finding of contraptions */

	public static <T extends Contraption> List<T> getContraptionsWithTag(Level level, String name) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getContraptionsWithTag(name);
	}

	public static <T extends Contraption> List<T> getContraptionsWithName(Level level, String name) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getContraptionsWithName(name, false);
	}
	
	public static Long2ObjectMap<Set<String>> getContraptionTags(Level level) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getContraptionTags();
	}
	
	public static <T extends Contraption> List<T> getContraptionIntersecting(Level level, BlockPos position)  {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getContraptionIntersecting(position);
	}
	
	public static <T extends Contraption> T getContraptionOfBlock(Level level, BlockPos shipBlockPos) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getContraptionOfBlock(shipBlockPos);
	}

	public static <T extends Contraption> T getContraptionById(Level level, long id) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getContraptionById(id);
	}

	/* Translating of positions and moving of contraptions */

	public static Direction toWorldDirection(ShipTransform contraption, Direction direction) {
		Vec3d vec = Vec3d.fromVec(direction.getNormal());
		Vec3d worldVec = toWorldVector(contraption, vec);
		return Direction.getNearest(worldVec.x, worldVec.y, worldVec.z);
	}
	
	public static Vec3d toWorldVector(ShipTransform contraption, Vec3d vector) {
		Vector4d v = contraption.getShipToWorld().transform(new Vector4d(vector.x, vector.y, vector.z, 0));
		return new Vec3d(v.x, v.y, v.z);
	}

	public static Direction toContraptionDirection(ShipTransform contraption, Direction direction) {
		Vec3d vec = Vec3d.fromVec(direction.getNormal());
		Vec3d shipVec = toContraptionVector(contraption, vec);
		return Direction.getNearest(shipVec.x, shipVec.y, shipVec.z);
	}
	
	public static Vec3d toContraptionVector(ShipTransform contraption, Vec3d vector) {
		Vector4d v = contraption.getWorldToShip().transform(new Vector4d(vector.x, vector.y, vector.z, 0));
		return new Vec3d(v.x, v.y, v.z);
	}
	
	public static Vec3d toContraptionPos(ShipTransform contraption, Vec3d pos) {
		Matrix4dc worldToShip = contraption.getWorldToShip();
		if (worldToShip != null) {
			Vector3d transformPosition = worldToShip.transformPosition(pos.writeTo(new Vector3d()));
			return Vec3d.fromVec(transformPosition);
		}
		return new Vec3d(0, 0, 0);
	}
	
	public static BlockPos toContraptionBlockPos(ShipTransform contraption, Vec3d pos) {
		Vec3d position = toContraptionPos(contraption, pos);
		return MathUtility.toBlockPos(position);
	}
	
	public static BlockPos toContraptionBlockPos(ShipTransform contraption, BlockPos pos) {
		return toContraptionBlockPos(contraption, Vec3d.fromVec(pos));
	}

	public static Vec3d toWorldPos(ShipTransform contraption, Vec3d pos) {
		Matrix4dc shipToWorld = contraption.getShipToWorld();
		if (shipToWorld != null) {
			Vector3d transformedPosition = shipToWorld.transformPosition(pos.writeTo(new Vector3d()));
			return Vec3d.fromVec(transformedPosition);
		}
		return new Vec3d(0, 0, 0);
	}
	
	public static Vec3d toWorldPos(ShipTransform contraption, BlockPos pos) {
		return toWorldPos(contraption, Vec3d.fromVec(pos).addI(0.5, 0.5, 0.5));
	}

	public static BlockPos toWorldBlockPos(ShipTransform contraption, BlockPos pos) {
		Vec3d position = toWorldPos(contraption, pos);
		return MathUtility.toBlockPos(position);
	}

	public static Vec3d ensureWorldCoordinates(Level level, BlockPos referencePos, Vec3d position) {
		return optionalContraptionTransform(level, referencePos, ContraptionUtility::toWorldPos, position);
	}
	
	public static Vec3d ensureContraptionCoordinates(Level level, BlockPos referencePos, Vec3d position) {
		return optionalContraptionTransform(level, referencePos, ContraptionUtility::toContraptionPos, position);
	}

	public static BlockPos ensureWorldBlockCoordinates(Level level, BlockPos referencePos, BlockPos position) {
		return optionalContraptionTransform(level, referencePos, ContraptionUtility::toWorldBlockPos, position);
	}
	
	public static BlockPos ensureContraptionBlockCoordinates(Level level, BlockPos referencePos, BlockPos position) {
		return optionalContraptionTransform(level, referencePos, ContraptionUtility::toContraptionBlockPos, position);
	}
	
	public static Direction ensureWorldDirection(Level level, BlockPos referencePos, Direction direction) {
		return optionalContraptionTransform(level, referencePos, ContraptionUtility::toWorldDirection, direction);
	}

	public static Vec3d ensureWorldVector(Level level, BlockPos referencePos, Vec3d vector) {
		return optionalContraptionTransform(level, referencePos, ContraptionUtility::toWorldVector, vector);
	}

	public static Direction ensureContraptionDirection(Level level, BlockPos referencePos, Direction direction) {
		return optionalContraptionTransform(level, referencePos, ContraptionUtility::toContraptionDirection, direction);
	}

	public static Vec3d ensureContraptionVector(Level level, BlockPos referencePos, Vec3d vector) {
		return optionalContraptionTransform(level, referencePos, ContraptionUtility::toContraptionVector, vector);
	}
	
	public static boolean teleportContraption(ServerContraption contraption, ContraptionPosition position) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(contraption.getLevel(), Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.teleportContraption(contraption, position, true);
	}

	public static void teleportContraption(ServerContraption contraption, ContraptionPosition position, boolean useGeometricCenter) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(contraption.getLevel(), Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		handler.teleportContraption(contraption, position, useGeometricCenter);
	}
	
	/* Listing and creation contraptions in the world */
	
	public static <T extends Contraption> List<T> getLoadedContraptions(Level level, boolean onlyThisDimension) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getLoadedContraptions(onlyThisDimension);
	}

	public static <T extends Contraption> List<T> getAllContraptions(Level level, boolean onlyThisDimension) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getAllContraptions(onlyThisDimension);
	}
	
	public static ServerContraption createContraptionAt(ServerLevel level, ContraptionPosition position, float scale) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.createContraptionAt(position, scale);
	}
	
	public static boolean removeContraption(ServerContraption contraption) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(contraption.getLevel(), Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.removeContraption(contraption);
	}
	
	public static boolean convertToContraption(ServerLevel level, BlockPos pos1, BlockPos pos2, boolean removeOriginal, float scale) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.convertToContraption(pos1, pos2, removeOriginal, scale);
	}
	
	public static boolean assembleToContraption(ServerLevel level, List<BlockPos> blocks, boolean removeOriginal, float scale) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.assembleToContraption(blocks, removeOriginal, scale);
	}
	
	/* Raycasting for contraptions */
	
	public static ContraptionHitResult clipForContraption(Level level, Vec3d from, Vec3d direction, double range) {
		return clipForContraption(level, from, from.add(direction.mul(range)));
	}
	
	public static ContraptionHitResult clipForContraption(Level level, Vec3d from, Vec3d to) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.clipForContraption(from, to);
	}
	
	/* Constraints */
	
	public static CompletableFuture<Integer> addConstraint(Level level, VSJoint constraint) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.addConstraint(constraint);
	}
	
	public static boolean removeConstraint(Level level, int constraint) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.removeConstaint(constraint);
	}

	public static Set<Integer> getAllConstraints(Level level) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getAllConstraints().keySet();
	}
	
	public static VSJoint getConstraintInstance(Level level, int constraint) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getConstraint(constraint);
	}
	
	/* Util stuff */

	public static long getGroundBodyId(Level level) {
		ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
		return handler.getGroundBodyId();
	}
	
	public static void triggerBlockChange(Level level, BlockPos pos, BlockState prevState, BlockState newState) {
		BlockStateInfo.INSTANCE.onSetBlock(level, pos, prevState, newState);
	}
	
	public static double getBlockMass(BlockState state) {
		return VS2MassSyncPatch.getPatchedBlockMass(state);
	}
	
	public static boolean isSolidContraptionBlock(BlockState state) {
		if (state.getBlock() == Blocks.ERROR_BLOCK.get()) return false;
		double mass = VS2MassSyncPatch.getPatchedBlockMass(state);
		return mass > 0 && state.getCollisionShape(null, null) != null;
	}
	
	public static boolean isValidContraptionBlock(BlockState state) {
		return !state.isAir();
	}
	
	public static <T> T optionalContraptionTransform(Level level, BlockPos position, BiFunction<ShipTransform, T, T> optionalTransform, T input) {
		Contraption contraption = getContraptionOfBlock(level, position);
		if (contraption != null) {
			return optionalTransform.apply(contraption.getShip().getTransform(), input);
		}
		return input;
	}

	public static <T> T optionalContraptionRenderTransform(Level level, BlockPos position, BiFunction<ShipTransform, T, T> optionalTransform, T input) {
		Contraption contraption = getContraptionOfBlock(level, position);
		if (contraption != null) {
			ShipTransform transform = null;
			if (contraption instanceof ClientContraption clientContraption) {
				transform = clientContraption.getShip().getRenderTransform();
			} else {
				transform = contraption.getShip().getTransform();
			}
			return optionalTransform.apply(transform, input);
		}
		return input;
	}
	
}
