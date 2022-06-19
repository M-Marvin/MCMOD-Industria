package de.m_marvin.industria.util.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.conduit.ConduitWorldStorageCapability;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import de.m_marvin.industria.util.unifiedvectors.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public interface IConduitConnector {
	
	public ConnectionPoint[] getConnectionPoints(BlockGetter level, BlockPos pos, BlockState state);
	
	public default PlacedConduit[] getConnectedConduits(Level level, BlockPos pos, BlockState state) {
		List<PlacedConduit> connections = new ArrayList<>();
		for (ConnectionPoint node : getConnectionPoints(level, pos, state)) {
			LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
			if (conduitHolder.isPresent()) {
				Optional<PlacedConduit> conduit = conduitHolder.resolve().get().getConduitAtNode(node);
				if (conduit.isPresent()) {
					connections.add(conduit.get());
				}
			}
		}
		return connections.toArray(new PlacedConduit[] {});
	}
	
	public default boolean connectionAviable(Level level, BlockPos pos, BlockState state) {
		LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			for (ConnectionPoint con : getConnectionPoints(level, pos, state)) {
				if (!conduitHolder.resolve().get().getConduitAtNode(con).isPresent()) return true;
			}
		}
		return false;
	}
	
	public default boolean connectionAviable(Level level, BlockPos pos, BlockState state, int id) {
		LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			ConnectionPoint[] connections = getConnectionPoints(level, pos, state);
			if (id >= connections.length) return false;
			return !conduitHolder.resolve().get().getConduitAtNode(connections[id]).isPresent();
		}
		return false;
	}
	
	public static record ConnectionPoint(
		BlockPos position,
		int connectionId,
		Vec3i offset,
		Direction attachmentFace
	) {}
	
}
