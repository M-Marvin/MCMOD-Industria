package de.m_marvin.industria.content.client.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.client.screens.PortableCoalGeneratorScreen;
import de.m_marvin.industria.content.client.screens.PortableFuelGeneratorScreen;
import de.m_marvin.industria.content.registries.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Industria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {
	
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		MenuScreens.register(ModMenuTypes.PORTABLE_FUEL_GENERATOR.get(), PortableFuelGeneratorScreen::new);
		MenuScreens.register(ModMenuTypes.PORTABLE_COAL_GENERATOR.get(), PortableCoalGeneratorScreen::new);
	}
	
}
