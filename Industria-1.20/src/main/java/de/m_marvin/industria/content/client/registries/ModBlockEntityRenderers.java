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
		
		// TODO
//		event.registerBlockEntityRenderer(ModBlockEntities.MOTOR.get(), MotorBlockEntityRenderer::new);
//		registerFlywheelShaftRendererGenerator(ModBlockEntities.MOTOR, ShaftType.HALF, (be) -> false);
		//InstancedRenderRegistry.configure(ModBlockEntities.MOTOR.get()).factory(HalfShaftInstance::new).skipRender(be -> false).apply();
//		event.registerBlockEntityRenderer(ModBlockEntities.GENERATOR.get(), MotorBlockEntityRenderer::new);
//		registerFlywheelShaftRenderer(ModBlockEntities.GENERATOR, ShaftType.HALF, (be) -> false);
		//InstancedRenderRegistry.configure(ModBlockEntities.GENERATOR.get()).factory(HalfShaftInstance::new).skipRender(be -> false).apply();
		
	}

//	public static <T extends BaseKineticBlockEntity> void registerFlywheelShaftRenderer(RegistryObject<BlockEntityType<T>> type, ShaftType shaftType, final Predicate<T> skipRender) {
//		switch (shaftType) {
//		case FULL: InstancedRenderRegistry.configure(type.get()).factory(ShaftInstance::new).skipRender(skipRender).apply();
//		case HALF: InstancedRenderRegistry.configure(type.get()).factory(HalfShaftInstance::new).skipRender(skipRender).apply();
//		}
//	}
//
//	public static <T extends BaseGeneratingKineticBlockEntity> void registerFlywheelShaftRendererGenerator(RegistryObject<BlockEntityType<T>> type, ShaftType shaftType, final Predicate<T> skipRender) {
//		switch (shaftType) {
//		case FULL: InstancedRenderRegistry.configure(type.get()).factory(ShaftInstance::new).skipRender(skipRender).apply();
//		case HALF: InstancedRenderRegistry.configure(type.get()).factory(HalfShaftInstance::new).skipRender(skipRender).apply();
//		}
//	}

//	public static enum ShaftType {
//		FULL,HALF;
//	}
	
}
