package de.industria.items;

import com.google.common.collect.ImmutableSet;

import de.industria.Industria;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

public class ItemToolBase extends ToolItem {
	
	protected ToolType toolType;
	
	public ItemToolBase(String name, float attackDamageIn, float attackSpeedIn, ToolType toolType, IItemTier tier, Properties builderIn) {
		super(attackDamageIn, attackSpeedIn, tier, ImmutableSet.of(), builderIn);
		this.setRegistryName(new ResourceLocation(Industria.MODID, name));
		this.toolType = toolType;
	}
	
	@Override
	public boolean isRepairable(ItemStack stack) {
		return false;
	}
	
	public float getDestroySpeed(ItemStack stack, BlockState state) {
	   if (getToolTypes(stack).stream().anyMatch(e -> state.isToolEffective(e))) return efficiency;
	   return state.getBlock().getHarvestTool(state) == this.toolType ? this.efficiency : 1.0F;
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
	      int i = this.getTier().getHarvestLevel();
	      if (blockIn.getHarvestTool() == this.toolType) {
	         return i >= blockIn.getHarvestLevel();
	      }
	      Material material = blockIn.getMaterial();
	      return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
	}
	
}
