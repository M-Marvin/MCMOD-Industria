package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.industria.typeregistys.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStackedRedstoneWire extends BlockBase implements IWaterLoggable {
	
	public static final EnumProperty<AttachType> NORTH = EnumProperty.create("north", AttachType.class);
	public static final EnumProperty<AttachType> SOUTH = EnumProperty.create("south", AttachType.class);
	public static final EnumProperty<AttachType> EAST = EnumProperty.create("east", AttachType.class);
	public static final EnumProperty<AttachType> WEST = EnumProperty.create("west", AttachType.class);
	public static final EnumProperty<AttachType> UP = EnumProperty.create("up", AttachType.class);
	public static final EnumProperty<AttachType> DOWN = EnumProperty.create("down", AttachType.class);
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
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
	
	public BlockStackedRedstoneWire() {
		super("stacked_redstone_wire", Material.METAL, 1.5F, 0.5F, SoundType.LANTERN, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, AttachType.NONE).setValue(SOUTH, AttachType.NONE).setValue(EAST, AttachType.NONE).setValue(WEST, AttachType.NONE).setValue(POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, POWERED, WATERLOGGED);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		
		int connectedRedstone = 0;
		BlockState newState = stateIn;
		for (Direction direction : Direction.values()) {
			
			BlockPos attachPos = currentPos.relative(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			AttachType attachment = AttachType.NONE;
			if (attachState.getBlock() == this || attachState.isSignalSource() || attachState.canConnectRedstone(worldIn, attachPos, direction.getOpposite()) || isRedstoneSource(attachState, direction)) {
				attachment = AttachType.REDSTONE;
				connectedRedstone++;
			} else if (attachState.isFaceSturdy(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attachment = AttachType.SCAFFOLDING;
			}
			
			newState = newState.setValue(CONNECTIONS[direction.get3DDataValue()], attachment);
			
		}
		
		if (connectedRedstone == 1) {
			
			for (Direction direction : Direction.values()) {

				AttachType attachment = (AttachType) newState.getValue(CONNECTIONS[direction.get3DDataValue()]);
				if (attachment == AttachType.SCAFFOLDING) newState = newState.setValue(CONNECTIONS[direction.get3DDataValue()], AttachType.REDSTONE);
			}
			
		}
		
		return newState;
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		if (canSurvive(state, worldIn, pos)) {
			
			int connectedRedstone = 0;
			BlockState newState = state;
			for (Direction direction : Direction.values()) {
				
				BlockPos attachPos = pos.relative(direction);
				BlockState attachState = worldIn.getBlockState(attachPos);
				
				AttachType attachment = AttachType.NONE;
				if (attachState.getBlock() == this || attachState.isSignalSource() || attachState.canConnectRedstone(worldIn, attachPos, direction.getOpposite()) || isRedstoneSource(attachState, direction)) {
					attachment = AttachType.REDSTONE;
					connectedRedstone++;
				} else if (attachState.isFaceSturdy(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
					attachment = AttachType.SCAFFOLDING;
				}
				
				newState = newState.setValue(CONNECTIONS[direction.get3DDataValue()], attachment);
				
			}
			
			if (connectedRedstone == 1) {
				
				for (Direction direction : Direction.values()) {

					AttachType attachment = (AttachType) newState.getValue(CONNECTIONS[direction.get3DDataValue()]);
					if (attachment == AttachType.SCAFFOLDING) newState = newState.setValue(CONNECTIONS[direction.get3DDataValue()], AttachType.REDSTONE);
				}
				
			}
			
			worldIn.setBlockAndUpdate(pos, newState);
			
			if (blockIn != this) updatePoweredState(worldIn, pos);
			
		} else {
			
			dropResources(state, worldIn, pos);
			worldIn.removeBlock(pos, false);
			
		}
		
	}
	
	public static boolean isRedstoneSource(BlockState attachState, Direction direction) {
		return	(attachState.getBlock() == ModItems.stacked_redstone_torch && (direction == Direction.DOWN || direction == Direction.UP)) ||
				attachState.getBlock() instanceof PistonBlock || attachState.getBlock() instanceof BlockRAdvancedPiston;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		World worldIn = context.getLevel();
		BlockState state = this.defaultBlockState();
		int connectedRedstone = 0;
		for (Direction direction : Direction.values()) {
			
			BlockPos attachPos = context.getClickedPos().relative(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			AttachType attachment = AttachType.NONE;
			if (attachState.getBlock() == this || attachState.isSignalSource() || attachState.canConnectRedstone(worldIn, attachPos, direction.getOpposite())) {
				attachment = AttachType.REDSTONE;
				connectedRedstone++;
			} else if (attachState.isFaceSturdy(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attachment = AttachType.SCAFFOLDING;
			}
			
			state = state.setValue(CONNECTIONS[direction.get3DDataValue()], attachment);
			
		}
		
		if (connectedRedstone == 1) {
			
			for (Direction direction : Direction.values()) {
				AttachType attachment = (AttachType) state.getValue(CONNECTIONS[direction.get3DDataValue()]);
				if (attachment == AttachType.SCAFFOLDING) state = state.setValue(CONNECTIONS[direction.get3DDataValue()], AttachType.REDSTONE);
			}
			
		}
		
		BlockState replaceState = context.getLevel().getBlockState(context.getClickedPos());
		return state.setValue(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		boolean flag = blockAccess.getBlockState(pos.relative(side.getOpposite())).getBlock() != this;
		return (flag && blockState.getValue(POWERED) && blockState.getValue(CONNECTIONS[side.getOpposite().get3DDataValue()]) == AttachType.REDSTONE) ? 8 : 0;
	}
	
	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return this.getSignal(blockState, blockAccess, pos, side);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
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
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	     if (stateIn.getValue(POWERED)) {
	        double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d1 = (double)pos.getY() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        worldIn.addParticle(RedstoneParticleData.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	     }
	}
	
	public static enum AttachType implements IStringSerializable {
		
		NONE("none"),REDSTONE("redstone"),SCAFFOLDING("scaffolding");
		
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
			
			if (attachState.getBlock() == this || attachState.isSignalSource() || attachState.canConnectRedstone(worldIn, attachPos, direction.getOpposite()) || attachState.isFaceSturdy(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attached = true;
			}
			
		}
		
		return attached;
		
	}
	
	public static void updatePoweredState(World worldIn, BlockPos pos) {
		
		List<BlockPos> wires = new ArrayList<BlockPos>();
		boolean power = listConnectedWires(worldIn, pos, wires, 0);
		
		wires.forEach((wire) -> {
			BlockState state = worldIn.getBlockState(wire);
			boolean powered = state.getValue(POWERED);
			worldIn.setBlockAndUpdate(wire, state.setValue(POWERED, power));
			if (powered != power) {
				for (Direction d : Direction.values()) {
					BlockPos notifyPos = wire.relative(d);
					BlockState notifyState = worldIn.getBlockState(notifyPos);
					if (notifyState.getBlock() != ModItems.stacked_redstone_wire) {
						worldIn.updateNeighborsAt(notifyPos, ModItems.stacked_redstone_wire);
					}
				}
			}
		});
		
	}
	
	private static boolean listConnectedWires(World worldIn, BlockPos scannPos, List<BlockPos> wires, int scannCount) {
		
		BlockState scannState = worldIn.getBlockState(scannPos);
		boolean powered = false;
		
		if (scannState.getBlock() == ModItems.stacked_redstone_wire && !wires.contains(scannPos) && scannCount < 2000) {
			
			wires.add(scannPos);
			if (isWirePowered(worldIn, scannPos)) powered = true;
			
			for (Direction direction : Direction.values()) {
				
				boolean flag = listConnectedWires(worldIn, scannPos.relative(direction), wires, scannCount += 1);
				if (flag) powered = true;
				
			}
			
		}

		return powered;
		
	}
	
	public static boolean isWirePowered(World worldIn, BlockPos pos) {
		
		for (Direction direction : Direction.values()) {
			
			boolean powered = worldIn.getSignal(pos.relative(direction), direction) > 8;
			if (powered) return true;
			
		}
		
		return false;
		
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
