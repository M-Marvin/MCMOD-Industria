package de.m_marvin.industria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.m_marvin.industria.content.registries.ModBlockEntities;
import de.m_marvin.industria.content.registries.ModBlocks;
import de.m_marvin.industria.content.registries.ModConduits;
import de.m_marvin.industria.content.registries.ModItems;
import de.m_marvin.industria.core.Config;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.registries.Container;
import de.m_marvin.industria.core.registries.Items;
import de.m_marvin.industria.core.registries.NetworkPackages;
import de.m_marvin.industria.core.registries.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod("industria")
public class IndustriaCore {
	
	public static final String MODID = "industria";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "main"), () -> NetworkPackages.PROTOCOL_VERSION, NetworkPackages.PROTOCOL_VERSION::equals, NetworkPackages.PROTOCOL_VERSION::equals);
	
	public IndustriaCore() {
		
		Config.register();
		NetworkPackages.setupPackages(NETWORK);
		Conduits.register();
		Container.register();
		ParticleTypes.register();
		Blocks.register();
		Items.register();
		
		registerTestingContent();
		
	}
	
	public static void registerTestingContent() {
		
		ModItems.register();
		ModBlocks.register();
		ModBlockEntities.register();
		ModConduits.register();
		
	}
	
}
