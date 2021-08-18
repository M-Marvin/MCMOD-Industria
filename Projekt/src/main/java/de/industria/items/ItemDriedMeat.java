package de.industria.items;

import de.industria.Industria;
import de.industria.typeregistys.ModTabs;
import net.minecraft.item.Food;

public class ItemDriedMeat extends ItemBase {
	
	public static final Food DRIED_MEAT = (new Food.Builder()).nutrition(3).saturationMod(4F).build();
	
	public ItemDriedMeat() {
		super(new Properties().tab(ModTabs.MATERIALS).food(DRIED_MEAT));
		this.setRegistryName(Industria.MODID, "dried_meat");
	}
	
}
