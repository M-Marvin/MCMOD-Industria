package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import de.m_marvin.industria.core.client.registries.NodeTypes;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PowerSourceBlock extends Block implements IElectricConnector{
	
	public static final NodePointSupplier NODE_POINTS = NodePointSupplier.define()
			.addNodesAround(Axis.Y, NodeTypes.ALL, 1, new Vec3i(8, 16, 4));
	public static final int NODE_COUNT = 4;

	public PowerSourceBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODE_POINTS.getNodes();
	}

	@Override
	public void neighborRewired(Level level, BlockState instance, BlockPos position, Component<?, ?, ?> neighbor) {}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<CircuitTemplate> plotter) {}

	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, NODE_COUNT).mapToObj(id -> new NodePos(pos, id)).toArray(i -> new NodePos[i]);
	}

	@Override
	public String[] getWireLanes(BlockPos pos, BlockState instance, NodePos node) {
		return new String[] {};
	}

	@Override
	public void setWireLanes(BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {}

}
