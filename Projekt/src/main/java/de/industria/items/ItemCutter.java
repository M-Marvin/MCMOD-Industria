package de.industria.items;

import de.industria.Industria;
import de.industria.typeregistys.ModToolType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;

public class ItemCutter extends ItemToolBase {
	
	public ItemCutter() {
		super("cutter", 0.5F, 0.4F, ModToolType.CUTTER, ItemTier.IRON, new Properties().group(Industria.TOOLS).maxStackSize(1).defaultMaxDamage(240));
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
