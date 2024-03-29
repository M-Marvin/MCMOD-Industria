package de.industria.fluids;

import de.industria.fluids.util.BlockModFlowingFluid;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockOreSolution extends BlockModFlowingFluid {
	
	public BlockOreSolution(String name, FlowingFluid fluid) {
		super(name, fluid, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
	}
	
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (entityIn instanceof LivingEntity) {
			
			EffectInstance effect = ((LivingEntity) entityIn).getEffect(Effects.POISON);
			if (effect != null ? effect.getDuration() < 100 : true) ((LivingEntity) entityIn).addEffect(new EffectInstance(Effects.POISON, 200, 0));
			
		}
		
	}
	
}
