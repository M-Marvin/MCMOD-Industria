package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.redtec.RedTec;
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
	
	public static final VoxelShape S_NORTH = Block.makeCuboidShape(5, 5, 0, 11, 11, 8);
	public static final VoxelShape S_SOUTH = Block.makeCuboidShape(5, 5, 8, 11, 11, 16);
	public static final VoxelShape S_WEST = Block.makeCuboidShape(0, 5, 5, 8, 11, 11);
	public static final VoxelShape S_EAST = Block.makeCuboidShape(8, 5, 5, 16, 11, 11);
	public static final VoxelShape S_DOWN = Block.makeCuboidShape(5, 0, 5, 11, 8, 11);
	public static final VoxelShape S_UP = Block.makeCuboidShape(5, 8, 5, 11, 16, 11);
	
	public static final VoxelShape[] SHAPES = new VoxelShape[] {S_DOWN, S_UP, S_NORTH, S_SOUTH, S_WEST, S_EAST};
	
	public BlockStackedRedstoneWire() {
		super("stacked_redstone_wire", Material.IRON, 1.5F, 0.5F, SoundType.LANTERN, true);
		this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, AttachType.NONE).with(SOUTH, AttachType.NONE).with(EAST, AttachType.NONE).with(WEST, AttachType.NONE).with(POWERED, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, POWERED, WATERLOGGED);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		
		int connectedRedstone = 0;
		BlockState newState = stateIn;
		for (Direction direction : Direction.values()) {
			
			BlockPos attachPos = currentPos.offset(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			AttachType attachment = AttachType.NONE;
			if (attachState.getBlock() == this || attachState.canProvidePower() || attachState.canConnectRedstone(worldIn, attachPos, direction.getOpposite()) || isRedstoneSource(attachState, direction)) {
				attachment = AttachType.REDSTONE;
				connectedRedstone++;
			} else if (attachState.func_242698_a(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attachment = AttachType.SCAFFOLDING;
			}
			
			newState = newState.with(CONNECTIONS[direction.getIndex()], attachment);
			
		}
		
		if (connectedRedstone == 1) {
			
			for (Direction direction : Direction.values()) {

				AttachType attachment = (AttachType) newState.get(CONNECTIONS[direction.getIndex()]);
				if (attachment == AttachType.SCAFFOLDING) newState = newState.with(CONNECTIONS[direction.getIndex()], AttachType.REDSTONE);
			}
			
		}
		
		return newState;
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		if (isValidPosition(state, worldIn, pos)) {
			
			int connectedRedstone = 0;
			BlockState newState = state;
			for (Direction direction : Direction.values()) {
				
				BlockPos attachPos = pos.offset(direction);
				BlockState attachState = worldIn.getBlockState(attachPos);
				
				AttachType attachment = AttachType.NONE;
				if (attachState.getBlock() == this || attachState.canProvidePower() || attachState.canConnectRedstone(worldIn, attachPos, direction.getOpposite()) || isRedstoneSource(attachState, direction)) {
					attachment = AttachType.REDSTONE;
					connectedRedstone++;
				} else if (attachState.func_242698_a(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
					attachment = AttachType.SCAFFOLDING;
				}
				
				newState = newState.with(CONNECTIONS[direction.getIndex()], attachment);
				
			}
			
			if (connectedRedstone == 1) {
				
				for (Direction direction : Direction.values()) {

					AttachType attachment = (AttachType) newState.get(CONNECTIONS[direction.getIndex()]);
					if (attachment == AttachType.SCAFFOLDING) newState = newState.with(CONNECTIONS[direction.getIndex()], AttachType.REDSTONE);
				}
				
			}
			
			worldIn.setBlockState(pos, newState);
			
			if (blockIn != this) updatePoweredState(worldIn, pos);
			
		} else {
			
			spawnDrops(state, worldIn, pos);
			worldIn.removeBlock(pos, false);
			
		}
		
	}
	
	public static boolean isRedstoneSource(BlockState attachState, Direction direction) {
		return	(attachState.getBlock() == RedTec.stacked_redstone_torch && (direction == Direction.DOWN || direction == Direction.UP)) ||
				attachState.getBlock() instanceof PistonBlock || attachState.getBlock() instanceof BlockAdvancedPiston;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		World worldIn = context.getWorld();
		BlockState state = this.getDefaultState();
		int connectedRedstone = 0;
		for (Direction direction : Direction.values()) {
			
			BlockPos attachPos = context.getPos().offset(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			AttachType attachment = AttachType.NONE;
			if (attachState.getBlock() == this || attachState.canProvidePower() || attachState.canConnectRedstone(worldIn, attachPos, direction.getOpposite())) {
				attachment = AttachType.REDSTONE;
				connectedRedstone++;
			} else if (attachState.func_242698_a(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attachment = AttachType.SCAFFOLDING;
			}
			
			state = state.with(CONNECTIONS[direction.getIndex()], attachment);
			
		}
		
		if (connectedRedstone == 1) {
			
			for (Direction direction : Direction.values()) {
				AttachType attachment = (AttachType) state.get(CONNECTIONS[direction.getIndex()]);
				if (attachment == AttachType.SCAFFOLDING) state = state.with(CONNECTIONS[direction.getIndex()], AttachType.REDSTONE);
			}
			
		}
		
		BlockState replaceState = context.getWorld().getBlockState(context.getPos());
		return state.with(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		boolean flag = blockAccess.getBlockState(pos.offset(side.getOpposite())).getBlock() != this;
		return (flag && blockState.get(POWERED) && blockState.get(CONNECTIONS[side.getOpposite().getIndex()]) == AttachType.REDSTONE) ? 8 : 0;
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
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	     if (stateIn.get(POWERED)) {
	        double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d1 = (double)pos.getY() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D);
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
		public String getString() {
			return name;
		}
		
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		
		boolean attached = false;
		for (Direction direction : Direction.values()) {

			BlockPos attachPos = pos.offset(direction);
			BlockState attachState = worldIn.getBlockState(attachPos);
			
			if (attachState.getBlock() == this || attachState.canProvidePower() || attachState.canConnectRedstone(worldIn, attachPos, direction.getOpposite()) || attachState.func_242698_a(worldIn, attachPos, direction.getOpposite(), BlockVoxelShape.RIGID)) {
				attached = true;
			}
			
		}
		
		return attached;
		
	}
	
	public static void updatePoweredState(World worldIn, BlockPos pos) {
		
		List<BlockPos> wires = new ArrayList<BlockPos>();
		boolean powered = listConnectedWires(worldIn, pos, wires, 0);
		
		wires.forEach((wire) -> {
			BlockState state = worldIn.getBlockState(wire);
			worldIn.setBlockState(wire, state.with(POWERED, powered));
		});
		
	}
	
	private static boolean listConnectedWires(World worldIn, BlockPos scannPos, List<BlockPos> wires, int scannCount) {
		
		BlockState scannState = worldIn.getBlockState(scannPos);
		boolean powered = false;
		
		if (scannState.getBlock() == RedTec.stacked_redstone_wire && !wires.contains(scannPos) && scannCount < 2000) {
			
			wires.add(scannPos);
			if (isWirePowered(worldIn, scannPos)) powered = true;
			
			for (Direction direction : Direction.values()) {
				
				boolean flag = listConnectedWires(worldIn, scannPos.offset(direction), wires, scannCount += 1);
				if (flag) powered = true;
				
			}
			
		}

		return powered;
		
	}
	
	public static boolean isWirePowered(World worldIn, BlockPos pos) {
		
		for (Direction direction : Direction.values()) {
			
			boolean powered = worldIn.getRedstonePower(pos.offset(direction), direction) > 8;
			if (powered) return true;
			
		}
		
		return false;
		
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
