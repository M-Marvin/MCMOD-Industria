package de.redtec.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.items.ItemBlockAdvancedInfo;
import de.redtec.items.ItemBlockAdvancedInfo.IBlockToolType;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
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
		IBlockToolType info = null;
		int burnTime = -1;
		Item.Properties properties = new Item.Properties().group(group).rarity(rarity);
		
		if (block instanceof IAdvancedBlockInfo) {
			info = ((IAdvancedBlockInfo) block).getBlockInfo();
			Supplier<Callable<ItemStackTileEntityRenderer>> ister = ((IAdvancedBlockInfo) block).getISTER();
			properties = properties.setISTER(ister);
		}
		
		if (block instanceof IBurnableBlock) {
			burnTime = ((IBurnableBlock) block).getBurnTime();
		}
		
		Item blockItem;
		if (burnTime != -1 || info != null) {
			blockItem = new ItemBlockAdvancedInfo(block, properties, info, burnTime);
		} else {
			blockItem = new BlockItem(block, properties);
		}
		
		blockItem.setRegistryName(block.getRegistryName());
		itemsToRegister.add(blockItem);
	}
	
	public static void registerBlock(Block block, ItemGroup group) {
		blocksToRegister.add(block);
		IBlockToolType info = null;
		int burnTime = 0;
		Item.Properties properties = new Item.Properties().group(group);
		
		if (block instanceof IAdvancedBlockInfo) {
			info = ((IAdvancedBlockInfo) block).getBlockInfo();
			Supplier<Callable<ItemStackTileEntityRenderer>> ister = ((IAdvancedBlockInfo) block).getISTER();
			properties = properties.setISTER(ister);
		}
		
		if (block instanceof IBurnableBlock) {
			burnTime = ((IBurnableBlock) block).getBurnTime();
		}
		
		Item blockItem;
		if (burnTime > 0 || info != null) {
			blockItem = new ItemBlockAdvancedInfo(block, properties, info, burnTime);
		} else {
			blockItem = new BlockItem(block, properties);
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
