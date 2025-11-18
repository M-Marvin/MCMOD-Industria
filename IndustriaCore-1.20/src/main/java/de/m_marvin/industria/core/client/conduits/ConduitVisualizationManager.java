package de.m_marvin.industria.core.client.conduits;

import java.util.HashMap;
import java.util.Map;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ConduitHolderCapability;
import de.m_marvin.industria.core.conduits.events.ConduitEvent.ConduitAddEvent;
import de.m_marvin.industria.core.conduits.events.ConduitEvent.ConduitRemoveEvent;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.ConditionalExecutor;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.EventStage;
import dev.engine_room.flywheel.api.event.EndClientResourceReloadEvent;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ConduitVisualizationManager {

	protected final static Map<ConduitEntity, ConduitEffect<?>> conduitEffects = new HashMap<ConduitEntity, ConduitEffect<?>>();
	
	@SubscribeEvent
	public static void onConduitAddEvent(ConduitAddEvent event) {
		
		if (!event.getLevel().isClientSide()) return;
		if (event.getStage() != EventStage.POST) return;
		ConduitEffect<?> effect = conduitEffects.get(event.getConduitEntity());
		if (effect != null) return;
		effect = new ConduitEffect<ConduitEntity>(event.getConduitEntity());
		conduitEffects.put(event.getConduitEntity(), effect);
		VisualizationHelper.queueAdd(effect);
		
	}

	@SubscribeEvent
	public static void onConduitRemoveEvent(ConduitRemoveEvent event) {

		if (!event.getLevel().isClientSide()) return;
		if (event.getStage() != EventStage.PRE) return;
		ConduitEffect<?> effect = conduitEffects.get(event.getConduitEntity());
		if (effect == null) return;
		VisualizationHelper.queueRemove(effect);
		conduitEffects.remove(event.getConduitEntity());
		
	}
	
	@SubscribeEvent
	public static void onWorldUnload(LevelEvent.Unload event) {
		
		if (conduitEffects.size() > 0) {
			for (var effect : conduitEffects.values())
				VisualizationHelper.queueRemove(effect);
			conduitEffects.clear();
		}
		
	}

	@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Bus.MOD, value = Dist.CLIENT)
	public static class ConduitVisualizerReloadListener {
		@SubscribeEvent
		public static void onResourceReloadRegister(EndClientResourceReloadEvent event) {
			
			ConditionalExecutor.CLIENT_TICK_EXECUTOR.execute(() -> {
				ClientLevel level = Minecraft.getInstance().level;
				if (level == null) return;
				conduitEffects.clear();
				ConduitHolderCapability conduitHolder = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
				if (conduitHolder == null) return;
				conduitHolder.getConduits().forEach(conduitEntity -> {
					ConduitEffect<ConduitEntity> effect = new ConduitEffect<ConduitEntity>(conduitEntity);
					conduitEffects.put(conduitEntity, effect);
					VisualizationHelper.queueAdd(effect);
				});
			});
			
		}
	}
	
}
