package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
	
	public static class Blocks {

		public static final TagKey<Block> BLOCK_SCREW_DRIVER_PICKUP = createBlock("screw_driver_pickup");
		public static final TagKey<Block> ELECTRO_MAGNETIC_COILS = createBlock("electro_magnetic_coils");
		
		private static TagKey<Block> createBlock(String name) {
			return TagKey.create(Registries.BLOCK, new ResourceLocation(Industria.MODID, name));
		}
		
	}
	
	public static class Items {

		@SuppressWarnings("unused")
		private static TagKey<Item> createItem(String name) {
			return TagKey.create(Registries.ITEM, new ResourceLocation(Industria.MODID, name));
		}
		
	}
	
}
