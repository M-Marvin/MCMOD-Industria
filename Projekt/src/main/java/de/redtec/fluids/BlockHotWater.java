package de.redtec.fluids;

import de.redtec.fluids.util.BlockModFlowingFluid;
import de.redtec.registys.ModDamageSource;
import de.redtec.registys.ModTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHotWater extends BlockModFlowingFluid {
	
	public BlockHotWater(String name, FlowingFluid fluidIn) {
		super(name, fluidIn, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn.isLiving()) {
			
			if (((LivingEntity) entityIn).isPotionActive(Effects.FIRE_RESISTANCE)) return;
			
			boolean hasLeatherLeggings = false;
			boolean hasLeatherBoots = false;
			Iterable<ItemStack> armor = entityIn.getArmorInventoryList();
			for (ItemStack item : armor) {
				if (item.getItem() == Items.LEATHER_BOOTS) hasLeatherBoots = true;
				if (item.getItem() == Items.LEATHER_LEGGINGS) hasLeatherLeggings = true;
			}
			boolean isHeadInFluid = entityIn.areEyesInFluid(ModTags.HOT_WATER);
			
			int dammage = hasLeatherBoots && hasLeatherLeggings ? 0 : 1;
			if (isHeadInFluid) dammage += 1;
			
			if (dammage > 0 )entityIn.attackEntityFrom(ModDamageSource.HOT_FLUID, dammage);
			
		}
		
	}
	
}
