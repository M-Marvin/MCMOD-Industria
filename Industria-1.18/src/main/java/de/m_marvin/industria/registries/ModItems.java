package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.items.ConduitCableItem;
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
	public static final RegistryObject<ConduitCableItem> CONDUIT_TEST = ITEMS.register("conduit_test", () -> new ConduitCableItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(16), Conduits.DEFAULT_CONDUIT));
	
}
