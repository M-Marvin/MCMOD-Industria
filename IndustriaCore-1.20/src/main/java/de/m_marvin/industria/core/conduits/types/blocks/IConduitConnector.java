package de.m_marvin.industria.core.conduits.types.blocks;

import com.google.common.base.Predicate;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitNode.NodeType;
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
	
	public default boolean hasFreeConduitNode(Level level, BlockPos pos, BlockState state, Predicate<NodeType> type) {
		ConduitNode[] nodes = getConduitNodes(level, pos, state);
		for (int nodeId = 0; nodeId < nodes.length; nodeId++) {
			if (!type.apply(nodes[nodeId].getType())) continue;
			if (ConduitUtility.getConduitsAtNode(level, pos, nodeId).size() < nodes[nodeId].getMaxConnections()) {
				return true;
			}
		}
		return false;
	}
	
}
