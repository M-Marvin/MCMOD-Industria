package de.m_marvin.industria.content.client.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.blockentities.kinetics.BaseBeltBlockEntity;
import de.m_marvin.industria.content.blockentities.kinetics.BaseSimpleKineticBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.ConveyorBeltBlockEntity;
import de.m_marvin.industria.content.client.blockentityrenderer.ConveyorBeltBlockEntityRenderer;
import de.m_marvin.industria.content.client.blockentityrenderer.ElectroMagneticCoilBlockEntityRenderer;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.BeltBlockEntityRenderer;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.BeltBlockEntityVisual;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.SimpleKineticBlockEntityRenderer;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.SimpleKineticBlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ModBlockEntityRenderers {
	
	@SubscribeEvent
	public static void onClientSetup(RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntityTypes.ELECTRO_MAGNETIC_COIL.get(), ElectroMagneticCoilBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.BASE_SIMPLE_KINETIC.get(), SimpleKineticBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.BASE_BELT.get(), BeltBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CONVEYOR_BELT.get(), ConveyorBeltBlockEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerVisuals(FMLClientSetupEvent event) {
		// TODO flywheel conduit renderer
		VisualizerRegistry.setVisualizer(ModBlockEntityTypes.BASE_SIMPLE_KINETIC.get(), new SimpleBlockEntityVisualizer<BaseSimpleKineticBlockEntity>(SimpleKineticBlockEntityVisual::new, b -> true));
		VisualizerRegistry.setVisualizer(ModBlockEntityTypes.BASE_BELT.get(), new SimpleBlockEntityVisualizer<BaseBeltBlockEntity>(BeltBlockEntityVisual::new, b -> true));
		VisualizerRegistry.setVisualizer(ModBlockEntityTypes.CONVEYOR_BELT.get(), new SimpleBlockEntityVisualizer<ConveyorBeltBlockEntity>(BeltBlockEntityVisual::new, b -> true)); // TODO flywheel conveyor item renderer
	}
	
}
