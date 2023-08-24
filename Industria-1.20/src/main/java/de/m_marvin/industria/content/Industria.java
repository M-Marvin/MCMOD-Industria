package de.m_marvin.industria.content;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;

@Mod("industria")
public class Industria {

	public static final String MODID = "industria";
	public static final Logger LOGGER = LogManager.getLogger();
	//public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "main"), () -> NetworkPackages.PROTOCOL_VERSION, NetworkPackages.PROTOCOL_VERSION::equals, NetworkPackages.PROTOCOL_VERSION::equals);
	
	public Industria() {
		
		//ModConfig.register();
		//ModNetworkPackages.setupPackages(NETWORK);
//		ModConduits.register();
		//ModContainer.register();
		//ModParticleTypes.register();
//		ModBlocks.register();
//		ModItems.register();
//		ModBlockEntityTypes.register();
		//ModCommandArguments.register();
		
	}
	
}
