package de.redtec.items;

import de.redtec.RedTec;
import de.redtec.renderer.ItemSchredderToolModel;
import net.minecraft.util.ResourceLocation;

public abstract class ItemSchredderTool extends ItemBase {
	
	protected ItemSchredderToolModel toolModel;
	
	public ItemSchredderTool(String name, int durability) {
		super(name, new Properties().group(RedTec.MACHINES).maxStackSize(8).defaultMaxDamage(durability));
	}
	
	public abstract ResourceLocation getModelTexture();
	
}
