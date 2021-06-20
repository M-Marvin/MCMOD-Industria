package de.industria.fluids;

import java.util.Random;

import de.industria.ModItems;
import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
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
		super("liquid_concrete", ModFluids.LIQUID_CONCRETE, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops().tickRandomly());
		this.setDefaultState(this.stateContainer.getBaseState().with(HARDENED, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(HARDENED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return super.getFluidState(state).with(FluidLiquidConcrete.HARDENED, state.get(HARDENED));
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {

		Vector3d motion = entityIn.getMotion().mul(new Vector3d(0.1F, 0.1F, 0.1F));
		entityIn.setMotion(motion);
		
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		if (random.nextInt(20) == 0) {
			
			FluidState concreteDown = worldIn.getFluidState(pos.down());
			FluidState concreteUp = worldIn.getFluidState(pos.up());
			
			if (!state.get(HARDENED) && (concreteDown.isEmpty() || (concreteDown.getFluid().isEquivalentTo(ModFluids.LIQUID_CONCRETE) ? concreteDown.get(HARDENED) : false))) {
				worldIn.setBlockState(pos, state.with(HARDENED, true));
			}
			
			if (state.get(HARDENED) && concreteDown.isEmpty() && (concreteUp.isEmpty() || (concreteUp.getFluid().isEquivalentTo(ModFluids.LIQUID_CONCRETE) ? concreteUp.get(HARDENED) : false))) {
				
				int height = state.getFluidState().getLevel();
				BlockState concreteBlock = height >= 7 ? ModItems.concrete.getDefaultState() : (height >= 3 ? ModItems.concrete_slab.getDefaultState() : ModItems.concrete_sheet.getDefaultState());
				worldIn.setBlockState(pos, concreteBlock);
				
			}
			
		}
		
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if ((newState.getBlock() == ModItems.liquid_concrete ? newState.get(HARDENED) == false && state.get(HARDENED) == true : false) && !(newState.getFluidState().isSource() && !state.getFluidState().isSource())) {
			worldIn.setBlockState(pos, state);
		}
	}
	
}