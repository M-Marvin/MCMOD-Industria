package de.industria.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

public class ItemBurneable extends ItemBase {
	
	private int burnTime;
	
	public ItemBurneable(Item.Properties properties, int burnTime) {
		super(properties);
		this.burnTime = burnTime;
	}
	
	public ItemBurneable(String name, Item.Properties properties, int burnTime) {
		super(name, properties);
		this.burnTime = burnTime;
	}
	
	public ItemBurneable(String name, ItemGroup itemGroup, int burnTime) {
		super(name, new Properties().group(itemGroup));
		this.burnTime = burnTime;
	}
	
	public ItemBurneable(String name, ItemGroup itemGroup, int maxStackSize, int burnTime) {
		super(name, new Properties().group(itemGroup).maxStackSize(maxStackSize));
		this.burnTime = burnTime;
	}

	public ItemBurneable(String name, ItemGroup itemGroup, int maxStackSize, Item containItem, int burnTime) {
		super(name, new Properties().group(itemGroup).maxStackSize(maxStackSize).containerItem(containItem));
		this.burnTime = burnTime;
	}
	
	public ItemBurneable(String name, ItemGroup itemGroup, Rarity rarity, int burnTime) {
		super(name, new Properties().group(itemGroup).rarity(rarity));
		this.burnTime = burnTime;
	}
	
	public ItemBurneable(String name, ItemGroup itemGroup, int maxStackSize, Rarity rarity, int burnTime) {
		super(name, new Properties().group(itemGroup).maxStackSize(maxStackSize).rarity(rarity));
		this.burnTime = burnTime;
	}
	
	@Override
	public int getBurnTime(ItemStack itemStack) {
		return this.burnTime;
	}
	
}
