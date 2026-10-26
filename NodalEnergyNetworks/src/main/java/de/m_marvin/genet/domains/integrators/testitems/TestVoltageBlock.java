package de.m_marvin.genet.domains.integrators.testitems;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.genet.domains.TypeChain;
import de.m_marvin.genet.domains.integrators.IDeviceImplementor;
import de.m_marvin.genet.domains.integrators.LevelBlockDomainIntegrator.BlockDevice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TestVoltageBlock extends Block implements IDeviceImplementor {
	
	public TestVoltageBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public FaceConnection hasDeviceConnection(Level level, BlockPos pos, BlockState state, Direction side) {
		return state.getValue(BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise().getAxis() == side.getAxis() ? FaceConnection.OPEN : FaceConnection.NONE;
	}

	@Override
	public void installDeviceElements(BlockDevice device) {
		
		Direction facing = device.getState().getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise();
		var source = NodeNet.ELEMENTS.<TypeChain, TypeChain>makeElement(device.elementName("VSOURCE"), ResourceLocation.tryBuild(NodeNet.MODID, "electric/voltage_independent"));
		source.getState().setNodeNames(device.outerNode(facing), device.outerNode(facing.getOpposite()));
		source.getState().setParameter("U", 24);
		device.install(source);
		
	}
	
}
