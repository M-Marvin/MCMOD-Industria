package de.m_marvin.industria.core.conduits;

import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ConduitHolderCapability;
import de.m_marvin.industria.core.conduits.engine.network.SUpdateConduitEntity;
import de.m_marvin.industria.core.conduits.types.ConduitHitResult;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.ConduitState;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class ConduitUtility {
	
	private ConduitUtility() {}
	
	public static boolean setConduit(Level level, ConduitPos position, ConduitState state, float length, boolean forceReplace) {
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		if (!forceReplace) {
			Optional<ConduitEntity> conduitEntity = handler.getConduit(position);
			if (conduitEntity.isPresent() && conduitEntity.get().getConduitState().getConduit() == state.getConduit()) {
				conduitEntity.get().setConduitState(state);
				if (!level.isClientSide())
					triggerClientSync(level, position);
				return true;
			}
		}
		return handler.placeConduit(position, state, length);
	}

	public static boolean setConduit(Level level, ConduitPos position, ConduitState state, float length) {
		return setConduit(level, position, state, length, false);
	}
	
	public static boolean removeConduit(Level level, ConduitPos position, boolean dropItems) {
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		return handler.breakConduit(position, dropItems);
	}
	
	public static void triggerClientSync(Level level, ConduitPos position) {
		if (!level.isClientSide()) return;
		Optional<ConduitEntity> conduit = getConduit(level, position);
		if (conduit.isPresent()) {
			CompoundTag updateTag = conduit.get().getUpdateTag();
			
			BlockPos middle = MathUtility.getMiddleBlock(position.getNodeApos(), position.getNodeBpos());
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(middle)), new SUpdateConduitEntity(position, conduit.get().getConduitState().getConduit(), updateTag));
		}
	}
	
	public static Optional<ConduitEntity> getConduit(Level level, ConduitPos position) {
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		return handler.getConduit(position);
	}

	public static Optional<ConduitEntity> getConduitAtNode(Level level, NodePos node) {
		return getConduitAtNode(level, node.getBlock(), node.getNode());
	}
	
	public static Optional<ConduitEntity> getConduitAtNode(Level level, BlockPos block, int node) {
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		return handler.getConduitAtNode(block, node);
	}
	
	public static List<ConduitEntity> getConduitsAtNode(Level level, NodePos node) {
		return getConduitsAtNode(level, node.getBlock(), node.getNode());
	}
	
	public static List<ConduitEntity> getConduitsAtNode(Level level, BlockPos position, int node) {
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		return handler.getConduitsAtNode(position, node);
	}
	
	public static List<ConduitEntity> getConduitsAtBlock(Level level, BlockPos position) {
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		return handler.getConduitsAtBlock(position);
	}
	
	public static List<ConduitEntity> getConduitsInChunk(Level level, ChunkPos chunk, boolean includeExternal) {
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		return handler.getConduitsInChunk(chunk, includeExternal);
	}

	public static List<ConduitEntity> getConduitsInBounds(Level level, BlockPos pos1, BlockPos pos2, boolean includeExternal) {
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		return handler.getConduitsInBounds(pos1, pos2, includeExternal);
	}
	
	public static ConduitHitResult clipConduits(Level level, ClipContext context, boolean skipBlockClip) {
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		ConduitHitResult cResult = handler.clipConduits(context);
		if (cResult.isHit() && !skipBlockClip) {
			Vec3d newTarget = cResult.getHitPos().copy();
			Vec3d blockDistance = Vec3d.fromVec(context.getTo()).sub(Vec3d.fromVec(context.getFrom()));
			blockDistance.tryNormalize();
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
