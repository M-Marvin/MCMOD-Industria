package de.m_marvin.industria.core.scrollinput.type.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public interface IScrollOverride {
	
	public boolean overridesScroll(UseOnContext context, ItemStack stack);
	public void onScroll(UseOnContext context, double delta);
	
}
