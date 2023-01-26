package de.m_marvin.industria.core.conduits.engine;

import java.util.HashMap;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.registries.ModCapabilities;
import de.m_marvin.industria.core.conduits.engine.network.SSyncPlacedConduit;
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
	
	private static HashMap<ChunkPos, SSyncPlacedConduit> conduitClientSyncPackages = new HashMap<ChunkPos, SSyncPlacedConduit>();
	
	@SuppressWarnings("resource")
	public static void handleSyncConduitsFromServer(SSyncPlacedConduit msg, NetworkEvent.Context ctx) {
		if (Minecraft.getInstance().level.isLoaded(msg.getChunkPos().getWorldPosition())) {
			handlePackageInLoadedChunk(msg);
		} else {
			conduitClientSyncPackages.put(msg.getChunkPos(), msg);
		}
	}
	
	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		ChunkPos chunkPos = event.getChunk().getPos();
		if (conduitClientSyncPackages.containsKey(chunkPos)) {
			SSyncPlacedConduit msg = conduitClientSyncPackages.remove(chunkPos);
			handlePackageInLoadedChunk(msg);
		}
	}
	
	@SuppressWarnings("resource")
	public static void handlePackageInLoadedChunk(SSyncPlacedConduit msg) {
		Level level = Minecraft.getInstance().level;
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			if (!msg.removed) {
				for (PlacedConduit conduitState : msg.conduitStates) {
					conduitHolder.resolve().get().addConduit(conduitState);
				}
			} else {
				for (PlacedConduit conduitState : msg.conduitStates) {
					conduitHolder.resolve().get().removeConduit(conduitState);
				}
			}
		}
	}
	
	/* End of package handling */
	
}
