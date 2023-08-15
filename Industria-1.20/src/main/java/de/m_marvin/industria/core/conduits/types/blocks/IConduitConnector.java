package de.m_marvin.industria.core.conduits.types.blocks;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IConduitConnector {
	
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state);
	
	public default ConduitNode getConduitNode(Level level, BlockPos pos, BlockState state, int id) {
		ConduitNode[] nodes = getConduitNodes(level, pos, state);
		if (nodes.length > id) {
			return nodes[id];
		}
		return null;
	}
	
	public default boolean hasFreeConduitNode(Level level, BlockPos pos, BlockState state) {
		ConduitNode[] nodes = getConduitNodes(level, pos, state);
		for (int nodeId = 0; nodeId < nodes.length; nodeId++) {
			if (ConduitUtility.getConduitsAtNode(level, pos, nodeId).size() < nodes[nodeId].getMaxConnections()) {
				return true;
			}
		}
		return false;
	}
	
	
//	public default PlacedConduit[] getConnectedConduits(Level level, BlockPos pos, BlockState state) {
//		List<PlacedConduit> connections = new ArrayList<>();
//		for (ConnectionPoint node : getConnectionPoints(pos, state)) {
//			LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
//			if (conduitHolder.isPresent()) {
//				Optional<PlacedConduit> conduit = conduitHolder.resolve().get().getConduitAtNode(node);
//				if (conduit.isPresent()) {
//					connections.add(conduit.get());
//				}
//			}
//		}
//		return connections.toArray(new PlacedConduit[] {});
//	}
//	
//	public default boolean connectionAviable(Level level, BlockPos pos, BlockState state) {
//		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
//		if (conduitHolder.isPresent()) {
//			for (ConnectionPoint con : getConnectionPoints(pos, state)) {
//				if (!conduitHolder.resolve().get().getConduitAtNode(con).isPresent()) return true;
//			}
//		}
//		return false;
//	}
//	
//	public default boolean connectionAviable(Level level, BlockPos pos, BlockState state, int id) {
//		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
//		if (conduitHolder.isPresent()) {
//			ConnectionPoint[] connections = getConnectionPoints(pos, state);
//			if (id >= connections.length) return false;
//			return !conduitHolder.resolve().get().getConduitAtNode(connections[id]).isPresent();
//		}
//		return false;
//	}
	
}
