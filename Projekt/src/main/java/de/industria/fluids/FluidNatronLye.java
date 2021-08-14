package de.industria.fluids;

import java.util.Random;

import de.industria.Industria;
import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class FluidNatronLye extends FlowingFluid {
	
	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_NATRON_LYE;
	}

	@Override
	public Fluid getSource() {
		return ModFluids.NATRON_LYE;
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
		return ModItems.natron_lye_bucket;
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
		return ModItems.natron_lye.defaultBlockState().setValue(BlockModFlowingFluid.LEVEL, getLegacyLevel(state));
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation(Industria.MODID, "block/natron_lye_still"), 
				new ResourceLocation(Industria.MODID, "block/natron_lye_flow"))
					.overlay(new ResourceLocation(Industria.MODID, "block/natron_lye_overlay"))
					.build(this);
	}
	
	@Override
	public boolean isSame(Fluid fluidIn) {
		return fluidIn == ModFluids.NATRON_LYE || fluidIn == ModFluids.FLOWING_NATRON_LYE;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void randomTick(World world, BlockPos pos, FluidState state, Random random) {

		for (Direction d : Direction.values()) {

			BlockState state1 = world.getBlockState(pos.relative(d));
			int resistance = (int) state1.getExplosionResistance(world, pos.relative(d), null);
			resistance = Math.max(1, resistance);
			
			if (random.nextInt(resistance * 4) == 0 && resistance <= 1.5F) {
				
				if (resistance < 35 && !state1.isAir() && !(state1.getBlock() instanceof FlowingFluidBlock) && !state1.is(ModTags.ACID_RESISTANT)) {
					world.setBlockAndUpdate(pos.relative(d), d == Direction.DOWN ? ModFluids.FLOWING_NATRON_LYE.defaultFluidState().createLegacyBlock() : Blocks.AIR.defaultBlockState());
					world.playSound(null, pos.relative(d), SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1);
				}
				
			}
			
		}
		
	}

	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}
	
	public static class Still extends FluidNatronLye {

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}
		
	}
	
	public static class Flow extends FluidNatronLye {

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
