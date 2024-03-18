package de.m_marvin.industria.core.util.items;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface ITooltipAdditionsModifier {
	
	public boolean showTooltipType(String tooltipTypeName);
	public default void addAdditionsTooltip(List<Component> tooltips, ItemStack item) {}
	
}
