package de.m_marvin.industria.content;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.content.registries.ModBlocks;
import de.m_marvin.industria.content.registries.ModConduits;
import de.m_marvin.industria.content.registries.ModItems;
import de.m_marvin.industria.content.registries.ModMenuTypes;
import de.m_marvin.industria.content.registries.ModRecipeTypes;
import net.minecraftforge.fml.common.Mod;

@Mod("industria")
public class Industria {

	public static final String MODID = "industria";
	public static final Logger LOGGER = LogManager.getLogger();
	//public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "main"), () -> NetworkPackages.PROTOCOL_VERSION, NetworkPackages.PROTOCOL_VERSION::equals, NetworkPackages.PROTOCOL_VERSION::equals);
	
	public Industria() {
		
		//ModConfig.register();
		//ModNetworkPackages.setupPackages(NETWORK);
		ModBlocks.register();
		ModItems.register();
		ModConduits.register();
		ModMenuTypes.register();
		//ModParticleTypes.register();
		ModBlockEntityTypes.register();
		//ModCommandArguments.register();
		ModRecipeTypes.register();
		
	}
	
}
