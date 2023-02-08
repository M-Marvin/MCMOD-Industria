package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.items.ScrewDriverItem;
import de.m_marvin.industria.core.conduits.registry.Conduits;
import de.m_marvin.industria.core.conduits.types.items.ConduitCableItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
	
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Industria.MODID);
	public static void register() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<BlockItem> IRON_CONDUIT_CLAMP = ITEMS.register("iron_conduit_clamp", () -> new BlockItem(ModBlocks.IRON_CONDUIT_CLAMP.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
	public static final RegistryObject<ConduitCableItem> INSULATED_COPPER_WIRE = ITEMS.register("insulated_copper_wire", () -> new ConduitCableItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(16), Conduits.INSULATED_COPPER_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_ALUMINUM_WIRE = ITEMS.register("insulated_aluminum_wire", () -> new ConduitCableItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(16), Conduits.INSULATED_ALUMINUM_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_GOLD_WIRE = ITEMS.register("insulated_gold_wire", () -> new ConduitCableItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(16), Conduits.INSULATED_GOLD_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_TIN_WIRE = ITEMS.register("insulated_tin_wire", () -> new ConduitCableItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(16), Conduits.INSULATED_TIN_WIRE));
	public static final RegistryObject<ConduitCableItem> COPPER_WIRE = ITEMS.register("copper_wire", () -> new ConduitCableItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(16), Conduits.COPPER_WIRE));
	public static final RegistryObject<ConduitCableItem> ALUMINUM_WIRE = ITEMS.register("aluminum_wire", () -> new ConduitCableItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(16), Conduits.ALUMINUM_WIRE));
	public static final RegistryObject<ConduitCableItem> GOLD_WIRE = ITEMS.register("gold_wire", () -> new ConduitCableItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(16), Conduits.GOLD_WIRE));
	public static final RegistryObject<ConduitCableItem> TIN_WIRE = ITEMS.register("tin_wire", () -> new ConduitCableItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(16), Conduits.TIN_WIRE));
	
	public static final RegistryObject<ScrewDriverItem> SCREW_DRIVER = ITEMS.register("screw_driver", () -> new ScrewDriverItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1)));
	
	public static final RegistryObject<BlockItem> MOTOR = ITEMS.register("motor", () -> new BlockItem(ModBlocks.MOTOR.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
	
}
