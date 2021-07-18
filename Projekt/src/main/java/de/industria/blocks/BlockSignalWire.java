package de.industria.blocks;

import java.util.HashMap;
import java.util.Map.Entry;

import de.industria.util.blockfeatures.ISignalConnectiveBlock;
import de.industria.util.types.RedstoneControlSignal;
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

public class BlockSignalWire extends BlockBase implements ISignalConnectiveBlock, IWaterLoggable {
	
	public static final EnumProperty<AttachType> NORTH = EnumProperty.create("north", AttachType.class);
	public static final EnumProperty<AttachType> SOUTH = EnumProperty.create("south", AttachType.class);
	public static final EnumProperty<AttachType> EAST = EnumProperty.create("east", AttachType.class);
	public static final EnumProperty<AttachType> WEST = EnumProperty.create("west", AttachType.class);
	public static final EnumProperty<AttachType> UP = EnumProperty.create("up", AttachType.class);
	public static final EnumProperty<AttachType> DOWN = EnumProperty.create("down", AttachType.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	@SuppressWarnings("rawtypes")
	public static final EnumProperty[] CONNECTIONS = new EnumProperty[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};
	
	public static final VoxelShape S_NORTH = Block.box(5, 5, 0, 11, 11, 8);
	public static final VoxelShape S_SOUTH = Block.box(5, 5, 8, 11, 11, 16);
	public static final VoxelShape S_WEST = Block.box(0, 5, 5, 8, 11, 11);
	public static final VoxelShape S_EAST = Block.box(8, 5, 5, 16, 11, 11);
	public static final VoxelShape S_DOWN = Block.box(5, 0, 5, 11, 8, 11);
	public static final VoxelShape S_UP = Block.box(5, 8, 5, 11, 16, 11);
	
	public static final VoxelShape[] SHAPES = new VoxelShape[] {S_DOWN, S_UP, S_NORTH, S_SOUTH, S_WEST, S_EAST};
	
	public BlockSignalWire() {
		super("signal_wire", Material.STONE, 1F, 0.5F, SoundType.LANTERN, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, AttachType.NONE).setValue(SOUTH, AttachType.NONE).setValue(EAST, AttachType.NONE).setValue(WEST, AttachType.NONE));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		
		BlockState newState = stateIn;
		for (Direction direction : Direction.values()) {
			
			BlockPos attachPos = currentPos.relative(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			AttachType attachment = AttachType.NONE;
			if (attachState.getBlock() == this || isConective(attachState, direction, attachPos, worldIn)) {
				attachment = AttachType.WIRE;
			} else if (attachState.isFaceSturdy(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attachment = AttachType.SCAFFOLDING;
			}
			
			newState = newState.setValue(CONNECTIONS[direction.get3DDataValue()], attachment);
			
		}
		
		return newState;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		if (canSurvive(state, worldIn, pos)) {
			
			BlockState newState = state;
			for (Direction direction : Direction.values()) {
				
				BlockPos attachPos = pos.relative(direction);
				BlockState attachState = worldIn.getBlockState(attachPos);
				
				AttachType attachment = AttachType.NONE;
				if (attachState.getBlock() == this || isConective(attachState, direction, attachPos, worldIn)) {
					attachment = AttachType.WIRE;
				} else if (attachState.isFaceSturdy(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
					attachment = AttachType.SCAFFOLDING;
				}
				
				newState = newState.setValue(CONNECTIONS[direction.get3DDataValue()], attachment);
				
			}
			
			worldIn.setBlockAndUpdate(pos, newState);
						
		} else {
			
			dropResources(state, worldIn, pos);
			worldIn.removeBlock(pos, false);
			
		}
		
	}
	
	public static boolean isConective(BlockState attachState, Direction direction, BlockPos pos, IWorldReader world) {
		return attachState.getBlock() instanceof ISignalConnectiveBlock ? ((ISignalConnectiveBlock) attachState.getBlock()).canConectSignalWire(world, pos, direction.getOpposite()) : false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		World worldIn = context.getLevel();
		BlockState state = this.defaultBlockState();
		
		for (Direction direction : Direction.values()) {
			
			BlockPos attachPos = context.getClickedPos().relative(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			AttachType attachment = AttachType.NONE;
			if (attachState.getBlock() == this || isConective(attachState, direction, attachPos, worldIn)) {
				attachment = AttachType.WIRE;
			} else if (attachState.isFaceSturdy(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attachment = AttachType.SCAFFOLDING;
			}
			
			state = state.setValue(CONNECTIONS[direction.get3DDataValue()], attachment);
			
		}
		
		BlockState replaceState = context.getLevel().getBlockState(context.getClickedPos());
		return state.setValue(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		VoxelShape shape = VoxelShapes.empty();
		for (Direction direction : Direction.values()) {
			if (state.getValue(CONNECTIONS[direction.get3DDataValue()]) != AttachType.NONE) {
				shape = VoxelShapes.or(shape, SHAPES[direction.get3DDataValue()]);
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
		public String getSerializedName() {
			return name;
		}
		
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		
		boolean attached = false;
		for (Direction direction : Direction.values()) {

			BlockPos attachPos = pos.relative(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			if (attachState.getBlock() == this || isConective(attachState, direction, attachPos, worldIn) || attachState.isFaceSturdy(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
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
			boolean isSender = entry.getKey().equals(pos.relative(side));
			
			if (state2.getBlock() != this && !isSender && state2.getBlock() instanceof ISignalConnectiveBlock) ((ISignalConnectiveBlock) state2.getBlock()).onReciveSignal(worldIn, entry.getKey(), signal, entry.getValue());
			
		}
		
	}
	
	private void scannAt(World world, BlockPos scannPos, HashMap<BlockPos, Direction> foundDevices, int scannCount) {
		
		for (Direction direction : Direction.values()) {
			
			BlockState state = world.getBlockState(scannPos.relative(direction));
			
			if (state.getBlock() instanceof ISignalConnectiveBlock && !foundDevices.containsKey(scannPos.relative(direction)) && scannCount < 200 && ((ISignalConnectiveBlock) state.getBlock()).canConectSignalWire(world, scannPos.relative(direction), direction.getOpposite())) {
				
				foundDevices.put(scannPos.relative(direction), direction.getOpposite());
				
				scannAt(world, scannPos.relative(direction), foundDevices, scannCount++);
				
			}
			
		}
				
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
	      switch(mirrorIn) {
	      case LEFT_RIGHT:
	         return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
	      case FRONT_BACK:
	         return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
	      default:
	         return super.mirror(state, mirrorIn);
	      }
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
	      switch(rot) {
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
	
}