package de.redtec.worldgen;

import de.redtec.RedTec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModConfiguredFeatures {
	
	public static final ConfiguredFeature<?, ?> COPPER_ORE = registerConfiguredFeature(new ResourceLocation(RedTec.MODID, "copper_ore"), 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.field_241882_a, 
							RedTec.copper_ore.getDefaultState(),
							10
					)
			)
			.withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(11, 45, 15)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> BAUXIT_STONE_ORE = registerConfiguredFeature(new ResourceLocation(RedTec.MODID, "bauxit_stone_ore"),
			ModFeature.STONE_ORE.withConfiguration(
					new StoneOreFeatureConfig(
							OreFeatureConfig.FillerBlockType.field_241882_a,
							RedTec.bauxit.getDefaultState(),
							RedTec.bauxit_ore.getDefaultState(),
							42
					)
			)
			.withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(45, 256, 3)
					)
			)
	);
	
	public static ConfiguredFeature<?, ?> registerConfiguredFeature(ResourceLocation key, ConfiguredFeature<?, ?> configuredFeature) {
		return Registry.register(WorldGenRegistries.field_243653_e, key, configuredFeature);
	}
	
}
