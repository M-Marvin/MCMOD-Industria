package de.redtec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.redtec.blocks.BlockAdvancedMovingBlock;
import de.redtec.blocks.BlockAdvancedPiston;
import de.redtec.blocks.BlockAdvancedPistonHead;
import de.redtec.blocks.BlockBase;
import de.redtec.blocks.BlockBurnable;
import de.redtec.blocks.BlockButtonBlock;
import de.redtec.blocks.BlockConectorBlock;
import de.redtec.blocks.BlockControllPanel;
import de.redtec.blocks.BlockConveyorBelt;
import de.redtec.blocks.BlockConveyorSpliter;
import de.redtec.blocks.BlockCornerBlockBase;
import de.redtec.blocks.BlockElektricWire;
import de.redtec.blocks.BlockFluidInput;
import de.redtec.blocks.BlockFluidOutput;
import de.redtec.blocks.BlockFluidPipe;
import de.redtec.blocks.BlockFluidValve;
import de.redtec.blocks.BlockFuseBox;
import de.redtec.blocks.BlockHarvester;
import de.redtec.blocks.BlockHoverControler;
import de.redtec.blocks.BlockHoverExtension;
import de.redtec.blocks.BlockInfinityPowerSource;
import de.redtec.blocks.BlockIronRod;
import de.redtec.blocks.BlockJigsaw;
import de.redtec.blocks.BlockLeavesBase;
import de.redtec.blocks.BlockLinearConector;
import de.redtec.blocks.BlockMAlloyFurnace;
import de.redtec.blocks.BlockMBlender;
import de.redtec.blocks.BlockMCoalHeater;
import de.redtec.blocks.BlockMElectricFurnace;
import de.redtec.blocks.BlockMFluidBath;
import de.redtec.blocks.BlockMGenerator;
import de.redtec.blocks.BlockMMultimeter;
import de.redtec.blocks.BlockMRaffinery;
import de.redtec.blocks.BlockMSchredder;
import de.redtec.blocks.BlockMSteamGenerator;
import de.redtec.blocks.BlockMThermalZentrifuge;
import de.redtec.blocks.BlockMTransformatorCoil;
import de.redtec.blocks.BlockMTransformatorContact;
import de.redtec.blocks.BlockMotor;
import de.redtec.blocks.BlockPanelLamp;
import de.redtec.blocks.BlockPowerEmiting;
import de.redtec.blocks.BlockPowerSwitch;
import de.redtec.blocks.BlockRDCapacitor;
import de.redtec.blocks.BlockRDPulseCounter;
import de.redtec.blocks.BlockRadialConector;
import de.redtec.blocks.BlockRailPiston;
import de.redtec.blocks.BlockRedstoneContact;
import de.redtec.blocks.BlockRedstoneReciver;
import de.redtec.blocks.BlockRubberLog;
import de.redtec.blocks.BlockSalsolaSeeds;
import de.redtec.blocks.BlockSaplingBase;
import de.redtec.blocks.BlockSignalAntennaConector;
import de.redtec.blocks.BlockSignalProcessorContact;
import de.redtec.blocks.BlockSignalWire;
import de.redtec.blocks.BlockStackedRedstoneTorch;
import de.redtec.blocks.BlockStackedRedstoneWire;
import de.redtec.blocks.BlockStoringCraftingTable;
import de.redtec.blocks.BlockTreeTap;
import de.redtec.blocks.BlockWeathering;
import de.redtec.fluids.BlockChemicalWater;
import de.redtec.fluids.BlockDestilledWater;
import de.redtec.fluids.BlockNatronLye;
import de.redtec.fluids.BlockOreSolution;
import de.redtec.fluids.BlockRawOil;
import de.redtec.fluids.BlockSteam;
import de.redtec.fluids.BlockSulfuricAcid;
import de.redtec.fluids.util.ItemFluidBucket;
import de.redtec.fluids.util.ItemGasBucket;
import de.redtec.gui.ScreenHarvester;
import de.redtec.gui.ScreenHoverControler;
import de.redtec.gui.ScreenJigsaw;
import de.redtec.gui.ScreenMAlloyFurnace;
import de.redtec.gui.ScreenMBlender;
import de.redtec.gui.ScreenMCoalHeater;
import de.redtec.gui.ScreenMElectricFurnace;
import de.redtec.gui.ScreenMFluidBath;
import de.redtec.gui.ScreenMGenerator;
import de.redtec.gui.ScreenMRaffinery;
import de.redtec.gui.ScreenMSchredder;
import de.redtec.gui.ScreenMThermalZentrifuge;
import de.redtec.gui.ScreenProcessor;
import de.redtec.gui.ScreenReciver;
import de.redtec.gui.ScreenStoredCrafting;
import de.redtec.items.ItemBase;
import de.redtec.items.ItemBlueprint;
import de.redtec.items.ItemBurneable;
import de.redtec.items.ItemCuter;
import de.redtec.items.ItemEmptyBlueprint;
import de.redtec.items.ItemEnergyMeter;
import de.redtec.items.ItemFluidMeter;
import de.redtec.items.ItemFuse;
import de.redtec.items.ItemHammer;
import de.redtec.items.ItemProcessor;
import de.redtec.items.ItemRemoteControll;
import de.redtec.items.ItemSalsola;
import de.redtec.items.ItemSchredderToolCrusher;
import de.redtec.items.panelitems.ItemButtonElement;
import de.redtec.items.panelitems.ItemLampElement;
import de.redtec.items.panelitems.ItemLeverElement;
import de.redtec.packet.CEditJigsawTileEntityPacket;
import de.redtec.packet.CEditProcessorCodePacket;
import de.redtec.packet.CGenerateJigsaw;
import de.redtec.packet.SSendENHandeler;
import de.redtec.renderer.TileEntityAdvancedMovingBlockRenderer;
import de.redtec.renderer.TileEntityControllPanelRenderer;
import de.redtec.renderer.TileEntityConveyorBeltRenderer;
import de.redtec.renderer.TileEntityFuseBoxRenderer;
import de.redtec.renderer.TileEntityGaugeRenderer;
import de.redtec.renderer.TileEntityMBlenderRenderer;
import de.redtec.renderer.TileEntityMFluidBathRenderer;
import de.redtec.renderer.TileEntityMRaffineryRenderer;
import de.redtec.renderer.TileEntityMSchredderRenderer;
import de.redtec.renderer.TileEntityMSteamGeneratorRenderer;
import de.redtec.renderer.TileEntitySignalProcessorContactRenderer;
import de.redtec.typeregistys.ModConfiguredFeatures;
import de.redtec.typeregistys.ModContainerType;
import de.redtec.typeregistys.ModFluids;
import de.redtec.typeregistys.ModSoundEvents;
import de.redtec.typeregistys.ModTileEntityType;
import de.redtec.util.ModGameRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;

