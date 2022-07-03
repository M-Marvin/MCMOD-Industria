package de.m_marvin.industria.client.registries;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.simibubi.create.content.contraptions.base.HalfShaftInstance;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.client.rendering.MotorBlockEntityRenderer;
import de.m_marvin.industria.registries.ModBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ModBlockEntityRenderers {
	
	@SubscribeEvent
	public static void onClientSetup(RegisterRenderers event) {
		
		event.registerBlockEntityRenderer(ModBlockEntities.MOTOR.get(), MotorBlockEntityRenderer::new);
		InstancedRenderRegistry.configure(ModBlockEntities.MOTOR.get()).factory(HalfShaftInstance::new).skipRender(be -> false).apply();
		event.registerBlockEntityRenderer(ModBlockEntities.GENERATOR.get(), MotorBlockEntityRenderer::new);
		InstancedRenderRegistry.configure(ModBlockEntities.GENERATOR.get()).factory(HalfShaftInstance::new).skipRender(be -> false).apply();
		
	}
	
	
}
