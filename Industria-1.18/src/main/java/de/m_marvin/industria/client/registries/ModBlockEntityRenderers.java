package de.m_marvin.industria.client.registries;

import java.util.function.Predicate;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.simibubi.create.content.contraptions.base.HalfShaftInstance;
import com.simibubi.create.content.contraptions.relays.encased.ShaftInstance;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.blockentities.BaseGeneratingKineticBlockEntity;
import de.m_marvin.industria.blockentities.BaseKineticBlockEntity;
import de.m_marvin.industria.client.rendering.MotorBlockEntityRenderer;
import de.m_marvin.industria.registries.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ModBlockEntityRenderers {
	
	@SubscribeEvent
	public static void onClientSetup(RegisterRenderers event) {
		
		// TODO
		event.registerBlockEntityRenderer(ModBlockEntities.MOTOR.get(), MotorBlockEntityRenderer::new);
		registerFlywheelShaftRendererGenerator(ModBlockEntities.MOTOR, ShaftType.HALF, (be) -> false);
		//InstancedRenderRegistry.configure(ModBlockEntities.MOTOR.get()).factory(HalfShaftInstance::new).skipRender(be -> false).apply();
		event.registerBlockEntityRenderer(ModBlockEntities.GENERATOR.get(), MotorBlockEntityRenderer::new);
		registerFlywheelShaftRenderer(ModBlockEntities.GENERATOR, ShaftType.HALF, (be) -> false);
		//InstancedRenderRegistry.configure(ModBlockEntities.GENERATOR.get()).factory(HalfShaftInstance::new).skipRender(be -> false).apply();
		
	}

	public static <T extends BaseKineticBlockEntity> void registerFlywheelShaftRenderer(RegistryObject<BlockEntityType<T>> type, ShaftType shaftType, final Predicate<T> skipRender) {
		switch (shaftType) {
		case FULL: InstancedRenderRegistry.configure(type.get()).factory(ShaftInstance::new).skipRender(skipRender).apply();
		case HALF: InstancedRenderRegistry.configure(type.get()).factory(HalfShaftInstance::new).skipRender(skipRender).apply();
		}
	}

	public static <T extends BaseGeneratingKineticBlockEntity> void registerFlywheelShaftRendererGenerator(RegistryObject<BlockEntityType<T>> type, ShaftType shaftType, final Predicate<T> skipRender) {
		switch (shaftType) {
		case FULL: InstancedRenderRegistry.configure(type.get()).factory(ShaftInstance::new).skipRender(skipRender).apply();
		case HALF: InstancedRenderRegistry.configure(type.get()).factory(HalfShaftInstance::new).skipRender(skipRender).apply();
		}
	}

	public static enum ShaftType {
		FULL,HALF;
	}
	
}
