package de.redtec.blocks;

import java.util.HashMap;
import java.util.Map.Entry;

import de.redtec.util.ISignalConnective;
import de.redtec.util.RedstoneControlSignal;
import net.minecraft.block.Block;
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
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockSignalWire extends BlockBase implements ISignalConnective, IWaterLoggable {
	
	public static final EnumProperty<AttachType> NORTH = EnumProperty.create("north", AttachType.class);
	public static final EnumProperty<AttachType> SOUTH = EnumProperty.create("south", AttachType.class);
	public static final EnumProperty<AttachType> EAST = EnumProperty.create("east", AttachType.class);
	public static final EnumProperty<AttachType> WEST = EnumProperty.create("west", AttachType.class);
	public static final EnumProperty<AttachType> UP = EnumProperty.create("up", AttachType.class);
	public static final EnumProperty<AttachType> DOWN = EnumProperty.create("down", AttachType.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	@SuppressWarnings("rawtypes")
	public static final EnumProperty[] CONNECTIONS = new EnumProperty[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};
	
	public static final VoxelShape S_NORTH = Block.makeCuboidShape(5, 5, 0, 11, 11, 8);
	public static final VoxelShape S_SOUTH = Block.makeCuboidShape(5, 5, 8, 11, 11, 16);
	public static final VoxelShape S_WEST = Block.makeCuboidShape(0, 5, 5, 8, 11, 11);
	public static final VoxelShape S_EAST = Block.makeCuboidShape(8, 5, 5, 16, 11, 11);
	public static final VoxelShape S_DOWN = Block.makeCuboidShape(5, 0, 5, 11, 8, 11);
	public static final VoxelShape S_UP = Block.makeCuboidShape(5, 8, 5, 11, 16, 11);
	
	public static final VoxelShape[] SHAPES = new VoxelShape[] {S_DOWN, S_UP, S_NORTH, S_SOUTH, S_WEST, S_EAST};
	
	public BlockSignalWire() {
		super("signal_wire", Material.ROCK, 1, SoundType.LANTERN);
		this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, AttachType.NONE).with(SOUTH, AttachType.NONE).with(EAST, AttachType.NONE).with(WEST, AttachType.NONE));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		
		BlockState newState = stateIn;
		for (Direction direction : Direction.values()) {
			
			BlockPos attachPos = currentPos.offset(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			AttachType attachment = AttachType.NONE;
			if (attachState.getBlock() == this || isConective(attachState, direction, attachPos, worldIn)) {
				attachment = AttachType.WIRE;
			} else if (attachState.func_242698_a(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attachment = AttachType.SCAFFOLDING;
			}
			
			newState = newState.with(CONNECTIONS[direction.getIndex()], attachment);
			
		}
		
		return newState;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		if (isValidPosition(state, worldIn, pos)) {
			
			BlockState newState = state;
			for (Direction direction : Direction.values()) {
				
				BlockPos attachPos = pos.offset(direction);
				BlockState attachState = worldIn.getBlockState(attachPos);
				
				AttachType attachment = AttachType.NONE;
				if (attachState.getBlock() == this || isConective(attachState, direction, attachPos, worldIn)) {
					attachment = AttachType.WIRE;
				} else if (attachState.func_242698_a(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
					attachment = AttachType.SCAFFOLDING;
				}
				
				newState = newState.with(CONNECTIONS[direction.getIndex()], attachment);
				
			}
			
			worldIn.setBlockState(pos, newState);
						
		} else {
			
			spawnDrops(state, worldIn, pos);
			worldIn.removeBlock(pos, false);
			
		}
		
	}
	
	public static boolean isConective(BlockState attachState, Direction direction, BlockPos pos, IWorldReader world) {
		return attachState.getBlock() instanceof ISignalConnective ? ((ISignalConnective) attachState.getBlock()).canConectSignalWire(world, pos, direction.getOpposite()) : false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		World worldIn = context.getWorld();
		BlockState state = this.getDefaultState();
		
		for (Direction direction : Direction.values()) {
			
			BlockPos attachPos = context.getPos().offset(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			AttachType attachment = AttachType.NONE;
			if (attachState.getBlock() == this || isConective(attachState, direction, attachPos, worldIn)) {
				attachment = AttachType.WIRE;
			} else if (attachState.func_242698_a(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attachment = AttachType.SCAFFOLDING;
			}
			
			state = state.with(CONNECTIONS[direction.getIndex()], attachment);
			
		}
		
		BlockState replaceState = context.getWorld().getBlockState(context.getPos());
		return state.with(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		VoxelShape shape = VoxelShapes.empty();
		for (Direction direction : Direction.values()) {
			if (state.get(CONNECTIONS[direction.getIndex()]) != AttachType.NONE) {
				shape = VoxelShapes.or(shape, SHAPES[direction.getIndex()]);
			}
		}
		
		return shape;
		
	}
	
	public static enum AttachType implements IStringSerializable {
		
		NONE("none"),WIRE("wire"),SCAFFOLDING("scaffolding");
		
		private String name;
		
		private AttachType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String func_176610_l() {
			return this.name;
		}
		
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		
		boolean attached = false;
		for (Direction direction : Direction.values()) {

			BlockPos attachPos = pos.offset(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			if (attachState.getBlock() == this || isConective(attachState, direction, attachPos, worldIn) || attachState.func_242698_a(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attached = true;
			}
			
		}
		
		return attached;
		
	}
	
	@Override
	public boolean canConectSignalWire(IWorldReader world, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public void onReciveSignal(World worldIn, BlockPos pos, RedstoneControlSignal signal, Direction side) {
		
		HashMap<BlockPos, Direction> network = new HashMap<BlockPos, Direction>();
		network.put(pos, side);
		scannAt(worldIn, pos, network, 0);
		
		for (Entry<BlockPos, Direction> entry : network.entrySet()) {
			
			BlockState state2 = worldIn.getBlockState(entry.getKey());
			boolean isSender = entry.getKey().equals(pos.offset(side));
			
			if (state2.getBlock() != this && !isSender && state2.getBlock() instanceof ISignalConnective) ((ISignalConnective) state2.getBlock()).onReciveSignal(worldIn, entry.getKey(), signal, entry.getValue());
			
		}
		
	}
	
	private void scannAt(World world, BlockPos scannPos, HashMap<BlockPos, Direction> foundDevices, int scannCount) {
		
		for (Direction direction : Direction.values()) {
			
			BlockState state = world.getBlockState(scannPos.offset(direction));
			
			if (state.getBlock() instanceof ISignalConnective && !foundDevices.containsKey(scannPos.offset(direction)) && scannCount < 200 && ((ISignalConnective) state.getBlock()).canConectSignalWire(world, scannPos.offset(direction), direction.getOpposite())) {
				
				foundDevices.put(scannPos.offset(direction), direction.getOpposite());
				
				scannAt(world, scannPos.offset(direction), foundDevices, scannCount++);
				
			}
			
		}
				
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluid().getDefaultState() : Fluids.EMPTY.getDefaultState();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
	      switch(mirrorIn) {
	      case LEFT_RIGHT:
	         return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
	      case FRONT_BACK:
	         return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
	      default:
	         return super.mirror(state, mirrorIn);
	      }
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
	      switch(rot) {
	      case CLOCKWISE_180:
	         return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
	      case COUNTERCLOCKWISE_90:
	         return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
	      case CLOCKWISE_90:
	         return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
	      default:
	         return state;
	      }
	}
	
}