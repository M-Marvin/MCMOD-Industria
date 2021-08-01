package de.industria.typeregistys;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.blocks.BlockJigsaw.JigsawType;
import de.industria.worldgen.CrystalOreFeatureConfig;
import de.industria.worldgen.JigsawFeatureConfig;
import de.industria.worldgen.LakeFeatureConfig;
import de.industria.worldgen.StoneOreFeatureConfig;
import de.industria.worldgen.placements.HorizontalSpreadPlacementConfig;
import de.industria.worldgen.placements.ModChancePlacementConfig;
import de.industria.worldgen.placements.SimpleOrePlacementConfig;
import de.industria.worldgen.placements.VerticalOffsetPlacementConfig;
import de.industria.worldgen.placements.VerticalSpreadPlacementConfig;
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
	
	public static final ConfiguredFeature<?, ?> COPPER_ORE = registerConfiguredFeature("copper_ore", 
			Feature.ORE.configured(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE, 
							ModItems.copper_ore.defaultBlockState(),
							14
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(11, 45, 14)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> NICKEL_ORE = registerConfiguredFeature("nickel_ore", 
			Feature.ORE.configured(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE, 
							ModItems.nickel_ore.defaultBlockState(),
							8
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(0, 64, 6)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> TIN_ORE = registerConfiguredFeature("tin_ore", 
			Feature.ORE.configured(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE, 
							ModItems.tin_ore.defaultBlockState(),
							12
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(0, 48, 6)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> TIN_ORE_EXTRA = registerConfiguredFeature("tin_ore_extra", 
			Feature.ORE.configured(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE, 
							ModItems.tin_ore.defaultBlockState(),
							12
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(0, 48, 6)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> SILVER_ORE = registerConfiguredFeature("silver_ore", 
			Feature.ORE.configured(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE, 
							ModItems.silver_ore.defaultBlockState(),
							6
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(16, 40, 1)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> SILVER_ORE_EXTRA = registerConfiguredFeature("silver_ore_extra", 
			Feature.ORE.configured(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE, 
							ModItems.silver_ore.defaultBlockState(),
							3
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(16, 40, 1)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> SULFUR_ORE = registerConfiguredFeature("sulfur_ore", 
			Feature.ORE.configured(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES, 
							ModItems.sulfur_ore.defaultBlockState(),
							10
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(0, 64, 15)
					)
			)
	);
	
	public static final ConfiguredFeature<?, ?> BAUXIT_STONE_ORE = registerConfiguredFeature("bauxite_stone_ore",
			ModFeature.STONE_ORE.configured(
					new StoneOreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE,
							ModItems.bauxite.defaultBlockState(),
							ModItems.bauxite_ore.defaultBlockState(),
							42
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(20, 180, 5)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> WOLFRAM_STONE_ORE = registerConfiguredFeature("wolfram_stone_ore",
			ModFeature.STONE_ORE.configured(
					new StoneOreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE,
							ModItems.wolframite.defaultBlockState(),
							ModItems.wolframite_ore.defaultBlockState(),
							42
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(0, 32, 1)
					)
			).decorated(
					Placement.CHANCE.configured(
							new ChanceConfig(5)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> FLUORITE_CRYSTALS = registerConfiguredFeature("fluorite_crystals",
			ModFeature.CRYSTAL_ORE.configured(
					new CrystalOreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE,
							ModItems.fluorite_crystal.defaultBlockState(),
							10,
							20
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(0, 50, 1)
					)
			).decorated(
					ModPlacement.CHANCE.configured(
							new ModChancePlacementConfig(10)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> ZIRCON_CRYSTALS = registerConfiguredFeature("zircon_crystals",
			ModFeature.CRYSTAL_ORE.configured(
					new CrystalOreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE,
							ModItems.zircon_crystal.defaultBlockState(),
							10,
							20
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(0, 50, 1)
					)
			).decorated(
					ModPlacement.CHANCE.configured(
							new ModChancePlacementConfig(10)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> OIL_DEPOT = registerConfiguredFeature("oil_depot",
			Feature.ORE.configured(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.NATURAL_STONE,
							ModFluids.RAW_OIL.defaultFluidState().createLegacyBlock(),
							128
					)
			).decorated(
					ModPlacement.SIMPLE_ORE.configured(
							new SimpleOrePlacementConfig(0, 30, 6)
					)
			).decorated(
					Placement.CHANCE.configured(
							new ChanceConfig(180)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> SULFUR_LAKE = registerConfiguredFeature("sulfur_lake",
			ModFeature.LAKE_FEATUR.configured(
					new LakeFeatureConfig(
							3,
							10,
							7,
							3,
							ModItems.sulfuric_acid.defaultBlockState(),
							ModItems.sulfur_crust_block.defaultBlockState(),
							Blocks.NETHERRACK.defaultBlockState()
					)
			).decorated(
					ModPlacement.VERTICAL_OFFSET.configured(new VerticalOffsetPlacementConfig(1))
			).decorated(
					ModPlacement.NEXT_TOP_SURFACE.configured(new NoPlacementConfig())
			).decorated(
					ModPlacement.VERTICAL_SPREAD.configured(new VerticalSpreadPlacementConfig(2, 70))
			).decorated(
					ModPlacement.HORIZONTAL_SPREAD.configured(
							new HorizontalSpreadPlacementConfig(1)
					)
			).decorated(
					ModPlacement.CHANCE.configured(
							new ModChancePlacementConfig(8)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> SULFUR_LAKE_LARGE = registerConfiguredFeature("sulfur_lake_large",
			ModFeature.LAKE_FEATUR.configured(
					new LakeFeatureConfig(
							6,
							20,
							7,
							5,
							ModItems.sulfuric_acid.defaultBlockState(),
							ModItems.sulfur_crust_block.defaultBlockState(),
							Blocks.NETHERRACK.defaultBlockState()
					)
			).decorated(
					ModPlacement.VERTICAL_OFFSET.configured(new VerticalOffsetPlacementConfig(1))
			).decorated(
					ModPlacement.NEXT_TOP_SURFACE.configured(new NoPlacementConfig())
			).decorated(
					ModPlacement.VERTICAL_SPREAD.configured(new VerticalSpreadPlacementConfig(1, 70))
			).decorated(
					ModPlacement.HORIZONTAL_SPREAD.configured(
							new HorizontalSpreadPlacementConfig(1)
					)
			).decorated(
					ModPlacement.CHANCE.configured(
							new ModChancePlacementConfig(2)
					)
			)
	);
	
	// Trees
	public static final ConfiguredFeature<?, ?> RUBBER_TREE = registerConfiguredFeature("rubber_tree",
			ModFeature.JIGSAW_FEATURE.configured(new JigsawFeatureConfig(
					new TagMatchRuleTest(ModTags.DIRT), Direction.NORTH, JigsawType.VERTICAL_UP, 
					new ResourceLocation(Industria.MODID, "nature/rubber_tree"), new ResourceLocation(Industria.MODID, "tree_log"),
					Blocks.DIRT.defaultBlockState(), false, 1, 1)
			).decorated(
					ModPlacement.VERTICAL_OFFSET.configured(
							new VerticalOffsetPlacementConfig(-1)
					)
			).decorated(
					Placement.HEIGHTMAP_WORLD_SURFACE.configured(new NoPlacementConfig())
			).decorated(
					ModPlacement.HORIZONTAL_SPREAD.configured(
							new HorizontalSpreadPlacementConfig(2)
					)
			).decorated(
					Placement.CHANCE.configured(new ChanceConfig(10))
			)
	);
	public static final ConfiguredFeature<?, ?> ACACIA_TREE = registerConfiguredFeature("acacia_tree",
			ModFeature.JIGSAW_FEATURE.configured(new JigsawFeatureConfig(
					new TagMatchRuleTest(ModTags.DIRT), Direction.NORTH, JigsawType.VERTICAL_UP, 
					new ResourceLocation(Industria.MODID, "nature/acacia_normal"), new ResourceLocation(Industria.MODID, "tree_log"),
					Blocks.DIRT.defaultBlockState(), false, 1, 1)
			).decorated(
					ModPlacement.VERTICAL_OFFSET.configured(
							new VerticalOffsetPlacementConfig(-2)
					)
			).decorated(
					ModPlacement.HORIZONTAL_SPREAD.configured(
							new HorizontalSpreadPlacementConfig(1)
					)
			).decorated(
					Placement.HEIGHTMAP_WORLD_SURFACE.configured(new NoPlacementConfig())
			).decorated(
					Placement.CHANCE.configured(new ChanceConfig(10))
			)
	);
	public static final ConfiguredFeature<?, ?> OAK_TREE_RARE = registerConfiguredFeature("oak_tree",
			ModFeature.JIGSAW_FEATURE.configured(new JigsawFeatureConfig(
					new TagMatchRuleTest(ModTags.DIRT), Direction.NORTH, JigsawType.VERTICAL_UP, 
					new ResourceLocation(Industria.MODID, "nature/oak_normal"), new ResourceLocation(Industria.MODID, "tree_log"),
					Blocks.DIRT.defaultBlockState(), false, 1, 1)
			).decorated(
					ModPlacement.VERTICAL_OFFSET.configured(
							new VerticalOffsetPlacementConfig(-2)
					)
			).decorated(
					Placement.HEIGHTMAP_WORLD_SURFACE.configured(new NoPlacementConfig())
			).decorated(
					ModPlacement.HORIZONTAL_SPREAD.configured(
							new HorizontalSpreadPlacementConfig(2)
					)
			).decorated(
					ModPlacement.CHANCE.configured(
						new ModChancePlacementConfig(20)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> OAK_TREE_NORMAL = registerConfiguredFeature("oak_tree",
			ModFeature.JIGSAW_FEATURE.configured(new JigsawFeatureConfig(
					new TagMatchRuleTest(ModTags.DIRT), Direction.NORTH, JigsawType.VERTICAL_UP, 
					new ResourceLocation(Industria.MODID, "nature/oak_normal"), new ResourceLocation(Industria.MODID, "tree_log"),
					Blocks.DIRT.defaultBlockState(), false, 1, 1)
			).decorated(
					ModPlacement.VERTICAL_OFFSET.configured(
							new VerticalOffsetPlacementConfig(-2)
					)
			).decorated(
					Placement.HEIGHTMAP_WORLD_SURFACE.configured(new NoPlacementConfig())
			).decorated(
					ModPlacement.HORIZONTAL_SPREAD.configured(
							new HorizontalSpreadPlacementConfig(7)
					)
			).decorated(
					ModPlacement.CHANCE.configured(
						new ModChancePlacementConfig(85)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> OAK_TREE_EXTRA = registerConfiguredFeature("oak_tree",
			ModFeature.JIGSAW_FEATURE.configured(new JigsawFeatureConfig(
					new TagMatchRuleTest(ModTags.DIRT), Direction.NORTH, JigsawType.VERTICAL_UP, 
					new ResourceLocation(Industria.MODID, "nature/oak_normal"), new ResourceLocation(Industria.MODID, "tree_log"),
					Blocks.DIRT.defaultBlockState(), false, 1, 1)
			).decorated(
					ModPlacement.VERTICAL_OFFSET.configured(
							new VerticalOffsetPlacementConfig(-2)
					)
			).decorated(
					Placement.HEIGHTMAP_WORLD_SURFACE.configured(new NoPlacementConfig())
			).decorated(
					ModPlacement.HORIZONTAL_SPREAD.configured(
							new HorizontalSpreadPlacementConfig(10)
					)
			)
	);
	
	public static ConfiguredFeature<?, ?> registerConfiguredFeature(String key, ConfiguredFeature<?, ?> configuredFeature) {
		return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Industria.MODID, key), configuredFeature);
	}
	
}
