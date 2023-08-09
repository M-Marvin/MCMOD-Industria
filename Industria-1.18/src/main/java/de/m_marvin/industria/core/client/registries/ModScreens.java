package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.electrics.JunctionBoxScreen;
import de.m_marvin.industria.core.registries.Container;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {
	
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		MenuScreens.register(Container.JUNCTION_BOX.get(), (menu, inv, title) -> new JunctionBoxScreen(menu, inv, title));
	}
	
}
