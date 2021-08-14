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
		this.registerDefaultState(this.stateDefinition.any().setValue(HARDENED, false));
	}
	
	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_LIQUID_CONCRETE;
	}
	
	@Override
	protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
		builder.add(HARDENED);
		super.createFluidStateDefinition(builder);
	}
	
	@Override
	public Fluid getSource() {
		return ModFluids.LIQUID_CONCRETE;
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
		return ModItems.liquid_concrete_bucket;
	}
	
	@Override
	protected boolean canBeReplacedWith(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
		return p_215665_5_ == Direction.DOWN && !p_215665_4_.isSame(this);
	}
	
	@Override
	protected boolean canSpreadTo(IBlockReader worldIn, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluidIn) {
		FluidState fluid = worldIn.getBlockState(fromPos).getFluidState();
		return(fluid.getType().isSame(this) ? !fluid.getValue(HARDENED) : false) ? super.canSpreadTo(worldIn, fromPos, fromBlockState, direction, toPos, toBlockState, toFluidState, fluidIn) : false;
	}
	
	@Override
	public int getTickDelay(IWorldReader p_205569_1_) {
		return 6;
	}
	
	@Override
	protected float getExplosionResistance() {
		return 100;
	}
	
	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return ModItems.liquid_concrete.defaultBlockState().setValue(BlockModFlowingFluid.LEVEL, getLegacyLevel(state)).setValue(BlockLiquidConcrete.HARDENED, state.getValue(HARDENED));
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation(Industria.MODID, "block/liquid_concrete_still"), 
				new ResourceLocation(Industria.MODID, "block/liquid_concrete_flow"))
					.overlay(new ResourceLocation(Industria.MODID, "block/liquid_concrete_overlay"))
					.sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA)
					.build(this);
	}
	
	@Override
	public boolean isSame(Fluid fluidIn) {
		return fluidIn == ModFluids.LIQUID_CONCRETE || fluidIn == ModFluids.FLOWING_LIQUID_CONCRETE;
	}
	
	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}
	
	public static class Still extends FluidLiquidConcrete {

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}
		
	}
	
	public static class Flow extends FluidLiquidConcrete {

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
