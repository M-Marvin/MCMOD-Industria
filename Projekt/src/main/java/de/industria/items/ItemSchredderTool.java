package de.industria.items;

import de.industria.Industria;
import de.industria.renderer.ItemSchredderToolModel;

public abstract class ItemSchredderTool extends ItemBase {
	
	protected ItemSchredderToolModel toolModel;
	
	public ItemSchredderTool(String name, int durability) {
		super(name, new Properties().tab(Industria.MACHINES).stacksTo(8).defaultDurability(durability));
	}
	
}
