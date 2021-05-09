package de.industria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.industria.blocks.BlockAdvancedMovingBlock;
import de.industria.blocks.BlockBase;
import de.industria.blocks.BlockBurnable;
import de.industria.blocks.BlockBurnedCable;
import de.industria.blocks.BlockConveyorBelt;
import de.industria.blocks.BlockConveyorSpliter;
import de.industria.blocks.BlockConveyorSwitch;
import de.industria.blocks.BlockCornerBlockBase;
import de.industria.blocks.BlockElektricWire;
import de.industria.blocks.BlockEnderCore;
import de.industria.blocks.BlockFallingDust;
import de.industria.blocks.BlockFluidPipe;
import de.industria.blocks.BlockFluidValve;
import de.industria.blocks.BlockInfinityPowerSource;
import de.industria.blocks.BlockIronRod;
import de.industria.blocks.BlockItemDistributor;
import de.industria.blocks.BlockJigsaw;
import de.industria.blocks.BlockLeavesBase;
import de.industria.blocks.BlockLimestoneSheet;
import de.industria.blocks.BlockLogBase;
import de.industria.blocks.BlockMAirCompressor;
import de.industria.blocks.BlockMAlloyFurnace;
import de.industria.blocks.BlockMBlastFurnace;
import de.industria.blocks.BlockMBlender;
import de.industria.blocks.BlockMChunkLoader;
import de.industria.blocks.BlockMCoalHeater;
import de.industria.blocks.BlockMElectricFurnace;
import de.industria.blocks.BlockMFluidBath;
import de.industria.blocks.BlockMFluidInput;
import de.industria.blocks.BlockMFluidOutput;
import de.industria.blocks.BlockMFuseBox;
import de.industria.blocks.BlockMGenerator;
import de.industria.blocks.BlockMMultimeter;
import de.industria.blocks.BlockMPanelLamp;
import de.industria.blocks.BlockMPowerSwitch;
import de.industria.blocks.BlockMRaffinery;
import de.industria.blocks.BlockMSchredder;
import de.industria.blocks.BlockMSteamGenerator;
import de.industria.blocks.BlockMStoringCraftingTable;
import de.industria.blocks.BlockMThermalZentrifuge;
import de.industria.blocks.BlockMTransformatorCoil;
import de.industria.blocks.BlockMTransformatorContact;
import de.industria.blocks.BlockMotor;
import de.industria.blocks.BlockNComputer;
import de.industria.blocks.BlockNetworkCable;
import de.industria.blocks.BlockPipePreassurizer;
import de.industria.blocks.BlockPowerEmiting;
import de.industria.blocks.BlockPreassurePipe;
import de.industria.blocks.BlockPreassurePipeItemTerminal;
import de.industria.blocks.BlockRAdvancedPiston;
import de.industria.blocks.BlockRAdvancedPistonHead;
import de.industria.blocks.BlockRButtonBlock;
import de.industria.blocks.BlockRConectorBlock;
import de.industria.blocks.BlockRControllPanel;
import de.industria.blocks.BlockRDCapacitor;
import de.industria.blocks.BlockRDPulseCounter;
import de.industria.blocks.BlockRHarvester;
import de.industria.blocks.BlockRHoverControler;
import de.industria.blocks.BlockRHoverExtension;
import de.industria.blocks.BlockRItemDetector;
import de.industria.blocks.BlockRLinearConector;
import de.industria.blocks.BlockRRadialConector;
import de.industria.blocks.BlockRRailPiston;
import de.industria.blocks.BlockRRedstoneContact;
import de.industria.blocks.BlockRRedstoneReciver;
import de.industria.blocks.BlockRSignalProcessorContact;
import de.industria.blocks.BlockReinforcedCasing;
import de.industria.blocks.BlockRubberLog;
import de.industria.blocks.BlockSalsolaSeeds;
import de.industria.blocks.BlockSaplingBase;
import de.industria.blocks.BlockSignalAntennaConector;
import de.industria.blocks.BlockSignalWire;
import de.industria.blocks.BlockStackedRedstoneTorch;
import de.industria.blocks.BlockStackedRedstoneWire;
import de.industria.blocks.BlockStructureScaffold;
import de.industria.blocks.BlockTInductiveRail;
import de.industria.blocks.BlockTRailAdapter;
import de.industria.blocks.BlockTSteelRail;
import de.industria.blocks.BlockTileBlock;
import de.industria.blocks.BlockTreeTap;
import de.industria.blocks.BlockWeathering;
import de.industria.fluids.BlockChemicalWater;
import de.industria.fluids.BlockCompressedAir;
import de.industria.fluids.BlockDestilledWater;
import de.industria.fluids.BlockNatronLye;
import de.industria.fluids.BlockOreSolution;
import de.industria.fluids.BlockRawOil;
import de.industria.fluids.BlockSteam;
import de.industria.fluids.BlockSulfuricAcid;
import de.industria.fluids.util.ItemFluidBucket;
import de.industria.fluids.util.ItemGasBucket;
import de.industria.items.ItemBase;
import de.industria.items.ItemBlueprint;
import de.industria.items.ItemBurneable;
import de.industria.items.ItemCutter;
import de.industria.items.ItemEmptyBlueprint;
import de.industria.items.ItemEnergyMeter;
import de.industria.items.ItemFluidMeter;
import de.industria.items.ItemFuse;
import de.industria.items.ItemHammer;
import de.industria.items.ItemHardDrive;
import de.industria.items.ItemNetworkConfigurator;
import de.industria.items.ItemProcessor;
import de.industria.items.ItemRemoteControll;
import de.industria.items.ItemSalsola;
import de.industria.items.ItemSchredderToolCrusher;
import de.industria.items.ItemStructureCladdingPane;
import de.industria.items.panelitems.ItemButtonElement;
import de.industria.items.panelitems.ItemLampElement;
import de.industria.items.panelitems.ItemLeverElement;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.util.handler.ModGameRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;

