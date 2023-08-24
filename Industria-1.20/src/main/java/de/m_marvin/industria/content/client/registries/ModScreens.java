package de.m_marvin.industria.content.client.registries;

import de.m_marvin.industria.content.Industria;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Industria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {
	
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		//MenuScreens.register(ModMenuTypes.JUNCTION_BOX.get(), (a, b, c) -> new JunctionBoxScreen(a, b, c)); // Why can't this this shortened with Class::new ???
	}
	
}
