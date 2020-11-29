package de.redtec.fluids;

import java.util.Random;

import de.redtec.RedTec;
import de.redtec.fluids.util.BlockModFlowingFluid;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class FluidDestilledWater extends FlowingFluid {
	
	public static final BooleanProperty HOT = BooleanProperty.create("hot");
	
	public FluidDestilledWater() {
		this.setDefaultState(this.stateContainer.getBaseState().with(HOT, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Fluid, FluidState> builder) {
		builder.add(HOT);
		super.fillStateContainer(builder);
	}
	
	@Override
	public Fluid getFlowingFluid() {
		return ModFluids.FLOWING_DESTILLED_WATER;
	}

	@Override
	public Fluid getStillFluid() {
		return ModFluids.DESTILLED_WATER;
	}

	@Override
	protected boolean canSourcesMultiply() {
		return false;
	}

	@Override
	protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {}

	@Override
	protected int getSlopeFindDistance(IWorldReader worldIn) {
		return 4;
	}

	@Override
	protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
		return 1;
	}

	@Override
	public Item getFilledBucket() {
		return RedTec.destilled_water_bucket;
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
		return RedTec.destilled_water.getDefaultState().with(BlockModFlowingFluid.LEVEL, getLevelFromState(state)).with(BlockDestilledWater.HOT, state.get(HOT));
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation(RedTec.MODID, "block/destilled_water_still"), 
				new ResourceLocation(RedTec.MODID, "block/destilled_water_flow"))
					.overlay(new ResourceLocation(RedTec.MODID, "block/fluid_overlay"))
					.build(this);
	}
	
	@Override
	public boolean isEquivalentTo(Fluid fluidIn) {
		return fluidIn == ModFluids.DESTILLED_WATER || fluidIn == ModFluids.FLOWING_DESTILLED_WATER;
	}

	@Override
	protected void randomTick(World world, BlockPos pos, FluidState state, Random random) {

		if (random.nextInt(80) == 0 && state.get(HOT)) {
			
			world.setBlockState(pos, state.with(HOT, false).getBlockState());
			
		}
		
	}
	
	public static class Still extends FluidDestilledWater {

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getLevel(FluidState state) {
			return 8;
		}

		@Override
		protected void randomTick(World world, BlockPos pos, FluidState state, Random random) {

			if (random.nextInt(20) == 0) {
				
				for (Direction d : Direction.values()) {
					
					BlockState state2 = world.getBlockState(pos.offset(d));
					boolean isIce =
							state2.getBlock() == Blocks.BLUE_ICE ||
							state2.getBlock() == Blocks.ICE ||
							state2.getBlock() == Blocks.PACKED_ICE ||
							state2.getBlock() == Blocks.FROSTED_ICE;
					boolean isWater = state2.getFluidState().getFluid().isEquivalentTo(Fluids.WATER);
					
					if (isIce || isWater) {
						
						world.setBlockState(pos, Fluids.WATER.getDefaultState().getBlockState());
						
					}
					
				}
				
			}
			
			super.randomTick(world, pos, state, random);
			
		}
		
		@Override
		protected boolean ticksRandomly() {
			return true;
		}
		
	}
	
	public static class Flow extends FluidDestilledWater {

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
		
		@Override
		public void tick(World worldIn, BlockPos pos, FluidState state) {

			super.tick(worldIn, pos, state);
			
			if (state.get(FlowingFluid.FALLING)) {
				FluidState source = worldIn.getFluidState(pos.up());
				if (source.getFluid().isEquivalentTo(this)) worldIn.setBlockState(pos, state.with(HOT, source.get(HOT)).getBlockState());
			} else {

				for (Direction d : Direction.Plane.HORIZONTAL) {
					
					BlockPos blockpos = pos.offset(d);
					FluidState fluid = worldIn.getFluidState(blockpos);
					
					if (fluid.getFluid().isEquivalentTo(this)) {
						
						int level1 = fluid.getLevel();
						int level2 = state.getLevel();
						
						if (level1 > level2) {
							worldIn.setBlockState(pos, state.with(HOT, fluid.get(HOT)).getBlockState());
							break;
						}
						
					}
					
				}
				
			}
			
		}
		
	}

	public FluidState getHot() {
		return this.getDefaultState().with(HOT, true);
	}
	
	public FluidState getCold() {
		return this.getDefaultState().with(HOT, false);
	}
	
}
