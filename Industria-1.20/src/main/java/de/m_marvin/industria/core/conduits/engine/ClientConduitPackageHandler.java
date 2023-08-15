package de.m_marvin.industria.core.conduits.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.network.SSyncConduit;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitLoadEvent;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitUnloadEvent;
import de.m_marvin.industria.core.conduits.engine.network.SCConduitPackage.SCBreakConduitPackage;
import de.m_marvin.industria.core.conduits.engine.network.SCConduitPackage.SCPlaceConduitPackage;
import de.m_marvin.industria.core.conduits.engine.network.SSyncConduit.Status;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientConduitPackageHandler {
	
	/* Handle SSyncPlacedConduit package */
	
	private static HashMap<ChunkPos, SSyncConduit> receivedPackages = new HashMap<ChunkPos, SSyncConduit>();
	private static List<ChunkPos> handledPackages = new ArrayList<>();
	
	@SuppressWarnings("resource")
	public static void handleSyncConduitsFromServer(SSyncConduit msg, NetworkEvent.Context ctx) {
		if (Minecraft.getInstance().level.isLoaded(msg.getChunkPos().getWorldPosition())) {
			handlePackageInLoadedChunk(msg);
		} else {
			synchronized (receivedPackages) {
				receivedPackages.put(msg.getChunkPos(), msg);
			}
		}
	}

	@SuppressWarnings("resource")
	public static void handlePlaceConduit(SCPlaceConduitPackage msg, Context ctx) {
		ConduitUtility.setConduit(Minecraft.getInstance().level, msg.getPosition(), msg.getConduit(), msg.getLength());
	}
	
	@SuppressWarnings("resource")
	public static void handleRemoveConduit(SCBreakConduitPackage msg, Context ctx) {
		ConduitUtility.removeConduit(Minecraft.getInstance().level, msg.getPosition(), msg.dropItems());
	}
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		Level level = Minecraft.getInstance().level;
		if (level != null) {
			for (ChunkPos chunk : receivedPackages.keySet()) {
				if (level.isLoaded(chunk.getWorldPosition())) {
					SSyncConduit msg = receivedPackages.get(chunk);
					if (msg != null) {
						handlePackageInLoadedChunk(msg);
						handledPackages.add(chunk);
					}
				}
			}
			synchronized (receivedPackages) {
				handledPackages.forEach(receivedPackages::remove);
			}
			handledPackages.clear();}
		}
	
	@SuppressWarnings("resource")
	public static void handlePackageInLoadedChunk(SSyncConduit msg) {
		Level level = Minecraft.getInstance().level;
		
		if (level != null) {
			LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(Capabilities.CONDUIT_HANDLER_CAPABILITY);
			if (conduitHolder.isPresent()) {
				if (msg.getStatus() == Status.ADDED) {
					for (ConduitEntity conduitState : msg.conduits) {
						conduitHolder.resolve().get().addConduit(conduitState);
						MinecraftForge.EVENT_BUS.post(new ConduitLoadEvent(level, conduitState.getPosition(), conduitState));
					}
				} else if (msg.getStatus() == Status.REMOVED) {
					for (ConduitEntity conduitState : msg.conduits) {
						conduitHolder.resolve().get().removeConduit(conduitState);
						MinecraftForge.EVENT_BUS.post(new ConduitUnloadEvent(level, conduitState.getPosition(), conduitState));
					}
				}
			}
		}
	}
	
	/* End of package handling */
	
}