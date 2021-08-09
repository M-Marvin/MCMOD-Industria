package de.industria.items;

import de.industria.renderer.ItemSchredderToolModel;
import de.industria.typeregistys.ModTabs;

public abstract class ItemSchredderTool extends ItemBase {
	
	protected ItemSchredderToolModel toolModel;
	
	public ItemSchredderTool(String name, int durability) {
		super(name, new Properties().tab(ModTabs.MACHINES).stacksTo(8).defaultDurability(durability));
	}
	
}
