package de.m_marvin.industria.core.conduits;

import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ConduitHandlerCapability;
import de.m_marvin.industria.core.conduits.engine.network.SCConduitPackage;
import de.m_marvin.industria.core.conduits.types.ConduitHitResult;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class ConduitUtility {
	
	public static boolean setConduit(Level level, ConduitPos position, Conduit conduit, double length) {
		if (!level.isClientSide()) {
			BlockPos middlePos = MathUtility.getMiddleBlock(
					PhysicUtility.ensureWorldBlockCoordinates(level, position.getNodeApos(), position.getNodeApos()), 
					PhysicUtility.ensureWorldBlockCoordinates(level, position.getNodeBpos(), position.getNodeBpos()));
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(middlePos)), new SCConduitPackage.SCPlaceConduitPackage(position, conduit, length));
		}
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.placeConduit(position, conduit, length);
	}
	
	public static boolean removeConduitFromClient(Level level, ConduitPos conduitPosition, boolean dropItems) {
		IndustriaCore.NETWORK.sendToServer(new SCConduitPackage.SCBreakConduitPackage(conduitPosition, dropItems));
		return ConduitUtility.removeConduit(level, conduitPosition, dropItems);
	}
	
	public static boolean removeConduit(Level level, ConduitPos position, boolean dropItems) {
		if (!level.isClientSide()) {
			BlockPos middlePos = MathUtility.getMiddleBlock(
					PhysicUtility.ensureWorldBlockCoordinates(level, position.getNodeApos(), position.getNodeApos()), 
					PhysicUtility.ensureWorldBlockCoordinates(level, position.getNodeBpos(), position.getNodeBpos()));
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(middlePos)), new SCConduitPackage.SCBreakConduitPackage(position, dropItems));
		}
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.breakConduit(position, dropItems);
	}

	public static Optional<ConduitEntity> getConduit(Level level, ConduitPos position) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduit(position);
	}

	public static Optional<ConduitEntity> getConduitAtNode(Level level, BlockPos block, int node) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduitAtNode(block, node);
	}
	
	public static List<ConduitEntity> getConduitsAtNode(Level level, BlockPos position, int node) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduitsAtNode(position, node);
	}
	
	public static List<ConduitEntity> getConduitsAtBlock(Level level, BlockPos position) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduitsAtBlock(position);
	}
	
	public static List<ConduitEntity> getConduitsInChunk(Level level, ChunkPos chunk) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduitsInChunk(chunk);
	}

	public static ConduitHitResult clipConduits(Level level, ClipContext context, boolean skipBlockClip) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		ConduitHitResult cResult = handler.clipConduits(context);
		if (cResult.isHit() && !skipBlockClip) {
			Vec3d newTarget = cResult.getHitPos().copy();
			Vec3d blockDistance = Vec3d.fromVec(context.getTo()).sub(Vec3d.fromVec(context.getFrom()));
			blockDistance.normalize();
			newTarget.add(blockDistance.mul(-0.1));
			context.to = newTarget.writeTo(new Vec3(0, 0, 0));
			
			BlockHitResult bResult = level.clip(context);
			if (bResult.getType() == Type.BLOCK) {
				return ConduitHitResult.block(bResult);
			}
		}
		return cResult;
	}
	
}
