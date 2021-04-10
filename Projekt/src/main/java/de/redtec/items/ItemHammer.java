package de.redtec.items;

import de.redtec.RedTec;
import de.redtec.typeregistys.ModToolType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;

public class ItemHammer extends ItemToolBase {

	public ItemHammer() {
		super("hammer", 1.5F, 0.5F, ModToolType.HAMMER, ItemTier.IRON, new Properties().group(RedTec.TOOLS).maxStackSize(1).defaultMaxDamage(320));
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
