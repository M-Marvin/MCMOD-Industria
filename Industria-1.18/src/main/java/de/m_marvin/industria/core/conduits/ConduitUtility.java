package de.m_marvin.industria.core.conduits;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.registries.ModCapabilities;
import de.m_marvin.industria.core.conduits.engine.ConduitHandlerCapability;
import de.m_marvin.industria.core.conduits.engine.ConduitHitResult;
import de.m_marvin.industria.core.conduits.engine.ConduitPos;
import de.m_marvin.industria.core.conduits.engine.PlacedConduit;
import de.m_marvin.industria.core.conduits.engine.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.core.conduits.engine.network.CBreakConduitPackage;
import de.m_marvin.industria.core.conduits.types.Conduit;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.util.LazyOptional;

public class ConduitUtility {
	
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

	public static ConduitHitResult clipConduits(Level level, ClipContext context, boolean skipBlockClip) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			ConduitHitResult cResult = conduitHolder.resolve().get().clipConduits(context);
			if (cResult.isHit() && !skipBlockClip) {
				Vec3f newTarget = cResult.getHitPos().copy();
				Vec3f blockDistance = new Vec3f(Vec3f.fromVec(context.getTo()).sub(Vec3f.fromVec(context.getFrom())));
				blockDistance.normalize();
				newTarget.add(blockDistance.mul(-0.1F));
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
