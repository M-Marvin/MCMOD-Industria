package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
	
	public static final TagKey<Block> BLOCK_SCREW_DRIVER_PICKUP = createBlock("screw_driver_pickup");
	
	private static TagKey<Block> createBlock(String name) {
		return TagKey.create(Registries.BLOCK, new ResourceLocation(IndustriaCore.MODID, name));
	}
	
	@SuppressWarnings("unused")
	private static TagKey<Item> createItem(String name) {
		return TagKey.create(Registries.ITEM, new ResourceLocation(IndustriaCore.MODID, name));
	}
	
}
