package de.m_marvin.industria;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.m_marvin.archiveutility.ArchiveAccess;
import de.m_marvin.archiveutility.IArchiveAccess;
import de.m_marvin.industria.core.Config;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.registries.CommandArguments;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.registries.Items;
import de.m_marvin.industria.core.registries.MenuTypes;
import de.m_marvin.industria.core.registries.NetworkPackages;
import de.m_marvin.industria.core.registries.ParticleTypes;
import de.m_marvin.univec.VectorParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod("industriacore")
public class IndustriaCore {
	
	public static final String MODID = "industriacore";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "main"), () -> NetworkPackages.PROTOCOL_VERSION, NetworkPackages.PROTOCOL_VERSION::equals, NetworkPackages.PROTOCOL_VERSION::equals);
	
	public static IArchiveAccess ARCHIVE_ACCESS = null;
	
	// Initialize mod-jar access for extracting additional files (like SPICE init files)
	static {
		String codeSourcePath = IndustriaCore.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		Pattern filePathPattern = Pattern.compile("\\/([^\"*?<>|]*)[%#][0-9]{1,}!\\/");
		Matcher m = filePathPattern.matcher(codeSourcePath);
		if (!m.find()) throw new RuntimeException("Could not resolve mod file location: " + codeSourcePath);
		String codeSourceLoc = m.group(1);
		if (!codeSourceLoc.endsWith(".jar")) {
			// FIXME [Forge] running in ide gets not set from forge for some reason ?
			//SharedConstants.IS_RUNNING_IN_IDE = true;
		}
		try {
			ARCHIVE_ACCESS = ArchiveAccess.getArchiveAccess(new File(codeSourceLoc));
		} catch (IOException e) {
			IndustriaCore.LOGGER.fatal("!!!FAILED TO GET ARCHIVE ACCESS TO MOD JAR!!!");
			e.printStackTrace();
		}
	}
	
	public IndustriaCore() {
				
		/* Begin of UniVec deobfuscation configuration */
		
		Map<String, String> obfuscationMappings = new HashMap<>();
		obfuscationMappings.put("f_123285_", "x");
		obfuscationMappings.put("f_123286_", "y");
		obfuscationMappings.put("f_123289_", "z");
		obfuscationMappings.put("f_82479_", "x");
		obfuscationMappings.put("f_82480_", "y");
		obfuscationMappings.put("f_82481_", "z");
		
		LOGGER.info("Set UniVec obfuscation resolver");
		VectorParser.setObfuscationResolver((clazz, field) -> Optional.ofNullable(obfuscationMappings.getOrDefault(field, field)));

		/* End of UniVec deobfuscation configuration */
		
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
