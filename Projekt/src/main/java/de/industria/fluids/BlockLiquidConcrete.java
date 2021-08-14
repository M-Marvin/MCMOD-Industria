package de.industria.fluids;

import java.util.Random;

import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockLiquidConcrete extends BlockModFlowingFluid {
	
	public static final BooleanProperty HARDENED = BooleanProperty.create("hardened");
	
	public BlockLiquidConcrete() {
		super("liquid_concrete", ModFluids.LIQUID_CONCRETE, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops().randomTicks());
		this.registerDefaultState(this.stateDefinition.any().setValue(HARDENED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HARDENED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return super.getFluidState(state).setValue(FluidLiquidConcrete.HARDENED, state.getValue(HARDENED));
	}
	
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {

		Vector3d motion = entityIn.getDeltaMovement().multiply(new Vector3d(0.1F, 0.1F, 0.1F));
		entityIn.setDeltaMovement(motion);
		
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		if (random.nextInt(20) == 0) {
			
			FluidState concreteDown = worldIn.getFluidState(pos.below());
			FluidState concreteUp = worldIn.getFluidState(pos.above());
			
			if (!state.getValue(HARDENED) && (concreteDown.isEmpty() || (concreteDown.getType().isSame(ModFluids.LIQUID_CONCRETE) ? concreteDown.getValue(HARDENED) : false))) {
				worldIn.setBlockAndUpdate(pos, state.setValue(HARDENED, true));
			}
			
			if (state.getValue(HARDENED) && concreteDown.isEmpty() && (concreteUp.isEmpty() || (concreteUp.getType().isSame(ModFluids.LIQUID_CONCRETE) ? concreteUp.getValue(HARDENED) : false))) {
				
				int height = state.getFluidState().getAmount();
				BlockState concreteBlock = height >= 7 ? ModItems.concrete.defaultBlockState() : (height >= 3 ? ModItems.concrete_slab.defaultBlockState() : ModItems.concrete_sheet.defaultBlockState());
				worldIn.setBlockAndUpdate(pos, concreteBlock);
				
			}
			
		}
		
	}
	
	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if ((newState.getBlock() == ModItems.liquid_concrete ? newState.getValue(HARDENED) == false && state.getValue(HARDENED) == true : false) && !(newState.getFluidState().isSource() && !state.getFluidState().isSource())) {
			worldIn.setBlockAndUpdate(pos, state);
		}
	}
	
}