package de.m_marvin.industria.core.magnetism;

import java.util.Optional;

import de.m_marvin.industria.core.magnetism.engine.network.SMagneticInfluencePackage;
import de.m_marvin.industria.core.magnetism.engine.network.SSyncMagneticPackage;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworkPackages {
	
	public static final String PROTOCOL_VERSION = "1";
	
	public static void setupPackages(SimpleChannel network) {
		int id = 0;
		network.registerMessage(id++, SSyncMagneticPackage.class, SSyncMagneticPackage::encode, SSyncMagneticPackage::decode, SSyncMagneticPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SMagneticInfluencePackage.SAddInfluencePackage.class, SMagneticInfluencePackage.SAddInfluencePackage::encode, SMagneticInfluencePackage.SAddInfluencePackage::decode, SMagneticInfluencePackage.SAddInfluencePackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SMagneticInfluencePackage.SRemoveInfluencePackage.class, SMagneticInfluencePackage.SRemoveInfluencePackage::encode, SMagneticInfluencePackage.SRemoveInfluencePackage::decode, SMagneticInfluencePackage.SRemoveInfluencePackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
	
}
