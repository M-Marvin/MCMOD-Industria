package de.industria.fluids;

import java.util.Random;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.BlockTags;
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

public abstract class FluidSulfuricAcid extends FlowingFluid {
	
	@Override
	public Fluid getFlowingFluid() {
		return ModFluids.FLOWING_SULFURIC_ACID;
	}

	@Override
	public Fluid getStillFluid() {
		return ModFluids.SULFURIC_ACID;
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
		return ModItems.sulfuric_acid_bucket;
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
		return ModItems.sulfuric_acid.getDefaultState().with(BlockModFlowingFluid.LEVEL, getLevelFromState(state));
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation(Industria.MODID, "block/sulfuric_acid_still"), 
				new ResourceLocation(Industria.MODID, "block/sulfuric_acid_flow"))
					.overlay(new ResourceLocation(Industria.MODID, "block/sulfuric_acid_overlay"))
					.build(this);
	}
	
	@Override
	public boolean isEquivalentTo(Fluid fluidIn) {
		return fluidIn == ModFluids.SULFURIC_ACID || fluidIn == ModFluids.FLOWING_SULFURIC_ACID;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void randomTick(World world, BlockPos pos, FluidState state, Random random) {

		for (Direction d : Direction.values()) {

			BlockState state1 = world.getBlockState(pos.offset(d));
			int resistance = (int) state1.getExplosionResistance(world, pos.offset(d), null);
			resistance = Math.max(1, resistance);
			
			if (random.nextInt(resistance * 2) == 0) {
				
				if (resistance < 35 && !state1.isAir() && !(state1.getBlock() instanceof FlowingFluidBlock) && !state1.isIn(BlockTags.BASE_STONE_NETHER) && !state1.isIn(BlockTags.NYLIUM)) {
					world.setBlockState(pos.offset(d), d == Direction.DOWN ? ModFluids.FLOWING_SULFURIC_ACID.getDefaultState().getBlockState() : Blocks.AIR.getDefaultState());
					world.playSound(null, pos.offset(d), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1);
				}
				
			}
			
		}
		
	}

	@Override
	protected boolean ticksRandomly() {
		return true;
	}
	
	public static class Still extends FluidSulfuricAcid {

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getLevel(FluidState state) {
			return 8;
		}
		
	}
	
	public static class Flow extends FluidSulfuricAcid {

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
