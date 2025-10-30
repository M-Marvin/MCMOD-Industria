package de.m_marvin.industria.content.types;

import de.m_marvin.industria.content.Industria;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class ModItemDisplayContext {
	
	public static final ItemDisplayContext CONVEYOR = ItemDisplayContext.create("conveyor", ResourceLocation.tryBuild(Industria.MODID, "conveyor"), ItemDisplayContext.FIXED);

	public static void register() {	} // dummy method to ensure the class is loaded during mod construction
	
}
