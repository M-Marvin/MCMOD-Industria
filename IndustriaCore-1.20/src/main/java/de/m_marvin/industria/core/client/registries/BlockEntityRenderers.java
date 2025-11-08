package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.compound.renderers.CompoundBlockEntityRenderer;
import de.m_marvin.industria.core.client.compound.renderers.CompoundBlockEntityVisual;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.BeltBlockEntityRenderer;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.BeltBlockEntityVisual;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.SimpleKineticBlockEntityRenderer;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.SimpleKineticBlockEntityVisual;
import de.m_marvin.industria.core.compound.types.blockentities.CompoundBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blockentities.MotorBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blockentities.SimpleKineticBlockEntity;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class BlockEntityRenderers {
	
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BlockEntityTypes.SIMPLE_KINETIC.get(), SimpleKineticBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityTypes.MOTOR.get(), SimpleKineticBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityTypes.COMPOUND_BLOCK.get(), CompoundBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(BlockEntityTypes.BELT.get(), BeltBlockEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerVisuals(FMLClientSetupEvent event) {
		VisualizerRegistry.setVisualizer(BlockEntityTypes.SIMPLE_KINETIC.get(), new SimpleBlockEntityVisualizer<SimpleKineticBlockEntity>(SimpleKineticBlockEntityVisual::new, b -> true));
		VisualizerRegistry.setVisualizer(BlockEntityTypes.MOTOR.get(), new SimpleBlockEntityVisualizer<MotorBlockEntity>(SimpleKineticBlockEntityVisual::new, b -> true));
		VisualizerRegistry.setVisualizer(BlockEntityTypes.COMPOUND_BLOCK.get(), new SimpleBlockEntityVisualizer<CompoundBlockEntity>(CompoundBlockEntityVisual::new, b -> false));
		VisualizerRegistry.setVisualizer(BlockEntityTypes.BELT.get(), new SimpleBlockEntityVisualizer<BeltBlockEntity>(BeltBlockEntityVisual::new, b -> true));
	}
	
}
