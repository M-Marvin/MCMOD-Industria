package de.m_marvin.industria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.m_marvin.industria.content.registries.ModBlockEntities;
import de.m_marvin.industria.content.registries.ModBlocks;
import de.m_marvin.industria.content.registries.ModItems;
import de.m_marvin.industria.content.registries.ModParticleTypes;
import de.m_marvin.industria.core.Config;
import de.m_marvin.industria.core.NetworkPackages;
import de.m_marvin.industria.core.conduits.registy.Conduits;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod("industria")
public class Industria {
	
	public static final String MODID = "industria";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "main"), () -> NetworkPackages.PROTOCOL_VERSION, NetworkPackages.PROTOCOL_VERSION::equals, NetworkPackages.PROTOCOL_VERSION::equals);
	
	public Industria() {
				
		Config.register();
		NetworkPackages.setupPackages(NETWORK);
		ModBlocks.register();
		ModItems.register();
		ModBlockEntities.register();
		Conduits.register();
		ModParticleTypes.register();
		
	}
	
}
