package de.redtec.fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.redtec.blocks.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockMaterialFluid extends BlockBase implements IBucketPickupHandler {
	
	protected Fluid fluid;
	
	public BlockMaterialFluid(String name, Fluid fluid, Properties properties) {
		super(name, properties);
		this.fluid = fluid;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		if (worldIn.getBlockState(pos.up(this.fluid.getAttributes().isGaseous() ? 1 : -1)).isAir()) {
			
			worldIn.setBlockState(pos.up(this.fluid.getAttributes().isGaseous() ? 1 : -1), this.getDefaultState(), 2);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			
		} else {
			
			List<Direction> directions = new ArrayList<Direction>();
			
			Direction.Plane.HORIZONTAL.forEach((d) -> {
				
				BlockPos replacePos = pos.offset(d);
				BlockState replaceState = worldIn.getBlockState(replacePos);
				
				if (replaceState.isAir()) directions.add(d);
				
			});
			
			if (directions.size() > 0) {
				
				BlockPos replacePos = pos.offset(directions.get(random.nextInt(directions.size())));
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
				worldIn.setBlockState(replacePos, this.getDefaultState());
				
			}
			
		}
		
		super.randomTick(state, worldIn, pos, random);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!worldIn.isRemote()) this.randomTick(state, (ServerWorld) worldIn, pos, worldIn.rand);
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
			BlockState replaceState = world.getBlockState(replacePos);
			
			((MaterialFluid) this.fluid).beforeReplacingBlock(world, replacePos, replaceState);
			
			world.setBlockState(replacePos, this.getDefaultState(), 2);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			
			
		} else {
			
			List<Direction> pushableDirections = new ArrayList<Direction>();
			
			for (Direction d : Direction.values()) {
				
				BlockPos pos2 = pos.offset(d);
				BlockState pushState = world.getBlockState(pos2);
				
				if (pushState.getBlock() instanceof BlockMaterialFluid && !pushPath.contains(pos2)) pushableDirections.add(d);
				
			}
			
			if (pushableDirections.size() > 0) {
				
				Direction pushDirection = pushableDirections.get(random.nextInt(pushableDirections.size()));;
				BlockPos pushPos = pos.offset(pushDirection);
				BlockState pushState = world.getBlockState(pushPos);
				
				pushPath.add(pushPos);
				
				((BlockMaterialFluid) pushState.getBlock()).pushFluid(pushPath, pushState, world, pushPos, random);
				
				BlockState replaceState = world.getBlockState(pushPos);
				
				if (replaceState.isAir()) {
					
					((MaterialFluid) this.fluid).beforeReplacingBlock(world, pushPos, replaceState);
					
					world.setBlockState(pushPos, this.getDefaultState(), 2);
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
