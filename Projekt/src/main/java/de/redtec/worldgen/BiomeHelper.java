package de.redtec.worldgen;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BiomeHelper {
	
	public static void addFeature(Biome biome, GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> feature) {
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = new ArrayList(
				biome.func_242440_e().func_242498_c()
		);
		
		while(biomeFeatures.size() <= decoration.ordinal()) {
			biomeFeatures.add(Lists.newArrayList());
		}
		
		List<Supplier<ConfiguredFeature<?, ?>>> features = new ArrayList<>(biomeFeatures.get(decoration.ordinal()));
		features.add(() -> feature);
		biomeFeatures.set(decoration.ordinal(), features);
		
		ObfuscationReflectionHelper.setPrivateValue(BiomeGenerationSettings.class, biome.func_242440_e(), biomeFeatures, "field_242484_f");
		
	}
	
}
