package de.industria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod("industria")
public class Industria {
	
	// TODO Sounds:
	// - Heater
	// - BlastFurance
	// - MetalFormer
	// RC Drops
	// Burned Encased Cable
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "industria";
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(MODID, "main"), 
			() -> Industria.PROTOCOL_VERSION, 
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);
	
	public static CheckResult MOD_STATUS = null;
	
	public Industria() {
		
		// TODO
//		Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(MODID);
//		if (modContainer.isPresent()) {
//			MOD_STATUS = VersionChecker.getResult(modContainer.get().getModInfo());
//			
//			if (MOD_STATUS.status.equals(Status.PENDING)) {
//				try {
//					Thread.sleep(10000);
//					MOD_STATUS = VersionChecker.getResult(modContainer.get().getModInfo());
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		}
//		
//		LOGGER.log(Level.INFO, "#####################################################################################");
//		LOGGER.log(Level.INFO, MOD_STATUS.status);
		
		ServerSetup.constructMod();
		
	}
	
}
