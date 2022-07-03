package de.m_marvin.industria.util.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.conduit.ConduitHandlerCapability;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public interface IConduitConnector {
	
	public ConnectionPoint[] getConnectionPoints(BlockPos pos, BlockState state);
	
	public default PlacedConduit[] getConnectedConduits(Level level, BlockPos pos, BlockState state) {
		List<PlacedConduit> connections = new ArrayList<>();
		for (ConnectionPoint node : getConnectionPoints(pos, state)) {
			LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
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
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			for (ConnectionPoint con : getConnectionPoints(pos, state)) {
				if (!conduitHolder.resolve().get().getConduitAtNode(con).isPresent()) return true;
			}
		}
		return false;
	}
	
	public default boolean connectionAviable(Level level, BlockPos pos, BlockState state, int id) {
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			ConnectionPoint[] connections = getConnectionPoints(pos, state);
			if (id >= connections.length) return false;
			return !conduitHolder.resolve().get().getConduitAtNode(connections[id]).isPresent();
		}
		return false;
	}
	
}
