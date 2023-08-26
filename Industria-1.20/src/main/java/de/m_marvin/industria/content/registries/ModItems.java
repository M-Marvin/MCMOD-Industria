package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.items.ConduitCableItem;
import de.m_marvin.industria.content.items.ScrewDriverItem;
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
	
	public static final RegistryObject<ConduitCableItem> INSULATED_COPPER_WIRE 		= ITEMS.register("insulated_copper_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_COPPER_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_ALUMINUM_WIRE 	= ITEMS.register("insulated_aluminum_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_ALUMINUM_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_GOLD_WIRE 		= ITEMS.register("insulated_gold_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_GOLD_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_TIN_WIRE 		= ITEMS.register("insulated_tin_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_TIN_WIRE));
	public static final RegistryObject<ConduitCableItem> COPPER_WIRE 				= ITEMS.register("copper_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.COPPER_WIRE));
	public static final RegistryObject<ConduitCableItem> ALUMINUM_WIRE 				= ITEMS.register("aluminum_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.ALUMINUM_WIRE));
	public static final RegistryObject<ConduitCableItem> GOLD_WIRE 					= ITEMS.register("gold_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.GOLD_WIRE));
	public static final RegistryObject<ConduitCableItem> TIN_WIRE 					= ITEMS.register("tin_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.TIN_WIRE));
	
	public static final RegistryObject<ScrewDriverItem> SCREW_DRIVER				= ITEMS.register("screw_driver", () -> new ScrewDriverItem(new Item.Properties().stacksTo(1)));
	
	public static final RegistryObject<BlockItem> COPPER_WIRE_HOLDER 				= ITEMS.register("copper_wire_holder", () -> new BlockItem(ModBlocks.COPPER_WIRE_HOLDER.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> ALUMINUM_WIRE_HOLDER 				= ITEMS.register("aluminum_wire_holder", () -> new BlockItem(ModBlocks.ALUMINUM_WIRE_HOLDER.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> GOLD_WIRE_HOLDER 					= ITEMS.register("gold_wire_holder", () -> new BlockItem(ModBlocks.GOLD_WIRE_HOLDER.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> TIN_WIRE_HOLDER 					= ITEMS.register("tin_wire_holder", () -> new BlockItem(ModBlocks.TIN_WIRE_HOLDER.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> JUNCTION_BOX						= ITEMS.register("junction_box", () -> new BlockItem(ModBlocks.JUNCTION_BOX.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> BRASS_FLOODLIGHT					= ITEMS.register("brass_floodlight", () -> new BlockItem(ModBlocks.BRASS_FLOODLIGHT.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> STEEL_FLOODLIGHT					= ITEMS.register("steel_floodlight", () -> new BlockItem(ModBlocks.STEEL_FLOOFLIGHT.get(), new Item.Properties()));
	
	/* Creative tabs */
	
	public static final RegistryObject<CreativeModeTab> MACHINES_TAB = CREATIVE_MODE_TABS.register("machines", () -> CreativeModeTab.builder()
			.title(Component.translatable("industria.creative_tab.machines"))
			.icon(() -> INSULATED_COPPER_WIRE.get().getDefaultInstance())
			.displayItems((displayParams, output) -> {
				output.accept(COPPER_WIRE.get().getDefaultInstance());
				output.accept(ALUMINUM_WIRE.get().getDefaultInstance());
				output.accept(GOLD_WIRE.get().getDefaultInstance());
				output.accept(TIN_WIRE.get().getDefaultInstance());
				output.accept(INSULATED_COPPER_WIRE.get().getDefaultInstance());
				output.accept(INSULATED_ALUMINUM_WIRE.get().getDefaultInstance());
				output.accept(INSULATED_GOLD_WIRE.get().getDefaultInstance());
				output.accept(INSULATED_TIN_WIRE.get().getDefaultInstance());
				
				output.accept(COPPER_WIRE_HOLDER.get().getDefaultInstance());
				output.accept(ALUMINUM_WIRE_HOLDER.get().getDefaultInstance());
				output.accept(GOLD_WIRE_HOLDER.get().getDefaultInstance());
				output.accept(TIN_WIRE_HOLDER.get().getDefaultInstance());
				output.accept(JUNCTION_BOX.get().getDefaultInstance());
				
				output.accept(BRASS_FLOODLIGHT.get().getDefaultInstance());
				output.accept(STEEL_FLOODLIGHT.get().getDefaultInstance());
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
