package de.industria.fluids;

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

public class BlockRawOil extends BlockModFlowingFluid {
	
	public BlockRawOil() {
		super("raw_oil", ModFluids.RAW_OIL, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
	}
	
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		double d0 = entityIn.getEyeY() - (double)0.11111111F;
		BlockPos blockpos = new BlockPos(entityIn.getX(), d0, entityIn.getZ());
		FluidState fluidstate = worldIn.getFluidState(blockpos);
		
		if (ModTags.RAW_OIL.contains(fluidstate.getType()) && entityIn instanceof LivingEntity) {
			// TODO
			((LivingEntity) entityIn).addEffect(new EffectInstance(Effects.BLINDNESS, 40, 1));
		}
		
	}
	
}