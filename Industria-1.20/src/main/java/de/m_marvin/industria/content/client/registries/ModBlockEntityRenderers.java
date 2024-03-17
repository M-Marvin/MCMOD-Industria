package de.m_marvin.industria.content.client.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.client.blockentityrenderer.ElectroMagneticCoilBlockEntityRenderer;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ModBlockEntityRenderers {
	
	@SubscribeEvent
	public static void onClientSetup(RegisterRenderers event) {
		
		event.registerBlockEntityRenderer(ModBlockEntityTypes.ELECTRO_MAGNETIC_COIL.get(), ElectroMagneticCoilBlockEntityRenderer::new);
		
	}
	
}
