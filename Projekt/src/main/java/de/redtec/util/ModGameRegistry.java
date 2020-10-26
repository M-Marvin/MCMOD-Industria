package de.redtec.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.base.Supplier;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;

public class ModGameRegistry {
	
	private static List<Block> blocksToRegister = new ArrayList<Block>();
	private static List<Item> itemsToRegister = new ArrayList<Item>();
	
	public static void registerItem(Item item) {
		itemsToRegister.add(item);
	}
	
	public static void registerBlock(Block block, ItemGroup group, Rarity rarity) {
		blocksToRegister.add(block);
		Item blockItem = group != null ? new BlockItem(block, new Item.Properties().group(group).rarity(rarity)) : new BlockItem(block, new Item.Properties().rarity(rarity));
		blockItem.setRegistryName(block.getRegistryName());
		itemsToRegister.add(blockItem);
	}
	
	public static void registerBlock(Block block, ItemGroup group) {
		blocksToRegister.add(block);
		Item blockItem = group != null ? new BlockItem(block, new Item.Properties().group(group)) : new BlockItem(block, new Item.Properties());
		blockItem.setRegistryName(block.getRegistryName());
		itemsToRegister.add(blockItem);
	}
	
	public static void registerBlock(Block block, ItemGroup group, Supplier<Callable<ItemStackTileEntityRenderer>> ister) {
		blocksToRegister.add(block);
		Item blockItem = new BlockItem(block, new Item.Properties().group(group).setISTER(ister));
		blockItem.setRegistryName(block.getRegistryName());
		itemsToRegister.add(blockItem);
	}
	
	public static void registerTechnicalBlock(Block block, ItemGroup group) {
		blocksToRegister.add(block);
	}
	
	public static Item[] getItemsToRegister() {
		return itemsToRegister.toArray(new Item[] {});
	}
	
	public static Block[] getBlocksToRegister() {
		return blocksToRegister.toArray(new Block[] {});
	}
	
}
