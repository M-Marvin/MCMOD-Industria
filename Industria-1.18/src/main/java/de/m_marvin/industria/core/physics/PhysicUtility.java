package de.m_marvin.industria.core.physics;

import java.util.List;
import java.util.OptionalLong;

import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.world.chunks.BlockType;
import org.valkyrienskies.mod.common.BlockStateInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import de.m_marvin.industria.core.physics.engine.PhysicHandlerCapability;
import de.m_marvin.industria.core.physics.types.ContraptionHitResult;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.registries.ModCapabilities;
import de.m_marvin.univec.impl.Vec3d;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;

public class PhysicUtility {
	
	/* Naming and finding of contraptions */
	
	public static void setContraptionName(Level level, Ship contraption, String name) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			dataHolder.resolve().get().setContraptionName(contraption.getId(), name);
		}
	}
	
	public static String getContraptionName(Level level, Ship contraption, String name) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getContraptionName(contraption.getId());
		}
		return null;
	}
	
	public static OptionalLong getContraptionIdByName(Level level, String name) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getContraption(name);
		}
		return OptionalLong.empty();
	}

	public static Ship getContraptionByName(Level level, String name) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			OptionalLong id = dataHolder.resolve().get().getContraption(name);
			if (id.isPresent()) return getContraptionById(level, id.getAsLong());
		}
		return null;
	}

	public static Iterable<Ship> getContraptionIntersecting(Level level, BlockPos position)  {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getContraptionIntersecting(position);
		}
		return null;
	}
	
	public static Ship getContraptionOfBlock(Level level, BlockPos shipBlockPos) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getContraptionOfBlock(shipBlockPos);
		}
		return null;
	}

	public static Ship getContraptionById(Level level, long id) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getContraptionById(id);
		}
		return null;
	}
	
	/* Translating of positions and moving of contraptions */
	
	public static Vec3d toContraptionPos(Ship contraption, Vec3d pos) {
		Matrix4dc worldToShip = contraption.getWorldToShip();
		if (worldToShip != null) {
			Vector3d transformPosition = worldToShip.transformPosition(pos.writeTo(new Vector3d()));
			return Vec3d.fromVec(transformPosition);
		}
		return new Vec3d(0, 0, 0);
	}
	
	public static BlockPos toContraptionBlockPos(Ship contraption, Vec3d pos) {
		Vec3d position = toContraptionPos(contraption, pos);
		return new BlockPos(position.x, position.y, position.z);
	}
	
	public static BlockPos toContraptionBlockPos(Ship contraption, BlockPos pos) {
		return toContraptionBlockPos(contraption, Vec3d.fromVec(pos));
	}

	public static Vec3d toWorldPos(Ship contraption, Vec3d pos) {
		Matrix4dc shipToWorld = contraption.getShipToWorld();
		if (shipToWorld != null) {
			Vector3d transformedPosition = shipToWorld.transformPosition(pos.writeTo(new Vector3d()));
			return Vec3d.fromVec(transformedPosition);
		}
		return new Vec3d(0, 0, 0);
	}
	
	public static Vec3d toWorldPos(Ship contaption, BlockPos pos) {
		return toWorldPos(contaption, Vec3d.fromVec(pos).addI(0.5, 0.5, 0.5));
	}

	public static BlockPos toWorldBlockPos(Ship contraption, BlockPos pos) {
		Vec3d position = toWorldPos(contraption, pos);
		return new BlockPos(position.x, position.y, position.z);
	}

	public static Vec3d ensureWorldCoordinates(Level level, BlockPos referencePos, Vec3d position) {
		Ship contraption = getContraptionOfBlock(level, referencePos);
		if (contraption != null) {
			return toWorldPos(contraption, position);
		}
		return position;
	}
	
	public static ContraptionPosition getPosition(Level level, ServerShip contraption, boolean massCenter) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getPosition(contraption, massCenter);
		}
		return null;
	}
	
	public static void setPosition(ServerLevel level, ServerShip contraption, ContraptionPosition position, boolean massCenter) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			dataHolder.resolve().get().setPosition(contraption, position, massCenter);
		}
	}
	
	/* Listing and creation contraptions in the world */
	
	public static List<Ship> getLoadedContraptions(Level level) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getLoadedContraptions();
		}
		return null;
	}

	public static List<Ship> getAllContraptions(Level level) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getAllContraptions();
		}
		return null;
	}
	
	public static Ship createNewContraptionAt(ServerLevel level, BlockPos position, float scale) {
		return createContraptionAt(level, Vec3d.fromVec(position), scale);
	}
	
	public static ServerShip createContraptionAt(ServerLevel level, Vec3d position, float scale) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().createContraptionAt(position, scale);
		}
		return null;
	}
	
	public static boolean removeContraption(ServerLevel level, Ship contraption) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().removeContraption(contraption);
		}
		return false;
	}
	
	public static Ship convertToContraption(ServerLevel level, AABB areaBounds, boolean removeOriginal, float scale) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().convertToContraption(areaBounds, removeOriginal, scale);
		}
		return null;
	}
	
	public static ServerShip assembleToContraption(ServerLevel level, List<BlockPos> blocks, boolean removeOriginal, float scale) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().assembleToContraption(blocks, removeOriginal, scale);
		}
		return null;
	}
	
	/* Raycasting for contraptions */
	
	public static ContraptionHitResult clipForContraption(Level level, Vec3d from, Vec3d direction, double range) {
		return clipForContraption(level, from, from.add(direction.mul(range)));
	}
	
	public static ContraptionHitResult clipForContraption(Level level, Vec3d from, Vec3d to) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().clipForContraption(from, to);
		}
		return null;
	}
		
	/* Util stuff */

	public static String getDimensionId(Level level) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getDimensionId();
		}
		return null;
	}
	
	public static long getGroundBodyId(Level level) {
		return VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getDimensionToGroundBodyIdImmutable().get(getDimensionId(level));
	}
	
	public static void triggerBlockChange(Level level, BlockPos pos, BlockState prevState, BlockState newState) {
		BlockStateInfo.INSTANCE.onSetBlock(level, pos, prevState, newState);
	}
	
	public static boolean isSolidContraptionBlock(BlockState state) {
		Pair<Double, BlockType> blockData = BlockStateInfo.INSTANCE.get(state);
		return blockData.getSecond() == VSGameUtilsKt.getVsCore().getBlockTypes().getSolid() && blockData.getFirst() > 0;
	}
	
	public static boolean isValidContraptionBlock(BlockState state) {
		return !state.isAir();
	}
	
	/* Constraints */
	
	public static int addConstraint(Level level, VSConstraint constraint) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().addConstraint(constraint);
		}
		return 0;
	}
	
	public static boolean removeConstraint(Level level, int constraint) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().removeConstaint(constraint);
		}
		return false;
	}

	public static List<Integer> getAllConstraints(Level level) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			dataHolder.resolve().get().getAllConstraints();
		}
		return null;
	}
	
	public static VSConstraint getConstraintInstance(Level level, int constraint) {
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			return dataHolder.resolve().get().getConstraint(constraint);
		}
		return null;
	}
	
}
