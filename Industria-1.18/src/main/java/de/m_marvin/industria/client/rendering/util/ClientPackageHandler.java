package de.m_marvin.industria.client.rendering.util;

import java.util.HashMap;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.network.SSyncPlacedConduit;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.conduit.ConduitWorldStorageCapability;
import de.m_marvin.industria.util.conduit.PlacedConduit;
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
public class ClientPackageHandler {
	
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
		LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			for (PlacedConduit conduitState : msg.conduitStates) {
				conduitHolder.resolve().get().addConduit(conduitState);
			}
		}
	}
	
	/* End of package handling */
	
}
