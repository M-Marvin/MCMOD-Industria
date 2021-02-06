package de.redtec;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.redtec.blocks.BlockAdvancedMovingBlock;
import de.redtec.blocks.BlockAdvancedPiston;
import de.redtec.blocks.BlockAdvancedPistonHead;
import de.redtec.blocks.BlockBase;
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
import de.redtec.blocks.BlockLinearConector;
import de.redtec.blocks.BlockMAlloyFurnace;
import de.redtec.blocks.BlockMBlender;
import de.redtec.blocks.BlockMCoalHeater;
import de.redtec.blocks.BlockMElectricFurnace;
import de.redtec.blocks.BlockMGenerator;
import de.redtec.blocks.BlockMMultimeter;
import de.redtec.blocks.BlockMRaffinery;
import de.redtec.blocks.BlockMSchredder;
import de.redtec.blocks.BlockMSteamGenerator;
import de.redtec.blocks.BlockMThermalZentrifuge;
import de.redtec.blocks.BlockMTransformatorCoil;
import de.redtec.blocks.BlockMTransformatorContact;
import de.redtec.blocks.BlockPanelLamp;
import de.redtec.blocks.BlockPowerEmiting;
import de.redtec.blocks.BlockPowerSwitch;
import de.redtec.blocks.BlockRDCapacitor;
import de.redtec.blocks.BlockRDPulseCounter;
import de.redtec.blocks.BlockRadialConector;
import de.redtec.blocks.BlockRailPiston;
import de.redtec.blocks.BlockRedstoneContact;
import de.redtec.blocks.BlockRedstoneReciver;
import de.redtec.blocks.BlockSalsolaSeeds;
import de.redtec.blocks.BlockSignalAntennaConector;
import de.redtec.blocks.BlockSignalProcessorContact;
import de.redtec.blocks.BlockSignalWire;
import de.redtec.blocks.BlockStackedRedstoneTorch;
import de.redtec.blocks.BlockStackedRedstoneWire;
import de.redtec.blocks.BlockStoringCraftingTable;
import de.redtec.fluids.BlockDestilledWater;
import de.redtec.fluids.BlockOreSolution;
import de.redtec.fluids.BlockSteam;
import de.redtec.fluids.BlockSulfuricAcid;
import de.redtec.fluids.ModFluids;
import de.redtec.fluids.util.ItemFluidBucket;
import de.redtec.fluids.util.ItemGasBucket;
import de.redtec.gui.ScreenHarvester;
import de.redtec.gui.ScreenHoverControler;
import de.redtec.gui.ScreenJigsaw;
import de.redtec.gui.ScreenMBlender;
import de.redtec.gui.ScreenMCoalHeater;
import de.redtec.gui.ScreenMElectricFurnace;
import de.redtec.gui.ScreenMGenerator;
import de.redtec.gui.ScreenMRaffinery;
import de.redtec.gui.ScreenMSchredder;
import de.redtec.gui.ScreenMThermalZentrifuge;
import de.redtec.gui.ScreenProcessor;
import de.redtec.gui.ScreenReciver;
import de.redtec.gui.ScreenStoredCrafting;
import de.redtec.items.ItemBase;
import de.redtec.items.ItemBlueprint;
import de.redtec.items.ItemEmptyBlueprint;
import de.redtec.items.ItemEnergyMeter;
import de.redtec.items.ItemFluidMeter;
import de.redtec.items.ItemFuse;
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
import de.redtec.registys.ModConfiguredFeatures;
import de.redtec.registys.ModContainerType;
import de.redtec.registys.ModTileEntityType;
import de.redtec.renderer.TileEntityAdvancedMovingBlockRenderer;
import de.redtec.renderer.TileEntityControllPanelRenderer;
import de.redtec.renderer.TileEntityConveyorBeltRenderer;
import de.redtec.renderer.TileEntityFuseBoxRenderer;
import de.redtec.renderer.TileEntityGaugeRenderer;
import de.redtec.renderer.TileEntityMBlenderRenderer;
import de.redtec.renderer.TileEntityMRaffineryRenderer;
import de.redtec.renderer.TileEntityMSchredderRenderer;
import de.redtec.renderer.TileEntityMSteamGeneratorRenderer;
import de.redtec.renderer.TileEntitySignalProcessorContactRenderer;
import de.redtec.util.ModGameRegistry;
import de.redtec.worldgen.BiomeHelper;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
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
	public static final Block signal_antenna_conector = new BlockSignalAntennaConector();
	public static final Block linear_conector = new BlockLinearConector();
	public static final Block storing_crafting_table = new BlockStoringCraftingTable();
	public static final Block signal_processor_contact = new BlockSignalProcessorContact();
	public static final Block rail_piston = new BlockRailPiston();
	public static final Block conector_block = new BlockConectorBlock();
	public static final Block redstone_contact = new BlockRedstoneContact();
	public static final Block button_block = new BlockButtonBlock();
	public static final Block stone_corner = new BlockCornerBlockBase("stone_corner", () -> Blocks.STONE.getDefaultState(), AbstractBlock.Properties.create(Material.ROCK).sound(SoundType.STONE));
	public static final Block iron_rod = new BlockIronRod();
	public static final Block radial_conector = new BlockRadialConector();
	public static final Block salsola_seeds = new BlockSalsolaSeeds();
	public static final Block hover_engine = new BlockHoverControler();
	public static final Block hover_extension = new BlockHoverExtension();
	public static final Block controll_panel = new BlockControllPanel();
	public static final Block harvester = new BlockHarvester();
	public static final Block jigsaw = new BlockJigsaw();
	
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
	public static final Block block_multimeter = new BlockMMultimeter();
	public static final Block power_switch = new BlockPowerSwitch();
	public static final Block electric_furnace = new BlockMElectricFurnace();
	public static final Block schredder = new BlockMSchredder();
	public static final Block blender = new BlockMBlender();
	public static final Block raffinery = new BlockMRaffinery();
	public static final Block alloy_furnace = new BlockMAlloyFurnace();
	public static final Block thermal_zentrifuge = new BlockMThermalZentrifuge();
	
	public static final Block bauxit = new BlockBase("bauxit", Material.ROCK, 1.5F, SoundType.STONE);
	public static final Block bauxit_ore = new BlockBase("bauxit_ore", Material.ROCK, 1.5F, SoundType.STONE);
	public static final Block copper_ore = new BlockBase("copper_ore", Material.ROCK, 3F, SoundType.STONE);
	public static final Block copper_block = new BlockBase("copper_block", Material.IRON, 1.2F, SoundType.METAL);
	public static final Block redstone_alloy_block = new BlockPowerEmiting("redstone_alloy_block", Material.IRON, 1.2F, SoundType.METAL, 8);
	public static final Block aluminium_block = new BlockBase("aluminium_block", Material.IRON, 1.2F, SoundType.METAL);
	public static final Block electrolyt_copper_block = new BlockBase("electrolyt_copper_block", Material.IRON, 1.2F, SoundType.METAL);
	public static final Block steel_block = new BlockBase("steel_block", Material.IRON, 1.2F, SoundType.METAL);
	public static final Block gold_plates = new BlockBase("gold_plates", Material.IRON, 0.9F, SoundType.METAL);
	public static final Block iron_plates = new BlockBase("iron_plates", Material.IRON, 1.2F, SoundType.METAL);
	public static final Block copper_plates = new BlockBase("copper_plates", Material.IRON, 1.2F, SoundType.METAL);
	public static final Block aluminium_plates = new BlockBase("aluminium_plates", Material.IRON, 1.2F, SoundType.METAL);
	public static final Block electrolyt_copper_plates = new BlockBase("electrolyt_copper_plates", Material.IRON, 1.2F, SoundType.METAL);
	public static final Block steel_plates = new BlockBase("steel_plates", Material.IRON, 1.2F, SoundType.METAL);
	public static final Block redstone_alloy_plates = new BlockPowerEmiting("redstone_alloy_plates", Material.IRON, 1.2F, SoundType.METAL, 4);
	public static final Block chiseled_smooth_stone = new BlockBase("chiseled_smooth_stone", Material.ROCK, 1.5F, SoundType.STONE);
	public static final Block smooth_cobblestone = new BlockBase("smooth_cobblestone", Material.ROCK, 2, SoundType.STONE);
	
	public static final Block steam = new BlockSteam();
	public static final Item steam_bucket = new ItemGasBucket(ModFluids.STEAM, "steam_bucket", ItemGroup.MATERIALS);
	public static final Block destilled_water = new BlockDestilledWater();
	public static final Item destilled_water_bucket = new ItemFluidBucket(ModFluids.DESTILLED_WATER, "destilled_water_bucket", ItemGroup.MATERIALS);
	public static final Block sulfuric_acid = new BlockSulfuricAcid();
	public static final Item sulfuric_acid_bucket = new ItemFluidBucket(ModFluids.SULFURIC_ACID, "sulfuric_acid_bucket", ItemGroup.MATERIALS);
	
	public static final Block iron_solution = new BlockOreSolution("iron_solution", ModFluids.IRON_SOLUTION);
	public static final Item iron_solution_bucket = new ItemFluidBucket(ModFluids.IRON_SOLUTION, "iron_solution_bucket", ItemGroup.MATERIALS);
	public static final Block copper_solution = new BlockOreSolution("copper_solution", ModFluids.COPPER_SOLUTION);
	public static final Item copper_solution_bucket = new ItemFluidBucket(ModFluids.COPPER_SOLUTION, "copper_solution_bucket", ItemGroup.MATERIALS);
	public static final Block aluminium_solution = new BlockOreSolution("aluminium_solution", ModFluids.ALUMINIUM_SOLUTION);
	public static final Item aluminium_solution_bucket = new ItemFluidBucket(ModFluids.ALUMINIUM_SOLUTION, "aluminium_solution_bucket", ItemGroup.MATERIALS);
	
	public static final ItemGroup MACHINES = new ItemGroup("machines") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(generator);
		}
	};
	
	public static final Item copper_ingot = new ItemBase("copper_ingot", ItemGroup.MATERIALS);
	public static final Item electrolyt_copper_ingot = new ItemBase("electrolyt_copper_ingot", ItemGroup.MATERIALS);
	public static final Item aluminium_ingot = new ItemBase("aluminium_ingot", ItemGroup.MATERIALS);
	public static final Item steel_ingot = new ItemBase("steel_ingot", ItemGroup.MATERIALS);
	public static final Item copper_nugget = new ItemBase("copper_nugget", ItemGroup.MATERIALS);
	public static final Item electrolyt_copper_nugget = new ItemBase("electrolyt_copper_nugget", ItemGroup.MATERIALS);
	public static final Item aluminium_nugget = new ItemBase("aluminium_nugget", ItemGroup.MATERIALS);
	public static final Item steel_nugget = new ItemBase("steel_nugget", ItemGroup.MATERIALS);
	public static final Item redstone_nugget = new ItemBase("redstone_nugget", ItemGroup.MATERIALS);
	public static final Item redstone_ingot = new ItemBase("redstone_ingot", ItemGroup.MATERIALS);
	public static final Item redstone_alloy_dust = new ItemBase("redstone_alloy_dust", ItemGroup.MATERIALS);
	public static final Item salsola = new ItemSalsola();
	public static final Item crushed_stone = new ItemBase("crushed_stone", ItemGroup.MATERIALS);
	public static final Item crushed_blackstone = new ItemBase("crushed_blackstone", ItemGroup.MATERIALS);
	public static final Item crushed_netherrack = new ItemBase("crushed_netherrack", ItemGroup.MATERIALS);
	
	public static final Item crushed_bauxite = new ItemBase("crushed_bauxite", ItemGroup.MATERIALS);
	public static final Item crushed_iron_ore = new ItemBase("crushed_iron_ore", ItemGroup.MATERIALS);
	public static final Item crushed_copper_ore = new ItemBase("crushed_copper_ore", ItemGroup.MATERIALS);
	public static final Item crushed_gold_ore = new ItemBase("crushed_gold_ore", ItemGroup.MATERIALS);
	public static final Item iron_oxid = new ItemBase("iron_oxid", ItemGroup.MATERIALS);
	public static final Item copper_oxid = new ItemBase("copper_oxid", ItemGroup.MATERIALS);
	public static final Item pure_iron_ore = new ItemBase("pure_iron_ore", ItemGroup.MATERIALS);
	public static final Item pure_copper_ore = new ItemBase("pure_copper_ore", ItemGroup.MATERIALS);
	public static final Item pure_gold_ore = new ItemBase("pure_gold_ore", ItemGroup.MATERIALS);
	
	public static final Item sulfur = new ItemBase("sulfur", ItemGroup.MATERIALS);
	public static final Item sulfur_dioxid = new ItemBase("sulfur_dioxid", ItemGroup.MATERIALS);
	
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
	
	public static final Item fluid_meter = new ItemFluidMeter();
	public static final Item energy_meter = new ItemEnergyMeter();
	
	public RedTec() {
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		
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
		ModGameRegistry.registerBlock(signal_antenna_conector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(linear_conector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(storing_crafting_table, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(signal_processor_contact, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(rail_piston, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(conector_block, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(redstone_contact, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(button_block, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(radial_conector, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(hover_engine, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(hover_extension, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(controll_panel, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(harvester, ItemGroup.REDSTONE);
		ModGameRegistry.registerBlock(copper_cable, MACHINES);
		ModGameRegistry.registerBlock(electrolyt_copper_cable, MACHINES);
		ModGameRegistry.registerBlock(aluminium_cable, MACHINES);
		ModGameRegistry.registerBlock(infinity_power_source, null);
		ModGameRegistry.registerBlock(panel_lamp, MACHINES);
		ModGameRegistry.registerBlock(steel_block, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(copper_block, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(aluminium_block, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(electrolyt_copper_block, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(redstone_alloy_block, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(copper_ore, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(bauxit, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(bauxit_ore, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(iron_plates, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(gold_plates, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(steel_plates, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(copper_plates, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(aluminium_plates, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(electrolyt_copper_plates, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(redstone_alloy_plates, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(smooth_cobblestone, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(chiseled_smooth_stone, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(iron_rod, ItemGroup.DECORATIONS);
		ModGameRegistry.registerBlock(stone_corner, ItemGroup.BUILDING_BLOCKS);
		ModGameRegistry.registerBlock(salsola_seeds, ItemGroup.MISC);
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
		ModGameRegistry.registerBlock(block_multimeter, MACHINES);
		ModGameRegistry.registerBlock(power_switch, MACHINES);
		ModGameRegistry.registerBlock(electric_furnace, MACHINES);
		ModGameRegistry.registerBlock(schredder, MACHINES);
		ModGameRegistry.registerBlock(blender, MACHINES);
		ModGameRegistry.registerBlock(raffinery, MACHINES);
		ModGameRegistry.registerBlock(alloy_furnace, MACHINES);
		ModGameRegistry.registerBlock(conveyor_belt, MACHINES);
		ModGameRegistry.registerBlock(conveyor_spliter, MACHINES);
		ModGameRegistry.registerBlock(thermal_zentrifuge, MACHINES);
		
		ModGameRegistry.registerTechnicalBlock(steam);
		ModGameRegistry.registerItem(steam_bucket);
		ModGameRegistry.registerTechnicalBlock(destilled_water);
		ModGameRegistry.registerItem(destilled_water_bucket);
		ModGameRegistry.registerTechnicalBlock(sulfuric_acid);
		ModGameRegistry.registerItem(sulfuric_acid_bucket);
		
		ModGameRegistry.registerTechnicalBlock(iron_solution);
		ModGameRegistry.registerItem(iron_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(copper_solution);
		ModGameRegistry.registerItem(copper_solution_bucket);
		ModGameRegistry.registerTechnicalBlock(aluminium_solution);
		ModGameRegistry.registerItem(aluminium_solution_bucket);
		
		// register Items
		ModGameRegistry.registerItem(crushed_blackstone);
		ModGameRegistry.registerItem(crushed_netherrack);
		ModGameRegistry.registerItem(crushed_bauxite);
		ModGameRegistry.registerItem(iron_oxid);
		ModGameRegistry.registerItem(copper_oxid);
		ModGameRegistry.registerItem(crushed_iron_ore);
		ModGameRegistry.registerItem(crushed_copper_ore);
		ModGameRegistry.registerItem(crushed_gold_ore);
		ModGameRegistry.registerItem(pure_iron_ore);
		ModGameRegistry.registerItem(pure_copper_ore);
		ModGameRegistry.registerItem(pure_gold_ore);
		ModGameRegistry.registerItem(crushed_stone);
		ModGameRegistry.registerItem(redstone_ingot);
		ModGameRegistry.registerItem(redstone_alloy_dust);
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
		ModGameRegistry.registerItem(redstone_ingot);
		ModGameRegistry.registerItem(redstone_nugget);
		ModGameRegistry.registerItem(fluid_meter);
		ModGameRegistry.registerItem(energy_meter);
		ModGameRegistry.registerItem(fuse_elv);
		ModGameRegistry.registerItem(fuse_lv);
		ModGameRegistry.registerItem(fuse_nv);
		ModGameRegistry.registerItem(fuse_hv);
		ModGameRegistry.registerItem(schredder_crusher);
		ModGameRegistry.registerItem(sulfur);
		ModGameRegistry.registerItem(sulfur_dioxid);
		
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
		for (Biome biome : ForgeRegistries.BIOMES) {
			if (biome.getCategory().equals(Biome.Category.NETHER)) {
				//Nether
			} else if (biome.getCategory().equals(Biome.Category.THEEND)) {
				//End
			} else {
				//Overworld
				BiomeHelper.addFeature(biome, Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.COPPER_ORE);
				BiomeHelper.addFeature(biome, Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.BAUXIT_STONE_ORE);
			}
		}
		
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
		RenderTypeLookup.setRenderLayer(block_multimeter, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(fluid_valve, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(blender, RenderType.getCutoutMipped());
		RenderTypeLookup.setRenderLayer(ModFluids.SULFURIC_ACID, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_SULFURIC_ACID, RenderType.getTranslucent());
		
		RenderTypeLookup.setRenderLayer(ModFluids.IRON_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_IRON_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.COPPER_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_COPPER_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.ALUMINIUM_SOLUTION, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModFluids.FLOWING_ALUMINIUM_SOLUTION, RenderType.getTranslucent());
		
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
		
	}
	
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
	
}
