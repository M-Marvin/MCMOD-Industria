package de.m_marvin.industria.registries;

import java.util.Optional;

import de.m_marvin.industria.network.CChangeNodesPerBlockPackage;
import de.m_marvin.industria.network.SSyncPlacedConduit;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkPackages {
	
	public static final String PROTOCOL_VERSION = "1";
	
	public static void setupPackages(SimpleChannel network) {
		
		int id = 0;
		network.registerMessage(id++, SSyncPlacedConduit.class, SSyncPlacedConduit::encode, SSyncPlacedConduit::decode, SSyncPlacedConduit::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, CChangeNodesPerBlockPackage.class, CChangeNodesPerBlockPackage::encode, CChangeNodesPerBlockPackage::decode, CChangeNodesPerBlockPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		
	}
	
}
