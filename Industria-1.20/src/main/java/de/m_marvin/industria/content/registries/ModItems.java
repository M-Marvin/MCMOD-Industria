package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.items.ConduitCableItem;
import de.m_marvin.industria.content.items.ScrewDriverItem;
import net.minecraft.world.item.BlockItem;
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
	
	public static final RegistryObject<BlockItem> IRON_CONDUIT_CLAMP = ITEMS.register("iron_conduit_clamp", () -> new BlockItem(ModBlocks.IRON_CONDUIT_CLAMP.get(), new Item.Properties()));
	public static final RegistryObject<ConduitCableItem> INSULATED_COPPER_WIRE = ITEMS.register("insulated_copper_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_COPPER_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_ALUMINUM_WIRE = ITEMS.register("insulated_aluminum_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_ALUMINUM_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_GOLD_WIRE = ITEMS.register("insulated_gold_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_GOLD_WIRE));
	public static final RegistryObject<ConduitCableItem> INSULATED_TIN_WIRE = ITEMS.register("insulated_tin_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.INSULATED_TIN_WIRE));
	public static final RegistryObject<ConduitCableItem> COPPER_WIRE = ITEMS.register("copper_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.COPPER_WIRE));
	public static final RegistryObject<ConduitCableItem> ALUMINUM_WIRE = ITEMS.register("aluminum_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.ALUMINUM_WIRE));
	public static final RegistryObject<ConduitCableItem> GOLD_WIRE = ITEMS.register("gold_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.GOLD_WIRE));
	public static final RegistryObject<ConduitCableItem> TIN_WIRE = ITEMS.register("tin_wire", () -> new ConduitCableItem(new Item.Properties().stacksTo(16), ModConduits.TIN_WIRE));
	
	public static final RegistryObject<ScrewDriverItem> SCREW_DRIVER = ITEMS.register("screw_driver", () -> new ScrewDriverItem(new Item.Properties().stacksTo(1)));
	
	public static final RegistryObject<BlockItem> MOTOR = ITEMS.register("motor", () -> new BlockItem(ModBlocks.MOTOR.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> JUNCTION_BOX = ITEMS.register("junction_box", () -> new BlockItem(ModBlocks.JUNCTION_BOX.get(), new Item.Properties()));
	public static final RegistryObject<BlockItem> ELECTRIC_LAMP = ITEMS.register("electric_light", () -> new BlockItem(ModBlocks.ELECTRIC_LAMP.get(), new Item.Properties()));
	
}
