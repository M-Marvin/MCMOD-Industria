package de.m_marvin.industria.content.client.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.client.screens.MobileFuelGeneratorScreen;
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
		MenuScreens.register(ModMenuTypes.MOBILE_FUEL_GENERATOR.get(), MobileFuelGeneratorScreen::new);
	}
	
}
