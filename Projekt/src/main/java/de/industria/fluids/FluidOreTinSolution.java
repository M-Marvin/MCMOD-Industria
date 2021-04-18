package de.industria.fluids;

import de.industria.Industria;
import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class FluidOreTinSolution extends FlowingFluid {
	
	@Override
	public Fluid getFlowingFluid() {
		return ModFluids.FLOWING_TIN_SOLUTION;
	}

	@Override
	public Fluid getStillFluid() {
		return ModFluids.TIN_SOLUTION;
	}

	@Override
	protected boolean canSourcesMultiply() {
		return false;
	}

	@Override
	protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {}

	@Override
	protected int getSlopeFindDistance(IWorldReader worldIn) {
		return 2;
	}

	@Override
	protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
		return 2;
	}

	@Override
	public Item getFilledBucket() {
		return Industria.tin_solution_bucket;
	}

	@Override
	protected boolean canDisplace(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
		return p_215665_5_ == Direction.DOWN && !p_215665_4_.isEquivalentTo(this);
	}

	@Override
	public int getTickRate(IWorldReader p_205569_1_) {
		return 5;
	}

	@Override
	protected float getExplosionResistance() {
		return 100;
	}

	@Override
	protected BlockState getBlockState(FluidState state) {
		return Industria.tin_solution.getDefaultState().with(BlockModFlowingFluid.LEVEL, getLevelFromState(state));
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation(Industria.MODID, "block/tin_solution_still"), 
				new ResourceLocation(Industria.MODID, "block/tin_solution_flow"))
					.overlay(new ResourceLocation(Industria.MODID, "block/tin_solution_overlay"))
					.build(this);
	}
	
	@Override
	public boolean isEquivalentTo(Fluid fluidIn) {
		return fluidIn == ModFluids.TIN_SOLUTION || fluidIn == ModFluids.FLOWING_TIN_SOLUTION;
	}
	
	@Override
	protected boolean ticksRandomly() {
		return true;
	}
	
	public static class Still extends FluidOreTinSolution {

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getLevel(FluidState state) {
			return 8;
		}
		
	}
	
	public static class Flow extends FluidOreTinSolution {

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}

		@Override
		public int getLevel(FluidState state) {
			return state.get(LEVEL_1_8);
		}
		
		@Override
		protected void fillStateContainer(Builder<Fluid, FluidState> builder) {
			builder.add(LEVEL_1_8);
			super.fillStateContainer(builder);
		}
		
	}
	
}