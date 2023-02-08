package de.m_marvin.industria.core.conduits;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.conduits.engine.ConduitHandlerCapability;
import de.m_marvin.industria.core.conduits.engine.network.CBreakConduitPackage;
import de.m_marvin.industria.core.conduits.types.ConduitHitResult;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.registries.ModCapabilities;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

public class ConduitUtility {
	
	public static boolean setConduit(Level level, ConduitPos position, Conduit conduit, double length) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			return conduitHolder.resolve().get().placeConduit(position, conduit, length);
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

	public static Optional<PlacedConduit> getConduitAtNode(Level level, BlockPos block, int node) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			return conduitHolder.resolve().get().getConduitAtNode(block, node);
		}
		return Optional.empty();
	}
	
	public static List<PlacedConduit> getConduitsAtNode(Level level, BlockPos position, int node) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			return conduitHolder.resolve().get().getConduitsAtNode(position, node);
		}
		return new ArrayList<>();
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

	public static ConduitHitResult clipConduits(Level level, ClipContext context, boolean skipBlockClip) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			ConduitHitResult cResult = conduitHolder.resolve().get().clipConduits(context);
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
		return ConduitHitResult.miss();
	}

	public static void removeConduitFromClient(Level level, ConduitPos conduitPosition, boolean dropItems) {
		ConduitUtility.removeConduit(level, conduitPosition, dropItems);
		Industria.NETWORK.sendToServer(new CBreakConduitPackage(conduitPosition, dropItems));
	}
	
}
