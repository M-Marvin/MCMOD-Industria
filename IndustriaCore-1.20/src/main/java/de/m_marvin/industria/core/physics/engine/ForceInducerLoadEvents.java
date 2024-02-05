package de.m_marvin.industria.core.physics.engine;

import java.util.List;

import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.hooks.VSEvents;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("deprecation")
@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ForceInducerLoadEvents {

	@SubscribeEvent
	public static void onServerStartEvent(ServerStartingEvent event) {
		
		setupVSEvents(event.getServer());
		
	}
	
	public static void setupVSEvents(MinecraftServer server) {
		
		VSEvents.INSTANCE.getShipLoadEvent().on((loadEvent, registeredEvent) -> {
			
			if (server.isShutdown()) {
				registeredEvent.unregister();
				return;
			}
			
			ServerLevel level = VSGameUtilsKt.getLevelFromDimensionId(server, loadEvent.getShip().getChunkClaimDimension());
			
			List<ShipForcesInducer> forceInducers = loadEvent.getShip().getForceInducers();
			
			for (ShipForcesInducer inducer : forceInducers) {
				if (inducer instanceof ForcesInducer forceInducer) {
					forceInducer.initLevel(level);
				}
			}
			
		});
		
	}
	
}
