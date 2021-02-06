package de.redtec.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ItemStackHelper {
	
	public static boolean isItemStackItemEqual(ItemStack stack, ItemStack other, boolean whenEmpty) {
		
		if (stack == null) return whenEmpty;
		
        if (stack.isEmpty())
            return false;
        else if (other.isEmpty())
        	return whenEmpty;
        else
            return !other.isEmpty() && stack.getItem() == other.getItem() &&
            (other.getDisplayName().getUnformattedComponentText().equals(stack.getDisplayName().getUnformattedComponentText()) || stack.getDisplayName().getUnformattedComponentText().length() == 0);
		
	}
	
	public static boolean canMergeRecipeStacks(ItemStack stack, ItemStack recipeItem) {
		
		if (stack.isEmpty()) return true;
		if (stack.getItem() == recipeItem.getItem() && stack.getMaxStackSize() - stack.getCount() >= recipeItem.getCount()) return true;
		return false;
		
	}
	
	public static boolean canMergeRecipeFluidStacks(FluidStack fluidStack, FluidStack recipeStack, int maxFluidStorage) {
		
		if (fluidStack.isEmpty()) return true;
		if (fluidStack.getFluid() == recipeStack.getFluid() && fluidStack.getAmount() + recipeStack.getAmount() <= maxFluidStorage) return true;
		return false;
		
	}
	
}
