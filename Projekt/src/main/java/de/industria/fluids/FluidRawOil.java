package de.industria.fluids;

import de.industria.Industria;
import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class FluidRawOil extends FlowingFluid {
	
	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_RAW_OIL;
	}

	@Override
	public Fluid getSource() {
		return ModFluids.RAW_OIL;
	}

	@Override
	protected boolean canConvertToSource() {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(IWorld worldIn, BlockPos pos, BlockState state) {}

	@Override
	protected int getSlopeFindDistance(IWorldReader worldIn) {
		return 2;
	}

	@Override
	protected int getDropOff(IWorldReader worldIn) {
		return 2;
	}

	@Override
	public Item getBucket() {
		return ModItems.raw_oil_bucket;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
		return p_215665_5_ == Direction.DOWN && !p_215665_4_.isSame(this);
	}

	@Override
	public int getTickDelay(IWorldReader p_205569_1_) {
		return 5;
	}

	@Override
	protected float getExplosionResistance() {
		return 100;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return ModItems.raw_oil.defaultBlockState().setValue(BlockModFlowingFluid.LEVEL, getLegacyLevel(state));
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation(Industria.MODID, "block/raw_oil_still"), 
				new ResourceLocation(Industria.MODID, "block/raw_oil_flow"))
					.overlay(new ResourceLocation(Industria.MODID, "block/raw_oil_overlay"))
					.sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_FILL_LAVA)
					.build(this);
	}
	
	@Override
	public boolean isSame(Fluid fluidIn) {
		return fluidIn == ModFluids.RAW_OIL || fluidIn == ModFluids.FLOWING_RAW_OIL;
	}
	
	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}
	
	public static class Still extends FluidRawOil {

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}
		
	}
	
	public static class Flow extends FluidRawOil {

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}
		
		@Override
		protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
			builder.add(LEVEL);
			super.createFluidStateDefinition(builder);
		}
		
	}
	
}
