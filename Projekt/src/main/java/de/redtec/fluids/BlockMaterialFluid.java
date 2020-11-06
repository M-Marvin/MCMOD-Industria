package de.redtec.fluids;

import java.util.Random;

import de.redtec.blocks.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockMaterialFluid extends BlockBase {
	
	protected Fluid fluid;
	
	public BlockMaterialFluid(String name, Fluid fluid, Properties properties) {
		super(name, properties);
		this.fluid = fluid;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		if (random.nextInt(2) == 0) {
			
			if (worldIn.getBlockState(pos.up()).isAir()) {
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
				worldIn.setBlockState(pos.up(), state);
			} else {
				Direction direction = Direction.values()[random.nextInt(5)];
				BlockState replaceState = worldIn.getBlockState(pos.offset(direction));
				
				if (replaceState.isAir()) {
					worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
					worldIn.setBlockState(pos.offset(direction), state);
				} else if (replaceState.getBlock() instanceof BlockMaterialFluid) {
					replaceState.getBlock().randomTick(replaceState, worldIn, pos.offset(direction), random);
				}
			}
			
		}
		
		super.randomTick(state, worldIn, pos, random);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!worldIn.isRemote()) this.randomTick(state, (ServerWorld) worldIn, pos, worldIn.rand);
	}
	
	@Override
	public boolean ticksRandomly(BlockState state) {
		return true;
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return this.fluid.getDefaultState();
	}
	
}
