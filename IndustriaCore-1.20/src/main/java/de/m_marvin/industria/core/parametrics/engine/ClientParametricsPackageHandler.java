package de.m_marvin.industria.core.parametrics.engine;

import de.m_marvin.industria.core.parametrics.BlockParametricsManager;
import de.m_marvin.industria.core.parametrics.engine.network.SSyncParametricsPackage;
import net.minecraftforge.network.NetworkEvent;

public class ClientParametricsPackageHandler {
	
	public static void handleSyncParametricsFromServer(SSyncParametricsPackage msg, NetworkEvent.Context ctx) {
		
		BlockParametricsManager.getInstance().updateParametrics(msg.getParametrics());
		
	}
	
}
