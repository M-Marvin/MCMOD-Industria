package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.compound.types.items.CompoundableBlockItem;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import de.m_marvin.industria.core.kinetics.types.items.BeltItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Items {
	
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IndustriaCore.MODID);
	public static void register(FMLJavaModLoadingContext modctx) {
		ITEMS.register(modctx.getModEventBus());
	}
	
	public static final RegistryObject<BlockItem> ERROR_BLCOK = 	ITEMS.register("error_block", () -> new BlockItem(Blocks.ERROR_BLOCK.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> VOLTAGE_SOURCE = 	ITEMS.register("voltage_source", () -> new BlockItem(Blocks.VOLTAGE_SOURCE.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> MAGNET =			ITEMS.register("magnet", () -> new BlockItem(Blocks.MAGNET.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> WIRE_HOLDER = 	ITEMS.register("wire_holder", () -> new BlockItem(Blocks.WIRE_HOLDER.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> GEAR = 			ITEMS.register("gear", () -> new CompoundableBlockItem(Blocks.GEAR.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> LARGE_GEAR = 		ITEMS.register("large_gear", () -> new CompoundableBlockItem(Blocks.LARGE_GEAR.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> SHAFT = 			ITEMS.register("shaft", () -> new CompoundableBlockItem(Blocks.SHAFT.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> SHORT_SHAFT_1 = 	ITEMS.register("short_shaft_1", () -> new CompoundableBlockItem(Blocks.SHORT_SHAFT_1.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> SHORT_SHAFT_2 = 	ITEMS.register("short_shaft_2", () -> new CompoundableBlockItem(Blocks.SHORT_SHAFT_2.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> MOTOR = 			ITEMS.register("motor", () -> new BlockItem(Blocks.MOTOR.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> COMPOUND_BLOCK =	ITEMS.register("compound_block", () -> new BlockItem(Blocks.COMPOUND_BLOCK.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> BELT =			ITEMS.register("belt", () -> new BeltItem(Blocks.BELT.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> BELT_SHAFT = 		ITEMS.register("belt_shaft", () -> new CompoundableBlockItem(Blocks.BELT_SHAFT.get(), new Item.Properties().rarity(Rarity.EPIC)));
	
	public static final RegistryObject<AbstractConduitItem> ELECTRIC_WIRE = ITEMS.register("electric_wire", () -> new AbstractConduitItem(new Item.Properties().rarity(Rarity.EPIC), Conduits.ELECTRIC_CONDUIT) {
		@Override
		public void onPlaced(UseOnContext context, int length) {}
		@Override
		public int getMaxPlacingLength(ItemStack stack) {
			return 64;
		}
	});
	
}
