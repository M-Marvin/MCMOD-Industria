package de.industria.util.types;

import net.minecraft.item.ItemStack;

public class RedstoneControlSignal {
	
	private ItemStack chanelItem;
	private boolean powered;
	
	public RedstoneControlSignal(ItemStack chanelItem, boolean powered) {
		this.chanelItem = chanelItem;
		this.powered = powered;
	}
	
	public boolean isPowered() {
		return powered;
	}
	
	public ItemStack getChanelItem() {
		return chanelItem;
	}
	
}
