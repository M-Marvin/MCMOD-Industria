package de.industria.fluids;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class FluidLiquidConcrete extends FlowingFluid {
	
	public static final BooleanProperty HARDENED = BooleanProperty.create("hardened");
	
	public FluidLiquidConcrete() {
		this.setDefaultState(this.stateContainer.getBaseState().with(HARDENED, false));
	}
	
	@Override
	public Fluid getFlowingFluid() {
		return ModFluids.FLOWING_LIQUID_CONCRETE;
	}
	
	@Override
	protected void fillStateContainer(Builder<Fluid, FluidState> builder) {
		builder.add(HARDENED);
		super.fillStateContainer(builder);
	}
	
	@Override
	public Fluid getStillFluid() {
		return ModFluids.LIQUID_CONCRETE;
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
		return ModItems.liquid_concrete_bucket;
	}
	
	@Override
	protected boolean canDisplace(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
		return p_215665_5_ == Direction.DOWN && !p_215665_4_.isEquivalentTo(this);
	}
	
	@Override
	protected boolean canFlow(IBlockReader worldIn, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluidIn) {
		FluidState fluid = worldIn.getBlockState(fromPos).getFluidState();
		return(fluid.getFluid().isEquivalentTo(this) ? !fluid.get(HARDENED) : false) ? super.canFlow(worldIn, fromPos, fromBlockState, direction, toPos, toBlockState, toFluidState, fluidIn) : false;
	}
	
	@Override
	public int getTickRate(IWorldReader p_205569_1_) {
		return 6;
	}
	
	@Override
	protected float getExplosionResistance() {
		return 100;
	}
	
	@Override
	protected BlockState getBlockState(FluidState state) {
		return ModItems.liquid_concrete.getDefaultState().with(BlockModFlowingFluid.LEVEL, getLevelFromState(state)).with(BlockLiquidConcrete.HARDENED, state.get(HARDENED));
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation(Industria.MODID, "block/liquid_concrete_still"), 
				new ResourceLocation(Industria.MODID, "block/liquid_concrete_flow"))
					.overlay(new ResourceLocation(Industria.MODID, "block/liquid_concrete_overlay"))
					.sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA)
					.build(this);
	}
	
	@Override
	public boolean isEquivalentTo(Fluid fluidIn) {
		return fluidIn == ModFluids.LIQUID_CONCRETE || fluidIn == ModFluids.FLOWING_LIQUID_CONCRETE;
	}
	
	@Override
	protected boolean ticksRandomly() {
		return true;
	}
	
	public static class Still extends FluidLiquidConcrete {

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getLevel(FluidState state) {
			return 8;
		}
		
	}
	
	public static class Flow extends FluidLiquidConcrete {

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
