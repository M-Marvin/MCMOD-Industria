package de.m_marvin.industria.core.conduits.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitLoadEvent;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitUnloadEvent;
import de.m_marvin.industria.core.conduits.engine.network.SCConduitPackage.SCBreakConduitPackage;
import de.m_marvin.industria.core.conduits.engine.network.SCConduitPackage.SCPlaceConduitPackage;
import de.m_marvin.industria.core.conduits.engine.network.SSyncConduitPackage;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientConduitPackageHandler {
	
	/* Handle SSyncConduitPackage package */
	
	private static HashMap<ChunkPos, SSyncConduitPackage> receivedPackages = new HashMap<ChunkPos, SSyncConduitPackage>();
	private static List<ChunkPos> handledPackages = new ArrayList<>();
	
	@SuppressWarnings("resource")
	public static void handleSyncConduitsFromServer(SSyncConduitPackage msg, NetworkEvent.Context ctx) {
		if (Minecraft.getInstance().level.isLoaded(msg.getChunkPos().getWorldPosition())) {
			if (!handlePackageInLoadedChunk(msg)) receivedPackages.put(msg.getChunkPos(), msg);
		} else {
			synchronized (receivedPackages) {
				receivedPackages.put(msg.getChunkPos(), msg);
			}
		}
	}
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onWorldTick(ClientTickEvent event) {
		Level level = Minecraft.getInstance().level;
		if (level != null) {
			for (ChunkPos chunk : receivedPackages.keySet()) {
				if (level.isLoaded(chunk.getWorldPosition())) {
					SSyncConduitPackage msg = receivedPackages.get(chunk);
					if (msg != null) {
						if (handlePackageInLoadedChunk(msg)) handledPackages.add(chunk);
					}
				}
			}
			synchronized (receivedPackages) {
				handledPackages.forEach(receivedPackages::remove);
			}
			handledPackages.clear();
		}
	}

	@SuppressWarnings("resource")
	public static boolean handlePackageInLoadedChunk(SSyncConduitPackage msg) {
		Level level = Minecraft.getInstance().level;
		ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
		
		if (msg.getRquest() == SyncRequestType.ADDED) {
			for (ConduitEntity conduitState : msg.conduits) {
				handler.addConduit(conduitState);
				MinecraftForge.EVENT_BUS.post(new ConduitLoadEvent(level, conduitState.getPosition(), conduitState));
			}
		} else if (msg.getRquest() == SyncRequestType.REMOVED) {
			for (ConduitEntity conduitState : msg.conduits) {
				handler.removeConduit(conduitState);
				MinecraftForge.EVENT_BUS.post(new ConduitUnloadEvent(level, conduitState.getPosition(), conduitState));
			}
		}
		return true;
	}

	/* Handle SCConduitPackage package */
	
	@SuppressWarnings("resource")
	public static void handlePlaceConduit(SCPlaceConduitPackage msg, Context ctx) {
		ConduitUtility.setConduit(Minecraft.getInstance().level, msg.getPosition(), msg.getConduit(), msg.getLength());
	}
	
	@SuppressWarnings("resource")
	public static void handleRemoveConduit(SCBreakConduitPackage msg, Context ctx) {
		ConduitUtility.removeConduit(Minecraft.getInstance().level, msg.getPosition(), msg.dropItems());
	}
	
	/* End of package handling */
	
}
