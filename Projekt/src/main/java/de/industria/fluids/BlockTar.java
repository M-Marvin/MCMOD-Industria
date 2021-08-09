package de.industria.fluids;

import java.util.Random;

import de.industria.ModItems;
import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockTar extends BlockModFlowingFluid {
	
	public BlockTar() {
		super("tar", ModFluids.TAR, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
	}
	
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		double d0 = entityIn.getEyeY() - (double)0.11111111F;
		BlockPos blockpos = new BlockPos(entityIn.getX(), d0, entityIn.getZ());
		FluidState fluidstate = worldIn.getFluidState(blockpos);
		
		if (ModTags.TAR.contains(fluidstate.getType()) && entityIn instanceof LivingEntity) {
			((LivingEntity) entityIn).addEffect(new EffectInstance(Effects.BLINDNESS, 40, 1));
		}
		
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if (rand.nextInt(100) == 0 && !world.getFluidState(pos.above()).is(ModTags.TAR) && state.getFluidState().isSource()) {
			world.setBlockAndUpdate(pos, ModItems.tar_crust.defaultBlockState());
		}
	}
	
}