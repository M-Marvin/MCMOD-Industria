package de.redtec.typeregistys;

import de.redtec.RedTec;
import de.redtec.blocks.BlockJigsaw.JigsawType;
import de.redtec.worldgen.JigsawFeatureConfig;
import de.redtec.worldgen.StoneOreFeatureConfig;
import de.redtec.worldgen.placements.HorizontalSpreadPlacementConfig;
import de.redtec.worldgen.placements.SimpleOrePlacementConfig;
import de.redtec.worldgen.placements.VerticalOffsetPlacementConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModConfiguredFeatures {
	
	// Ores
	public static final ConfiguredFeature<?, ?> COPPER_ORE = registerConfiguredFeature("copper_ore", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, 
							RedTec.copper_ore.getDefaultState(),
							14
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(11, 45, 14)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> NICKEL_ORE = registerConfiguredFeature("nickel_ore", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, 
							RedTec.nickel_ore.getDefaultState(),
							8
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(0, 64, 6)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> TIN_ORE = registerConfiguredFeature("tin_ore", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, 
							RedTec.tin_ore.getDefaultState(),
							12
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(0, 48, 6)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> TIN_ORE_EXTRA = registerConfiguredFeature("tin_ore_extra", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, 
							RedTec.tin_ore.getDefaultState(),
							12
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(0, 48, 6)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> SILVER_ORE = registerConfiguredFeature("silver_ore", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, 
							RedTec.silver_ore.getDefaultState(),
							6
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(16, 40, 1)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> SILVER_ORE_EXTRA = registerConfiguredFeature("silver_ore_extra", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, 
							RedTec.silver_ore.getDefaultState(),
							3
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(16, 40, 1)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> PALLADIUM_ORE = registerConfiguredFeature("palladium_ore", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, 
							RedTec.palladium_ore.getDefaultState(),
							2
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(0, 16, 1)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> PALLADIUM_ORE_EXTRA = registerConfiguredFeature("palladium_ore_extra", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, 
							RedTec.palladium_ore.getDefaultState(),
							2
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(0, 16, 1)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> SULFUR_ORE = registerConfiguredFeature("sulfur_ore", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_NETHER, 
							RedTec.sulfur_ore.getDefaultState(),
							10
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(0, 64, 15)
					)
			)
	);
	
	public static final ConfiguredFeature<?, ?> BAUXIT_STONE_ORE = registerConfiguredFeature("bauxit_stone_ore",
			ModFeature.STONE_ORE.withConfiguration(
					new StoneOreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
							RedTec.bauxit.getDefaultState(),
							RedTec.bauxit_ore.getDefaultState(),
							42
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(45, 256, 4)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> WOLFRAM_STONE_ORE = registerConfiguredFeature("wolfram_stone_ore",
			ModFeature.STONE_ORE.withConfiguration(
					new StoneOreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
							RedTec.wolframit.getDefaultState(),
							RedTec.wolframit_ore.getDefaultState(),
							42
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(0, 32, 1)
					)
			).withPlacement(
					Placement.CHANCE.configure(
							new ChanceConfig(5)
					)
			)
	);
	
	public static final ConfiguredFeature<?, ?> OIL_DEPOT = registerConfiguredFeature("oil_depot",
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
							ModFluids.RAW_OIL.getDefaultState().getBlockState(),
							128
					)
			).withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(0, 30, 6)
					)
			).withPlacement(
					Placement.CHANCE.configure(
							new ChanceConfig(180)
					)
			)
	);
	
	// Trees
	public static final ConfiguredFeature<?, ?> RUBBER_TREE = registerConfiguredFeature("rubber_tree",
			ModFeature.JIGSAW_FEATURE.withConfiguration(new JigsawFeatureConfig(
					new TagMatchRuleTest(ModTags.DIRT), Direction.NORTH, JigsawType.VERTICAL_UP, 
					new ResourceLocation(RedTec.MODID, "nature/rubber_tree"), new ResourceLocation(RedTec.MODID, "tree_log"),
					Blocks.DIRT, false, 1, 1)
			).withPlacement(
					ModPlacement.VERTICAL_OFFSET.configure(
							new VerticalOffsetPlacementConfig(-1)
					)
			).withPlacement(
					ModPlacement.HORIZONTAL_SPREAD.configure(
							new HorizontalSpreadPlacementConfig(2)
					)
			).withPlacement(
					Placement.HEIGHTMAP_WORLD_SURFACE.configure(new NoPlacementConfig())
			).withPlacement(
					Placement.CHANCE.configure(new ChanceConfig(10))
			)
	);
	
	public static ConfiguredFeature<?, ?> registerConfiguredFeature(String key, ConfiguredFeature<?, ?> configuredFeature) {
		return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(RedTec.MODID, key), configuredFeature);
	}
	
}
