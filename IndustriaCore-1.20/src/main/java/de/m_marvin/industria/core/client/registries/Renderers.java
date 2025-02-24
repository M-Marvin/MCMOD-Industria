package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.compound.renderers.CompoundBlockEntityRenderer;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.BeltBlockentityRenderer;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.SimpleKineticBlockEntityRenderer;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class Renderers {
	
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BlockEntityTypes.SIMPLE_KINETIC.get(), SimpleKineticBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityTypes.MOTOR.get(), SimpleKineticBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityTypes.COMPOUND_BLOCK.get(), CompoundBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityTypes.BELT.get(), BeltBlockentityRenderer::new);
	}
	
}
