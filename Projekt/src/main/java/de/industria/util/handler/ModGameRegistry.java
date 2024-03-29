package de.industria.util.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo;
import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBBurnableBlock;
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
		if (itemsToRegister.contains(item)) throw new RuntimeException("Dupplicated Registry Entry: " + item.getRegistryName());
		itemsToRegister.add(item);
	}
	
	public static void registerBlock(Block block, ItemGroup group, Rarity rarity) {
		if (blocksToRegister.contains(block)) throw new RuntimeException("Dupplicated Registry Entry: " + block.getRegistryName());
		blocksToRegister.add(block);
		IBlockToolType info = null;
		int burnTime = -1;
		int stackSize = 64;
		Item.Properties properties = new Item.Properties().tab(group).rarity(rarity);
		
		if (block instanceof IBAdvancedBlockInfo) {
			info = ((IBAdvancedBlockInfo) block).getBlockInfo();
			Supplier<Callable<ItemStackTileEntityRenderer>> ister = ((IBAdvancedBlockInfo) block).getISTER();
			properties = properties.setISTER(ister);
			stackSize = ((IBAdvancedBlockInfo) block).getStackSize();
		}
		
		if (block instanceof IBBurnableBlock) {
			burnTime = ((IBBurnableBlock) block).getBurnTime();
		}
		
		Item blockItem;
		if (burnTime != -1 || info != null || stackSize != 64) {
			blockItem = new ItemBlockAdvancedInfo(block, properties.stacksTo(stackSize), info, burnTime);
		} else {
			blockItem = new BlockItem(block, properties);
		}
		
		blockItem.setRegistryName(block.getRegistryName());
		itemsToRegister.add(blockItem);
	}
	
	public static void registerBlock(Block block, ItemGroup group) {
		if (blocksToRegister.contains(block)) throw new RuntimeException("Dupplicated Registry Entry: " + block.getRegistryName());
		blocksToRegister.add(block);
		IBlockToolType info = null;
		int burnTime = 0;
		int stackSize = 64;
		Item.Properties properties = new Item.Properties().tab(group);
		
		if (block instanceof IBAdvancedBlockInfo) {
			info = ((IBAdvancedBlockInfo) block).getBlockInfo();
			Supplier<Callable<ItemStackTileEntityRenderer>> ister = ((IBAdvancedBlockInfo) block).getISTER();
			properties = properties.setISTER(ister);
			stackSize = ((IBAdvancedBlockInfo) block).getStackSize();
		}
		
		if (block instanceof IBBurnableBlock) {
			burnTime = ((IBBurnableBlock) block).getBurnTime();
		}
		
		Item blockItem;
		if (burnTime > 0 || info != null || stackSize != 64) {
			blockItem = new ItemBlockAdvancedInfo(block, properties.stacksTo(stackSize), info, burnTime);
		} else {
			blockItem = new BlockItem(block, properties);
		}
		
		blockItem.setRegistryName(block.getRegistryName());
		itemsToRegister.add(blockItem);
	}
	
	public static void registerTechnicalBlock(Block block) {
		if (blocksToRegister.contains(block)) throw new RuntimeException("Dupplicated Registry Entry: " + block.getRegistryName());
		blocksToRegister.add(block);
	}
	
	public static Item[] getItemsToRegister() {
		return itemsToRegister.toArray(new Item[] {});
	}
	
	public static Block[] getBlocksToRegister() {
		return blocksToRegister.toArray(new Block[] {});
	}
	
	private static HashMap<ResourceLocation, HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>> featuresToRegister = new HashMap<ResourceLocation, HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>>();
	private static HashMap<ResourceLocation, HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>> featuresToDeactivate = new HashMap<ResourceLocation, HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>>();;
	
	@SafeVarargs
	public static void addFeatureToBiomes(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> feature, RegistryKey<Biome>... biomes) {
		for (RegistryKey<Biome> biome : biomes) {
			HashMap<Decoration, List<ConfiguredFeature<?, ?>>> featureMap = featuresToRegister.getOrDefault(biome.location(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>());
			List<ConfiguredFeature<?, ?>> features = featureMap.getOrDefault(decoration, new ArrayList<ConfiguredFeature<?, ?>>());
			features.add(feature);
			featureMap.put(decoration, features);
			featuresToRegister.put(biome.getRegistryName(), featureMap);
		}
	}
	
	public static void addFeatureToBiomes(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> feature, Biome.Category category) {
		for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
			if (biome.getBiomeCategory() == category) {
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
			if (biome.getBiomeCategory() != Biome.Category.NETHER && biome.getBiomeCategory() != Biome.Category.THEEND) {
				HashMap<Decoration, List<ConfiguredFeature<?, ?>>> featureMap = featuresToRegister.getOrDefault(biome.getRegistryName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>());
				List<ConfiguredFeature<?, ?>> features = featureMap.getOrDefault(decoration, new ArrayList<ConfiguredFeature<?, ?>>());
				features.add(feature);
				featureMap.put(decoration, features);
				featuresToRegister.put(biome.getRegistryName(), featureMap);
			}
		}
	}

	@SafeVarargs
	public static void addFeatureToRemove(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> feature, RegistryKey<Biome>... biomes) {
		for (RegistryKey<Biome> biome : biomes) {
			HashMap<Decoration, List<ConfiguredFeature<?, ?>>> featureMap = featuresToDeactivate.getOrDefault(biome.location(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>());
			List<ConfiguredFeature<?, ?>> features = featureMap.getOrDefault(decoration, new ArrayList<ConfiguredFeature<?, ?>>());
			features.add(feature);
			featureMap.put(decoration, features);
			featuresToDeactivate.put(biome.getRegistryName(), featureMap);
		}
	}
	
	public static void addFeatureToRemove(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> feature, Biome.Category category) {
		for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
			if (biome.getBiomeCategory() == category) {
				HashMap<Decoration, List<ConfiguredFeature<?, ?>>> featureMap = featuresToDeactivate.getOrDefault(biome.getRegistryName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>());
				List<ConfiguredFeature<?, ?>> features = featureMap.getOrDefault(decoration, new ArrayList<ConfiguredFeature<?, ?>>());
				features.add(feature);
				featureMap.put(decoration, features);
				featuresToDeactivate.put(biome.getRegistryName(), featureMap);
			}
		}
	}
	
	public static void addFeatureToRemoveInOverworld(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> feature) {
		for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
			if (biome.getBiomeCategory() != Biome.Category.NETHER && biome.getBiomeCategory() != Biome.Category.THEEND) {
				HashMap<Decoration, List<ConfiguredFeature<?, ?>>> featureMap = featuresToDeactivate.getOrDefault(biome.getRegistryName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>());
				List<ConfiguredFeature<?, ?>> features = featureMap.getOrDefault(decoration, new ArrayList<ConfiguredFeature<?, ?>>());
				features.add(feature);
				featureMap.put(decoration, features);
				featuresToDeactivate.put(biome.getRegistryName(), featureMap);
			}
		}
	}
	
	public static HashMap<ResourceLocation, HashMap<Decoration, List<ConfiguredFeature<?, ?>>>> getFeaturesToRegister() {
		return featuresToRegister;
	}
	
	public static HashMap<ResourceLocation, HashMap<Decoration, List<ConfiguredFeature<?, ?>>>> getFeaturesToDeactivate() {
		return featuresToDeactivate;
	}
	
}
