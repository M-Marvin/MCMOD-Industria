package de.m_marvin.industria.core.registries;

import java.util.Optional;

import de.m_marvin.industria.core.conduits.engine.network.CChangeConduitPlacementLengthPackage;
import de.m_marvin.industria.core.conduits.engine.network.SSyncAddedConduits;
import de.m_marvin.industria.core.conduits.engine.network.SSyncRemovedConduits;
import de.m_marvin.industria.core.conduits.engine.network.SUpdateConduitEntity;
import de.m_marvin.industria.core.contraptions.engine.VS2MassSyncPatch.SSyncVS2BlockInfoPackage;
import de.m_marvin.industria.core.electrics.engine.network.CSwitchNetworkStatePackage;
import de.m_marvin.industria.core.electrics.engine.network.CUpdateJunctionLanesPackage;
import de.m_marvin.industria.core.electrics.engine.network.SSyncElectricComponentsPackage;
import de.m_marvin.industria.core.electrics.engine.network.SUpdateElectricNetworkPackage;
import de.m_marvin.industria.core.kinetics.engine.network.SSyncKineticComponentsPackage;
import de.m_marvin.industria.core.kinetics.engine.network.SUpdateKineticNetworkPackage;
import de.m_marvin.industria.core.magnetism.engine.network.SMagneticInfluencePackage;
import de.m_marvin.industria.core.magnetism.engine.network.SSyncMagneticPackage;
import de.m_marvin.industria.core.magnetism.engine.network.SUpdateMagneticFieldPackage;
import de.m_marvin.industria.core.parametrics.engine.network.SSyncParametricsPackage;
import de.m_marvin.industria.core.scrollinput.engine.network.CScrollInputPackage;
import de.m_marvin.industria.core.util.network.CContainerDataChanged;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkPackages {
	
	public static final String PROTOCOL_VERSION = "1";
	
	public static void setupPackages(SimpleChannel network) {
		int id = 0;
		network.registerMessage(id++, CContainerDataChanged.class, CContainerDataChanged::encode, CContainerDataChanged::decode, CContainerDataChanged::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, SSyncAddedConduits.class, SSyncAddedConduits::encode, SSyncAddedConduits::decode, SSyncAddedConduits::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SSyncRemovedConduits.class, SSyncRemovedConduits::encode, SSyncRemovedConduits::decode, SSyncRemovedConduits::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SUpdateConduitEntity.class, SUpdateConduitEntity::encode, SUpdateConduitEntity::decode, SUpdateConduitEntity::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, CChangeConduitPlacementLengthPackage.class, CChangeConduitPlacementLengthPackage::encode, CChangeConduitPlacementLengthPackage::decode, CChangeConduitPlacementLengthPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, CScrollInputPackage.class, CScrollInputPackage::encode, CScrollInputPackage::decode, CScrollInputPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, CUpdateJunctionLanesPackage.class, CUpdateJunctionLanesPackage::encode, CUpdateJunctionLanesPackage::decode, CUpdateJunctionLanesPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, SSyncElectricComponentsPackage.class, SSyncElectricComponentsPackage::encode, SSyncElectricComponentsPackage::decode, SSyncElectricComponentsPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SUpdateElectricNetworkPackage.class, SUpdateElectricNetworkPackage::encode, SUpdateElectricNetworkPackage::decode, SUpdateElectricNetworkPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SSyncMagneticPackage.class, SSyncMagneticPackage::encode, SSyncMagneticPackage::decode, SSyncMagneticPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SMagneticInfluencePackage.SAddInfluencePackage.class, SMagneticInfluencePackage.SAddInfluencePackage::encode, SMagneticInfluencePackage.SAddInfluencePackage::decode, SMagneticInfluencePackage.SAddInfluencePackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SMagneticInfluencePackage.SRemoveInfluencePackage.class, SMagneticInfluencePackage.SRemoveInfluencePackage::encode, SMagneticInfluencePackage.SRemoveInfluencePackage::decode, SMagneticInfluencePackage.SRemoveInfluencePackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SUpdateMagneticFieldPackage.class, SUpdateMagneticFieldPackage::encode, SUpdateMagneticFieldPackage::decode, SUpdateMagneticFieldPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SSyncParametricsPackage.class, SSyncParametricsPackage::encode, SSyncParametricsPackage::decode, SSyncParametricsPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, CSwitchNetworkStatePackage.class, CSwitchNetworkStatePackage::encode, CSwitchNetworkStatePackage::decode, CSwitchNetworkStatePackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, SSyncVS2BlockInfoPackage.class, SSyncVS2BlockInfoPackage::encode, SSyncVS2BlockInfoPackage::decode, SSyncVS2BlockInfoPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SSyncKineticComponentsPackage.class, SSyncKineticComponentsPackage::encode, SSyncKineticComponentsPackage::decode, SSyncKineticComponentsPackage::handle);
		network.registerMessage(id++, SUpdateKineticNetworkPackage.class, SUpdateKineticNetworkPackage::encode, SUpdateKineticNetworkPackage::decode, SUpdateKineticNetworkPackage::handle);
	}
	
}
