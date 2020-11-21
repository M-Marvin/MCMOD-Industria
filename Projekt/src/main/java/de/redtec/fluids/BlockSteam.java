package de.redtec.fluids;

import de.redtec.fluids.util.BlockGasFluid;
import de.redtec.fluids.util.GasFluid;
import de.redtec.registys.ModDamageSource;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSteam extends BlockGasFluid {
	
	public BlockSteam(String name, GasFluid fluid) {
		super(name, fluid, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn.isLiving()) {

			if (((LivingEntity) entityIn).isPotionActive(Effects.FIRE_RESISTANCE)) return;
			
			entityIn.attackEntityFrom(ModDamageSource.HOT_STEAM, 1F);
			
		}
		
	}
	
}
