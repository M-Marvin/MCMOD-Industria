package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.Industria;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
	
	public static final TagKey<Block> BLOCK_SCREW_DRIVER_PICKUP = createBlock("screw_driver_pickup");
	
	private static TagKey<Block> createBlock(String name) {
		return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Industria.MODID, name));
	}
	
	@SuppressWarnings("unused")
	private static TagKey<Item> createItem(String name) {
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Industria.MODID, name));
	}
	
}
