package de.industria.items;

import de.industria.Industria;
import de.industria.renderer.ItemSchredderToolModel;
import net.minecraft.util.ResourceLocation;

public abstract class ItemSchredderTool extends ItemBase {
	
	protected ItemSchredderToolModel toolModel;
	
	public ItemSchredderTool(String name, int durability) {
		super(name, new Properties().group(Industria.MACHINES).maxStackSize(8).defaultMaxDamage(durability));
	}
	
	public abstract ResourceLocation getModelTexture();
	
}
