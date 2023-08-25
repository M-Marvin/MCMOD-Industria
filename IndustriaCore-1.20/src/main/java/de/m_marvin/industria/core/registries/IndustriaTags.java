package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class IndustriaTags {
	
	public static class Blocks {
		
		private static TagKey<Block> tag(String name) {
			return BlockTags.create(new ResourceLocation(IndustriaCore.MODID, name));
		}
		
	}
	
	public static class Items {

		public static final TagKey<Item> CONDUITS = tag("conduits");
		
		private static TagKey<Item> tag(String name) {
			return ItemTags.create(new ResourceLocation(IndustriaCore.MODID, name));
		}
		
	}
	
}
