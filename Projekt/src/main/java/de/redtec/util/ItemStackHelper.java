package de.redtec.util;

import net.minecraft.item.ItemStack;

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
	
}
