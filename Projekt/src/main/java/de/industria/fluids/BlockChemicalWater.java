package de.industria.fluids;

import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockChemicalWater extends BlockModFlowingFluid {
	
	public BlockChemicalWater() {
		super("chemical_water", ModFluids.CHEMICAL_WATER, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn instanceof LivingEntity) {

			EffectInstance effect = ((LivingEntity) entityIn).getActivePotionEffect(Effects.POISON);
			if (effect != null ? effect.getDuration() < 100 : true) ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.POISON, 200, 1));
			effect = ((LivingEntity) entityIn).getActivePotionEffect(Effects.NAUSEA);
			if (effect != null ? effect.getDuration() < 100 : true) ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.NAUSEA, 400, 2));
			
		}
		
	}
	
}