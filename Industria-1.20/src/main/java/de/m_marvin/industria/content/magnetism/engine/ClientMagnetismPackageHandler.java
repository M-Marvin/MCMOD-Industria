package de.m_marvin.industria.content.magnetism.engine;

import de.m_marvin.industria.content.magnetism.MagnetismUtility;
import de.m_marvin.industria.content.magnetism.engine.network.SMagneticInfluencePackage.SCAddInfluencePackage;
import de.m_marvin.industria.content.magnetism.engine.network.SMagneticInfluencePackage.SCRemoveInfluencePackage;
import de.m_marvin.industria.content.magnetism.engine.network.SSyncMagneticPackage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientMagnetismPackageHandler {
	
	public static void handleSyncMagneticFromServer(SSyncMagneticPackage msg, Context ctx) {
		
		
		
	}

	/* Handle SMagneticInfluencePackage package */
	
	@SuppressWarnings("resource")
	public static void handleRemoveInfluence(SCRemoveInfluencePackage msg, Context ctx) {
		MagnetismUtility.removeFieldInfluence(Minecraft.getInstance().level, msg.getPosition());
	}

	@SuppressWarnings("resource")
	public static void handleAddInfluence(SCAddInfluencePackage msg, Context ctx) {
		MagnetismUtility.setFieldInfluence(Minecraft.getInstance().level, msg.getInfluence());
	}

	/* End of package handling */
	
}
