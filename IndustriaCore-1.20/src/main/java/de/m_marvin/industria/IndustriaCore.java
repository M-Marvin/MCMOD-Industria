package de.m_marvin.industria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.m_marvin.industria.core.Config;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.registries.CommandArguments;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.registries.Items;
import de.m_marvin.industria.core.registries.MenuTypes;
import de.m_marvin.industria.core.registries.NetworkPackages;
import de.m_marvin.industria.core.registries.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod("industriacore")
public class IndustriaCore {
	
	public static final String MODID = "industriacore";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "main"), () -> NetworkPackages.PROTOCOL_VERSION, NetworkPackages.PROTOCOL_VERSION::equals, NetworkPackages.PROTOCOL_VERSION::equals);

	public static final ResourceLocation UTILITY_WIDGETS_TEXTURE = new ResourceLocation(IndustriaCore.MODID, "textures/gui/utility_widgets.png");
	
	public IndustriaCore() {
		
		// FIXME
//		LOGGER.info("Set UniVec obfuscation resolver");
//		VectorParser.setObfuscationResolver((clazz, field) -> {
//			LOGGER.warn("Try to unobfuscate " + field);
//			String s = ObfuscationReflectionHelper.remapName(Domain.FIELD, field);
//			LOGGER.warn("Result: " + s);
//			return Optional.of(s);
//		});
		
		Config.register();
		NetworkPackages.setupPackages(NETWORK);
		Conduits.register();
		MenuTypes.register();
		ParticleTypes.register();
		Blocks.register();
		Items.register();
		BlockEntityTypes.register();
		CommandArguments.register();
		
	}
	
}
