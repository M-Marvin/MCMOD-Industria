package de.industria.fluids.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.industria.blocks.BlockBase;
import de.industria.tileentity.TileEntitySimpleBlockTicking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

public class BlockGasFluid extends BlockBase implements IBucketPickupHandler {
	
	public static final BooleanProperty FALLING_NOT_IN_USE = BlockStateProperties.FALLING;
	
	protected GasFluid fluid;
	
	public BlockGasFluid(String name, GasFluid fluid, Properties properties) {
		super(name, properties);
		this.fluid = fluid;
		
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FALLING_NOT_IN_USE);
		super.fillStateContainer(builder);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntitySimpleBlockTicking();
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		if (worldIn.rand.nextInt(150) == 0) this.randomTick(state, worldIn, pos, worldIn.rand);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		state = worldIn.getBlockState(pos);
		BlockState replaceState = worldIn.getBlockState(pos.up(this.fluid.getAttributes().isGaseous() ? 1 : -1));
		
		if (replaceState.isAir()) {
			
			worldIn.setBlockState(pos.up(this.fluid.getAttributes().isGaseous() ? 1 : -1), state, 2);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			this.fluid.onMoved(worldIn, pos, this.fluid.getAttributes().isGaseous() ? Direction.UP : Direction.DOWN, state.getFluidState(), worldIn.rand);
			
		} else if (replaceState.getBlock() instanceof FlowingFluidBlock) {
			
			worldIn.setBlockState(pos.up(this.fluid.getAttributes().isGaseous() ? 1 : -1), state, 2);
			worldIn.setBlockState(pos, replaceState, 2);
			this.fluid.onMoved(worldIn, pos, this.fluid.getAttributes().isGaseous() ? Direction.UP : Direction.DOWN, state.getFluidState(), worldIn.rand);
			
		} else {
			
			List<Direction> directions = new ArrayList<Direction>();
			
			Direction.Plane.HORIZONTAL.forEach((d) -> {
				
				BlockPos replacePos = pos.offset(d);
				BlockState replaceState2 = worldIn.getBlockState(replacePos);
				
				if (replaceState2.isAir() || replaceState2.getBlock() instanceof FlowingFluidBlock) directions.add(d);
				
			});
			
			if (directions.size() > 0) {
				
				Direction direction = directions.get(random.nextInt(directions.size()));
				BlockPos replacePos = pos.offset(direction);
				BlockState replaceState3 = worldIn.getBlockState(replacePos);
				worldIn.setBlockState(pos, replaceState3, 2);
				worldIn.setBlockState(replacePos, state);
				this.fluid.onMoved(worldIn, replacePos, direction, state.getFluidState(), worldIn.rand);
				
			}
			
		}
		
		super.randomTick(state, worldIn, pos, random);
	}
	
	@SuppressWarnings("deprecation")
	public void pushFluid(List<BlockPos> pushPath, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		
		List<Direction> directions = new ArrayList<Direction>();
		
		for (Direction d : Direction.values()) {
			
			BlockPos pos2 = pos.offset(d);
			BlockState replaceState = world.getBlockState(pos2);
			
			if  (replaceState.isAir() && !pushPath.contains(pos2)) directions.add(d);
			
		}
		
		if (directions.size() > 0) {
			
			Direction d = directions.get(random.nextInt(directions.size()));
			
			BlockPos replacePos = pos.offset(d);
			
			world.setBlockState(replacePos, state, 2);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			
			
		} else {
			
			List<Direction> pushableDirections = new ArrayList<Direction>();
			
			for (Direction d : new Direction[] {Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
				
				BlockPos pos2 = pos.offset(d);
				BlockState pushState = world.getBlockState(pos2);
				
				if (pushState.getBlock() instanceof BlockGasFluid && !pushPath.contains(pos2)) pushableDirections.add(d);
				
			}
			
			if (pushableDirections.size() > 0) {
				
				Direction pushDirection = pushableDirections.get(random.nextInt(pushableDirections.size()));;
				BlockPos pushPos = pos.offset(pushDirection);
				BlockState pushState = world.getBlockState(pushPos);
				
				pushPath.add(pushPos);
				
				((BlockGasFluid) pushState.getBlock()).pushFluid(pushPath, pushState, world, pushPos, random);
				
				BlockState replaceState = world.getBlockState(pushPos);
				
				if (replaceState.isAir()) {
					
					world.setBlockState(pushPos, state, 2);
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public boolean ticksRandomly(BlockState state) {
		return true;
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return this.fluid.getDefaultState();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return VoxelShapes.fullCube();
	}

	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        return this.fluid;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
	
}
