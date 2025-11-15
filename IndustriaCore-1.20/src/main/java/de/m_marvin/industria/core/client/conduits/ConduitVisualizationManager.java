package de.m_marvin.industria.core.client.conduits;

import java.util.HashMap;
import java.util.Map;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.events.ConduitEvent;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ConduitVisualizationManager {

	protected final static Map<ConduitEntity, ConduitEffect<?>> conduitEffects = new HashMap<ConduitEntity, ConduitEffect<?>>();
	
	@SubscribeEvent
	public static void onConduitEvent(ConduitEvent event) {

		if (event instanceof ConduitEvent.ConduitLoadEvent || event instanceof ConduitEvent.ConduitPlaceEvent) {
			
			ConduitEffect<?> effect = conduitEffects.get(event.getConduitEntity());
			if (effect != null) return;
			effect = new ConduitEffect<ConduitEntity>(event.getConduitEntity());
			conduitEffects.put(event.getConduitEntity(), effect);
			VisualizationHelper.queueAdd(effect);
			
		} else {

			ConduitEffect<?> effect = conduitEffects.get(event.getConduitEntity());
			if (effect == null) return;
			VisualizationHelper.queueRemove(effect);
			conduitEffects.remove(event.getConduitEntity());
			
		}
		
	}
	
	@SubscribeEvent
	public static void onWorldUnload(LevelEvent.Unload event) {
		
		if (conduitEffects.size() > 0) {
			for (var effect : conduitEffects.values())
				VisualizationHelper.queueRemove(effect);
			conduitEffects.clear();
		}
		
	}
	
}
