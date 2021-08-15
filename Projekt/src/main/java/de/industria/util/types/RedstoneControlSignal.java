package de.industria.util.types;

import net.minecraft.item.ItemStack;

public class RedstoneControlSignal {
	
	private ItemStack chanelItem;
	private int power;
	
	public RedstoneControlSignal(ItemStack chanelItem,int power) {
		this.chanelItem = chanelItem;
		this.power = Math.min(15, Math.max(0, power));
	}
	
	public int getPower() {
		return power;
	}
	
	public ItemStack getChanelItem() {
		return chanelItem;
	}

	public boolean isPowered() {
		return this.power > 0;
	}
	
}
