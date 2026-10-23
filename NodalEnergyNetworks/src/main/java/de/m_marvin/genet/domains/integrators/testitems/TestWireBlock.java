package de.m_marvin.genet.domains.integrators.testitems;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.genet.domains.TypeChain;
import de.m_marvin.genet.domains.integrators.IDeviceImplementor;
import de.m_marvin.genet.domains.integrators.LevelBlockDomainIntegrator.BlockDevice;
import de.m_marvin.genet.network.nodal.Element;
import de.m_marvin.genet.network.nodal.ElementManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TestWireBlock extends Block implements IDeviceImplementor {
	
	public TestWireBlock(Properties p_49795_) {
		super(p_49795_);
		registerDefaultState(this.stateDefinition.any()
				.setValue(BlockStateProperties.NORTH, false)
				.setValue(BlockStateProperties.SOUTH, false)
				.setValue(BlockStateProperties.EAST, false)
				.setValue(BlockStateProperties.WEST, false)
				.setValue(BlockStateProperties.UP, false)
				.setValue(BlockStateProperties.DOWN, false)
		);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.NORTH);
		builder.add(BlockStateProperties.SOUTH);
		builder.add(BlockStateProperties.EAST);
		builder.add(BlockStateProperties.WEST);
		builder.add(BlockStateProperties.UP);
		builder.add(BlockStateProperties.DOWN);
	}
	
	public boolean isWireBlock(Level level, BlockPos pos, Direction side) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof IDeviceImplementor deviceProvider) {
			return deviceProvider.hasDeviceConnection(level, pos, state, side) != FaceConnection.NONE;
		}
		return false;
	}
	
	public boolean shouldConnectTowards(Level level, BlockPos pos, Direction side) {
		return isWireBlock(level, pos.relative(side), side.getOpposite());
	}
	
	public BlockState getStateForLocation(Level level, BlockPos pos) {
		return defaultBlockState()
			.setValue(BlockStateProperties.NORTH, shouldConnectTowards(level, pos, Direction.NORTH))
			.setValue(BlockStateProperties.SOUTH, shouldConnectTowards(level, pos, Direction.SOUTH))
			.setValue(BlockStateProperties.EAST, shouldConnectTowards(level, pos, Direction.EAST))
			.setValue(BlockStateProperties.WEST, shouldConnectTowards(level, pos, Direction.WEST))
			.setValue(BlockStateProperties.UP, shouldConnectTowards(level, pos, Direction.UP))
			.setValue(BlockStateProperties.DOWN, shouldConnectTowards(level, pos, Direction.DOWN));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return getStateForLocation(context.getLevel(), context.getClickedPos());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean moved) {
		super.neighborChanged(state, level, pos, block, neighbor, moved);
		BlockState newState = state;
		if (neighbor.equals(pos.relative(Direction.NORTH)))
			newState = state.setValue(BlockStateProperties.NORTH, shouldConnectTowards(level, pos, Direction.NORTH));
		if (neighbor.equals(pos.relative(Direction.SOUTH)))
			newState = state.setValue(BlockStateProperties.SOUTH, shouldConnectTowards(level, pos, Direction.SOUTH));
		if (neighbor.equals(pos.relative(Direction.EAST)))
			newState = state.setValue(BlockStateProperties.EAST, shouldConnectTowards(level, pos, Direction.EAST));
		if (neighbor.equals(pos.relative(Direction.WEST)))
			newState = state.setValue(BlockStateProperties.WEST, shouldConnectTowards(level, pos, Direction.WEST));
		if (neighbor.equals(pos.relative(Direction.UP)))
			newState = state.setValue(BlockStateProperties.UP, shouldConnectTowards(level, pos, Direction.UP));
		if (neighbor.equals(pos.relative(Direction.DOWN)))
			newState = state.setValue(BlockStateProperties.DOWN, shouldConnectTowards(level, pos, Direction.DOWN));
		if (!state.equals(newState))
			level.setBlockAndUpdate(pos, newState);
	}
	
	@Override
	public FaceConnection hasDeviceConnection(Level level, BlockPos pos, BlockState state, Direction side) {
		switch (side) {
		case NORTH: return state.getValue(BlockStateProperties.NORTH) ? FaceConnection.OPEN : FaceConnection.OFFERS;
		case SOUTH: return state.getValue(BlockStateProperties.SOUTH) ? FaceConnection.OPEN : FaceConnection.OFFERS;
		case EAST: return state.getValue(BlockStateProperties.EAST) ? FaceConnection.OPEN : FaceConnection.OFFERS;
		case WEST: return state.getValue(BlockStateProperties.WEST) ? FaceConnection.OPEN : FaceConnection.OFFERS;
		case UP: return state.getValue(BlockStateProperties.UP) ? FaceConnection.OPEN : FaceConnection.OFFERS;
		case DOWN: return state.getValue(BlockStateProperties.DOWN) ? FaceConnection.OPEN : FaceConnection.OFFERS;
		default: return FaceConnection.NONE;
		}
	}

	@Override
	public void installDeviceElements(BlockDevice device) {
		var innerNode = device.innerNode("junction");
		for (var prop : device.getState().getProperties()) {
			if (!device.getState().getValue((BooleanProperty) prop))
				continue;
			Direction side = Direction.valueOf(prop.getName().toUpperCase());
			var name = device.elementName("R" + side.name());
			var outerNode = device.outerNode(side);

			var resistor = NodeNet.ELEMENTS.<TypeChain, TypeChain>makeElement(name, ResourceLocation.tryBuild(NodeNet.MODID, "electric/resistor"));
			resistor.getState().setNodeNames(innerNode, outerNode);
			device.install(resistor);
		}		
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return super.getCollisionShape(state, level, pos, context);
	}
	
}
