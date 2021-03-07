package de.redtec.fluids;

import de.redtec.RedTec;
import de.redtec.fluids.util.BlockModFlowingFluid;
import de.redtec.typeregistys.ModFluids;
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

public abstract class FluidOreIronSolution extends FlowingFluid {
	
	@Override
	public Fluid getFlowingFluid() {
		return ModFluids.FLOWING_IRON_SOLUTION;
	}

	@Override
	public Fluid getStillFluid() {
		return ModFluids.IRON_SOLUTION;
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
		return RedTec.iron_solution_bucket;
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
		return RedTec.iron_solution.getDefaultState().with(BlockModFlowingFluid.LEVEL, getLevelFromState(state));
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation(RedTec.MODID, "block/iron_solution_still"), 
				new ResourceLocation(RedTec.MODID, "block/iron_solution_flow"))
					.overlay(new ResourceLocation(RedTec.MODID, "block/iron_solution_overlay"))
					.build(this);
	}
	
	@Override
	public boolean isEquivalentTo(Fluid fluidIn) {
		return fluidIn == ModFluids.IRON_SOLUTION || fluidIn == ModFluids.FLOWING_IRON_SOLUTION;
	}

	@Override
	protected boolean ticksRandomly() {
		return true;
	}
	
	public static class Still extends FluidOreIronSolution {

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getLevel(FluidState state) {
			return 8;
		}
		
	}
	
	public static class Flow extends FluidOreIronSolution {

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
