package de.m_marvin.industria.core.conduits;

import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ConduitHandlerCapability;
import de.m_marvin.industria.core.conduits.engine.network.SUpdateConduitEntity;
import de.m_marvin.industria.core.conduits.types.ConduitHitResult;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.PacketDistributor;
import net.minecraft.world.phys.Vec3;

public class ConduitUtility {
	
	private ConduitUtility() {}
	
	public static boolean setConduit(Level level, ConduitPos position, Conduit conduit, double length) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.placeConduit(position, conduit, length);
	}
	
	public static boolean removeConduit(Level level, ConduitPos position, boolean dropItems) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.breakConduit(position, dropItems);
	}
	
	public static void triggerClientSync(Level level, ConduitPos position) {
		if (!level.isClientSide()) return;
		Optional<ConduitEntity> conduit = getConduit(level, position);
		if (conduit.isPresent()) {
			CompoundTag updateTag = conduit.get().getUpdateTag();
			
			BlockPos middle = MathUtility.getMiddleBlock(position.getNodeApos(), position.getNodeBpos());
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(middle)), new SUpdateConduitEntity(position, conduit.get().getConduit(), updateTag));
		}
	}
	
	public static Optional<ConduitEntity> getConduit(Level level, ConduitPos position) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduit(position);
	}

	public static Optional<ConduitEntity> getConduitAtNode(Level level, NodePos node) {
		return getConduitAtNode(level, node.getBlock(), node.getNode());
	}
	
	public static Optional<ConduitEntity> getConduitAtNode(Level level, BlockPos block, int node) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduitAtNode(block, node);
	}
	
	public static List<ConduitEntity> getConduitsAtNode(Level level, NodePos node) {
		return getConduitsAtNode(level, node.getBlock(), node.getNode());
	}
	
	public static List<ConduitEntity> getConduitsAtNode(Level level, BlockPos position, int node) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduitsAtNode(position, node);
	}
	
	public static List<ConduitEntity> getConduitsAtBlock(Level level, BlockPos position) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduitsAtBlock(position);
	}
	
	public static List<ConduitEntity> getConduitsInChunk(Level level, ChunkPos chunk, boolean includeExternal) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduitsInChunk(chunk, includeExternal);
	}

	public static List<ConduitEntity> getConduitsInBounds(Level level, BlockPos pos1, BlockPos pos2, boolean includeExternal) {
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		return handler.getConduitsInBounds(pos1, pos2, includeExternal);
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
