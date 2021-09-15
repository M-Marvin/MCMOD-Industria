package de.industria.items;

import com.google.common.collect.ImmutableSet;

import de.industria.Industria;
import net.minecraft.block.BlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

public class ItemToolBase extends ToolItem {
	
	public ItemToolBase(String name, float attackDamageIn, float attackSpeedIn, ToolType toolType, IItemTier tier, Properties builderIn) {
		super(attackDamageIn, attackSpeedIn, tier, ImmutableSet.of(), builderIn.addToolType(toolType, 1));
		this.setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
	@Override
	public boolean isRepairable(ItemStack stack) {
		return false;
	}
	
	public float getDestroySpeed(ItemStack stack, BlockState state) {
	   if (getToolTypes(stack).stream().anyMatch(e -> state.isToolEffective(e))) return speed;
	   return isCorrectToolForDrops(state) ? this.speed : 1.0F;
	}
	
	@Override
	public boolean isCorrectToolForDrops(BlockState blockIn) {
	      int i = this.getTier().getLevel();
	      if (this.getToolTypes(new ItemStack(this, 1)).contains(blockIn.getHarvestTool())) {
	         return i >= blockIn.getHarvestLevel();
	      }
	      return false;
	}
	
}
