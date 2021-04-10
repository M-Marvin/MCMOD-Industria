package de.redtec.items;

import de.redtec.RedTec;
import net.minecraft.util.ResourceLocation;

public class ItemSchredderToolCrusher extends ItemSchredderTool {
	
	public ItemSchredderToolCrusher() {
		super("schredder_crusher", 12000);
	}
	
	@Override
	public ResourceLocation getModelTexture() {
		return new ResourceLocation(RedTec.MODID, "textures/item/schredder_crusher.png");
	}

}
