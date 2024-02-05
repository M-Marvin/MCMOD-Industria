package de.m_marvin.industria.core.magnetism.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.m_marvin.industria.core.magnetism.MagnetismUtility;
import de.m_marvin.industria.core.magnetism.engine.network.SMagneticInfluencePackage.SAddInfluencePackage;
import de.m_marvin.industria.core.magnetism.engine.network.SMagneticInfluencePackage.SRemoveInfluencePackage;
import de.m_marvin.industria.core.magnetism.engine.network.SSyncMagneticPackage;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientMagnetismPackageHandler {
	
	/* Handle SSyncConduitPackage package */
	
	private static HashMap<ChunkPos, SSyncMagneticPackage> receivedPackages = new HashMap<ChunkPos, SSyncMagneticPackage>();
	private static List<ChunkPos> handledPackages = new ArrayList<>();
	
	@SuppressWarnings("resource")
	public static void handleSyncMagneticFromServer(SSyncMagneticPackage msg, Context ctx) {
		if (Minecraft.getInstance().level.isLoaded(msg.getChunkPos().getWorldPosition()) || msg.request == SyncRequestType.REMOVED) {
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
					SSyncMagneticPackage msg = receivedPackages.get(chunk);
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
	public static boolean handlePackageInLoadedChunk(SSyncMagneticPackage msg) {
		Level level = Minecraft.getInstance().level;
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		
		if (msg.getRquest() == SyncRequestType.ADDED) {
			for (MagneticField magneticField : msg.fields) {
				handler.addField(magneticField);
			}
		} else if (msg.getRquest() == SyncRequestType.REMOVED) {
			for (MagneticField magneticField : msg.fields) {
				handler.removeField(magneticField);
			}
		}
		return true;
	}

	/* Handle SMagneticInfluencePackage package */
	
	@SuppressWarnings("resource")
	public static void handleRemoveInfluence(SRemoveInfluencePackage msg, Context ctx) {
		// TODO delay package
		MagnetismUtility.removeFieldInfluence(Minecraft.getInstance().level, msg.getPosition());
	}

	@SuppressWarnings("resource")
	public static void handleAddInfluence(SAddInfluencePackage msg, Context ctx) {
		// TODO delay package
		MagnetismUtility.setFieldInfluence(Minecraft.getInstance().level, msg.getInfluence());
	}

	/* End of package handling */
	
}
