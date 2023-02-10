package de.m_marvin.industria.core.conduits.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.conduits.engine.network.SSyncPlacedConduit;
import de.m_marvin.industria.core.conduits.engine.network.SSyncPlacedConduit.Status;
import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import de.m_marvin.industria.core.registries.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientConduitPackageHandler {
	
	/* Handle SSyncPlacedConduit package */
	
	private static HashMap<ChunkPos, SSyncPlacedConduit> receivedPackages = new HashMap<ChunkPos, SSyncPlacedConduit>();
	private static List<ChunkPos> handledPackages = new ArrayList<>();
	
	@SuppressWarnings("resource")
	public static void handleSyncConduitsFromServer(SSyncPlacedConduit msg, NetworkEvent.Context ctx) {
		if (Minecraft.getInstance().level.isLoaded(msg.getChunkPos().getWorldPosition())) {
			handlePackageInLoadedChunk(msg);
		} else {
			receivedPackages.put(msg.getChunkPos(), msg);
		}
	}
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		Level level = Minecraft.getInstance().level;
		if (level != null) {
			for (ChunkPos chunk : receivedPackages.keySet()) {
				if (level.isLoaded(chunk.getWorldPosition())) {
					SSyncPlacedConduit msg = receivedPackages.get(chunk);
					handlePackageInLoadedChunk(msg);
					handledPackages.add(chunk);
				}
			}
			handledPackages.forEach(receivedPackages::remove);
			handledPackages.clear();}
		}
	
	@SuppressWarnings("resource")
	public static void handlePackageInLoadedChunk(SSyncPlacedConduit msg) {
		Level level = Minecraft.getInstance().level;
		
		if (level != null) {
			LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
			if (conduitHolder.isPresent()) {
				if (msg.getStatus() == Status.ADDED) {
					for (PlacedConduit conduitState : msg.conduitStates) {
						conduitHolder.resolve().get().addConduit(conduitState);
						if (msg.isTriggerEvents()) conduitState.getConduit().onPlace(level, conduitState.getPosition(), conduitState);
					}
				} else if (msg.getStatus() == Status.REMOVED) {
					for (PlacedConduit conduitState : msg.conduitStates) {
						conduitHolder.resolve().get().removeConduit(conduitState);
						if (msg.isTriggerEvents()) conduitState.getConduit().onBreak(level, conduitState.getPosition(), conduitState, msg.isEventDropItems());
					}
				}
			}
		}
	}
	
	/* End of package handling */
	
}
