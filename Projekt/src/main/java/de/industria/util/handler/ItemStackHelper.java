package de.industria.util.handler;

import net.minecraft.block.BlockState;
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
	
	public static String getBlockStateString(BlockState state) {
		String s = state.toString();
		if (s.startsWith("Block{")) {
			s = s.split("Block\\{")[1];
			if (s.contains("}")) {
				return s.split("\\}").length > 1 ? s.split("\\}")[0] + s.split("\\}")[1] : s.split("\\}")[0];
			}
		}
		return "minecraft:air";
	}
	
}
