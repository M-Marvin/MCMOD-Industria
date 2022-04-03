package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.items.FlexibleConduitItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=Industria.MODID)
public class ModItems {
	
	public static final Item IRON_CONDUIT_CLAMP = registerItem("conduit_clamp", new BlockItem(ModBlocks.IRON_CONDUIT_CLAMP, new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
	public static final Item CONDUIT_TEST = registerItem("conduit_test", new FlexibleConduitItem(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE), Conduits.DEFAULT_CONDUIT));
	
	public static BlockItem registerBlockItem(Block block) {
		return registerBlockItem(block, new Item.Properties());
	}
	public static BlockItem registerBlockItem(Block block, Item.Properties properties) {
		BlockItem item = new BlockItem(block, properties);
		GameData.getBlockItemMap().put(block, item);
		return registerItem(block.getRegistryName().getPath(), item);
	}
	
	public static <T extends Item> T registerItem(String name, T item) {
		item.setRegistryName(Industria.MODID, name);
		ForgeRegistries.ITEMS.register(item);
		return item;
	}
	
}
