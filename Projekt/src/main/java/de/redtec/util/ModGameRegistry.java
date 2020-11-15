package de.redtec.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.items.ItemBlockAdvancedInfo;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;

public class ModGameRegistry {
	
	private static List<Block> blocksToRegister = new ArrayList<Block>();
	private static List<Item> itemsToRegister = new ArrayList<Item>();
	
	public static void registerItem(Item item) {
		itemsToRegister.add(item);
	}
	
	public static void registerBlock(Block block, ItemGroup group, Rarity rarity) {
		blocksToRegister.add(block);
		BlockItem blockItem;
		if (block instanceof IAdvancedBlockInfo) {
			List<ITextComponent> info = ((IAdvancedBlockInfo) block).getBlockInfo();
			Supplier<Callable<ItemStackTileEntityRenderer>> ister = ((IAdvancedBlockInfo) block).getISTER();
			blockItem = group != null ? new ItemBlockAdvancedInfo(block, new Item.Properties().group(group).rarity(rarity).setISTER(ister), info) : new ItemBlockAdvancedInfo(block, new Item.Properties().rarity(rarity).setISTER(ister), info);
		} else {
			blockItem = group != null ? new BlockItem(block, new Item.Properties().group(group).rarity(rarity)) : new BlockItem(block, new Item.Properties().rarity(rarity));
		}
		blockItem.setRegistryName(block.getRegistryName());
		itemsToRegister.add(blockItem);
	}
	
	public static void registerBlock(Block block, ItemGroup group) {
		blocksToRegister.add(block);
		Item blockItem;
		if (block instanceof IAdvancedBlockInfo) {
			List<ITextComponent> info = ((IAdvancedBlockInfo) block).getBlockInfo();
			Supplier<Callable<ItemStackTileEntityRenderer>> ister = ((IAdvancedBlockInfo) block).getISTER();
			blockItem = group != null ? new ItemBlockAdvancedInfo(block, new Item.Properties().group(group).setISTER(ister), info) : new ItemBlockAdvancedInfo(block, new Item.Properties(), info);
		} else {
			blockItem = group != null ? new BlockItem(block, new Item.Properties().group(group)) : new BlockItem(block, new Item.Properties());
		}
		blockItem.setRegistryName(block.getRegistryName());
		itemsToRegister.add(blockItem);
	}
	
	public static void registerTechnicalBlock(Block block) {
		blocksToRegister.add(block);
	}
	
	public static Item[] getItemsToRegister() {
		return itemsToRegister.toArray(new Item[] {});
	}
	
	public static Block[] getBlocksToRegister() {
		return blocksToRegister.toArray(new Block[] {});
	}
	
}
