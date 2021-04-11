package de.industria.items;

import de.industria.Industria;
import net.minecraft.util.ResourceLocation;

public class ItemSchredderToolCrusher extends ItemSchredderTool {
	
	public ItemSchredderToolCrusher() {
		super("schredder_crusher", 12000);
	}
	
	@Override
	public ResourceLocation getModelTexture() {
		return new ResourceLocation(Industria.MODID, "textures/item/schredder_crusher.png");
	}

}
