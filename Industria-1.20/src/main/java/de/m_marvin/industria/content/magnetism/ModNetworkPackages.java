package de.m_marvin.industria.content.magnetism;

import java.util.Optional;

import de.m_marvin.industria.content.magnetism.engine.network.SMagneticInfluencePackage;
import de.m_marvin.industria.content.magnetism.engine.network.SSyncMagneticPackage;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworkPackages {
	
	public static final String PROTOCOL_VERSION = "1";
	
	public static void setupPackages(SimpleChannel network) {
		int id = 0;
		network.registerMessage(id++, SSyncMagneticPackage.class, SSyncMagneticPackage::encode, SSyncMagneticPackage::decode, SSyncMagneticPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SMagneticInfluencePackage.SCAddInfluencePackage.class, SMagneticInfluencePackage.SCAddInfluencePackage::encode, SMagneticInfluencePackage.SCAddInfluencePackage::decode, SMagneticInfluencePackage.SCAddInfluencePackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SMagneticInfluencePackage.SCRemoveInfluencePackage.class, SMagneticInfluencePackage.SCRemoveInfluencePackage::encode, SMagneticInfluencePackage.SCRemoveInfluencePackage::decode, SMagneticInfluencePackage.SCRemoveInfluencePackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
	
}
