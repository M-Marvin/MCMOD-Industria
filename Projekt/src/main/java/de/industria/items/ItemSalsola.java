package de.industria.items;

import de.industria.Industria;
import de.industria.typeregistys.ModTabs;
import net.minecraft.item.Food;

public class ItemSalsola extends ItemBase {
	
	public static final Food SALSOLA = (new Food.Builder()).nutrition(1).saturationMod(0.1F).build();
	
	public ItemSalsola() {
		super(new Properties().tab(ModTabs.MATERIALS).food(SALSOLA));
		this.setRegistryName(Industria.MODID, "salsola");
	}
	
}
