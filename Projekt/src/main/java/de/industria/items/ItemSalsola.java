package de.industria.items;

import de.industria.Industria;
import net.minecraft.item.Food;

public class ItemSalsola extends ItemBase {
	
	public static final Food SALSOLA = (new Food.Builder()).hunger(1).saturation(0.1F).build();
	
	public ItemSalsola() {
		super(new Properties().group(Industria.MATERIALS).food(SALSOLA));
		this.setRegistryName(Industria.MODID, "salsola");
	}
	
}
