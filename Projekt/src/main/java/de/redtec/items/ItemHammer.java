package de.redtec.items;

import de.redtec.RedTec;
import net.minecraft.item.ItemStack;

public class ItemHammer extends ItemBase {

	public ItemHammer() {
		super("hammer", new Properties().group(RedTec.TOOLS).maxStackSize(1).defaultMaxDamage(120));
	}
	
	@Override
	public boolean hasContainerItem() {
		return true;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		int dammage = itemStack.getDamage() + 1;
		if (dammage > this.getMaxDamage(itemStack)) {
			return ItemStack.EMPTY.copy();
		} else {
			ItemStack itemStack2 = itemStack.copy();
			itemStack2.setDamage(dammage);
			return itemStack2;
		}
	}
	
	@Override
	public boolean isRepairable(ItemStack stack) {
		return true;
	}
	
}
