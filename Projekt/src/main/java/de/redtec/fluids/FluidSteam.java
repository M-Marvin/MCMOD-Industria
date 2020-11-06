package de.redtec.fluids;

import de.redtec.RedTec;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.fluids.FluidAttributes;


public class FluidSteam extends MaterialFluid implements IBucketPickupHandler {
	
	@Override
	public Item getFilledBucket() {
		return RedTec.steam_bucket;
	}
	
	@Override
	protected BlockState getBlockState(FluidState state) {
		return RedTec.steam.getDefaultState();
	}

	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
		System.out.println("TEST");
		worldIn.removeBlock(pos, false);
		return this;
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation("block/water_still"),
				new ResourceLocation("block/water_flow"))
				.gaseous()
				.build(this);
	}
	
}

//public abstract class FluidSteam extends FlowingFluid {
//
//	@Override
//	public Fluid getFlowingFluid() {
//		return ModFluids.FLOWING_STEAM;
//	}
//
//	@Override
//	public Fluid getStillFluid() {
//		return ModFluids.STEAM;
//	}
//
//	@Override
//	protected boolean canSourcesMultiply() {
//		return false;
//	}
//
//	@Override
//	protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {}
//
//	@Override
//	protected int getSlopeFindDistance(IWorldReader worldIn) {
//		return 3;
//	}
//
//	@Override
//	protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
//		return 1;
//	}
//
//	@Override
//	public Item getFilledBucket() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	protected boolean canDisplace(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
//		return false;
//	}
//
//	@Override
//	public int getTickRate(IWorldReader p_205569_1_) {
//		return 2;
//	}
//
//	@Override
//	protected float getExplosionResistance() {
//		return 100F;
//	}
//
//	@Override
//	protected BlockState getBlockState(FluidState state) {
//		return RedTec.steam.getDefaultState().with(FlowingFluidBlock.LEVEL, Integer.valueOf(getLevelFromState(state)));
//	}
//	
//	@Override
//	public boolean isEquivalentTo(Fluid fluidIn) {
//		return fluidIn == ModFluids.STEAM || fluidIn == ModFluids.FLOWING_STEAM;
//	}
//	
//	@Override
//	protected FluidAttributes createAttributes() {
//		return FluidAttributes.builder(
////				new ResourceLocation(RedTec.MODID, "block/steam_still"), 
////				new ResourceLocation(RedTec.MODID, "block/steam_flow"))
////				.overlay(new ResourceLocation(RedTec.MODID, "block/steam_overlay"))	
//				new ResourceLocation("block/water_still"),
//				new ResourceLocation("block/water_flow"))
//             	.overlay(new ResourceLocation("block/water_overlay"))
//				.translationKey("block.redtec.steam")
//				.sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
//				.color(0xBCE2D7)
//				.density(-100)
//				.sound(SoundEvents.BLOCK_CAMPFIRE_CRACKLE)
//				.build(this);
//		
//	}
//	
//	public static class Flowing extends FluidSteam {
//
//		@Override
//		public boolean isSource(FluidState state) {
//			return false;
//		}
//
//		@Override
//		public int getLevel(FluidState state) {
//			return state.get(LEVEL_1_8);
//		}
//		
//		@Override
//		protected void fillStateContainer(Builder<Fluid, FluidState> builder) {
//			builder.add(LEVEL_1_8);
//			super.fillStateContainer(builder);
//		}
//		
//		
//	}
//	
//	public static class Source extends FluidSteam {
//
//		@Override
//		public boolean isSource(FluidState state) {
//			return true;
//		}
//
//		@Override
//		public int getLevel(FluidState state) {
//			return 8;
//		}
//		
//	}
//	
//}
