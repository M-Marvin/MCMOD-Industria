package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.items.ConduitCableItem;
import de.m_marvin.industria.content.items.ConduitCoilItem;
import de.m_marvin.industria.content.items.ScrewDriverItem;
import de.m_marvin.industria.core.electrics.types.item.ElectricBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
	
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Industria.MODID);
	private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Industria.MODID);
	public static void register() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		CREATIVE_MODE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<ConduitCableItem> INSULATED_COPPER_WIRE 			= ITEMS.register("insulated_copper_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_COPPER_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_ALUMINUM_WIRE 		= ITEMS.register("insulated_aluminum_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_ALUMINUM_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_GOLD_WIRE 			= ITEMS.register("insulated_gold_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_GOLD_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_TIN_WIRE 			= ITEMS.register("insulated_tin_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_TIN_WIRE));
	public static final RegistryObject<ConduitCableItem> COPPER_WIRE 					= ITEMS.register("copper_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.COPPER_WIRE));
	public static final RegistryObject<ConduitCableItem> ALUMINUM_WIRE 					= ITEMS.register("aluminum_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.ALUMINUM_WIRE));
	public static final RegistryObject<ConduitCableItem> GOLD_WIRE 						= ITEMS.register("gold_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.GOLD_WIRE));
	public static final RegistryObject<ConduitCableItem> TIN_WIRE 						= ITEMS.register("tin_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.TIN_WIRE));
	
	public static final RegistryObject<BlockItem> EMPTY_WIRE_COIL						= ITEMS.register("empty_wire_coil", () -> new BlockItem(ModBlocks.EMPTY_WIRE_COIL.get(), new Item.Properties().stacksTo(16)));
	public static final RegistryObject<ConduitCoilItem> INSULATED_COPPER_WIRE_COIL		= ITEMS.register("insulated_copper_wire_coil", () -> new ConduitCoilItem(ModBlocks.INSULATED_COPPER_WIRE_COIL.get(), () -> ModConduits.INSULATED_COPPER_WIRE.get(), () -> EMPTY_WIRE_COIL.get(), new Item.Properties().stacksTo(16)));
	public static final RegistryObject<ConduitCoilItem> INSULATED_TIN_WIRE_COIL			= ITEMS.register("insulated_tin_wire_coil", () -> new ConduitCoilItem(ModBlocks.INSULATED_TIN_WIRE_COIL.get(), () -> ModConduits.INSULATED_TIN_WIRE.get(), () -> EMPTY_WIRE_COIL.get(), new Item.Properties().stacksTo(16)));
	public static final RegistryObject<ConduitCoilItem> INSULATED_GOLD_WIRE_COIL		= ITEMS.register("insulated_gold_wire_coil", () -> new ConduitCoilItem(ModBlocks.INSULATED_GOLD_WIRE_COIL.get(), () -> ModConduits.INSULATED_GOLD_WIRE.get(), () -> EMPTY_WIRE_COIL.get(), new Item.Properties().stacksTo(16)));
	public static final RegistryObject<ConduitCoilItem> INSULATED_ALUMINUM_WIRE_COIL	= ITEMS.register("insulated_aluminum_wire_coil", () -> new ConduitCoilItem(ModBlocks.INSULATED_ALUMINUM_WIRE_COIL.get(), () -> ModConduits.INSULATED_ALUMINUM_WIRE.get(), () -> EMPTY_WIRE_COIL.get(), new Item.Properties().stacksTo(16)));
	public static final RegistryObject<ConduitCoilItem> COPPER_WIRE_COIL				= ITEMS.register("copper_wire_coil", () -> new ConduitCoilItem(ModBlocks.COPPER_WIRE_COIL.get(), () -> ModConduits.COPPER_WIRE.get(), () -> EMPTY_WIRE_COIL.get(), new Item.Properties().stacksTo(16)));
	public static final RegistryObject<ConduitCoilItem> TIN_WIRE_COIL					= ITEMS.register("tin_wire_coil", () -> new ConduitCoilItem(ModBlocks.TIN_WIRE_COIL.get(), () -> ModConduits.TIN_WIRE.get(), () -> EMPTY_WIRE_COIL.get(), new Item.Properties().stacksTo(16)));
	public static final RegistryObject<ConduitCoilItem> GOLD_WIRE_COIL					= ITEMS.register("gold_wire_coil", () -> new ConduitCoilItem(ModBlocks.GOLD_WIRE_COIL.get(), () -> ModConduits.GOLD_WIRE.get(), () -> EMPTY_WIRE_COIL.get(), new Item.Properties().stacksTo(16)));
	public static final RegistryObject<ConduitCoilItem> ALUMINUM_WIRE_COIL				= ITEMS.register("aluminum_wire_coil", () -> new ConduitCoilItem(ModBlocks.ALUMINUM_WIRE_COIL.get(), () -> ModConduits.ALUMINUM_WIRE.get(), () -> EMPTY_WIRE_COIL.get(), new Item.Properties().stacksTo(16)));
	
	public static final RegistryObject<ScrewDriverItem> SCREW_DRIVER					= ITEMS.register("screw_driver", () -> new ScrewDriverItem(new Item.Properties().stacksTo(1)));
	
	public static final RegistryObject<BlockItem> COPPER_WIRE_HOLDER 					= ITEMS.register("copper_wire_holder", () -> new BlockItem(ModBlocks.COPPER_WIRE_HOLDER.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> ALUMINUM_WIRE_HOLDER 					= ITEMS.register("aluminum_wire_holder", () -> new BlockItem(ModBlocks.ALUMINUM_WIRE_HOLDER.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> GOLD_WIRE_HOLDER 						= ITEMS.register("gold_wire_holder", () -> new BlockItem(ModBlocks.GOLD_WIRE_HOLDER.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> TIN_WIRE_HOLDER 						= ITEMS.register("tin_wire_holder", () -> new BlockItem(ModBlocks.TIN_WIRE_HOLDER.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> IRON_JUNCTION_BOX						= ITEMS.register("iron_junction_box", () -> new BlockItem(ModBlocks.IRON_JUNCTION_BOX.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> ZINC_JUNCTION_BOX						= ITEMS.register("zinc_junction_box", () -> new BlockItem(ModBlocks.ZINC_JUNCTION_BOX.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> BRASS_JUNCTION_BOX					= ITEMS.register("brass_junction_box", () -> new BlockItem(ModBlocks.BRASS_JUNCTION_BOX.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> STEEL_JUNCTION_BOX					= ITEMS.register("steel_junction_box", () -> new BlockItem(ModBlocks.STEEL_JUNCTION_BOX.get(), new Item.Properties()));
	
	public static final RegistryObject<BlockItem> BRASS_FLOODLIGHT						= ITEMS.register("brass_floodlight", () -> new ElectricBlockItem(ModBlocks.BRASS_FLOODLIGHT.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> STEEL_FLOODLIGHT						= ITEMS.register("steel_floodlight", () -> new ElectricBlockItem(ModBlocks.STEEL_FLOOFLIGHT.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> PORTABLE_FUEL_GENERATOR				= ITEMS.register("portable_fuel_generator", () -> new ElectricBlockItem(ModBlocks.PORTABLE_FUEL_GENERATOR.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> PORTABLE_COAL_GENERATOR				= ITEMS.register("portable_coal_generator", () -> new ElectricBlockItem(ModBlocks.PORTABLE_COAL_GENERATOR.get(), new Item.Properties()));
	
	public static final RegistryObject<Item> ALUMINUM_INGOT								= ITEMS.register("aluminum_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> BRASS_INGOT								= ITEMS.register("brass_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> LEAD_INGOT									= ITEMS.register("lead_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> MONEL_INGOT								= ITEMS.register("monel_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> NICKEL_INGOT								= ITEMS.register("nickel_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PALLADIUM_INGOT 							= ITEMS.register("palladium_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> REDSTONE_ALLOY_INGOT						= ITEMS.register("redstone_alloy_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SILVER_INGOT 								= ITEMS.register("silver_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> STEEL_INGOT								= ITEMS.register("steel_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> TIN_INGOT									= ITEMS.register("tin_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> TUNGSTEN_INGOT								= ITEMS.register("tungsten_ingot", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> ZINC_INGOT									= ITEMS.register("zinc_ingot", () -> new Item(new Item.Properties()));
	
	public static final RegistryObject<Item> ALUMINUM_NUGGET							= ITEMS.register("aluminum_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> BRASS_NUGGET								= ITEMS.register("brass_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> LEAD_NUGGET 								= ITEMS.register("lead_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> MONEL_NUGGET								= ITEMS.register("monel_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> NICKEL_NUGGET								= ITEMS.register("nickel_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PALLADIUM_NUGGET							= ITEMS.register("palladium_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> REDSTONE_ALLOY_NUGGET						= ITEMS.register("redstone_alloy_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SILVER_NUGGET								= ITEMS.register("silver_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> STEEL_NUGGET								= ITEMS.register("steel_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> TIN_NUGGET									= ITEMS.register("tin_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> TUNGSTEN_NUGGET							= ITEMS.register("tungsten_nugget", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> ZINC_NUGGET								= ITEMS.register("zinc_nugget", () -> new Item(new Item.Properties()));
	
	public static final RegistryObject<Item> ALUMINUM_PLATE								= ITEMS.register("aluminum_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> BRASS_PLATE								= ITEMS.register("brass_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> LEAD_PLATE									= ITEMS.register("lead_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> MONEL_PLATE								= ITEMS.register("monel_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> NICKEL_PLATE								= ITEMS.register("nickel_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PALLADIUM_PLATE							= ITEMS.register("palladium_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> REDSTONE_ALLOY_PLATE						= ITEMS.register("redstone_alloy_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SILVER_PLATE								= ITEMS.register("silver_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> STEEL_PLATE								= ITEMS.register("steel_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> TIN_PLATE									= ITEMS.register("tin_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> TUNGSTEN_PLATE								= ITEMS.register("tungsten_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> ZINC_PLATE									= ITEMS.register("zinc_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> IRON_PLATE									= ITEMS.register("iron_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> COPPER_PLATE								= ITEMS.register("copper_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> GOLD_PLATE									= ITEMS.register("gold_plate", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> NETHERITE_PLATE							= ITEMS.register("netherite_plate", () -> new Item(new Item.Properties()));

	public static final RegistryObject<Item> ALUMINUM_DUST								= ITEMS.register("aluminum_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> BRASS_DUST									= ITEMS.register("brass_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> LEAD_DUST									= ITEMS.register("lead_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> MONEL_DUST 								= ITEMS.register("monel_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> NICKEL_DUST 								= ITEMS.register("nickel_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PALLADIUM_DUST								= ITEMS.register("palladium_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> REDSTONE_ALLOY_DUST						= ITEMS.register("redstone_alloy_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SILVER_DUST								= ITEMS.register("silver_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> STEEL_DUST									= ITEMS.register("steel_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> TIN_DUST									= ITEMS.register("tin_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> TUNGSTEN_DUST								= ITEMS.register("tungsten_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> ZINC_DUST									= ITEMS.register("zinc_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> IRON_DUST									= ITEMS.register("iron_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> COPPER_DUST								= ITEMS.register("copper_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> GOLD_DUST									= ITEMS.register("gold_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> NETHERITE_DUST								= ITEMS.register("netherite_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> STONE_DUST									= ITEMS.register("stone_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> BLACKSTONE_DUST							= ITEMS.register("blackstone_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> NETHERRACK_DUST							= ITEMS.register("netherrack_dust", () -> new Item(new Item.Properties()));
	
	public static final RegistryObject<Item> SPO_ALUMINUM_DUST							= ITEMS.register("spo_aluminum_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_BRASS_DUST								= ITEMS.register("spo_brass_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_LEAD_DUST								= ITEMS.register("spo_lead_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_MONEL_DUST								= ITEMS.register("spo_monel_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_NICKEL_DUST							= ITEMS.register("spo_nickel_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_PALLADIUM_DUST							= ITEMS.register("spo_palladium_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_REDSTONE_ALLOY_DUST					= ITEMS.register("spo_redstone_alloy_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_SILVER_DUST							= ITEMS.register("spo_silver_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_STEEL_DUST								= ITEMS.register("spo_steel_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_TIN_DUST								= ITEMS.register("spo_tin_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_TUNGSTEN_DUST							= ITEMS.register("spo_tungsten_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_ZINC_DUST								= ITEMS.register("spo_zinc_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_IRON_DUST								= ITEMS.register("spo_iron_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_COPPER_DUST							= ITEMS.register("spo_copper_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_GOLD_DUST								= ITEMS.register("spo_gold_dust", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> SPO_NETHERITE_DUST							= ITEMS.register("spo_netherite_dust", () -> new Item(new Item.Properties()));
	
	public static final RegistryObject<Item> RAW_SILVER									= ITEMS.register("raw_silver", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> RAW_LEAD									= ITEMS.register("raw_lead", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> RAW_PLUTONIUM								= ITEMS.register("raw_plutonium", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> RAW_TIN									= ITEMS.register("raw_tin", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> RAW_NICKEL									= ITEMS.register("raw_nickel", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> RAW_PALLADIUM								= ITEMS.register("raw_palladium", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> RAW_ZINC									= ITEMS.register("raw_zinc", () -> new Item(new Item.Properties()));
	
	public static final RegistryObject<Item> CRUSHED_NETHER_GOLD_ORE					= ITEMS.register("crushed_nether_gold_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_TIN_ORE							= ITEMS.register("crushed_tin_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_STONE								= ITEMS.register("crushed_stone", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_SILVER_ORE							= ITEMS.register("crushed_silver_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_WOLFRAMITE							= ITEMS.register("crushed_wolframite", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_IRON_ORE							= ITEMS.register("crushed_iron_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_ZINC_ORE							= ITEMS.register("crushed_zinc_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_GOLD_ORE							= ITEMS.register("crushed_gold_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_NICKEL_ORE							= ITEMS.register("crushed_nickel_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_COPPER_ORE							= ITEMS.register("crushed_copper_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_NETHERRACK							= ITEMS.register("crushed_netherrack", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_PALLADIUM_ORE						= ITEMS.register("crushed_palladium_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_LEAD_ORE							= ITEMS.register("crushed_lead_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> CRUSHED_BAUXITE							= ITEMS.register("crushed_bauxite", () -> new Item(new Item.Properties()));
	
	public static final RegistryObject<Item> PURIFIED_CRUSHED_IRON_ORE					= ITEMS.register("purified_crushed_iron_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PURIFIED_CRUSHED_NICKEL_ORE				= ITEMS.register("purified_crushed_nickel_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PURIFIED_CRUSHED_COPPER_ORE				= ITEMS.register("purified_crushed_copper_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PURIFIED_CRUSHED_ZINC_ORE					= ITEMS.register("purified_crushed_zinc_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PURIFIED_CRUSHED_PALLADIUM_ORE				= ITEMS.register("purified_crushed_palladium_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PURIFIED_CRUSHED_LEAD_ORE					= ITEMS.register("purified_crushed_lead_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PURIFIED_CRUSHED_TIN_ORE					= ITEMS.register("purified_crushed_tin_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PURIFIED_CRUSHED_GOLD_ORE					= ITEMS.register("purified_crushed_gold_ore", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PURIFIED_CRUSHED_SILVER_ORE				= ITEMS.register("purified_crushed_silver_ore", () -> new Item(new Item.Properties()));
	
	/* Creative tabs */
	
	public static final RegistryObject<CreativeModeTab> MATERIALS = CREATIVE_MODE_TABS.register("materials", () -> CreativeModeTab.builder()
			.title(Component.translatable("industria.creative_tab.materials"))
			.icon(() -> COPPER_PLATE.get().getDefaultInstance())
			.displayItems((displayParams, output) -> {
				
				output.accept(ALUMINUM_INGOT.get().getDefaultInstance());
				output.accept(BRASS_INGOT.get().getDefaultInstance());
				output.accept(LEAD_INGOT.get().getDefaultInstance());
				output.accept(MONEL_INGOT.get().getDefaultInstance());
				output.accept(NICKEL_INGOT.get().getDefaultInstance());
				output.accept(PALLADIUM_INGOT .get().getDefaultInstance());
				output.accept(REDSTONE_ALLOY_INGOT.get().getDefaultInstance());
				output.accept(SILVER_INGOT .get().getDefaultInstance());
				output.accept(STEEL_INGOT.get().getDefaultInstance());
				output.accept(TIN_INGOT.get().getDefaultInstance());
				output.accept(TUNGSTEN_INGOT.get().getDefaultInstance());
				output.accept(ZINC_INGOT.get().getDefaultInstance());

				output.accept(ALUMINUM_NUGGET.get().getDefaultInstance());
				output.accept(BRASS_NUGGET.get().getDefaultInstance());
				output.accept(LEAD_NUGGET .get().getDefaultInstance());
				output.accept(MONEL_NUGGET.get().getDefaultInstance());
				output.accept(NICKEL_NUGGET.get().getDefaultInstance());
				output.accept(PALLADIUM_NUGGET.get().getDefaultInstance());
				output.accept(REDSTONE_ALLOY_NUGGET.get().getDefaultInstance());
				output.accept(SILVER_NUGGET.get().getDefaultInstance());
				output.accept(STEEL_NUGGET.get().getDefaultInstance());
				output.accept(TIN_NUGGET.get().getDefaultInstance());
				output.accept(TUNGSTEN_NUGGET.get().getDefaultInstance());
				output.accept(ZINC_NUGGET.get().getDefaultInstance());

				output.accept(ALUMINUM_PLATE.get().getDefaultInstance());
				output.accept(BRASS_PLATE.get().getDefaultInstance());
				output.accept(LEAD_PLATE.get().getDefaultInstance());
				output.accept(MONEL_PLATE.get().getDefaultInstance());
				output.accept(NICKEL_PLATE.get().getDefaultInstance());
				output.accept(PALLADIUM_PLATE.get().getDefaultInstance());
				output.accept(REDSTONE_ALLOY_PLATE.get().getDefaultInstance());
				output.accept(SILVER_PLATE.get().getDefaultInstance());
				output.accept(STEEL_PLATE.get().getDefaultInstance());
				output.accept(TIN_PLATE.get().getDefaultInstance());
				output.accept(TUNGSTEN_PLATE.get().getDefaultInstance());
				output.accept(ZINC_PLATE.get().getDefaultInstance());
				output.accept(IRON_PLATE.get().getDefaultInstance());
				output.accept(COPPER_PLATE.get().getDefaultInstance());
				output.accept(GOLD_PLATE.get().getDefaultInstance());
				output.accept(NETHERITE_PLATE.get().getDefaultInstance());

				output.accept(ALUMINUM_DUST.get().getDefaultInstance());
				output.accept(BRASS_DUST.get().getDefaultInstance());
				output.accept(LEAD_DUST.get().getDefaultInstance());
				output.accept(MONEL_DUST .get().getDefaultInstance());
				output.accept(NICKEL_DUST .get().getDefaultInstance());
				output.accept(PALLADIUM_DUST.get().getDefaultInstance());
				output.accept(REDSTONE_ALLOY_DUST.get().getDefaultInstance());
				output.accept(SILVER_DUST.get().getDefaultInstance());
				output.accept(STEEL_DUST.get().getDefaultInstance());
				output.accept(TIN_DUST.get().getDefaultInstance());
				output.accept(TUNGSTEN_DUST.get().getDefaultInstance());
				output.accept(ZINC_DUST.get().getDefaultInstance());
				output.accept(IRON_DUST.get().getDefaultInstance());
				output.accept(COPPER_DUST.get().getDefaultInstance());
				output.accept(GOLD_DUST.get().getDefaultInstance());
				output.accept(NETHERITE_DUST.get().getDefaultInstance());
				output.accept(STONE_DUST.get().getDefaultInstance());
				output.accept(BLACKSTONE_DUST.get().getDefaultInstance());
				output.accept(NETHERRACK_DUST.get().getDefaultInstance());
				
				output.accept(SPO_ALUMINUM_DUST.get().getDefaultInstance());
				output.accept(SPO_BRASS_DUST.get().getDefaultInstance());
				output.accept(SPO_LEAD_DUST.get().getDefaultInstance());
				output.accept(SPO_MONEL_DUST.get().getDefaultInstance());
				output.accept(SPO_NICKEL_DUST.get().getDefaultInstance());
				output.accept(SPO_PALLADIUM_DUST.get().getDefaultInstance());
				output.accept(SPO_REDSTONE_ALLOY_DUST.get().getDefaultInstance());
				output.accept(SPO_SILVER_DUST.get().getDefaultInstance());
				output.accept(SPO_STEEL_DUST.get().getDefaultInstance());
				output.accept(SPO_TIN_DUST.get().getDefaultInstance());
				output.accept(SPO_TUNGSTEN_DUST.get().getDefaultInstance());
				output.accept(SPO_ZINC_DUST.get().getDefaultInstance());
				output.accept(SPO_IRON_DUST.get().getDefaultInstance());
				output.accept(SPO_COPPER_DUST.get().getDefaultInstance());
				output.accept(SPO_GOLD_DUST.get().getDefaultInstance());
				output.accept(SPO_NETHERITE_DUST.get().getDefaultInstance());

				output.accept(RAW_SILVER.get().getDefaultInstance());
				output.accept(RAW_LEAD.get().getDefaultInstance());
				output.accept(RAW_PLUTONIUM.get().getDefaultInstance());
				output.accept(RAW_TIN.get().getDefaultInstance());
				output.accept(RAW_NICKEL.get().getDefaultInstance());
				output.accept(RAW_PALLADIUM.get().getDefaultInstance());
				output.accept(RAW_ZINC.get().getDefaultInstance());

				output.accept(CRUSHED_NETHER_GOLD_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_TIN_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_STONE.get().getDefaultInstance());
				output.accept(CRUSHED_SILVER_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_WOLFRAMITE.get().getDefaultInstance());
				output.accept(CRUSHED_IRON_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_ZINC_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_GOLD_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_NICKEL_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_COPPER_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_NETHERRACK.get().getDefaultInstance());
				output.accept(CRUSHED_PALLADIUM_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_LEAD_ORE.get().getDefaultInstance());
				output.accept(CRUSHED_BAUXITE.get().getDefaultInstance());

				output.accept(PURIFIED_CRUSHED_IRON_ORE.get().getDefaultInstance());
				output.accept(PURIFIED_CRUSHED_NICKEL_ORE.get().getDefaultInstance());
				output.accept(PURIFIED_CRUSHED_COPPER_ORE.get().getDefaultInstance());
				output.accept(PURIFIED_CRUSHED_ZINC_ORE.get().getDefaultInstance());
				output.accept(PURIFIED_CRUSHED_PALLADIUM_ORE.get().getDefaultInstance());
				output.accept(PURIFIED_CRUSHED_LEAD_ORE.get().getDefaultInstance());
				output.accept(PURIFIED_CRUSHED_TIN_ORE.get().getDefaultInstance());
				output.accept(PURIFIED_CRUSHED_GOLD_ORE.get().getDefaultInstance());
				output.accept(PURIFIED_CRUSHED_SILVER_ORE.get().getDefaultInstance());

			})
			.build()
	);
	public static final RegistryObject<CreativeModeTab> MACHINES_TAB = CREATIVE_MODE_TABS.register("machines", () -> CreativeModeTab.builder()
			.title(Component.translatable("industria.creative_tab.machines"))
			.icon(() -> TIN_WIRE_HOLDER.get().getDefaultInstance())
			.displayItems((displayParams, output) -> {
				output.accept(COPPER_WIRE.get().getDefaultInstance());
				output.accept(ALUMINUM_WIRE.get().getDefaultInstance());
				output.accept(GOLD_WIRE.get().getDefaultInstance());
				output.accept(TIN_WIRE.get().getDefaultInstance());
				output.accept(INSULATED_COPPER_WIRE.get().getDefaultInstance());
				output.accept(INSULATED_ALUMINUM_WIRE.get().getDefaultInstance());
				output.accept(INSULATED_GOLD_WIRE.get().getDefaultInstance());
				output.accept(INSULATED_TIN_WIRE.get().getDefaultInstance());
				
				output.accept(EMPTY_WIRE_COIL.get().getDefaultInstance());
				output.accept(INSULATED_COPPER_WIRE_COIL.get().getFilledInstance());
				output.accept(INSULATED_TIN_WIRE_COIL.get().getFilledInstance());
				output.accept(INSULATED_GOLD_WIRE_COIL.get().getFilledInstance());
				output.accept(INSULATED_ALUMINUM_WIRE_COIL.get().getFilledInstance());
				output.accept(COPPER_WIRE_COIL.get().getFilledInstance());
				output.accept(TIN_WIRE_COIL.get().getFilledInstance());
				output.accept(GOLD_WIRE_COIL.get().getFilledInstance());
				output.accept(ALUMINUM_WIRE_COIL.get().getFilledInstance());
				
				output.accept(COPPER_WIRE_HOLDER.get().getDefaultInstance());
				output.accept(ALUMINUM_WIRE_HOLDER.get().getDefaultInstance());
				output.accept(GOLD_WIRE_HOLDER.get().getDefaultInstance());
				output.accept(TIN_WIRE_HOLDER.get().getDefaultInstance());
				output.accept(IRON_JUNCTION_BOX.get().getDefaultInstance());
				output.accept(ZINC_JUNCTION_BOX.get().getDefaultInstance());
				output.accept(BRASS_JUNCTION_BOX.get().getDefaultInstance());
				output.accept(STEEL_JUNCTION_BOX.get().getDefaultInstance());
				
				output.accept(BRASS_FLOODLIGHT.get().getDefaultInstance());
				output.accept(STEEL_FLOODLIGHT.get().getDefaultInstance());
				output.accept(PORTABLE_FUEL_GENERATOR.get().getDefaultInstance());
				output.accept(PORTABLE_COAL_GENERATOR.get().getDefaultInstance());
			})
			.build()
	);
	public static final RegistryObject<CreativeModeTab> TOOLS_TAB = CREATIVE_MODE_TABS.register("tools", () -> CreativeModeTab.builder()
			.title(Component.translatable("industria.creative_tab.tools"))
			.icon(() -> SCREW_DRIVER.get().getDefaultInstance())
			.displayItems((displayParams, output) -> {
				output.accept(SCREW_DRIVER.get().getDefaultInstance());
			})
			.build()
	);
	
}
