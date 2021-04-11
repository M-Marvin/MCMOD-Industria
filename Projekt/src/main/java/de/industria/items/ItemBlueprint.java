package de.industria.items;

import de.industria.Industria;
import de.industria.renderer.ItemBluePrintRenderer;
import net.minecraft.item.Item;

public class ItemBlueprint extends ItemBase {
	
	public ItemBlueprint() {
		super(new Item.Properties().setISTER(() -> ItemBluePrintRenderer::new));
		this.setRegistryName(Industria.MODID, "blueprint");
	}
	
}
