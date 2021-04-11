package de.industria.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BlockWiring extends BlockBase implements IWaterLoggable {
	
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final EnumProperty<MiddleState> MIDDLE = EnumProperty.create("middle", MiddleState.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty[] CONNECTIONS = new BooleanProperty[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};
	
	public final VoxelShape S_NORTH;
	public final VoxelShape S_SOUTH;
	public final VoxelShape S_WEST;
	public final VoxelShape S_EAST;
	public final VoxelShape S_UP;
	public final VoxelShape S_MIDDLE;
	
	public final VoxelShape[] SHAPES;
	
	public BlockWiring(String name, Material material, float hardnessAndResistance, SoundType sound, int size) {
		super(name, material, hardnessAndResistance, sound, true);
		int halfSize = size / 2;
		this.S_NORTH = Block.makeCuboidShape(8 - halfSize, 0, 0, 8 + halfSize, halfSize * 2, 8 - halfSize);
		this.S_SOUTH = Block.makeCuboidShape(8 - halfSize, 0, 8 + halfSize, 8 + halfSize, halfSize * 2, 16);
		this.S_WEST = Block.makeCuboidShape(0, 0, 8 - halfSize, 8 - halfSize, halfSize * 2, 8 + halfSize);
		this.S_EAST = Block.makeCuboidShape(8 + halfSize, 0, 8 - halfSize, 16, halfSize * 2, 8 + halfSize);
		this.S_UP = Block.makeCuboidShape(8 - halfSize, size, 8 - halfSize, 8 + halfSize, 16, 8 + halfSize);
		this.S_MIDDLE = Block.makeCuboidShape(8 - halfSize, 0, 8 - halfSize, 8 + halfSize, size, 8 + halfSize);
		this.SHAPES = new VoxelShape[] {VoxelShapes.empty(), S_UP, S_NORTH, S_SOUTH, S_WEST, S_EAST};
		this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, false).with(NORTH, false).with(SOUTH, false).with(EAST, false).with(WEST, false).with(UP, false).with(DOWN, false));
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluid().getDefaultState() : Fluids.EMPTY.getDefaultState();
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, MIDDLE, WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		BlockState state = updateState(this.getDefaultState(), context.getWorld(), context.getPos());

		BlockState replaceState = context.getWorld().getBlockState(context.getPos());
		return state.with(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState newState = updateState(state, worldIn, pos);
		if (!newState.equals(state)) worldIn.setBlockState(pos, newState);
	}
	
	public BlockState updateState(BlockState state, World worldIn, BlockPos pos) {
		
		for (Direction side : Direction.values()) {
			
			BooleanProperty prop = CONNECTIONS[side.getIndex()];
			boolean mustConnect = canConnectTo(state, worldIn, pos, pos.offset(side), side.getOpposite());
			boolean isConnected = state.get(prop);
			
			if (mustConnect != isConnected) state = state.with(prop, mustConnect);
			
		}
		
		int connections = countConnections(state);
		MiddleState mState = MiddleState.CLOSED;
		if (connections == 1 && !state.get(DOWN)) {
			mState = MiddleState.NONE;
		} else if (connections == 1 || connections == 0) {
			mState = MiddleState.OPEN;
		}
		state = state.with(MIDDLE, mState);
		
		return state;
		
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		VoxelShape shape = state.get(MIDDLE) != MiddleState.NONE ? S_MIDDLE : VoxelShapes.empty();
		for (Direction direction : Direction.values()) {
			if (state.get(CONNECTIONS[direction.getIndex()])) {
				shape = VoxelShapes.or(shape, SHAPES[direction.getIndex()]);
			}
		}
		
		return shape;
		
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	public boolean hasOpenEnd(BlockState state) {
		return countConnections(state) <= 1;
	}
	
	public int countConnections(BlockState state) {
		int connections = 0;
		for (BooleanProperty side : CONNECTIONS) {
			if (state.get(side)) connections++;
		}
		return connections;
	}
	
	public abstract boolean canConnectTo(BlockState wireState, World worldIn, BlockPos wirePos, BlockPos connectPos, Direction direction);

	public static enum MiddleState implements IStringSerializable {
		
		CLOSED("closed"),OPEN("open"),NONE("none");
		
		private String name;
		
		private MiddleState(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		@Override
		public String getString() {
			return this.name;
		}
		
	}
	
}
