package de.industria.items;

import de.industria.Industria;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;

public class ItemBase extends Item {
	
	public ItemBase(Item.Properties properties) {
		super(properties);
	}
	
	public ItemBase(String name, Item.Properties properties) {
		super(properties);
		this.setRegistryName(Industria.MODID, name);
	}
	
	public ItemBase(String name, ItemGroup itemGroup) {
		super(new Properties().group(itemGroup));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public ItemBase(String name, ItemGroup itemGroup, int maxStackSize) {
		super(new Properties().group(itemGroup).maxStackSize(maxStackSize));
		this.setRegistryName(Industria.MODID, name);
	}

	public ItemBase(String name, ItemGroup itemGroup, int maxStackSize, Item containItem) {
		super(new Properties().group(itemGroup).maxStackSize(maxStackSize).containerItem(containItem));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public ItemBase(String name, ItemGroup itemGroup, Rarity rarity) {
		super(new Properties().group(itemGroup).rarity(rarity));
		this.setRegistryName(Industria.MODID, name);
	}
	
	public ItemBase(String name, ItemGroup itemGroup, int maxStackSize, Rarity rarity) {
		super(new Properties().group(itemGroup).maxStackSize(maxStackSize).rarity(rarity));
		this.setRegistryName(Industria.MODID, name);
	}
	
}
