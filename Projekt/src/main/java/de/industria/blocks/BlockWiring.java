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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
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
		this.S_NORTH = Block.box(8 - halfSize, 0, 0, 8 + halfSize, halfSize * 2, 8 - halfSize);
		this.S_SOUTH = Block.box(8 - halfSize, 0, 8 + halfSize, 8 + halfSize, halfSize * 2, 16);
		this.S_WEST = Block.box(0, 0, 8 - halfSize, 8 - halfSize, halfSize * 2, 8 + halfSize);
		this.S_EAST = Block.box(8 + halfSize, 0, 8 - halfSize, 16, halfSize * 2, 8 + halfSize);
		this.S_UP = Block.box(8 - halfSize, size, 8 - halfSize, 8 + halfSize, 16, 8 + halfSize);
		this.S_MIDDLE = Block.box(8 - halfSize, 0, 8 - halfSize, 8 + halfSize, size, 8 + halfSize);
		this.SHAPES = new VoxelShape[] {VoxelShapes.empty(), S_UP, S_NORTH, S_SOUTH, S_WEST, S_EAST};
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, MIDDLE, WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		BlockPos pos = context.getClickedPos();
		BlockState state = this.defaultBlockState();
		World world = context.getLevel();
		
		BlockState replaceState = context.getLevel().getBlockState(context.getClickedPos());
		state = state.setValue(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
		
		for (Direction side : Direction.values()) {
			BooleanProperty prop = CONNECTIONS[side.get3DDataValue()];
			boolean mustConnect = canConnectTo(state, world, pos, pos.relative(side), side.getOpposite());
			boolean isConnected = state.getValue(prop);
			if (mustConnect != isConnected) {
				state = state.setValue(prop, mustConnect);
				BlockState otherWire = world.getBlockState(pos.relative(side));
				if (otherWire.getBlock() instanceof BlockWiring) world.setBlockAndUpdate(pos.relative(side), otherWire.setValue(CONNECTIONS[side.getOpposite().get3DDataValue()], true));
			}
		}

		int connections = countConnections(state);
		MiddleState mState = MiddleState.CLOSED;
		if (connections == 1 && !state.getValue(DOWN)) {
			mState = MiddleState.NONE;
		} else if (connections == 1 || connections == 0) {
			mState = MiddleState.OPEN;
		}
		state = state.setValue(MIDDLE, mState);
		
		return state;
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState newState = updateState(state, worldIn, pos);
		if (!newState.equals(state)) {
			TileEntity te = worldIn.getBlockEntity(pos);
			if (te != null) {
				CompoundNBT nbt = te.save(new CompoundNBT());
				worldIn.setBlockAndUpdate(pos, newState);
				TileEntity newTe = worldIn.getBlockEntity(pos);
				if (newTe != null) newTe.load(newState, nbt);
			} else {
				worldIn.setBlockAndUpdate(pos, newState);
			}
		}
	}
	
	public BlockState updateState(BlockState state, World worldIn, BlockPos pos) {
		
		for (Direction side : Direction.values()) {
			
			BooleanProperty prop = CONNECTIONS[side.get3DDataValue()];
			boolean mustConnect = canConnectTo(state, worldIn, pos, pos.relative(side), side.getOpposite());
			boolean isConnected = state.getValue(prop);
			
			if (mustConnect != isConnected) state = state.setValue(prop, mustConnect);
			
		}
		
		int connections = countConnections(state);
		MiddleState mState = MiddleState.CLOSED;
		if (connections == 1 && !state.getValue(DOWN)) {
			mState = MiddleState.NONE;
		} else if (connections == 1 || connections == 0) {
			mState = MiddleState.OPEN;
		}
		state = state.setValue(MIDDLE, mState);
		
		return state;
		
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		VoxelShape shape = state.getValue(MIDDLE) != MiddleState.NONE ? S_MIDDLE : VoxelShapes.empty();
		for (Direction direction : Direction.values()) {
			if (state.getValue(CONNECTIONS[direction.get3DDataValue()])) {
				shape = VoxelShapes.or(shape, SHAPES[direction.get3DDataValue()]);
			}
		}
		
		return shape;
		
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	public boolean hasOpenEnd(BlockState state) {
		return countConnections(state) <= 1;
	}
	
	public int countConnections(BlockState state) {
		int connections = 0;
		for (BooleanProperty side : CONNECTIONS) {
			if (state.getValue(side)) connections++;
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
		public String getSerializedName() {
			return this.name;
		}
		
	}
	
	public BlockState rotate(BlockState state, Rotation rotation) {
		switch(rotation) {
		case CLOCKWISE_180:
			return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
		case COUNTERCLOCKWISE_90:
			return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
		case CLOCKWISE_90:
			return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
		default:
			return state;
		}
	}

	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirror) {
		switch(mirror) {
		case LEFT_RIGHT:
			return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
		case FRONT_BACK:
			return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
		default:
			return super.mirror(state, mirror);
		}
	}
	
}