@Mod("industria")
public class Industria {
	
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
			return new ItemStack(generator);
		}
	};
	public static final ItemGroup BUILDING_BLOCKS = new ItemGroup("building_blocks") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(cracked_polished_granite_bricks);
		}
	};
	public static final ItemGroup DECORATIONS = new ItemGroup("decorations") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(rubber_leaves);
		}
	};
	public static final ItemGroup TOOLS = new ItemGroup("tools") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(hammer);
		}
	};
	public static final ItemGroup MATERIALS = new ItemGroup("materials") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(rubber);
		}
	};
	
	// Redstone
	public static final Block pulse_counter = new BlockRDPulseCounter();
	public static final Block capacitor = new BlockRDCapacitor();
	public static final Block stacked_redstone_torch = new BlockStackedRedstoneTorch();
	public static final Block stacked_redstone_wire = new BlockStackedRedstoneWire();
	public static final Block advanced_piston = new BlockRAdvancedPiston(false, "advanced_piston");
	public static final Block advanced_piston_head = new BlockRAdvancedPistonHead("advanced_piston_head");
	public static final Block advanced_sticky_piston = new BlockRAdvancedPiston(true, "advanced_sticky_piston");
	public static final Block advanced_moving_block = new BlockAdvancedMovingBlock("advanced_moving_block");
	public static final Block redstone_reciver = new BlockRRedstoneReciver();
	public static final Block signal_wire = new BlockSignalWire();
	public static final Block antenna_conector = new BlockSignalAntennaConector();
	public static final Block linear_conector = new BlockRLinearConector();
	public static final Block signal_processor_contact = new BlockRSignalProcessorContact();
	public static final Block rail_piston = new BlockRRailPiston();
	public static final Block conector_block = new BlockRConectorBlock();
	public static final Block redstone_contact = new BlockRRedstoneContact();
	public static final Block button_block = new BlockRButtonBlock();
	public static final Block iron_rod = new BlockIronRod();
	public static final Block radial_conector = new BlockRRadialConector();
	public static final Block hover_controler = new BlockRHoverControler();
	public static final Block hover_extension = new BlockRHoverExtension();
	public static final Block controll_panel = new BlockRControllPanel();
	public static final Block harvester = new BlockRHarvester();
	public static final Block jigsaw = new BlockJigsaw();
	public static final Block item_detector = new BlockRItemDetector();
	public static final Block structure_scaffold = new BlockStructureScaffold("structure_scaffold");
	
	// TODO
	public static final Block storing_crafting_table = new BlockMStoringCraftingTable();	
	// Mechanic
	public static final Block motor = new BlockMotor();
	// Items
	//public static final Item sulfur_dioxid = new ItemBase("sulfur_dioxid", MATERIALS);
	
	// Sounds:
	// - Heater
	// - BlastFurance
	
	// Networks
	public static final Block computer = new BlockNComputer();
	
	// Rail
	public static final Block steel_rail = new BlockTSteelRail();
	public static final Block inductive_rail = new BlockTInductiveRail();
	public static final Block rail_adapter = new BlockTRailAdapter();
	
	// Machinery
	public static final Block conveyor_switch = new BlockConveyorSwitch();
	public static final Block conveyor_spliter = new BlockConveyorSpliter();
	public static final Block conveyor_belt = new BlockConveyorBelt();
	public static final Block generator = new BlockMGenerator();
	public static final Block infinity_power_source = new BlockInfinityPowerSource();
	public static final Block panel_lamp = new BlockMPanelLamp();
	public static final Block copper_cable = new BlockElektricWire("copper_cable", 16, 4);
	public static final Block electrolyt_copper_cable = new BlockElektricWire("electrolyt_copper_cable", 32, 4);
	public static final Block aluminium_cable = new BlockElektricWire("aluminium_cable", 64, 8);
	public static final Block burned_cable = new BlockBurnedCable();
	public static final Block network_cable = new BlockNetworkCable();
	public static final Block fluid_pipe = new BlockFluidPipe();
	public static final Block fluid_valve = new BlockFluidValve();
	public static final Block fluid_input = new BlockMFluidInput();
	public static final Block fluid_output = new BlockMFluidOutput();
	public static final Block steam_generator = new BlockMSteamGenerator();
	public static final Block coal_heater = new BlockMCoalHeater();
	public static final Block transformator_contact = new BlockMTransformatorContact();
	public static final Block transformator_coil = new BlockMTransformatorCoil();
	public static final Block fuse_box = new BlockMFuseBox();
	public static final Block multimeter = new BlockMMultimeter();
	public static final Block power_switch = new BlockMPowerSwitch();
	public static final Block electric_furnace = new BlockMElectricFurnace();
	public static final Block schredder = new BlockMSchredder();
	public static final Block blender = new BlockMBlender();
	public static final Block raffinery = new BlockMRaffinery();
	public static final Block alloy_furnace = new BlockMAlloyFurnace();
	public static final Block thermal_zentrifuge = new BlockMThermalZentrifuge();
	public static final Block fluid_bath = new BlockMFluidBath();
	public static final Block chunk_loader = new BlockMChunkLoader();
	public static final Block blast_furnace = new BlockMBlastFurnace();
	public static final Block preassure_pipe = new BlockPreassurePipe();
	public static final Block pipe_preassurizer = new BlockPipePreassurizer();
	public static final Block air_compressor = new BlockMAirCompressor();
	public static final Block item_distributor = new BlockItemDistributor();
	
	// Ore and Resource Blocks
	public static final Block bauxit = new BlockBase("bauxit", Material.ROCK, 1.5F, SoundType.STONE);
	public static final Block bauxit_ore = new BlockBase("bauxit_ore", Material.ROCK, 1.5F, SoundType.STONE);
	public static final Block wolframit = new BlockBase("wolframit", Material.ROCK, 1.5F, SoundType.STONE);
	public static final Block wolframit_ore = new BlockBase("wolframit_ore", Material.ROCK, 1.5F, SoundType.STONE);
	public static final Block copper_ore = new BlockBase("copper_ore", Material.ROCK, 3F, SoundType.STONE);
	public static final Block tin_ore = new BlockBase("tin_ore", Material.ROCK, 3F, SoundType.STONE);
	public static final Block silver_ore = new BlockBase("silver_ore", Material.ROCK, 3F, SoundType.STONE);
	public static final Block palladium_ore = new BlockBase("palladium_ore", Material.ROCK, 4F, 3F, SoundType.STONE);
	public static final Block nickel_ore = new BlockBase("nickel_ore", Material.ROCK, 3F, SoundType.STONE);
	public static final Block sulfur_ore = new BlockBase("sulfur_ore", Material.ROCK, 3F, SoundType.NETHER_ORE);
	public static final Block copper_block = new BlockBase("copper_block", Material.IRON, 5F, 6F, SoundType.METAL);
	public static final Block redstone_alloy_block = new BlockPowerEmiting("redstone_alloy_block", Material.IRON, 5F, 6F, SoundType.METAL, 8);
	public static final Block aluminium_block = new BlockBase("aluminium_block", Material.IRON, 2F, 4F, SoundType.METAL);
	public static final Block electrolyt_copper_block = new BlockBase("electrolyt_copper_block", Material.IRON, 5F, 6F, SoundType.METAL);
	public static final Block steel_block = new BlockBase("steel_block", Material.IRON, 5F, 6F, SoundType.METAL);
	public static final Block monel_block = new BlockBase("monel_block", Material.IRON, 5F, 6F, SoundType.METAL);
	public static final Block nickel_block = new BlockBase("nickel_block", Material.IRON, 5F, 6F, SoundType.METAL);
	public static final Block wolfram_block = new BlockBase("wolfram_block", Material.IRON, 7F, 10F, SoundType.METAL);
	public static final Block silver_block = new BlockBase("silver_block", Material.IRON, 5F, 6F, SoundType.METAL);
	public static final Block palladium_block = new BlockBase("palladium_block", Material.IRON, 5F, 6F, SoundType.METAL);
	public static final Block tin_block = new BlockBase("tin_block", Material.IRON, 5F, 6F, SoundType.METAL);
	public static final Block cardboard_block = new BlockBurnable("cardboard_block", Material.WOOL, 0.2F, 2.5F, ModSoundEvents.CARDBOARD, 400, 30, 60, true);
	public static final Block preassure_pipe_item_terminal = new BlockPreassurePipeItemTerminal();
	
	// Deko Blocks
	public static final Block clean_cladding_white = new BlockTileBlock("clean_cladding_white");
	public static final Block clean_cladding_black = new BlockTileBlock("clean_cladding_black");
	public static final Block oxidized_copper_planks = new BlockBase("oxidized_copper_planks", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block oxidized_tin_planks = new BlockBase("oxidized_tin_planks", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block oxidized_iron_planks = new BlockBase("oxidized_iron_planks", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block weathered_copper_planks = new BlockWeathering("weathered_copper_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_copper_planks);
	public static final Block weathered_tin_planks = new BlockWeathering("weathered_tin_planks", Material.IRON, 2.5F, 3F, SoundType.METAL,oxidized_tin_planks);
	public static final Block weathered_iron_planks = new BlockWeathering("weathered_iron_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_iron_planks);
	public static final Block exposed_copper_planks = new BlockWeathering("exposed_copper_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_copper_planks);
	public static final Block exposed_tin_planks = new BlockWeathering("exposed_tin_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_tin_planks);
	public static final Block exposed_iron_planks = new BlockWeathering("exposed_iron_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_iron_planks);
	public static final Block oxidized_copper_plates = new BlockBase("oxidized_copper_plates", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block oxidized_tin_plates = new BlockBase("oxidized_tin_plates", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block oxidized_iron_plates = new BlockBase("oxidized_iron_plates", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block oxidized_steel_plates = new BlockBase("oxidized_steel_plates", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block oxidized_steel_planks = new BlockBase("oxidized_steel_planks", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block weathered_copper_plates = new BlockWeathering("weathered_copper_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_copper_plates);
	public static final Block weathered_tin_plates = new BlockWeathering("weathered_tin_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_tin_plates);
	public static final Block weathered_iron_plates = new BlockWeathering("weathered_iron_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_iron_plates);
	public static final Block weathered_steel_plates = new BlockWeathering("weathered_steel_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_steel_plates);
	public static final Block weathered_steel_planks = new BlockWeathering("weathered_steel_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_steel_planks);
	public static final Block exposed_copper_plates = new BlockWeathering("exposed_copper_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_copper_plates);
	public static final Block exposed_tin_plates = new BlockWeathering("exposed_tin_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_tin_plates);
	public static final Block exposed_iron_plates = new BlockWeathering("exposed_iron_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_iron_plates);
	public static final Block exposed_steel_plates = new BlockWeathering("exposed_steel_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_steel_plates);
	public static final Block exposed_steel_planks = new BlockWeathering("exposed_steel_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_steel_planks);
	public static final Block gold_plates = new BlockBase("gold_plates", Material.IRON, 1.5F, 2.2F, SoundType.METAL);
	public static final Block iron_plates = new BlockWeathering("iron_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, exposed_iron_plates);
	public static final Block netherite_plates = new BlockBase("netherite_plates", Material.IRON, 25F, 600F, SoundType.NETHERITE);
	public static final Block copper_plates = new BlockWeathering("copper_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, exposed_copper_plates);
	public static final Block aluminium_plates = new BlockBase("aluminium_plates", Material.IRON, 1.5F, 2F, SoundType.METAL);
	public static final Block electrolyt_copper_plates = new BlockBase("electrolyt_copper_plates", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block steel_plates = new BlockWeathering("steel_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, exposed_steel_plates);
	public static final Block redstone_alloy_plates = new BlockPowerEmiting("redstone_alloy_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, 4);
	public static final Block tin_plates = new BlockWeathering("tin_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, exposed_tin_plates);
	public static final Block silver_plates = new BlockBase("silver_plates", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block palladium_plates = new BlockBase("palladium_plates", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block wolfram_plates = new BlockBase("wolfram_plates", Material.IRON, 3.5F, 3.7F, SoundType.METAL);
	public static final Block nickel_plates = new BlockBase("nickel_plates", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block monel_plates = new BlockBase("monel_plates", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block gold_planks = new BlockBase("gold_planks", Material.IRON, 1.5F, 2F, SoundType.METAL);
	public static final Block iron_planks = new BlockWeathering("iron_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, exposed_iron_planks);
	public static final Block copper_planks = new BlockWeathering("copper_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, exposed_copper_planks);
	public static final Block aluminium_planks = new BlockBase("aluminium_planks", Material.IRON, 1.5F, 2F, SoundType.METAL);
	public static final Block electrolyt_copper_planks = new BlockBase("electrolyt_copper_planks", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block steel_planks = new BlockWeathering("steel_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, exposed_steel_planks);
	public static final Block redstone_alloy_planks = new BlockPowerEmiting("redstone_alloy_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, 4);
	public static final Block tin_planks = new BlockWeathering("tin_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, exposed_tin_planks);
	public static final Block silver_planks = new BlockBase("silver_planks", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block palladium_planks = new BlockBase("palladium_planks", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block wolfram_planks = new BlockBase("wolfram_planks", Material.IRON, 3.5F, 3.7F, SoundType.METAL);
	public static final Block nickel_planks = new BlockBase("nickel_planks", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block monel_planks = new BlockBase("monel_planks", Material.IRON, 2.5F, 3F, SoundType.METAL);
	public static final Block netherite_planks = new BlockBase("netherite_planks", Material.IRON, 25F, 600F, SoundType.NETHERITE);
	public static final Block chiseled_smooth_stone = new BlockBase("chiseled_smooth_stone", Material.ROCK, 2F, 6F, SoundType.STONE);
	public static final Block smooth_cobblestone = new BlockBase("smooth_cobblestone", Material.ROCK, 2F, 6F, SoundType.STONE);
	public static final Block salt_block = new BlockBase("salt_block", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block salt_bricks = new BlockBase("salt_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block cracked_salt_bricks = new BlockBase("cracked_salt_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block polished_andesite_bricks = new BlockBase("polished_andesite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block polished_diorite_bricks = new BlockBase("polished_diorite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block polished_granite_bricks = new BlockBase("polished_granite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block polished_wolframite_bricks = new BlockBase("polished_wolframite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block polished_bauxite_bricks = new BlockBase("polished_bauxite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block cracked_polished_andesite_bricks = new BlockBase("cracked_polished_andesite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block cracked_polished_diorite_bricks = new BlockBase("cracked_polished_diorite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block cracked_polished_granite_bricks = new BlockBase("cracked_polished_granite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block cracked_polished_wolframite_bricks = new BlockBase("cracked_polished_wolframite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block cracked_polished_bauxite_bricks = new BlockBase("cracked_polished_bauxite_bricks", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block limestone = new BlockBase("limestone", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block limestone_sheet = new BlockLimestoneSheet();
	public static final Block polished_wolframite = new BlockBase("polished_wolframite", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block polished_bauxite = new BlockBase("polished_bauxite", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block white_painted_planks = new BlockBurnable("white_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block orange_painted_planks = new BlockBurnable("orange_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block magenta_painted_planks = new BlockBurnable("magenta_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block light_blue_painted_planks = new BlockBurnable("light_blue_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block yellow_painted_planks = new BlockBurnable("yellow_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block lime_painted_planks = new BlockBurnable("lime_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block pink_painted_planks = new BlockBurnable("pink_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block gray_painted_planks = new BlockBurnable("gray_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block light_gray_painted_planks = new BlockBurnable("light_gray_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block cyan_painted_planks = new BlockBurnable("cyan_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block purple_painted_planks = new BlockBurnable("purple_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block blue_painted_planks = new BlockBurnable("blue_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block brown_painted_planks = new BlockBurnable("brown_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block green_painted_planks = new BlockBurnable("green_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block red_painted_planks = new BlockBurnable("red_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	public static final Block black_painted_planks = new BlockBurnable("black_painted_planks", Material.WOOD, 2, 3, SoundType.WOOD, 5, 20, 300 , true);
	
	public static final Block stone_corner = new BlockCornerBlockBase("stone_corner", () -> Blocks.STONE.getDefaultState(), AbstractBlock.Properties.create(Material.ROCK).sound(SoundType.STONE));
	
	// Nature Blocks
	public static final Block salsola_seeds = new BlockSalsolaSeeds();
	public static final Block rubber_log = new BlockRubberLog("rubber_log", Material.WOOD, 2F, SoundType.WOOD);
	public static final Block rubber_wood = new BlockRubberLog("rubber_wood", Material.WOOD, 2F, SoundType.WOOD);
	public static final Block rubber_leaves = new BlockLeavesBase("rubber_leaves", Material.LEAVES, 0.2F, 0.2F, SoundType.PLANT);
	public static final Block rubber_sapling = new BlockSaplingBase("rubber_sapling", new ResourceLocation(Industria.MODID, "nature/rubber_tree"));
	public static final Block maple_log = new BlockLogBase("maple_log", Material.WOOD, 2F, SoundType.WOOD);
	public static final Block maple_wood = new BlockLogBase("maple_wood", Material.WOOD, 2F, SoundType.WOOD);
	public static final Block maple_leaves = new BlockLeavesBase("maple_leaves", Material.LEAVES, 0.2F, 0.2F, SoundType.PLANT);
	public static final Block mangrove_log = new BlockLogBase("mangrove_log", Material.WOOD, 2F, SoundType.WOOD);
	public static final Block mangrove_wood = new BlockLogBase("mangrove_wood", Material.WOOD, 2F, SoundType.WOOD);
	public static final Block mangrove_leaves = new BlockLeavesBase("mangrove_leaves", Material.LEAVES, 0.2F, 0.2F, SoundType.PLANT);
	
	// Util Blocks
	public static final Block reinforced_casing = new BlockReinforcedCasing();
	public static final Block ender_core = new BlockEnderCore();
	public static final Block ash = new BlockFallingDust("ash");
	
	// Fluids and Buckets
	public static final Block steam = new BlockSteam();
	public static final Item steam_bucket = new ItemGasBucket(ModFluids.STEAM, "steam_bucket", MATERIALS);
	public static final Block destilled_water = new BlockDestilledWater();
	public static final Item destilled_water_bucket = new ItemFluidBucket(ModFluids.DESTILLED_WATER, "destilled_water_bucket", MATERIALS);
	public static final Block sulfuric_acid = new BlockSulfuricAcid();
	public static final Item sulfuric_acid_bucket = new ItemFluidBucket(ModFluids.SULFURIC_ACID, "sulfuric_acid_bucket", MATERIALS);
	public static final Block natron_lye = new BlockNatronLye();
	public static final Item natron_lye_bucket = new ItemFluidBucket(ModFluids.NATRON_LYE, "natron_lye_bucket", MATERIALS);
	public static final Block chemical_water = new BlockChemicalWater();
	public static final Item chemical_water_bucket = new ItemFluidBucket(ModFluids.CHEMICAL_WATER, "chemical_water_bucket", MATERIALS);
	public static final Block iron_solution = new BlockOreSolution("iron_solution", ModFluids.IRON_SOLUTION);
	public static final Item iron_solution_bucket = new ItemFluidBucket(ModFluids.IRON_SOLUTION, "iron_solution_bucket", MATERIALS);
	public static final Block copper_solution = new BlockOreSolution("copper_solution", ModFluids.COPPER_SOLUTION);
	public static final Item copper_solution_bucket = new ItemFluidBucket(ModFluids.COPPER_SOLUTION, "copper_solution_bucket", MATERIALS);
	public static final Block aluminium_solution = new BlockOreSolution("aluminium_solution", ModFluids.ALUMINIUM_SOLUTION);
	public static final Item aluminium_solution_bucket = new ItemFluidBucket(ModFluids.ALUMINIUM_SOLUTION, "aluminium_solution_bucket", MATERIALS);
	public static final Block wolfram_solution = new BlockOreSolution("wolfram_solution", ModFluids.WOLFRAM_SOLUTION);
	public static final Item wolfram_solution_bucket = new ItemFluidBucket(ModFluids.WOLFRAM_SOLUTION, "wolfram_solution_bucket", MATERIALS);
	public static final Block tin_solution = new BlockOreSolution("tin_solution", ModFluids.TIN_SOLUTION);
	public static final Item tin_solution_bucket = new ItemFluidBucket(ModFluids.TIN_SOLUTION, "tin_solution_bucket", MATERIALS);
	public static final Block raw_oil = new BlockRawOil();
	public static final Item raw_oil_bucket = new ItemFluidBucket(ModFluids.RAW_OIL, "raw_oil_bucket", MATERIALS);
	public static final Block compressed_air = new BlockCompressedAir();
	public static final Item compressed_air_bucket = new ItemGasBucket(ModFluids.COMPRESSED_AIR, "compressed_air_bucket", MATERIALS);
	
	// Ingots and Nuggets
	public static final Item monel_ingot = new ItemBase("monel_ingot", MATERIALS);
	public static final Item tin_ingot = new ItemBase("tin_ingot", MATERIALS);
	public static final Item silver_ingot = new ItemBase("silver_ingot", MATERIALS);
	public static final Item palladium_ingot = new ItemBase("palladium_ingot", MATERIALS);
	public static final Item wolfram_ingot = new ItemBase("wolfram_ingot", MATERIALS);
	public static final Item nickel_ingot = new ItemBase("nickel_ingot", MATERIALS);
	public static final Item copper_ingot = new ItemBase("copper_ingot", MATERIALS);
	public static final Item electrolyt_copper_ingot = new ItemBase("electrolyt_copper_ingot", MATERIALS);
	public static final Item aluminium_ingot = new ItemBase("aluminium_ingot", MATERIALS);
	public static final Item steel_ingot = new ItemBase("steel_ingot", MATERIALS);
	public static final Item redstone_ingot = new ItemBase("redstone_ingot", MATERIALS);
	public static final Item monel_nugget = new ItemBase("monel_nugget", MATERIALS);
	public static final Item tin_nugget = new ItemBase("tin_nugget", MATERIALS);
	public static final Item silver_nugget = new ItemBase("silver_nugget", MATERIALS);
	public static final Item palladium_nugget = new ItemBase("palladium_nugget", MATERIALS);
	public static final Item wolfram_nugget = new ItemBase("wolfram_nugget", MATERIALS);
	public static final Item nickel_nugget = new ItemBase("nickel_nugget", MATERIALS);
	public static final Item copper_nugget = new ItemBase("copper_nugget", MATERIALS);
	public static final Item electrolyt_copper_nugget = new ItemBase("electrolyt_copper_nugget", MATERIALS);
	public static final Item aluminium_nugget = new ItemBase("aluminium_nugget", MATERIALS);
	public static final Item steel_nugget = new ItemBase("steel_nugget", MATERIALS);
	public static final Item redstone_nugget = new ItemBase("redstone_nugget", MATERIALS);
	public static final Item monel_dust = new ItemBase("monel_dust", MATERIALS);
	public static final Item tin_dust = new ItemBase("tin_dust", MATERIALS);
	public static final Item silver_dust = new ItemBase("silver_dust", MATERIALS);
	public static final Item palladium_dust = new ItemBase("palladium_dust", MATERIALS);
	public static final Item wolfram_dust = new ItemBase("wolfram_dust", MATERIALS);
	public static final Item nickel_dust = new ItemBase("nickel_dust", MATERIALS);
	public static final Item copper_dust = new ItemBase("copper_dust", MATERIALS);
	public static final Item electrolyt_copper_dust = new ItemBase("electrolyt_copper_dust", MATERIALS);
	public static final Item aluminium_dust = new ItemBase("aluminium_dust", MATERIALS);
	public static final Item steel_dust = new ItemBase("steel_dust", MATERIALS);
	public static final Item redstone_alloy_dust = new ItemBase("redstone_alloy_dust", MATERIALS);
	public static final Item iron_dust = new ItemBase("iron_dust", MATERIALS);
	public static final Item gold_dust = new ItemBase("gold_dust", MATERIALS);
	public static final Item tpo_iron_dust = new ItemBase("tpo_iron_dust", MATERIALS);
	public static final Item tpo_gold_dust = new ItemBase("tpo_gold_dust", MATERIALS);
	public static final Item tpo_monel_dust = new ItemBase("tpo_monel_dust", MATERIALS);
	public static final Item tpo_tin_dust = new ItemBase("tpo_tin_dust", MATERIALS);
	public static final Item tpo_silver_dust = new ItemBase("tpo_silver_dust", MATERIALS);
	public static final Item tpo_palladium_dust = new ItemBase("tpo_palladium_dust", MATERIALS);
	public static final Item tpo_wolfram_dust = new ItemBase("tpo_wolfram_dust", MATERIALS);
	public static final Item tpo_nickel_dust = new ItemBase("tpo_nickel_dust", MATERIALS);
	public static final Item tpo_copper_dust = new ItemBase("tpo_copper_dust", MATERIALS);
	public static final Item tpo_electrolyt_copper_dust = new ItemBase("tpo_electrolyt_copper_dust", MATERIALS);
	public static final Item tpo_aluminium_dust = new ItemBase("tpo_aluminium_dust", MATERIALS);
	public static final Item tpo_steel_dust = new ItemBase("tpo_steel_dust", MATERIALS);
	public static final Item tpo_redstone_alloy_dust = new ItemBase("tpo_redstone_alloy_dust", MATERIALS);
	public static final Item monel_plate = new ItemBase("monel_plate", MATERIALS);
	public static final Item tin_plate = new ItemBase("tin_plate", MATERIALS);
	public static final Item silver_plate = new ItemBase("silver_plate", MATERIALS);
	public static final Item palladium_plate = new ItemBase("palladium_plate", MATERIALS);
	public static final Item wolfram_plate = new ItemBase("wolfram_plate", MATERIALS);
	public static final Item nickel_plate = new ItemBase("nickel_plate", MATERIALS);
	public static final Item copper_plate = new ItemBase("copper_plate", MATERIALS);
	public static final Item electrolyt_copper_plate = new ItemBase("electrolyt_copper_plate", MATERIALS);
	public static final Item aluminium_plate = new ItemBase("aluminium_plate", MATERIALS);
	public static final Item steel_plate = new ItemBase("steel_plate", MATERIALS);
	public static final Item redstone_alloy_plate = new ItemBase("redstone_alloy_plate", MATERIALS);
	public static final Item iron_plate = new ItemBase("iron_plate", MATERIALS);
	public static final Item gold_plate = new ItemBase("gold_plate", MATERIALS);
	public static final Item netherite_plate = new ItemBase("netherite_plate", MATERIALS);
	
	// Resource Items
	public static final Item salsola = new ItemSalsola();
	public static final Item crushed_stone = new ItemBase("crushed_stone", MATERIALS);
	public static final Item crushed_blackstone = new ItemBase("crushed_blackstone", MATERIALS);
	public static final Item crushed_netherrack = new ItemBase("crushed_netherrack", MATERIALS);
	public static final Item crushed_bauxite = new ItemBase("crushed_bauxite", MATERIALS);
	public static final Item crushed_wolframite = new ItemBase("crushed_wolframite", MATERIALS);
	public static final Item crushed_iron_ore = new ItemBase("crushed_iron_ore", MATERIALS);
	public static final Item crushed_copper_ore = new ItemBase("crushed_copper_ore", MATERIALS);
	public static final Item crushed_gold_ore = new ItemBase("crushed_gold_ore", MATERIALS);
	public static final Item crushed_tin_ore = new ItemBase("crushed_tin_ore", MATERIALS);
	public static final Item crushed_silver_ore = new ItemBase("crushed_silver_ore", MATERIALS);
	public static final Item crushed_palladium_ore = new ItemBase("crushed_palladium_ore", MATERIALS);
	public static final Item crushed_nickel_ore = new ItemBase("crushed_nickel_ore", MATERIALS);
	public static final Item iron_oxid = new ItemBase("iron_oxid", MATERIALS);
	public static final Item copper_oxid = new ItemBase("copper_oxid", MATERIALS);
	public static final Item tin_oxid = new ItemBase("tin_oxid", MATERIALS);
	public static final Item pure_gold = new ItemBase("pure_gold", MATERIALS);
	public static final Item pure_silver = new ItemBase("pure_silver", MATERIALS);
	public static final Item pure_palladium = new ItemBase("pure_palladium", MATERIALS);
	public static final Item pure_nickel = new ItemBase("pure_nickel", MATERIALS);
	public static final Item pure_tin_ore = new ItemBase("pure_tin_ore", MATERIALS);
	public static final Item pure_iron_ore = new ItemBase("pure_iron_ore", MATERIALS);
	public static final Item pure_copper_ore = new ItemBase("pure_copper_ore", MATERIALS);
	public static final Item pure_gold_ore = new ItemBase("pure_gold_ore", MATERIALS);
	public static final Item pure_silver_ore = new ItemBase("pure_silver_ore", MATERIALS);
	public static final Item pure_palladium_ore = new ItemBase("pure_palladium_ore", MATERIALS);
	public static final Item pure_nickel_ore = new ItemBase("pure_nickel_ore", MATERIALS);
	public static final Item sulfur = new ItemBase("sulfur", MATERIALS);
	public static final Item crude_steel = new ItemBase("crude_steel", MATERIALS);
	public static final Item lime = new ItemBase("lime", MATERIALS);
	
	// Crafting items
	public static final Item bearing = new ItemBase("bearing", MATERIALS);
	public static final Item spring = new ItemBase("spring", MATERIALS);
	public static final Item led = new ItemBase("led", MATERIALS);
	public static final Item turbin = new ItemBase("turbin", MATERIALS);
	public static final Item condensator = new ItemBase("condensator", MATERIALS);
	public static final Item resistor = new ItemBase("resistor", MATERIALS);
	public static final Item rotor = new ItemBase("rotor", MATERIALS);
	public static final Item motor_coil = new ItemBase("motor_coil", MATERIALS);
	public static final Item copper_wire = new ItemBase("copper_wire", MATERIALS);
	public static final Item electrolyt_copper_wire = new ItemBase("electrolyt_copper_wire", MATERIALS);
	public static final Item aluminium_wire = new ItemBase("aluminium_wire", MATERIALS);
	public static final Item rubber = new ItemBase("rubber", MATERIALS);
	public static final Item plastic_pellets = new ItemBase("plastic_pellets", MATERIALS);
	public static final Item plastic_plate = new ItemBase("plastic_plate", MATERIALS);
	public static final Item polymer_resin = new ItemBase("polymer_resin", MATERIALS);
	public static final Item coal_coke = new ItemBase("coal_coke", MATERIALS);
	public static final Item silicon = new ItemBase("silicon", MATERIALS);
	public static final Item electrolyt_paper = new ItemBase("electrolyt_paper", MATERIALS);
	public static final Item salt = new ItemBase("salt", MATERIALS);
	public static final Item natrium = new ItemBase("natrium", MATERIALS);
	public static final Item raw_rubber_bottle = new ItemBase("raw_rubber_bottle", MATERIALS, 16);
	public static final Item rubber_bottle = new ItemBase("rubber_bottle", MATERIALS, 16, Items.GLASS_BOTTLE);
	public static final Item cardboard_sheet = new ItemBurneable("cardboard_sheet", MATERIALS, 64);
	
	// Functional Items
	public static final Item remote_control = new ItemRemoteControll();
	public static final Item iron_processor = new ItemProcessor("iron_processor", 4, false, Rarity.UNCOMMON);
	public static final Item redstone_processor = new ItemProcessor("redstone_processor", 7, false, Rarity.UNCOMMON);
	public static final Item emerald_processor = new ItemProcessor("emerald_processor", 10, false, Rarity.RARE);
	public static final Item netherite_processor = new ItemProcessor("netherite_processor", 14, true, Rarity.EPIC);
	public static final Item empty_blueprint = new ItemEmptyBlueprint();
	public static final Item blueprint = new ItemBlueprint();
	public static final Item lever_element = new ItemLeverElement();
	public static final Item button_element = new ItemButtonElement();
	public static final Item lamp_element = new ItemLampElement();
	public static final Item fuse_elv = new ItemFuse("fuse_elv", 8);
	public static final Item fuse_lv = new ItemFuse("fuse_lv", 16);
	public static final Item fuse_nv = new ItemFuse("fuse_nv", 32);
	public static final Item fuse_hv = new ItemFuse("fuse_hv", 64);
	public static final Item schredder_crusher = new ItemSchredderToolCrusher();
	public static final Item hard_drive = new ItemHardDrive();
	public static final Item structure_cladding_pane = new ItemStructureCladdingPane();
	
	// Tools
	public static final Item fluid_meter = new ItemFluidMeter();
	public static final Item energy_meter = new ItemEnergyMeter();
	public static final Item hammer = new ItemHammer();
	public static final Item cutter = new ItemCutter();
	public static final Block tree_tap = new BlockTreeTap();
	public static final Item network_configurtor = new ItemNetworkConfigurator();
	
	public Industria() {
		
		// register Blocks
		ModGameRegistry.registerBlock(capacitor, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(pulse_counter, ItemGroup.REDSTONE); 
		ModGameRegistry.registerBlock(stacked_redstone_torch, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(stacked_redstone_wire, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(advanced_piston, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(advanced_sticky_piston, ItemGroup.REDSTONE);
		ModGameRegistry.registerTechnicalBlock(advanced_piston_head);
		ModGameRegistry.registerTechnicalBlock(advanced_moving_block);
		ModGameRegistry.registerBlock(redstone_reciver, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(signal_wire, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(antenna_conector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(linear_conector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(storing_crafting_table, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(signal_processor_contact, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(rail_piston, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(conector_block, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(redstone_contact, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(button_block, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(radial_conector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(hover_controler, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(hover_extension, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(controll_panel, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(harvester, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(copper_cable, MACHINES);
		ModGameRegistry.registerBlock(electrolyt_copper_cable, MACHINES);
		ModGameRegistry.registerBlock(aluminium_cable, MACHINES);
		ModGameRegistry.registerBlock(burned_cable, MACHINES);
		ModGameRegistry.registerBlock(infinity_power_source, null);
		ModGameRegistry.registerBlock(panel_lamp, MACHINES);
		ModGameRegistry.registerBlock(steel_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(copper_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(aluminium_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(electrolyt_copper_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(redstone_alloy_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(tin_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(palladium_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(silver_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(wolfram_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(nickel_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(monel_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(tin_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(silver_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(palladium_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(copper_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(nickel_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(bauxit, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(bauxit_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(wolframit, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(wolframit_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(gold_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(steel_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(aluminium_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(electrolyt_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(redstone_alloy_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(silver_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(palladium_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(wolfram_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(nickel_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(monel_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(netherite_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(gold_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(steel_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(aluminium_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(electrolyt_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(redstone_alloy_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(silver_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(palladium_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(wolfram_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(nickel_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(monel_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(netherite_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(sulfur_ore, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(reinforced_casing, DECORATIONS);
		ModGameRegistry.registerBlock(ender_core, DECORATIONS);
		ModGameRegistry.registerBlock(preassure_pipe, MACHINES);
		ModGameRegistry.registerBlock(pipe_preassurizer, MACHINES);
		ModGameRegistry.registerBlock(air_compressor, MACHINES);
		ModGameRegistry.registerBlock(item_distributor, MACHINES);
		ModGameRegistry.registerBlock(preassure_pipe_item_terminal, MACHINES);
		
		ModGameRegistry.registerBlock(smooth_cobblestone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(chiseled_smooth_stone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(iron_rod, DECORATIONS);
		ModGameRegistry.registerBlock(stone_corner, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(salsola_seeds, MATERIALS);
		ModGameRegistry.registerBlock(jigsaw, null, Rarity.EPIC);
		ModGameRegistry.registerBlock(generator, MACHINES);
		ModGameRegistry.registerBlock(fluid_pipe, MACHINES);
		ModGameRegistry.registerBlock(fluid_valve, MACHINES);
		ModGameRegistry.registerBlock(fluid_input, MACHINES);
		ModGameRegistry.registerBlock(fluid_output, MACHINES);
		ModGameRegistry.registerBlock(steam_generator, MACHINES);
		ModGameRegistry.registerBlock(coal_heater, MACHINES);
		ModGameRegistry.registerBlock(transformator_coil, MACHINES);
		ModGameRegistry.registerBlock(transformator_contact, MACHINES);
		ModGameRegistry.registerBlock(fuse_box, MACHINES);
		ModGameRegistry.registerBlock(multimeter, MACHINES);
		ModGameRegistry.registerBlock(power_switch, MACHINES);
		ModGameRegistry.registerBlock(electric_furnace, MACHINES);
		ModGameRegistry.registerBlock(schredder, MACHINES);
		ModGameRegistry.registerBlock(blender, MACHINES);
		ModGameRegistry.registerBlock(raffinery, MACHINES);
		ModGameRegistry.registerBlock(alloy_furnace, MACHINES);
		ModGameRegistry.registerBlock(conveyor_belt, MACHINES);
		ModGameRegistry.registerBlock(conveyor_spliter, MACHINES);
		ModGameRegistry.registerBlock(conveyor_switch, MACHINES);
		ModGameRegistry.registerBlock(thermal_zentrifuge, MACHINES);
		ModGameRegistry.registerBlock(fluid_bath, MACHINES);
		ModGameRegistry.registerBlock(blast_furnace, MACHINES);
		ModGameRegistry.registerBlock(rubber_log, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(rubber_wood, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(rubber_leaves, DECORATIONS);
		ModGameRegistry.registerBlock(rubber_sapling, DECORATIONS);
		ModGameRegistry.registerBlock(maple_log, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(maple_wood, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(maple_leaves, DECORATIONS);
		
		ModGameRegistry.registerBlock(mangrove_log, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(mangrove_wood, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(mangrove_leaves, DECORATIONS);
		
		ModGameRegistry.registerBlock(tree_tap, TOOLS);
		ModGameRegistry.registerBlock(salt_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(salt_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(cracked_salt_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(polished_andesite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(polished_diorite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(polished_granite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(polished_wolframite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(polished_bauxite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(cracked_polished_andesite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(cracked_polished_diorite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(cracked_polished_granite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(cracked_polished_wolframite_bricks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(cracked_polished_bauxite_bricks, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(exposed_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_steel_plates, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(oxidized_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_steel_plates, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(weathered_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_steel_plates, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(exposed_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_steel_planks, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(oxidized_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_steel_planks, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(weathered_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_steel_planks, BUILDING_BLOCKS);
		
		ModGameRegistry.registerBlock(cardboard_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(polished_wolframite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(polished_bauxite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(white_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(orange_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(magenta_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(light_blue_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(yellow_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(lime_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(pink_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(gray_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(light_gray_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(cyan_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(purple_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(blue_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(brown_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(green_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(red_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(black_painted_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(limestone, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(limestone_sheet, DECORATIONS);
		ModGameRegistry.registerBlock(item_detector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(computer, MACHINES);
		ModGameRegistry.registerBlock(network_cable, MACHINES);
		ModGameRegistry.registerBlock(steel_rail, ItemGroup.TRANSPORTATION);
		ModGameRegistry.registerBlock(inductive_rail, ItemGroup.TRANSPORTATION);
		ModGameRegistry.registerBlock(rail_adapter, ItemGroup.TRANSPORTATION);
		ModGameRegistry.registerBlock(chunk_loader, MACHINES);
		ModGameRegistry.registerBlock(clean_cladding_black, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(clean_cladding_white, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(structure_scaffold, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(ash, DECORATIONS);
		
		ModGameRegistry.registerBlock(motor, MACHINES);
		
		ModGameRegistry.registerTechnicalBlock(steam);
		ModGameRegistry.registerItem(steam_bucket);
		ModGameRegistry.registerTechnicalBlock(destilled_water);
		ModGameRegistry.registerItem(destilled_water_bucket);
		ModGameRegistry.registerTechnicalBlock(sulfuric_acid);
		ModGameRegistry.registerItem(sulfuric_acid_bucket);
		ModGameRegistry.registerTechnicalBlock(natron_lye);
		ModGameRegistry.registerItem(natron_lye_bucket);
		ModGameRegistry.registerTechnicalBlock(raw_oil);
		ModGameRegistry.registerItem(raw_oil_bucket);
		
		ModGameRegistry.registerTechnicalBlock(iron_solution);
		ModGameRegistry.registerItem(iron_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(copper_solution);
		ModGameRegistry.registerItem(copper_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(aluminium_solution);
		ModGameRegistry.registerItem(aluminium_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(wolfram_solution);
		ModGameRegistry.registerItem(wolfram_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(tin_solution);
		ModGameRegistry.registerItem(tin_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(chemical_water);
		ModGameRegistry.registerItem(chemical_water_bucket);
		ModGameRegistry.registerTechnicalBlock(compressed_air);
		ModGameRegistry.registerItem(compressed_air_bucket);
		
		// register Items
		ModGameRegistry.registerItem(crushed_blackstone);
		ModGameRegistry.registerItem(crushed_netherrack);
		ModGameRegistry.registerItem(crushed_bauxite);
		ModGameRegistry.registerItem(crushed_wolframite);
		ModGameRegistry.registerItem(tin_oxid);
		ModGameRegistry.registerItem(iron_oxid);
		ModGameRegistry.registerItem(copper_oxid);
		ModGameRegistry.registerItem(pure_gold);
		ModGameRegistry.registerItem(pure_silver);
		ModGameRegistry.registerItem(pure_palladium);
		ModGameRegistry.registerItem(pure_nickel);
		ModGameRegistry.registerItem(crushed_tin_ore);
		ModGameRegistry.registerItem(crushed_iron_ore);
		ModGameRegistry.registerItem(crushed_copper_ore);
		ModGameRegistry.registerItem(crushed_nickel_ore);
		ModGameRegistry.registerItem(crushed_gold_ore);
		ModGameRegistry.registerItem(crushed_silver_ore);
		ModGameRegistry.registerItem(crushed_palladium_ore);
		ModGameRegistry.registerItem(pure_tin_ore);
		ModGameRegistry.registerItem(pure_iron_ore);
		ModGameRegistry.registerItem(pure_copper_ore);
		ModGameRegistry.registerItem(pure_nickel_ore);
		ModGameRegistry.registerItem(pure_gold_ore);
		ModGameRegistry.registerItem(pure_silver_ore);
		ModGameRegistry.registerItem(pure_palladium_ore);
		ModGameRegistry.registerItem(crushed_stone);
		ModGameRegistry.registerItem(redstone_ingot);
		ModGameRegistry.registerItem(remote_control);
		ModGameRegistry.registerItem(iron_processor);
		ModGameRegistry.registerItem(redstone_processor);
		ModGameRegistry.registerItem(emerald_processor);
		ModGameRegistry.registerItem(netherite_processor);
		ModGameRegistry.registerItem(salsola);
		ModGameRegistry.registerItem(empty_blueprint);
		ModGameRegistry.registerItem(blueprint);
		ModGameRegistry.registerItem(copper_ingot);
		ModGameRegistry.registerItem(copper_nugget);
		ModGameRegistry.registerItem(electrolyt_copper_ingot);
		ModGameRegistry.registerItem(electrolyt_copper_nugget);
		ModGameRegistry.registerItem(steel_ingot);
		ModGameRegistry.registerItem(steel_nugget);
		ModGameRegistry.registerItem(aluminium_ingot);
		ModGameRegistry.registerItem(aluminium_nugget);
		ModGameRegistry.registerItem(redstone_nugget);
		ModGameRegistry.registerItem(tin_ingot);
		ModGameRegistry.registerItem(tin_nugget);
		ModGameRegistry.registerItem(wolfram_ingot);
		ModGameRegistry.registerItem(wolfram_nugget);
		ModGameRegistry.registerItem(silver_ingot);
		ModGameRegistry.registerItem(silver_nugget);
		ModGameRegistry.registerItem(palladium_ingot);
		ModGameRegistry.registerItem(palladium_nugget);
		ModGameRegistry.registerItem(nickel_ingot);
		ModGameRegistry.registerItem(nickel_nugget);
		ModGameRegistry.registerItem(monel_ingot);
		ModGameRegistry.registerItem(monel_nugget);
		ModGameRegistry.registerItem(tpo_copper_dust);
		ModGameRegistry.registerItem(copper_dust);
		ModGameRegistry.registerItem(electrolyt_copper_dust);
		ModGameRegistry.registerItem(tpo_electrolyt_copper_dust);
		ModGameRegistry.registerItem(steel_dust);
		ModGameRegistry.registerItem(tpo_steel_dust);
		ModGameRegistry.registerItem(aluminium_dust);
		ModGameRegistry.registerItem(tpo_aluminium_dust);
		ModGameRegistry.registerItem(redstone_alloy_dust);
		ModGameRegistry.registerItem(tpo_redstone_alloy_dust);
		ModGameRegistry.registerItem(aluminium_dust);
		ModGameRegistry.registerItem(tin_dust);
		ModGameRegistry.registerItem(tpo_tin_dust);
		ModGameRegistry.registerItem(wolfram_dust);
		ModGameRegistry.registerItem(tpo_wolfram_dust);
		ModGameRegistry.registerItem(silver_dust);
		ModGameRegistry.registerItem(tpo_silver_dust);
		ModGameRegistry.registerItem(palladium_dust);
		ModGameRegistry.registerItem(tpo_palladium_dust);
		ModGameRegistry.registerItem(nickel_dust);
		ModGameRegistry.registerItem(tpo_nickel_dust);
		ModGameRegistry.registerItem(monel_dust);
		ModGameRegistry.registerItem(tpo_monel_dust);
		ModGameRegistry.registerItem(iron_dust);
		ModGameRegistry.registerItem(tpo_iron_dust);
		ModGameRegistry.registerItem(gold_dust);
		ModGameRegistry.registerItem(tpo_gold_dust);
		ModGameRegistry.registerItem(copper_plate);
		ModGameRegistry.registerItem(electrolyt_copper_plate);
		ModGameRegistry.registerItem(steel_plate);
		ModGameRegistry.registerItem(aluminium_plate);
		ModGameRegistry.registerItem(redstone_alloy_plate);
		ModGameRegistry.registerItem(tin_plate);
		ModGameRegistry.registerItem(wolfram_plate);
		ModGameRegistry.registerItem(silver_plate);
		ModGameRegistry.registerItem(palladium_plate);
		ModGameRegistry.registerItem(nickel_plate);
		ModGameRegistry.registerItem(monel_plate);
		ModGameRegistry.registerItem(gold_plate);
		ModGameRegistry.registerItem(iron_plate);
		ModGameRegistry.registerItem(netherite_plate);
		ModGameRegistry.registerItem(hammer);
		ModGameRegistry.registerItem(cutter);
		ModGameRegistry.registerItem(aluminium_wire);
		ModGameRegistry.registerItem(copper_wire);
		ModGameRegistry.registerItem(electrolyt_copper_wire);
		ModGameRegistry.registerItem(rotor);
		ModGameRegistry.registerItem(motor_coil);
		ModGameRegistry.registerItem(plastic_pellets);
		ModGameRegistry.registerItem(polymer_resin);
		ModGameRegistry.registerItem(rubber);
		ModGameRegistry.registerItem(resistor);
		ModGameRegistry.registerItem(condensator);
		ModGameRegistry.registerItem(turbin);
		ModGameRegistry.registerItem(led);
		ModGameRegistry.registerItem(spring);
		ModGameRegistry.registerItem(bearing);
		ModGameRegistry.registerItem(coal_coke);
		ModGameRegistry.registerItem(silicon);
		ModGameRegistry.registerItem(electrolyt_paper);
		ModGameRegistry.registerItem(salt);
		ModGameRegistry.registerItem(natrium);
		ModGameRegistry.registerItem(fluid_meter);
		ModGameRegistry.registerItem(energy_meter);
		ModGameRegistry.registerItem(fuse_elv);
		ModGameRegistry.registerItem(fuse_lv);
		ModGameRegistry.registerItem(fuse_nv);
		ModGameRegistry.registerItem(fuse_hv);
		ModGameRegistry.registerItem(schredder_crusher);
		ModGameRegistry.registerItem(sulfur);
		ModGameRegistry.registerItem(plastic_plate);
		ModGameRegistry.registerItem(raw_rubber_bottle);
		ModGameRegistry.registerItem(rubber_bottle);
		ModGameRegistry.registerItem(crude_steel);
		ModGameRegistry.registerItem(cardboard_sheet);
		ModGameRegistry.registerItem(lime);
		ModGameRegistry.registerItem(hard_drive);
		ModGameRegistry.registerItem(network_configurtor);
		ModGameRegistry.registerItem(structure_cladding_pane);
		
		// register Functional Items
		ModGameRegistry.registerItem(lever_element);
		ModGameRegistry.registerItem(button_element);
		ModGameRegistry.registerItem(lamp_element);
		
		try {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::setup);
		} catch (BootstrapMethodError e) {
			LOGGER.log(Level.INFO, "Skip ClientSettup on Server start!");
		}
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Server::setup);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onBiomeLoadingEvent);
		
	}
	
	// Registration Code
	
	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			IForgeRegistry<Block> registry = blockRegistryEvent.getRegistry();
			Block[] blocksToRegister = ModGameRegistry.getBlocksToRegister();
			for (Block block : blocksToRegister) {
				registry.register(block);
			}
		}
		
		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
			IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();
			Item[] itemsToRegister = ModGameRegistry.getItemsToRegister();
			for (Item item : itemsToRegister) {
				registry.register(item);
			}
		}
		
	}
	
	//n45_GaqTjuM
	public void onBiomeLoadingEvent(final BiomeLoadingEvent event) {
		for (GenerationStage.Decoration decoration : GenerationStage.Decoration.values()) {
			List<ConfiguredFeature<?, ?>> features = ModGameRegistry.getFeaturesToRegister().getOrDefault(event.getName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>()).getOrDefault(decoration, new ArrayList<>());
			for (ConfiguredFeature<?, ?> feature : features) {
				event.getGeneration().getFeatures(decoration).add(() -> feature);
			}
			List<Supplier<ConfiguredFeature<?, ?>>> featuresToRemove = new ArrayList<Supplier<ConfiguredFeature<?, ?>>>();
			event.getGeneration().getFeatures(decoration).forEach((registredFeature) -> {
				if (registredFeature.get() == Features.ACACIA) featuresToRemove.add(registredFeature);
				// This does not work, registredFeature always is an "minecraft:decorated" feature ...
			});
			event.getGeneration().getFeatures(decoration).removeAll(featuresToRemove);
		}
		
	}
	
}
