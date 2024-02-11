package de.m_marvin.industria.core.registries;

import java.util.Optional;

import de.m_marvin.industria.core.conduits.engine.network.CChangeConduitPlacementLengthPackage;
import de.m_marvin.industria.core.conduits.engine.network.SCConduitPackage;
import de.m_marvin.industria.core.conduits.engine.network.SSyncConduitPackage;
import de.m_marvin.industria.core.electrics.engine.network.CEditPowerSourcePackage;
import de.m_marvin.industria.core.electrics.engine.network.CUpdateJunctionLanesPackage;
import de.m_marvin.industria.core.electrics.engine.network.SSyncComponentsPackage;
import de.m_marvin.industria.core.electrics.engine.network.SUpdateNetworkPackage;
import de.m_marvin.industria.core.magnetism.engine.network.SMagneticInfluencePackage;
import de.m_marvin.industria.core.magnetism.engine.network.SSyncMagneticPackage;
import de.m_marvin.industria.core.parametrics.engine.network.SSyncParametricsPackage;
import de.m_marvin.industria.core.physics.engine.VS2MassSyncPatch.SSyncVS2BlockInfoPackage;
import de.m_marvin.industria.core.scrollinput.engine.network.CScrollInputPackage;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkPackages {
	
	public static final String PROTOCOL_VERSION = "1";
	
	public static void setupPackages(SimpleChannel network) {
		int id = 0;
		network.registerMessage(id++, SSyncConduitPackage.class, SSyncConduitPackage::encode, SSyncConduitPackage::decode, SSyncConduitPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, CChangeConduitPlacementLengthPackage.class, CChangeConduitPlacementLengthPackage::encode, CChangeConduitPlacementLengthPackage::decode, CChangeConduitPlacementLengthPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, SCConduitPackage.SCPlaceConduitPackage.class, SCConduitPackage.SCPlaceConduitPackage::encode, SCConduitPackage.SCPlaceConduitPackage::decode, SCConduitPackage.SCPlaceConduitPackage::handle);
		network.registerMessage(id++, SCConduitPackage.SCBreakConduitPackage.class, SCConduitPackage.SCBreakConduitPackage::encode, SCConduitPackage.SCBreakConduitPackage::decode, SCConduitPackage.SCBreakConduitPackage::handle);
		network.registerMessage(id++, CScrollInputPackage.class, CScrollInputPackage::encode, CScrollInputPackage::decode, CScrollInputPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, CUpdateJunctionLanesPackage.class, CUpdateJunctionLanesPackage::encode, CUpdateJunctionLanesPackage::decode, CUpdateJunctionLanesPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, SSyncComponentsPackage.class, SSyncComponentsPackage::encode, SSyncComponentsPackage::decode, SSyncComponentsPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, CEditPowerSourcePackage.class, CEditPowerSourcePackage::encode, CEditPowerSourcePackage::decode, CEditPowerSourcePackage::handle);
		network.registerMessage(id++, SUpdateNetworkPackage.class, SUpdateNetworkPackage::encode, SUpdateNetworkPackage::decode, SUpdateNetworkPackage::handle);
		network.registerMessage(id++, SSyncMagneticPackage.class, SSyncMagneticPackage::encode, SSyncMagneticPackage::decode, SSyncMagneticPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SMagneticInfluencePackage.SAddInfluencePackage.class, SMagneticInfluencePackage.SAddInfluencePackage::encode, SMagneticInfluencePackage.SAddInfluencePackage::decode, SMagneticInfluencePackage.SAddInfluencePackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SMagneticInfluencePackage.SRemoveInfluencePackage.class, SMagneticInfluencePackage.SRemoveInfluencePackage::encode, SMagneticInfluencePackage.SRemoveInfluencePackage::decode, SMagneticInfluencePackage.SRemoveInfluencePackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SSyncParametricsPackage.class, SSyncParametricsPackage::encode, SSyncParametricsPackage::decode, SSyncParametricsPackage::handle);
		
		network.registerMessage(id++, SSyncVS2BlockInfoPackage.class, SSyncVS2BlockInfoPackage::encode, SSyncVS2BlockInfoPackage::decode, SSyncVS2BlockInfoPackage::handle);
	}
	
}
