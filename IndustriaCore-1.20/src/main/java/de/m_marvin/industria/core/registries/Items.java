package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Items {
	
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IndustriaCore.MODID);
	public static void register() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<BlockItem> POWER_SOURCE = 	ITEMS.register("power_source", () -> new BlockItem(Blocks.POWER_SOURCE.get(), new Item.Properties().rarity(Rarity.EPIC)));
	public static final RegistryObject<BlockItem> MAGNET =			ITEMS.register("magnet", () -> new BlockItem(Blocks.MAGNET.get(), new Item.Properties().rarity(Rarity.EPIC)));
	
}
