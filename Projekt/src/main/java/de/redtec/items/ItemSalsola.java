package de.redtec.items;

import de.redtec.RedTec;
import net.minecraft.item.Food;

public class ItemSalsola extends ItemBase {
	
	public static final Food SALSOLA = (new Food.Builder()).hunger(1).saturation(0.1F).build();
	
	public ItemSalsola() {
		super(new Properties().group(RedTec.MATERIALS).food(SALSOLA));
		this.setRegistryName(RedTec.MODID, "salsola");
	}
	
}
