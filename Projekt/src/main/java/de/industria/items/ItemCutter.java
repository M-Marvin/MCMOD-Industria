package de.industria.items;

import de.industria.Industria;
import de.industria.typeregistys.ModToolType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;

public class ItemCutter extends ItemToolBase {
	
	public ItemCutter() {
		super("cutter", 0.5F, 0.4F, ModToolType.CUTTER, ItemTier.IRON, new Properties().tab(Industria.TOOLS).stacksTo(1).defaultDurability(240));
	}
	
	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		int dammage = itemStack.getDamageValue() + 1;
		if (dammage > this.getMaxDamage(itemStack)) {
			return ItemStack.EMPTY.copy();
		} else {
			ItemStack itemStack2 = itemStack.copy();
			itemStack2.setDamageValue(dammage);
			return itemStack2;
		}
	}
	
	@Override
	public boolean isRepairable(ItemStack stack) {
		return true;
	}
	
}
