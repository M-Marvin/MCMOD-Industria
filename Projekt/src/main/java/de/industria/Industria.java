package de.industria;

import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.industria.typeregistys.ModConfiguredFeatures;
import de.industria.util.handler.MethodHelper;
import de.industria.util.handler.ModGameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.Features;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;

@Mod("industria")
public class Industria {
	
	// TODO Sounds:
	// - Heater
	// - BlastFurance
	// - MetalFormer
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "industria";
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(MODID, "main"), 
			() -> Industria.PROTOCOL_VERSION, 
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);
	
	
	public static final ItemGroup MACHINES = new ItemGroup("machines") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.generator);
		}
	};
	public static final ItemGroup BUILDING_BLOCKS = new ItemGroup("building_blocks") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.cracked_polished_granite_bricks);
		}
	};
	public static final ItemGroup DECORATIONS = new ItemGroup("decorations") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.rubber_leaves);
		}
	};
	public static final ItemGroup TOOLS = new ItemGroup("tools") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.hammer);
		}
	};
	public static final ItemGroup MATERIALS = new ItemGroup("materials") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.rubber);
		}
	};
	
	public static CheckResult MOD_STATUS = null;
	
	public Industria() {
		
		// TODO
		Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(MODID);
		if (modContainer.isPresent()) {
			MOD_STATUS = VersionChecker.getResult(modContainer.get().getModInfo());
			
			if (MOD_STATUS.status.equals(Status.PENDING)) {
				try {
					Thread.sleep(10000);
					MOD_STATUS = VersionChecker.getResult(modContainer.get().getModInfo());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		LOGGER.log(Level.INFO, "#####################################################################################");
		LOGGER.log(Level.INFO, MOD_STATUS.status);
		
		// register Blocks
		ModGameRegistry.registerBlock(ModItems.tree_tap, TOOLS);
		ModGameRegistry.registerBlock(ModItems.capacitor, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.pulse_counter, ItemGroup.REDSTONE); 
		ModGameRegistry.registerBlock(ModItems.stacked_redstone_torch, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.stacked_redstone_wire, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.advanced_piston, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.advanced_sticky_piston, ItemGroup.REDSTONE);
		ModGameRegistry.registerTechnicalBlock(ModItems.advanced_piston_head);
		ModGameRegistry.registerTechnicalBlock(ModItems.advanced_moving_block);
		ModGameRegistry.registerBlock(ModItems.redstone_reciver, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.signal_wire, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.antenna_conector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.linear_conector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.storing_crafting_table, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.signal_processor_contact, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.rail_piston, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.conector_block, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.redstone_contact, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.button_block, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.radial_conector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.hover_controler, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.hover_extension, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.controll_panel, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.harvester, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.copper_cable, MACHINES);
		ModGameRegistry.registerBlock(ModItems.electrolyt_copper_cable, MACHINES);
		ModGameRegistry.registerBlock(ModItems.aluminium_cable, MACHINES);
		ModGameRegistry.registerBlock(ModItems.burned_cable, MACHINES);
		ModGameRegistry.registerBlock(ModItems.infinity_power_source, null);
		ModGameRegistry.registerBlock(ModItems.panel_lamp, MACHINES);
		ModGameRegistry.registerBlock(ModItems.steel_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.copper_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.aluminium_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.electrolyt_copper_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.redstone_alloy_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.tin_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.palladium_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.silver_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolfram_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.nickel_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.monel_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.tin_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.silver_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.palladium_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.copper_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.nickel_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.bauxite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.bauxite_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolframite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolframite_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gold_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.steel_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.aluminium_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.electrolyt_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.redstone_alloy_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.silver_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.palladium_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolfram_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.nickel_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.monel_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.netherite_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gold_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.steel_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.aluminium_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.electrolyt_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.redstone_alloy_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.silver_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.palladium_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolfram_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.nickel_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.monel_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.netherite_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.sulfur_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.reinforced_casing, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.ender_core, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.preassure_pipe, MACHINES);
		ModGameRegistry.registerBlock(ModItems.pipe_preassurizer, MACHINES);
		ModGameRegistry.registerBlock(ModItems.air_compressor, MACHINES);
		ModGameRegistry.registerBlock(ModItems.item_distributor, MACHINES);
		ModGameRegistry.registerBlock(ModItems.preassure_pipe_item_terminal, MACHINES);
		
		ModGameRegistry.registerBlock(ModItems.motor, MACHINES);
		
		ModGameRegistry.registerBlock(ModItems.smooth_cobblestone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.chiseled_smooth_stone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.iron_rod, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.stone_corner, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.salsola_seeds, MATERIALS);
		ModGameRegistry.registerBlock(ModItems.jigsaw, null, Rarity.EPIC);
		ModGameRegistry.registerBlock(ModItems.generator, MACHINES);
		ModGameRegistry.registerBlock(ModItems.fluid_pipe, MACHINES);
		ModGameRegistry.registerBlock(ModItems.fluid_valve, MACHINES);
		ModGameRegistry.registerBlock(ModItems.fluid_input, MACHINES);
		ModGameRegistry.registerBlock(ModItems.fluid_output, MACHINES);
		ModGameRegistry.registerBlock(ModItems.steam_generator, MACHINES);
		ModGameRegistry.registerBlock(ModItems.coal_heater, MACHINES);
		ModGameRegistry.registerBlock(ModItems.transformator_coil, MACHINES);
		ModGameRegistry.registerBlock(ModItems.transformator_contact, MACHINES);
		ModGameRegistry.registerBlock(ModItems.fuse_box, MACHINES);
		ModGameRegistry.registerBlock(ModItems.multimeter, MACHINES);
		ModGameRegistry.registerBlock(ModItems.power_switch, MACHINES);
		ModGameRegistry.registerBlock(ModItems.electric_furnace, MACHINES);
		ModGameRegistry.registerBlock(ModItems.schredder, MACHINES);
		ModGameRegistry.registerBlock(ModItems.blender, MACHINES);
		ModGameRegistry.registerBlock(ModItems.raffinery, MACHINES);
		ModGameRegistry.registerBlock(ModItems.alloy_furnace, MACHINES);
		ModGameRegistry.registerBlock(ModItems.conveyor_belt, MACHINES);
		ModGameRegistry.registerBlock(ModItems.conveyor_spliter, MACHINES);
		ModGameRegistry.registerBlock(ModItems.conveyor_switch, MACHINES);
		ModGameRegistry.registerBlock(ModItems.thermal_zentrifuge, MACHINES);
		ModGameRegistry.registerBlock(ModItems.fluid_bath, MACHINES);
		ModGameRegistry.registerBlock(ModItems.blast_furnace, MACHINES);
		ModGameRegistry.registerBlock(ModItems.metal_former, MACHINES);
		ModGameRegistry.registerBlock(ModItems.rubber_log, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.rubber_wood, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.rubber_leaves, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.rubber_sapling, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.marple_log, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.marple_wood, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.marple_leaves, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.marple_leaves_red, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.swamp_algae, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.hanging_vine, DECORATIONS);
		
		ModGameRegistry.registerBlock(ModItems.mangrove_log, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.mangrove_wood, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.mangrove_leaves, DECORATIONS);
		
		ModGameRegistry.registerBlock(ModItems.beech_log, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.beech_wood, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.beech_leaves, DECORATIONS);
		
		ModGameRegistry.registerBlock(ModItems.mossy_end_stone_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_nether_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_red_sandstone_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_sandstone_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.salt_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_blue_nether_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_red_nether_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_prismarine_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_quartz_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_end_stone_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_polished_red_sandstone_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_polished_sandstone_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_salt_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_blackstone_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_quartz_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_diorite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_granite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_andesite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_stone_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_bauxite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_wolframite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.chiseled_blue_nether_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.chiseled_red_nether_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_diorite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_granite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_bauxite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_dark_prismarine, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_prismarine, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_end_stone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_red_sandstone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_sandstone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_stone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blackstone_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.quartz_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.diorite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.granite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.andesite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.stone_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.bauxite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolframite_tiles, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.salt_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_andesite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_diorite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_granite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_wolframite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_bauxite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_polished_andesite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_polished_diorite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_polished_granite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_polished_wolframite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_polished_bauxite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_andesite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_wolframite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.concrete, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.concrete_sheet, DECORATIONS);
		
		ModGameRegistry.registerBlock(ModItems.concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_dark_prismarine_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_prismarine_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_end_stone_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_red_sandstone_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_sandstone_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_stone_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_bauxite_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_wolframite_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blackstone_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.quartz_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.diorite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.granite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.andesite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.stone_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.bauxite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolframite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_blackstone_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_quartz_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_diorite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_granite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_andesite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_stone_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_bauxite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_wolframite_tiles_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_nether_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.quartz_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_sandstone_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.sandstone_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.diorite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.granite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.andesite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.bauxite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolframite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.salt_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_diorite_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_granite_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_andesite_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_bauxite_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_wolframite_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.mossy_end_stone_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.white_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.orange_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.magenta_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_blue_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.yellow_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.green_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.pink_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gray_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_gray_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cyan_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.purple_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.black_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.brown_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.lime_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_stained_glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.glass_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.netherrack_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.end_stone_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.bauxite_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolframite_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.salt_slab, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(ModItems.cracked_red_nether_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_blue_nether_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_andesite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_diorite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_granite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_bauxite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_wolframite_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_sandstone_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_red_sandstone_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_nether_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_stone_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_blackstone_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_prismarine_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_end_stone_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_quartz_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_salt_brick_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.iron_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gold_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.steel_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.copper_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.aluminium_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.electrolyt_copper_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.redstone_alloy_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.silver_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.palladium_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.tin_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolfram_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.nickel_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.monel_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.netherite_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_copper_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_tin_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_iron_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_steel_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_copper_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_tin_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_iron_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_steel_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_copper_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_tin_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_iron_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_steel_planks_slab, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(ModItems.lime_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.purple_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.white_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.orange_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.magenta_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_blue_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.yellow_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.green_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.pink_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gray_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_gray_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cyan_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.brown_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.black_terracotta_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.white_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.orange_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.magenta_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_blue_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.yellow_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.green_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.pink_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gray_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_gray_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cyan_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.brown_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.black_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.lime_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.purple_painted_planks_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.purple_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.lime_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.white_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.orange_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.magenta_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_blue_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.yellow_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.green_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.pink_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gray_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_gray_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cyan_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.brown_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_concrete_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.black_concrete_slab, BUILDING_BLOCKS);
		
//		ModGameRegistry.registerBlock(ModItems.cracked_warped_wart_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_nether_wart_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_netherrack_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_blackstone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_prismarine_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_terracotta_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_quartz_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_end_stone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_red_sandstone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_sandstone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_diorite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_granite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_andesite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_stone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_bauxite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_wolframite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_salt_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cut_red_sandstone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cut_sandstone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_warped_wart_slab, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_nether_wart_slab, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.white_terracotta_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.white_stained_glass_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.glass_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.netherrack_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.terracotta_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.end_stone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.bauxite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.wolframite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.iron_plates_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.iron_planks_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.salt_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.white_painted_planks_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.burned_planks_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_dark_prismarine_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_prismarine_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_end_stone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_red_sandstone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_sandstone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_diorite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_granite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_andesite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_stone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_bauxite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_wolframite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.mossy_end_stone_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.mossy_end_stone_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.purpur_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_quartz_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_basalt_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_red_sandstone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_sandstone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_diorite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_granite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_andesite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_stone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_bauxite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_wolframite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_blackstone_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_quartz_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_diorite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_granite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_andesite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_stone_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_bauxite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_wolframite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.netherrack_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.dark_prismarine_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.terracotta_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.quartz_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.end_stone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.stone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.bauxite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.wolframite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.iron_plates_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.iron_planks_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.salt_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.white_painted_planks_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.burned_planks_fence, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.blackstone_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.quartz_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.diorite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.granite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.andesite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.stone_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.bauxite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.wolframite_tiles_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cut_red_sandstone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cut_sandstone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.blue_nether_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.prismarine_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.quartz_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.red_sandstone_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.sandstone_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.diorite_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.granite_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.andesite_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.bauxite_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.wolframite_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.salt_brick_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_warped_wart_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_nether_wart_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_netherrack_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_blackstone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_prismarine_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_terracotta_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_quartz_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_end_stone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_red_sandstone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_sandstone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_diorite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_granite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_andesite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_stone_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_bauxite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_wolframite_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_salt_wall, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.blackstone_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.quartz_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.diorite_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.granite_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.andesite_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.stone_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.bauxite_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.wolframite_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_basalt_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_diorite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_granite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_andesite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_bauxite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.smooth_wolframite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.blue_nether_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.quartz_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.red_sandstone_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.sandstone_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.diorite_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.granite_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.andesite_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.bauxite_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.wolframite_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.salt_brick_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_dark_prismarine_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_prismarine_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_end_stone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_red_sandstone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_sandstone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_stone_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_bauxite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.polished_wolframite_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_blackstone_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_quartz_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_diorite_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_granite_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_andesite_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_stone_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_bauxite_tiles_stairs, BUILDING_BLOCKS);
//		ModGameRegistry.registerBlock(ModItems.cracked_wolframite_tiles_stairs, BUILDING_BLOCKS);

		
		
		
		
		ModGameRegistry.registerBlock(ModItems.exposed_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_steel_plates, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(ModItems.oxidized_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_steel_plates, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(ModItems.weathered_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_steel_plates, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(ModItems.exposed_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_steel_planks, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(ModItems.oxidized_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_steel_planks, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(ModItems.weathered_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_steel_planks, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(ModItems.cardboard_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_wolframite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_bauxite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.white_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.orange_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.magenta_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_blue_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.yellow_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.lime_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.pink_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gray_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_gray_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cyan_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.purple_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.brown_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.green_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.black_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.limestone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.limestone_sheet, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.item_detector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.computer, MACHINES);
		ModGameRegistry.registerBlock(ModItems.network_cable, MACHINES);
		ModGameRegistry.registerBlock(ModItems.steel_rail, ItemGroup.TRANSPORTATION);
		ModGameRegistry.registerBlock(ModItems.inductive_rail, ItemGroup.TRANSPORTATION);
		ModGameRegistry.registerBlock(ModItems.rail_adapter, ItemGroup.TRANSPORTATION);
		ModGameRegistry.registerBlock(ModItems.chunk_loader, MACHINES);
		ModGameRegistry.registerBlock(ModItems.clean_cladding_black, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.clean_cladding_white, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.structure_scaffold, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ModItems.ash, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.burned_log, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.burned_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.burned_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.burned_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.burned_wood_slab, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.burned_trapdoor, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.burned_door, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.burned_fence, DECORATIONS);
		ModGameRegistry.registerBlock(ModItems.burned_scrap, DECORATIONS);
		
		ModGameRegistry.registerBlock(ModItems.gold_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.aluminium_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.electrolyt_copper_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.redstone_alloy_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.silver_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.palladium_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolfram_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.nickel_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.monel_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.netherite_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_copper_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_tin_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_iron_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.oxidized_steel_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_copper_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_tin_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_iron_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.weathered_steel_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_copper_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_tin_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_iron_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.exposed_steel_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.tin_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.steel_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.copper_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.iron_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.white_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.orange_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.magenta_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_blue_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.yellow_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.lime_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.pink_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gray_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_gray_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cyan_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.purple_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.brown_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.green_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.black_concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.white_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.orange_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.magenta_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_blue_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.yellow_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.lime_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.pink_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gray_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_gray_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cyan_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.purple_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.brown_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.green_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.black_terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.white_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.orange_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.magenta_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_blue_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.yellow_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.lime_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.pink_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.gray_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.light_gray_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cyan_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.purple_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.brown_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.green_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.black_painted_planks_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.concrete_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_blue_nether_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_nether_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_red_nether_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_andesite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_diorite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_granite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_bauxite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_wolframite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_sandstone_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_red_sandstone_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_stone_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_blackstone_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_prismarine_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_quartz_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_end_stone_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.netherrack_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.end_stone_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.bauxite_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolframite_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.salt_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blue_nether_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.quartz_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.diorite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.granite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.andesite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.bauxite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolframite_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.salt_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_salt_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.terracotta_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.burned_wood_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_red_sandstone_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_sandstone_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_end_stone_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_prismarine_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_dark_prismarine_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_stone_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_bauxite_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.polished_wolframite_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.mossy_end_stone_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.blackstone_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.quartz_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.diorite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.granite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.andesite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.stone_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.bauxite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.wolframite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_diorite_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_granite_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_andesite_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_bauxite_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.smooth_wolframite_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_blackstone_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_quartz_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_diorite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_granite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_andesite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_stone_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_bauxite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.cracked_wolframite_tiles_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.red_sandstone_brick_stairs, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(ModItems.sandstone_brick_stairs, BUILDING_BLOCKS);
		
		ModGameRegistry.registerTechnicalBlock(ModItems.steam);
		ModGameRegistry.registerItem(ModItems.steam_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.destilled_water);
		ModGameRegistry.registerItem(ModItems.destilled_water_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.sulfuric_acid);
		ModGameRegistry.registerItem(ModItems.sulfuric_acid_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.natron_lye);
		ModGameRegistry.registerItem(ModItems.natron_lye_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.raw_oil);
		ModGameRegistry.registerItem(ModItems.raw_oil_bucket);
		
		ModGameRegistry.registerTechnicalBlock(ModItems.iron_solution);
		ModGameRegistry.registerItem(ModItems.iron_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.copper_solution);
		ModGameRegistry.registerItem(ModItems.copper_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.aluminium_solution);
		ModGameRegistry.registerItem(ModItems.aluminium_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.wolfram_solution);
		ModGameRegistry.registerItem(ModItems.wolfram_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.tin_solution);
		ModGameRegistry.registerItem(ModItems.tin_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.chemical_water);
		ModGameRegistry.registerItem(ModItems.chemical_water_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.compressed_air);
		ModGameRegistry.registerItem(ModItems.compressed_air_bucket);
		ModGameRegistry.registerTechnicalBlock(ModItems.liquid_concrete);
		ModGameRegistry.registerItem(ModItems.liquid_concrete_bucket);
		
		// register Items
		ModGameRegistry.registerItem(ModItems.crushed_blackstone);
		ModGameRegistry.registerItem(ModItems.crushed_netherrack);
		ModGameRegistry.registerItem(ModItems.crushed_bauxite);
		ModGameRegistry.registerItem(ModItems.crushed_wolframite);
		ModGameRegistry.registerItem(ModItems.tin_oxid);
		ModGameRegistry.registerItem(ModItems.iron_oxid);
		ModGameRegistry.registerItem(ModItems.copper_oxid);
		ModGameRegistry.registerItem(ModItems.pure_gold);
		ModGameRegistry.registerItem(ModItems.pure_silver);
		ModGameRegistry.registerItem(ModItems.pure_palladium);
		ModGameRegistry.registerItem(ModItems.pure_nickel);
		ModGameRegistry.registerItem(ModItems.crushed_tin_ore);
		ModGameRegistry.registerItem(ModItems.crushed_iron_ore);
		ModGameRegistry.registerItem(ModItems.crushed_copper_ore);
		ModGameRegistry.registerItem(ModItems.crushed_nickel_ore);
		ModGameRegistry.registerItem(ModItems.crushed_gold_ore);
		ModGameRegistry.registerItem(ModItems.crushed_silver_ore);
		ModGameRegistry.registerItem(ModItems.crushed_palladium_ore);
		ModGameRegistry.registerItem(ModItems.pure_tin_ore);
		ModGameRegistry.registerItem(ModItems.pure_iron_ore);
		ModGameRegistry.registerItem(ModItems.pure_copper_ore);
		ModGameRegistry.registerItem(ModItems.pure_nickel_ore);
		ModGameRegistry.registerItem(ModItems.pure_gold_ore);
		ModGameRegistry.registerItem(ModItems.pure_silver_ore);
		ModGameRegistry.registerItem(ModItems.pure_palladium_ore);
		ModGameRegistry.registerItem(ModItems.crushed_stone);
		ModGameRegistry.registerItem(ModItems.redstone_ingot);
		ModGameRegistry.registerItem(ModItems.remote_control);
		ModGameRegistry.registerItem(ModItems.iron_processor);
		ModGameRegistry.registerItem(ModItems.redstone_processor);
		ModGameRegistry.registerItem(ModItems.emerald_processor);
		ModGameRegistry.registerItem(ModItems.netherite_processor);
		ModGameRegistry.registerItem(ModItems.salsola);
		ModGameRegistry.registerItem(ModItems.empty_blueprint);
		ModGameRegistry.registerItem(ModItems.blueprint);
		ModGameRegistry.registerItem(ModItems.copper_ingot);
		ModGameRegistry.registerItem(ModItems.copper_nugget);
		ModGameRegistry.registerItem(ModItems.electrolyt_copper_ingot);
		ModGameRegistry.registerItem(ModItems.electrolyt_copper_nugget);
		ModGameRegistry.registerItem(ModItems.steel_ingot);
		ModGameRegistry.registerItem(ModItems.steel_nugget);
		ModGameRegistry.registerItem(ModItems.aluminium_ingot);
		ModGameRegistry.registerItem(ModItems.aluminium_nugget);
		ModGameRegistry.registerItem(ModItems.redstone_nugget);
		ModGameRegistry.registerItem(ModItems.tin_ingot);
		ModGameRegistry.registerItem(ModItems.tin_nugget);
		ModGameRegistry.registerItem(ModItems.wolfram_ingot);
		ModGameRegistry.registerItem(ModItems.wolfram_nugget);
		ModGameRegistry.registerItem(ModItems.silver_ingot);
		ModGameRegistry.registerItem(ModItems.silver_nugget);
		ModGameRegistry.registerItem(ModItems.palladium_ingot);
		ModGameRegistry.registerItem(ModItems.palladium_nugget);
		ModGameRegistry.registerItem(ModItems.nickel_ingot);
		ModGameRegistry.registerItem(ModItems.nickel_nugget);
		ModGameRegistry.registerItem(ModItems.monel_ingot);
		ModGameRegistry.registerItem(ModItems.monel_nugget);
		ModGameRegistry.registerItem(ModItems.tpo_copper_dust);
		ModGameRegistry.registerItem(ModItems.copper_dust);
		ModGameRegistry.registerItem(ModItems.electrolyt_copper_dust);
		ModGameRegistry.registerItem(ModItems.tpo_electrolyt_copper_dust);
		ModGameRegistry.registerItem(ModItems.steel_dust);
		ModGameRegistry.registerItem(ModItems.tpo_steel_dust);
		ModGameRegistry.registerItem(ModItems.aluminium_dust);
		ModGameRegistry.registerItem(ModItems.tpo_aluminium_dust);
		ModGameRegistry.registerItem(ModItems.redstone_alloy_dust);
		ModGameRegistry.registerItem(ModItems.tpo_redstone_alloy_dust);
		ModGameRegistry.registerItem(ModItems.tin_dust);
		ModGameRegistry.registerItem(ModItems.tpo_tin_dust);
		ModGameRegistry.registerItem(ModItems.wolfram_dust);
		ModGameRegistry.registerItem(ModItems.tpo_wolfram_dust);
		ModGameRegistry.registerItem(ModItems.silver_dust);
		ModGameRegistry.registerItem(ModItems.tpo_silver_dust);
		ModGameRegistry.registerItem(ModItems.palladium_dust);
		ModGameRegistry.registerItem(ModItems.tpo_palladium_dust);
		ModGameRegistry.registerItem(ModItems.nickel_dust);
		ModGameRegistry.registerItem(ModItems.tpo_nickel_dust);
		ModGameRegistry.registerItem(ModItems.monel_dust);
		ModGameRegistry.registerItem(ModItems.tpo_monel_dust);
		ModGameRegistry.registerItem(ModItems.iron_dust);
		ModGameRegistry.registerItem(ModItems.tpo_iron_dust);
		ModGameRegistry.registerItem(ModItems.gold_dust);
		ModGameRegistry.registerItem(ModItems.tpo_gold_dust);
		ModGameRegistry.registerItem(ModItems.copper_plate);
		ModGameRegistry.registerItem(ModItems.electrolyt_copper_plate);
		ModGameRegistry.registerItem(ModItems.steel_plate);
		ModGameRegistry.registerItem(ModItems.aluminium_plate);
		ModGameRegistry.registerItem(ModItems.redstone_alloy_plate);
		ModGameRegistry.registerItem(ModItems.tin_plate);
		ModGameRegistry.registerItem(ModItems.wolfram_plate);
		ModGameRegistry.registerItem(ModItems.silver_plate);
		ModGameRegistry.registerItem(ModItems.palladium_plate);
		ModGameRegistry.registerItem(ModItems.nickel_plate);
		ModGameRegistry.registerItem(ModItems.monel_plate);
		ModGameRegistry.registerItem(ModItems.gold_plate);
		ModGameRegistry.registerItem(ModItems.iron_plate);
		ModGameRegistry.registerItem(ModItems.netherite_plate);
		ModGameRegistry.registerItem(ModItems.hammer);
		ModGameRegistry.registerItem(ModItems.cutter);
		ModGameRegistry.registerItem(ModItems.brush);
		ModGameRegistry.registerItem(ModItems.aluminium_wire);
		ModGameRegistry.registerItem(ModItems.copper_wire);
		ModGameRegistry.registerItem(ModItems.electrolyt_copper_wire);
		ModGameRegistry.registerItem(ModItems.rotor);
		ModGameRegistry.registerItem(ModItems.motor_coil);
		ModGameRegistry.registerItem(ModItems.plastic_pellets);
		ModGameRegistry.registerItem(ModItems.polymer_resin);
		ModGameRegistry.registerItem(ModItems.rubber);
		ModGameRegistry.registerItem(ModItems.resistor);
		ModGameRegistry.registerItem(ModItems.condensator);
		ModGameRegistry.registerItem(ModItems.turbin);
		ModGameRegistry.registerItem(ModItems.led);
		ModGameRegistry.registerItem(ModItems.spring);
		ModGameRegistry.registerItem(ModItems.bearing);
		ModGameRegistry.registerItem(ModItems.coal_coke);
		ModGameRegistry.registerItem(ModItems.silicon);
		ModGameRegistry.registerItem(ModItems.electrolyt_paper);
		ModGameRegistry.registerItem(ModItems.salt);
		ModGameRegistry.registerItem(ModItems.natrium);
		ModGameRegistry.registerItem(ModItems.fluid_meter);
		ModGameRegistry.registerItem(ModItems.energy_meter);
		ModGameRegistry.registerItem(ModItems.fuse_elv);
		ModGameRegistry.registerItem(ModItems.fuse_lv);
		ModGameRegistry.registerItem(ModItems.fuse_nv);
		ModGameRegistry.registerItem(ModItems.fuse_hv);
		ModGameRegistry.registerItem(ModItems.schredder_crusher);
		ModGameRegistry.registerItem(ModItems.schredder_macerator);
		ModGameRegistry.registerItem(ModItems.sulfur);
		ModGameRegistry.registerItem(ModItems.plastic_plate);
		ModGameRegistry.registerItem(ModItems.raw_rubber_bottle);
		ModGameRegistry.registerItem(ModItems.rubber_bottle);
		ModGameRegistry.registerItem(ModItems.crude_steel);
		ModGameRegistry.registerItem(ModItems.cardboard_sheet);
		ModGameRegistry.registerItem(ModItems.lime);
		ModGameRegistry.registerItem(ModItems.hard_drive);
		ModGameRegistry.registerItem(ModItems.network_configurtor);
		ModGameRegistry.registerItem(ModItems.structure_cladding_pane);
		ModGameRegistry.registerItem(ModItems.cement);
		
		// register Functional Items
		ModGameRegistry.registerItem(ModItems.lever_element);
		ModGameRegistry.registerItem(ModItems.button_element);
		ModGameRegistry.registerItem(ModItems.lamp_element);
		
		// World Generation Settings (Adding Features to the specific Biomes)
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.COPPER_ORE);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.TIN_ORE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.TIN_ORE_EXTRA, Category.JUNGLE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.TIN_ORE_EXTRA, Category.SAVANNA);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.NICKEL_ORE);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.SILVER_ORE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.SILVER_ORE_EXTRA, Category.JUNGLE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.SILVER_ORE_EXTRA, Category.EXTREME_HILLS);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.PALLADIUM_ORE);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.PALLADIUM_ORE_EXTRA, Category.SWAMP);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.PALLADIUM_ORE_EXTRA, Category.TAIGA);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.SULFUR_ORE, Category.NETHER);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.BAUXIT_STONE_ORE, Category.JUNGLE);
		ModGameRegistry.addFeatureToOverworldBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.WOLFRAM_STONE_ORE);
		
		// TODO
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.OIL_DEPOT, Category.DESERT);
		ModGameRegistry.addFeatureToBiomes(Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.OIL_DEPOT, Category.OCEAN);
		
		// Trees TODO: Rubber Tree Models
		ModGameRegistry.addFeatureToBiomes(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.RUBBER_TREE, Biomes.SWAMP_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE);
		ModGameRegistry.addFeatureToRemove(Decoration.VEGETAL_DECORATION, Features.ACACIA, Category.SAVANNA);
		ModGameRegistry.addFeatureToBiomes(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.ACACIA_TREE, Category.SAVANNA);
//		ModGameRegistry.addFeatureToRemoveInOverworld(Decoration.VEGETAL_DECORATION, Features.FANCY_OAK);
//		ModGameRegistry.addFeatureToRemoveInOverworld(Decoration.VEGETAL_DECORATION, Features.FANCY_OAK_BEES_0002);
//		ModGameRegistry.addFeatureToRemoveInOverworld(Decoration.VEGETAL_DECORATION, Features.FANCY_OAK_BEES_002);
//		ModGameRegistry.addFeatureToRemoveInOverworld(Decoration.VEGETAL_DECORATION, Features.FANCY_OAK_BEES_005);
//		ModGameRegistry.addFeatureToRemoveInOverworld(Decoration.VEGETAL_DECORATION, Features.OAK_BADLANDS);
//		ModGameRegistry.addFeatureToRemoveInOverworld(Decoration.VEGETAL_DECORATION, Features.OAK_BEES_0002);
//		ModGameRegistry.addFeatureToRemoveInOverworld(Decoration.VEGETAL_DECORATION, Features.OAK_BEES_002);
//		ModGameRegistry.addFeatureToRemoveInOverworld(Decoration.VEGETAL_DECORATION, Features.OAK_BEES_005);
//		ModGameRegistry.addFeatureToRemoveInOverworld(Decoration.VEGETAL_DECORATION, Features.OAK);
//		ModGameRegistry.addFeatureToBiomes(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.OAK_TREE_EXTRA, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WOODED_HILLS);
		
		// Vanilla FireInfo Overrides
		MethodHelper.addFireSetting(Blocks.ACACIA_DOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.OAK_DOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.BIRCH_DOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.DARK_OAK_DOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.SPRUCE_DOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.CHEST, 5, 20);
		MethodHelper.addFireSetting(Blocks.TRAPPED_CHEST, 5, 20);
		MethodHelper.addFireSetting(Blocks.OAK_TRAPDOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.BIRCH_TRAPDOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.ACACIA_TRAPDOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.DARK_OAK_TRAPDOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.SPRUCE_TRAPDOOR, 5, 20);
		MethodHelper.addFireSetting(Blocks.OAK_SIGN, 10, 30);
		MethodHelper.addFireSetting(Blocks.BIRCH_SIGN, 10, 30);
		MethodHelper.addFireSetting(Blocks.ACACIA_SIGN, 10, 30);
		MethodHelper.addFireSetting(Blocks.DARK_OAK_SIGN, 10, 30);
		MethodHelper.addFireSetting(Blocks.SPRUCE_SIGN, 10, 30);
		MethodHelper.addFireSetting(Blocks.OAK_PRESSURE_PLATE, 10, 30);
		MethodHelper.addFireSetting(Blocks.BIRCH_PRESSURE_PLATE, 10, 30);
		MethodHelper.addFireSetting(Blocks.ACACIA_PRESSURE_PLATE, 10, 30);
		MethodHelper.addFireSetting(Blocks.DARK_OAK_PRESSURE_PLATE, 10, 30);
		MethodHelper.addFireSetting(Blocks.SPRUCE_PRESSURE_PLATE, 10, 30);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::setupBlockColors);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::setupItemColors);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ServerSetup::setup);
		
	}
	
	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			IForgeRegistry<Block> registry = blockRegistryEvent.getRegistry();
			Block[] blocksToRegister = ModGameRegistry.getBlocksToRegister();
			registry.registerAll(blocksToRegister);
		}
		
		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
			IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();
			Item[] itemsToRegister = ModGameRegistry.getItemsToRegister();
			registry.registerAll(itemsToRegister);
		}
		
	}
	
}
