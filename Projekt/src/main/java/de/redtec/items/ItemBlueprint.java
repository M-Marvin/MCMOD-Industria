package de.redtec.items;

import de.redtec.RedTec;
import de.redtec.renderer.ItemBluePrintRenderer;
import net.minecraft.item.Item;

public class ItemBlueprint extends ItemBase {
	
	public ItemBlueprint() {
		super(new Item.Properties().setISTER(() -> ItemBluePrintRenderer::new));
		this.setRegistryName(RedTec.MODID, "blueprint");
	}
	
}
