package de.m_marvin.industria.core.electrics.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.engine.network.SSyncComponentsPackage;
import de.m_marvin.industria.core.electrics.types.IElectric;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.SyncRequestType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientElectricPackageHandler {
	
	/* Handle SSyncComponentsPackage package */
	
	private static HashMap<ChunkPos, SSyncComponentsPackage> receivedPackages = new HashMap<ChunkPos, SSyncComponentsPackage>();
	private static List<ChunkPos> handledPackages = new ArrayList<>();
	
	@SuppressWarnings("resource")
	public static void handleSyncComponentsServer(SSyncComponentsPackage msg, NetworkEvent.Context ctx) {
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
					SSyncComponentsPackage msg = receivedPackages.get(chunk);
					if (msg != null) {
						boolean flag = handlePackageInLoadedChunk(msg);
						if (flag) handledPackages.add(chunk);
					}
				}
			}
			synchronized (receivedPackages) {
				handledPackages.forEach(receivedPackages::remove);
			}
			handledPackages.clear();
		}
	}
	
	@SuppressWarnings({ "resource", "unchecked", "rawtypes" })
	public static boolean handlePackageInLoadedChunk(SSyncComponentsPackage msg) {
		Level level = Minecraft.getInstance().level;
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		
		if (msg.request == SyncRequestType.ADDED) {
			List<Object> handledComponents = new ArrayList<>();
			for (Entry<Object, IElectric<?, ?, ?>> electric : msg.components.entrySet()) {
				Component<?, ?, ?> component = new Component(level, electric.getKey(), electric.getValue());
				if (component.instance() == null) continue;
				handler.addToNetwork(component);
				handledComponents.add(electric.getKey());
			}
			handledComponents.forEach(msg.components::remove);
			if (msg.components.size() > 0) return false;
		} else {
			for (Entry<Object, IElectric<?, ?, ?>> component : msg.components.entrySet()) {
				handler.removeFromNetwork(component.getKey());
			}
		}
		return true;
	}
	
	/* End of package handling */
	
}
