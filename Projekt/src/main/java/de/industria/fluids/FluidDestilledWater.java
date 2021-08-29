package de.industria.fluids;

import java.util.Random;

import de.industria.Industria;
import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModItems;
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
		this.registerDefaultState(this.stateDefinition.any().setValue(HOT, false));
	}
	
	@Override
	protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
		builder.add(HOT);
		super.createFluidStateDefinition(builder);
	}
	
	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_DESTILLED_WATER;
	}

	@Override
	public Fluid getSource() {
		return ModFluids.DESTILLED_WATER;
	}

	@Override
	protected boolean canConvertToSource() {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(IWorld worldIn, BlockPos pos, BlockState state) {}

	@Override
	protected int getSlopeFindDistance(IWorldReader worldIn) {
		return 4;
	}

	@Override
	protected int getDropOff(IWorldReader worldIn) {
		return 1;
	}

	@Override
	public Item getBucket() {
		return ModItems.destilled_water_bucket;
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
		return ModItems.destilled_water.defaultBlockState().setValue(BlockModFlowingFluid.LEVEL, getLegacyLevel(state)).setValue(BlockDestilledWater.HOT, state.getValue(HOT));
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
				new ResourceLocation(Industria.MODID, "block/destilled_water_still"), 
				new ResourceLocation(Industria.MODID, "block/destilled_water_flow"))
					.overlay(new ResourceLocation(Industria.MODID, "block/destilled_water_overlay"))
					.build(this);
	}
	
	@Override
	public boolean isSame(Fluid fluidIn) {
		return fluidIn == ModFluids.DESTILLED_WATER || fluidIn == ModFluids.FLOWING_DESTILLED_WATER;
	}

	@Override
	protected void randomTick(World world, BlockPos pos, FluidState state, Random random) {

		if (random.nextInt(80) == 0 && state.getValue(HOT)) {
			
			world.setBlockAndUpdate(pos, state.setValue(HOT, false).createLegacyBlock());
			
		}
		
	}
	
	public static class Still extends FluidDestilledWater {

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		protected void randomTick(World world, BlockPos pos, FluidState state, Random random) {
			
			for (Direction d : Direction.values()) {
				
				BlockState state2 = world.getBlockState(pos.relative(d));
				boolean isIce =
						state2.getBlock() == Blocks.BLUE_ICE ||
						state2.getBlock() == Blocks.ICE ||
						state2.getBlock() == Blocks.PACKED_ICE ||
						state2.getBlock() == Blocks.FROSTED_ICE;
				boolean isWater = state2.getFluidState().getType().isSame(Fluids.WATER);
				
				if (isIce || isWater) {
					
					world.setBlockAndUpdate(pos, Fluids.WATER.defaultFluidState().createLegacyBlock());
					
				}
				
			}
			
			super.randomTick(world, pos, state, random);
			
		}
		
		@Override
		protected boolean isRandomlyTicking() {
			return true;
		}
		
	}
	
	public static class Flow extends FluidDestilledWater {

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
		
		@Override
		public void tick(World worldIn, BlockPos pos, FluidState state) {

			super.tick(worldIn, pos, state);
			
			if (state.getValue(FlowingFluid.FALLING)) {
				FluidState source = worldIn.getFluidState(pos.above());
				if (source.getType().isSame(this)) worldIn.setBlockAndUpdate(pos, state.setValue(HOT, source.getValue(HOT)).createLegacyBlock());
			} else {

				for (Direction d : Direction.Plane.HORIZONTAL) {
					
					BlockPos blockpos = pos.relative(d);
					FluidState fluid = worldIn.getFluidState(blockpos);
					
					if (fluid.getType().isSame(this)) {
						
						int level1 = fluid.getAmount();
						int level2 = state.getAmount();
						
						if (level1 > level2) {
							worldIn.setBlockAndUpdate(pos, state.setValue(HOT, fluid.getValue(HOT)).createLegacyBlock());
							break;
						}
						
					}
					
				}
				
			}
			
		}
		
	}

	public FluidState getHot() {
		return this.defaultFluidState().setValue(HOT, true);
	}
	
	public FluidState getCold() {
		return this.defaultFluidState().setValue(HOT, false);
	}
	
}
