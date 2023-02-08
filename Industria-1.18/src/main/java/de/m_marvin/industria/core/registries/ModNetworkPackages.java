package de.m_marvin.industria.core.registries;

import java.util.Optional;

import de.m_marvin.industria.core.conduits.engine.network.CBreakConduitPackage;
import de.m_marvin.industria.core.conduits.engine.network.CChangeConduitPlacementLength;
import de.m_marvin.industria.core.conduits.engine.network.SSyncPlacedConduit;
import de.m_marvin.industria.core.scrollinput.engine.network.CScrollInputPackage;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworkPackages {
	
	public static final String PROTOCOL_VERSION = "1";
	
	public static void setupPackages(SimpleChannel network) {
		int id = 0;
		network.registerMessage(id++, SSyncPlacedConduit.class, SSyncPlacedConduit::encode, SSyncPlacedConduit::decode, SSyncPlacedConduit::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, CChangeConduitPlacementLength.class, CChangeConduitPlacementLength::encode, CChangeConduitPlacementLength::decode, CChangeConduitPlacementLength::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, CBreakConduitPackage.class, CBreakConduitPackage::encode, CBreakConduitPackage::decode, CBreakConduitPackage::handle);
		network.registerMessage(id++, CScrollInputPackage.class, CScrollInputPackage::encode, CScrollInputPackage::decode, CScrollInputPackage::handle);
	}
	
}
