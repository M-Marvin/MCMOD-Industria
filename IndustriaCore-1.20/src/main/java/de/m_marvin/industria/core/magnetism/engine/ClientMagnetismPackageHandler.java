package de.m_marvin.industria.core.magnetism.engine;

import de.m_marvin.industria.core.magnetism.MagnetismUtility;
import de.m_marvin.industria.core.magnetism.engine.network.SMagneticInfluencePackage.SAddInfluencePackage;
import de.m_marvin.industria.core.magnetism.engine.network.SMagneticInfluencePackage.SRemoveInfluencePackage;
import de.m_marvin.industria.core.magnetism.engine.network.SSyncMagneticPackage;
import de.m_marvin.industria.core.magnetism.engine.network.SUpdateMagneticFieldPackage;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.ConditionalExecutor;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientMagnetismPackageHandler {
	
	/* Handle SSyncConduitPackage package */
	
	@SuppressWarnings("resource")
	public static void handleSyncMagneticFromServer(SSyncMagneticPackage msg, Context ctx) {
		
		ConditionalExecutor.CLIENT_TICK_EXECUTOR.executeAsSoonAs(() -> {
			
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
			
		}, () -> Minecraft.getInstance().level.isLoaded(msg.getChunkPos().getWorldPosition()) || msg.request == SyncRequestType.REMOVED);
		
	}
	
	/* Handle SMagneticInfluencePackage package */
	
	@SuppressWarnings("resource")
	public static void handleRemoveInfluence(SRemoveInfluencePackage msg, Context ctx) {
		// Delay to make sure package gets processed after block state changes
		ConditionalExecutor.CLIENT_TICK_EXECUTOR.executeAfterDelay(() -> {
			MagnetismUtility.removeFieldInfluence(Minecraft.getInstance().level, msg.getPosition());
		}, 0);
	}

	@SuppressWarnings("resource")
	public static void handleAddInfluence(SAddInfluencePackage msg, Context ctx) {
		// Delay to make sure package gets processed after block state changes
		ConditionalExecutor.CLIENT_TICK_EXECUTOR.executeAfterDelay(() -> {
			MagnetismUtility.setFieldInfluence(Minecraft.getInstance().level, msg.getInfluence());
		}, 1);
	}

	/* Handle SUpdateMagneticFieldPackage */
	
	@SuppressWarnings("resource")
	public static void handleUpdateMagneticField(SUpdateMagneticFieldPackage msg, Context context) {
		
		Level level = Minecraft.getInstance().level;
		MagnetismUtility.updateField(level, msg.getPos());
		
	}
	
	/* End of package handling */
	
}
