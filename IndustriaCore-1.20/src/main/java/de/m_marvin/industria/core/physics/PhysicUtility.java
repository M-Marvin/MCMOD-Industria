package de.m_marvin.industria.core.physics;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.OptionalLong;
import java.util.function.BiFunction;

import org.joml.Matrix3d;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.world.chunks.BlockType;
import org.valkyrienskies.mod.common.BlockStateInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.physics.engine.ForcesInducer;
import de.m_marvin.industria.core.physics.engine.PhysicHandlerCapability;
import de.m_marvin.industria.core.physics.types.ContraptionHitResult;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PhysicUtility {
	
	/* Naming and finding of contraptions */
	
	public static void setContraptionName(Level level, Ship contraption, String name) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		handler.setContraptionName(contraption.getId(), name);
	}
	
	public static String getContraptionName(Level level, Ship contraption, String name) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.getContraptionName(contraption.getId());
	}
	
	public static OptionalLong getContraptionIdByName(Level level, String name) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.getContraption(name);
	}

	public static Ship getContraptionByName(Level level, String name) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		OptionalLong id = handler.getContraption(name);
		if (id.isPresent()) return getContraptionById(level, id.getAsLong());
		return null;
	}

	public static Iterable<Ship> getContraptionIntersecting(Level level, BlockPos position)  {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.getContraptionIntersecting(position);
	}
	
	public static Ship getContraptionOfBlock(Level level, BlockPos shipBlockPos) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.getContraptionOfBlock(shipBlockPos);
	}

	public static Ship getContraptionById(Level level, long id) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.getContraptionById(id);
	}
	
	/* Translating of positions and moving of contraptions */

	public static Direction toWorldDirection(ShipTransform contraption, Direction direction) {
		Vec3d vec = new Vec3d(MathUtility.getDirectionVec(direction));
		Vec3d worldVec = toWorldVector(contraption, vec);
		return MathUtility.getVecDirection(new Vec3i((int) Math.round(worldVec.x), (int) Math.round(worldVec.x), (int) Math.round(worldVec.x)));
	}
	
	public static Vec3d toWorldVector(ShipTransform contraption, Vec3d vector) {
		Vector4d v = contraption.getShipToWorld().transform(new Vector4d(vector.x, vector.y, vector.z, 0));
		return new Vec3d(v.x, v.y, v.z);
	}

	public static Direction toContraptionDirection(ShipTransform contraption, Direction direction) {
		Vec3d vec = new Vec3d(MathUtility.getDirectionVec(direction));
		Vec3d shipVec = toContraptionVector(contraption, vec);
		return MathUtility.getVecDirection(new Vec3i((int) Math.round(shipVec.x), (int) Math.round(shipVec.x), (int) Math.round(shipVec.x)));
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
	
	public static Vec3d toWorldPos(ShipTransform contaption, BlockPos pos) {
		return toWorldPos(contaption, Vec3d.fromVec(pos).addI(0.5, 0.5, 0.5));
	}

	public static BlockPos toWorldBlockPos(ShipTransform contraption, BlockPos pos) {
		Vec3d position = toWorldPos(contraption, pos);
		return MathUtility.toBlockPos(position);
	}

	public static Vec3d ensureWorldCoordinates(Level level, BlockPos referencePos, Vec3d position) {
		return optionalContraptionTransform(level, referencePos, PhysicUtility::toWorldPos, position);
	}
	
	public static Vec3d ensureContraptionCoordinates(Level level, BlockPos referencePos, Vec3d position) {
		return optionalContraptionTransform(level, referencePos, PhysicUtility::toContraptionPos, position);
	}

	public static BlockPos ensureWorldBlockCoordinates(Level level, BlockPos referencePos, BlockPos position) {
		return optionalContraptionTransform(level, referencePos, PhysicUtility::toWorldBlockPos, position);
	}
	
	public static BlockPos ensureContraptionBlockCoordinates(Level level, BlockPos referencePos, BlockPos position) {
		return optionalContraptionTransform(level, referencePos, PhysicUtility::toContraptionBlockPos, position);
	}
	
	public static Direction ensureWorldDirection(Level level, BlockPos referencePos, Direction direction) {
		return optionalContraptionTransform(level, referencePos, PhysicUtility::toWorldDirection, direction);
	}

	public static Vec3d ensureWorldVector(Level level, BlockPos referencePos, Vec3d vector) {
		return optionalContraptionTransform(level, referencePos, PhysicUtility::toWorldVector, vector);
	}

	public static Direction ensureContraptionDirection(Level level, BlockPos referencePos, Direction direction) {
		return optionalContraptionTransform(level, referencePos, PhysicUtility::toContraptionDirection, direction);
	}

	public static Vec3d ensureContraptionVector(Level level, BlockPos referencePos, Vec3d vector) {
		return optionalContraptionTransform(level, referencePos, PhysicUtility::toContraptionVector, vector);
	}
		
	public static ContraptionPosition getPosition(ServerShip contraption, boolean massCenter) {
		return PhysicHandlerCapability.getPosition(contraption, massCenter);
	}
	
	public static void setPosition(ServerLevel level, ServerShip contraption, ContraptionPosition position, boolean massCenter) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		handler.setPosition(contraption, position, massCenter);
	}
	
	/* Listing and creation contraptions in the world */
	
	public static List<Ship> getLoadedContraptions(Level level) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.getLoadedContraptions();
	}

	public static List<Ship> getAllContraptions(Level level) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.getAllContraptions();
	}
	
	public static Ship createNewContraptionAt(ServerLevel level, BlockPos position, float scale) {
		return createContraptionAt(level, Vec3d.fromVec(position), scale);
	}
	
	public static ServerShip createContraptionAt(ServerLevel level, Vec3d position, float scale) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.createContraptionAt(position, scale);
	}
	
	public static boolean removeContraption(ServerLevel level, Ship contraption) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.removeContraption(contraption);
	}
	
	public static Ship convertToContraption(ServerLevel level, AABB areaBounds, boolean removeOriginal, float scale) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.convertToContraption(areaBounds, removeOriginal, scale);
	}
	
	public static ServerShip assembleToContraption(ServerLevel level, List<BlockPos> blocks, boolean removeOriginal, float scale) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.assembleToContraption(blocks, removeOriginal, scale);
	}
	
	/* Raycasting for contraptions */
	
	public static ContraptionHitResult clipForContraption(Level level, Vec3d from, Vec3d direction, double range) {
		return clipForContraption(level, from, from.add(direction.mul(range)));
	}
	
	public static ContraptionHitResult clipForContraption(Level level, Vec3d from, Vec3d to) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.clipForContraption(from, to);
	}
	
	/* Constraints */
	
	public static int addConstraint(Level level, VSConstraint constraint) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.addConstraint(constraint);
	}
	
	public static boolean removeConstraint(Level level, int constraint) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.removeConstaint(constraint);
	}

	public static List<Integer> getAllConstraints(Level level) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		handler.getAllConstraints(); // TODO
		return null;
	}
	
	public static VSConstraint getConstraintInstance(Level level, int constraint) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.getConstraint(constraint);
	}
	
	/* Attachments */
	
	public static <T extends ForcesInducer> T getOrCreateForceInducer(ServerLevel level, ServerShip contratption, Class<T> inducerClass) {
		T inducer = getOrCreateAttachment(contratption, inducerClass);
		inducer.initLevel(level);
		return inducer;
	}
	
	public static <T> T getOrCreateAttachment(ServerShip contraption, Class<T> attachmentClass) {
		T attachment = getAttachment(contraption, attachmentClass);
		if (attachment == null) {
			try {
				attachment = attachmentClass.getConstructor().newInstance();
				addAttachment(contraption, attachmentClass, attachment);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
				IndustriaCore.LOGGER.error("Failed to create attachment '" + attachmentClass.getSimpleName() + "'!");
				e.printStackTrace();
			}
		}
		return attachment;
	}
	
	public static <T> void addAttachment(ServerShip contraption, Class<T> attachmentClass, T attachmentObject) {
		PhysicHandlerCapability.attachObject(contraption, attachmentClass, attachmentObject);
	}
	
	public static <T> T getAttachment(ServerShip contraption, Class<T> attachmentClass) {
		return PhysicHandlerCapability.getAttachedObject(contraption, attachmentClass);
	}
	
	public static <T> void removeAttachment(ServerShip contraption, Class<T> attachmentClass) {
		PhysicHandlerCapability.attachObject(contraption, attachmentClass, null);
	}
	
	/* Util stuff */

	public static String getDimensionId(Level level) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.getDimensionId();
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
	
	public static boolean resetFrameQueue(ServerLevel level) {
		PhysicHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.PHYSIC_HANDLER_CAPABILITY);
		return handler.resetFrameQueue();
	}
	
	public static <T> T optionalContraptionTransform(Level level, BlockPos position, BiFunction<ShipTransform, T, T> optionalTransform, T input) {
		Ship contraption = getContraptionOfBlock(level, position);
		if (contraption != null) {
			return optionalTransform.apply(contraption.getTransform(), input);
		}
		return input;
	}

	public static <T> T optionalContraptionRenderTransform(Level level, BlockPos position, BiFunction<ShipTransform, T, T> optionalTransform, T input) {
		Ship contraption = getContraptionOfBlock(level, position);
		if (contraption != null) {
			ShipTransform transform = null;
			if (contraption instanceof ClientShip) {
				transform = ((ClientShip) contraption).getRenderTransform();
			} else {
				transform = contraption.getTransform();
			}
			return optionalTransform.apply(transform, input);
		}
		return input;
	}
	
}