@Mod("redtec")
public class RedTec {
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "redtec";
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(MODID, "main"), 
			() -> RedTec.PROTOCOL_VERSION, 
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
			return new ItemStack(steel_plates);
		}
	};
	public static final ItemGroup DECORATIONS = new ItemGroup("decorations") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(iron_rod);
		}
	};
	public static final ItemGroup TOOLS = new ItemGroup("tools") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(energy_meter);
		}
	};
	public static final ItemGroup MATERIALS = new ItemGroup("materials") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(copper_dust);
		}
	};
	
	// Redstone
	public static final Block pulse_counter = new BlockRDPulseCounter();
	public static final Block capacitor = new BlockRDCapacitor();
	public static final Block stacked_redstone_torch = new BlockStackedRedstoneTorch();
	public static final Block stacked_redstone_wire = new BlockStackedRedstoneWire();
	public static final Block advanced_piston = new BlockAdvancedPiston(false, "advanced_piston");
	public static final Block advanced_piston_head = new BlockAdvancedPistonHead("advanced_piston_head");
	public static final Block advanced_sticky_piston = new BlockAdvancedPiston(true, "advanced_sticky_piston");
	public static final Block advanced_moving_block = new BlockAdvancedMovingBlock("advanced_moving_block");
	public static final Block redstone_reciver = new BlockRedstoneReciver();
	public static final Block signal_wire = new BlockSignalWire();
	public static final Block antenna_conector = new BlockSignalAntennaConector();
	public static final Block linear_conector = new BlockLinearConector();
	public static final Block signal_processor_contact = new BlockSignalProcessorContact();
	public static final Block rail_piston = new BlockRailPiston();
	public static final Block conector_block = new BlockConectorBlock();
	public static final Block redstone_contact = new BlockRedstoneContact();
	public static final Block button_block = new BlockButtonBlock();
	public static final Block iron_rod = new BlockIronRod();
	public static final Block radial_conector = new BlockRadialConector();
	public static final Block hover_controler = new BlockHoverControler();
	public static final Block hover_extension = new BlockHoverExtension();
	public static final Block controll_panel = new BlockControllPanel();
	public static final Block harvester = new BlockHarvester();
	public static final Block jigsaw = new BlockJigsaw();
	
	// TODO
	public static final Block storing_crafting_table = new BlockStoringCraftingTable();	
	// Mechanic
	public static final Block motor = new BlockMotor();
	// Items
	public static final Item sulfur_dioxid = new ItemBase("sulfur_dioxid", MATERIALS);
	
	// Machinery
	public static final Block conveyor_spliter = new BlockConveyorSpliter();
	public static final Block conveyor_belt = new BlockConveyorBelt();
	public static final Block generator = new BlockMGenerator();
	public static final Block infinity_power_source = new BlockInfinityPowerSource();
	public static final Block panel_lamp = new BlockPanelLamp();
	public static final Block copper_cable = new BlockElektricWire("copper_cable", 16, 4);
	public static final Block electrolyt_copper_cable = new BlockElektricWire("electrolyt_copper_cable", 32, 4);
	public static final Block aluminium_cable = new BlockElektricWire("aluminium_cable", 64, 8);
	public static final Block fluid_pipe = new BlockFluidPipe();
	public static final Block fluid_valve = new BlockFluidValve();
	public static final Block fluid_input = new BlockFluidInput();
	public static final Block fluid_output = new BlockFluidOutput();
	public static final Block steam_generator = new BlockMSteamGenerator();
	public static final Block coal_heater = new BlockMCoalHeater();
	public static final Block transformator_contact = new BlockMTransformatorContact();
	public static final Block transformator_coil = new BlockMTransformatorCoil();
	public static final Block fuse_box = new BlockFuseBox();
	public static final Block multimeter = new BlockMMultimeter();
	public static final Block power_switch = new BlockPowerSwitch();
	public static final Block electric_furnace = new BlockMElectricFurnace();
	public static final Block schredder = new BlockMSchredder();
	public static final Block blender = new BlockMBlender();
	public static final Block raffinery = new BlockMRaffinery();
	public static final Block alloy_furnace = new BlockMAlloyFurnace();
	public static final Block thermal_zentrifuge = new BlockMThermalZentrifuge();
	public static final Block fluid_bath = new BlockMFluidBath();
	
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
	
	// Deko Blocks
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
	public static final Block weathered_copper_plates = new BlockWeathering("weathered_copper_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_copper_plates);
	public static final Block weathered_tin_plates = new BlockWeathering("weathered_tin_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_tin_plates);
	public static final Block weathered_iron_plates = new BlockWeathering("weathered_iron_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, oxidized_iron_plates);
	public static final Block exposed_copper_plates = new BlockWeathering("exposed_copper_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_copper_plates);
	public static final Block exposed_tin_plates = new BlockWeathering("exposed_tin_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_tin_plates);
	public static final Block exposed_iron_plates = new BlockWeathering("exposed_iron_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_iron_plates);
	public static final Block exposed_steel_plates = new BlockWeathering("exposed_steel_plates", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_iron_plates);
	public static final Block exposed_steel_planks = new BlockWeathering("exposed_steel_planks", Material.IRON, 2.5F, 3F, SoundType.METAL, weathered_iron_planks);
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
	public static final Block polished_wolframite = new BlockBase("polished_wolframite", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	public static final Block polished_bauxite = new BlockBase("polished_bauxite", Material.ROCK, 1.5F, 6F, SoundType.STONE);
	
	public static final Block stone_corner = new BlockCornerBlockBase("stone_corner", () -> Blocks.STONE.getDefaultState(), AbstractBlock.Properties.create(Material.ROCK).sound(SoundType.STONE));
	
	// Nature Blocks
	public static final Block salsola_seeds = new BlockSalsolaSeeds();
	public static final Block rubber_log = new BlockRubberLog("rubber_log", Material.WOOD, 2F, SoundType.WOOD);
	public static final Block rubber_wood = new BlockRubberLog("rubber_wood", Material.WOOD, 2F, SoundType.WOOD);
	public static final Block rubber_laves = new BlockLeavesBase("rubber_leaves", Material.WOOD, 0.2F, 0.2F, SoundType.PLANT);
	public static final Block rubber_sapling = new BlockSaplingBase("rubber_sapling", new ResourceLocation(RedTec.MODID, "nature/rubber_tree"));
	
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
	
	// Tools
	public static final Item fluid_meter = new ItemFluidMeter();
	public static final Item energy_meter = new ItemEnergyMeter();
	public static final Item hammer = new ItemHammer();
	public static final Item cuter = new ItemCuter();
	public static final Block tree_tap = new BlockTreeTap();
	
	public RedTec() {
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onBiomeLoadingEvent);
		
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
		ModGameRegistry.registerBlock(thermal_zentrifuge, MACHINES);
		ModGameRegistry.registerBlock(fluid_bath, MACHINES);
		ModGameRegistry.registerBlock(rubber_log, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(rubber_wood, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(rubber_laves, DECORATIONS);
		ModGameRegistry.registerBlock(rubber_sapling, DECORATIONS);
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
		ModGameRegistry.registerBlock(oxidized_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_copper_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_tin_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_iron_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(oxidized_iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_copper_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_tin_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(weathered_iron_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(cardboard_block, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_steel_planks, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(exposed_steel_plates, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(polished_wolframite, BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(polished_bauxite, BUILDING_BLOCKS);
		
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
		ModGameRegistry.registerItem(cuter);
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
		ModGameRegistry.registerItem(sulfur_dioxid);
		ModGameRegistry.registerItem(plastic_plate);
		ModGameRegistry.registerItem(raw_rubber_bottle);
		ModGameRegistry.registerItem(rubber_bottle);
		ModGameRegistry.registerItem(crude_steel);
		ModGameRegistry.registerItem(cardboard_sheet);
		
		// register Functional Items
		ModGameRegistry.registerItem(lever_element);
		ModGameRegistry.registerItem(button_element);
		ModGameRegistry.registerItem(lamp_element);
		
	}
	
	@SuppressWarnings("deprecation")
	private void setup(final FMLCommonSetupEvent event) {
		
		// Register Packets
		NETWORK.registerMessage(0, CEditProcessorCodePacket.class, CEditProcessorCodePacket::encode, CEditProcessorCodePacket::new, CEditProcessorCodePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		NETWORK.registerMessage(1, CEditJigsawTileEntityPacket.class, CEditJigsawTileEntityPacket::encode, CEditJigsawTileEntityPacket::new, CEditJigsawTileEntityPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		NETWORK.registerMessage(2, CGenerateJigsaw.class, CGenerateJigsaw::encode, CGenerateJigsaw::new, CGenerateJigsaw::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		NETWORK.registerMessage(3, SSendENHandeler.class, SSendENHandeler::encode, SSendENHandeler::new, SSendENHandeler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));		
		
		// Dispenser Behaviors
		IDispenseItemBehavior placeBlockBehavior = new IDispenseItemBehavior() {
			@Override
			public ItemStack dispense(IBlockSource source, ItemStack stack) {
				BlockPos pos = source.getBlockPos().offset(source.getBlockState().get(BlockStateProperties.FACING));
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block != null && source.getWorld().getBlockState(pos).isAir() && block.isValidPosition(block.getDefaultState(), source.getWorld(), pos)) {
					source.getWorld().setBlockState(pos, block.getDefaultState());
					stack.shrink(1);
					return stack;
				}
				return stack;
			}
		};
		Registry.BLOCK.stream().forEach((block) -> {
			Item item = Item.getItemFromBlock(block);
			if (item != null) {
				if (item != Items.TNT) DispenserBlock.registerDispenseBehavior(item, placeBlockBehavior);
			}
		});
		
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
		
		ModGameRegistry.addFeatureToBiomes(Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.RUBBER_TREE, Biomes.SWAMP_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.MODIFIED_JUNGLE, Biomes.MODIFIED_JUNGLE_EDGE);
		
	}
	
	@OnlyIn(Dist.CLIENT)
	private void clientSetup(final FMLClientSetupEvent event) {
		
		RenderTypeLookup.setRenderLayer(capacitor, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(pulse_counter, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(stacked_redstone_torch, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(stacked_redstone_wire, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(signal_wire, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(signal_processor_contact, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(salsola_seeds, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(steam, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(steam_generator, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.DESTILLED_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_DESTILLED_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.STEAM, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(multimeter, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(fluid_valve, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(blender, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(rubber_laves, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(rubber_sapling, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(tree_tap, RenderType.getCutoutMipped());
		
		RenderTypeLookup.setRenderLayer(ModFluids.SULFURIC_ACID, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_SULFURIC_ACID, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.IRON_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_IRON_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.COPPER_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_COPPER_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.WOLFRAM_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_WOLFRAM_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.ALUMINIUM_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_ALUMINIUM_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.TIN_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_TIN_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.NATRON_LYE, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_NATRON_LYE, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.CHEMICAL_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_CHEMICAL_WATER, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.RAW_OIL, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_RAW_OIL, RenderType.getTranslucent());
		
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.ADVANCED_PISTON, TileEntityAdvancedMovingBlockRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.SIGNAL_PROCESSOR, TileEntitySignalProcessorContactRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.CONTROLL_PANEL, TileEntityControllPanelRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.STEAM_GENERATOR, TileEntityMSteamGeneratorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.FUSE_BOX, TileEntityFuseBoxRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.MULTIMETER, TileEntityGaugeRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.SCHREDDER, TileEntityMSchredderRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.BLENDER, TileEntityMBlenderRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.CONVEYOR_BELT, TileEntityConveyorBeltRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.RAFFINERY, TileEntityMRaffineryRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityType.FLUID_BATH, TileEntityMFluidBathRenderer::new);
		
		ScreenManager.registerFactory(ModContainerType.STORED_CRAFTING, ScreenStoredCrafting::new);
		ScreenManager.registerFactory(ModContainerType.PROCESSOR, ScreenProcessor::new);
		ScreenManager.registerFactory(ModContainerType.HOVER_CONTROLER, ScreenHoverControler::new);
		ScreenManager.registerFactory(ModContainerType.REDSTONE_RECIVER, ScreenReciver::new);
		ScreenManager.registerFactory(ModContainerType.HARVESTER, ScreenHarvester::new);
		ScreenManager.registerFactory(ModContainerType.JIGSAW, ScreenJigsaw::new);
		ScreenManager.registerFactory(ModContainerType.GENERATOR, ScreenMGenerator::new);
		ScreenManager.registerFactory(ModContainerType.COAL_HEATER, ScreenMCoalHeater::new);
		ScreenManager.registerFactory(ModContainerType.ELECTRIC_FURNACE, ScreenMElectricFurnace::new);
		ScreenManager.registerFactory(ModContainerType.SCHREDDER, ScreenMSchredder::new);
		ScreenManager.registerFactory(ModContainerType.BLENDER, ScreenMBlender::new);
		ScreenManager.registerFactory(ModContainerType.RAFFINERY, ScreenMRaffinery::new);
		ScreenManager.registerFactory(ModContainerType.THERMAL_ZENTRIFUGE, ScreenMThermalZentrifuge::new);
		ScreenManager.registerFactory(ModContainerType.ALLOY_FURNACE, ScreenMAlloyFurnace::new);
		ScreenManager.registerFactory(ModContainerType.FLUID_BATH, ScreenMFluidBath::new);
		
	}
	
	// Registration Code
	
	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class Events {
		
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
	
	public void onBiomeLoadingEvent(final BiomeLoadingEvent event) {
		for (GenerationStage.Decoration decoration : GenerationStage.Decoration.values()) {
			List<ConfiguredFeature<?, ?>> features = ModGameRegistry.getFeaturesToRegister().getOrDefault(event.getName(), new HashMap<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>>()).getOrDefault(decoration, new ArrayList<>());
			for (ConfiguredFeature<?, ?> feature : features) {
				event.getGeneration().getFeatures(decoration).add(() -> feature);
			}
		}
	}
	
}
