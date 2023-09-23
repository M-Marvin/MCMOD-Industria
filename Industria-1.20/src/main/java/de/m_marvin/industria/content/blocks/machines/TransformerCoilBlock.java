package de.m_marvin.industria.content.blocks.machines;

import java.util.function.Consumer;

import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TransformerCoilBlock extends ElectroMagneticCoilBlock implements IElectricBlock {
	
	
	
	public TransformerCoilBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit,
			Consumer<ICircuitPlot> plotter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
