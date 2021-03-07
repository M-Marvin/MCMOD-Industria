package de.redtec.util;

import java.util.ArrayList;
import java.util.HashMap;
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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.registries.ForgeRegistries;

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
			blockItem = group != null ? new BlockItem(block, new Item.Properties().group(group).rarity(rarity).maxStackSize(((IAdvancedBlockInfo) block).getStackSize())) : new BlockItem(block, new Item.Properties().rarity(rarity));
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
			blockItem = group != null ? new ItemBlockAdvancedInfo(block, new Item.Properties().group(group).setISTER(ister).maxStackSize(((IAdvancedBlockInfo) block).getStackSize()), info) : new ItemBlockAdvancedInfo(block, new Item.Properties(), info);
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
	
	private static HashMap<ResourceLocation, HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>> featuresToRegister = new HashMap<ResourceLocation, HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>>();
	
	@SafeVarargs
	public static void addFeatureToBiomes(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> feature, RegistryKey<Biome>... biomes) {
		for (RegistryKey<Biome> biome : biomes) {
			HashMap<Decoration, List<ConfiguredFeature<?, ?>>> featureMap = featuresToRegister.getOrDefault(biome.getLocation(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>());
			List<ConfiguredFeature<?, ?>> features = featureMap.getOrDefault(decoration, new ArrayList<ConfiguredFeature<?, ?>>());
			features.add(feature);
			featureMap.put(decoration, features);
			featuresToRegister.put(biome.getRegistryName(), featureMap);
		}
	}
	
	public static void addFeatureToBiomes(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> feature, Biome.Category category) {
		for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
			if (biome.getCategory() == category) {
				HashMap<Decoration, List<ConfiguredFeature<?, ?>>> featureMap = featuresToRegister.getOrDefault(biome.getRegistryName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>());
				List<ConfiguredFeature<?, ?>> features = featureMap.getOrDefault(decoration, new ArrayList<ConfiguredFeature<?, ?>>());
				features.add(feature);
				featureMap.put(decoration, features);
				featuresToRegister.put(biome.getRegistryName(), featureMap);
			}
		}
	}
	
	public static void addFeatureToOverworldBiomes(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> feature) {
		for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
			if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
				HashMap<Decoration, List<ConfiguredFeature<?, ?>>> featureMap = featuresToRegister.getOrDefault(biome.getRegistryName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>());
				List<ConfiguredFeature<?, ?>> features = featureMap.getOrDefault(decoration, new ArrayList<ConfiguredFeature<?, ?>>());
				features.add(feature);
				featureMap.put(decoration, features);
				featuresToRegister.put(biome.getRegistryName(), featureMap);
			}
		}
	}
	
	public static HashMap<ResourceLocation, HashMap<Decoration, List<ConfiguredFeature<?, ?>>>> getFeaturesToRegister() {
		return featuresToRegister;
	}
	
}
