package de.industria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.industria.util.UpdateChecker;
import net.minecraft.util.ResourceLocation;
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
	
	protected UpdateChecker updateChecker;
	
	public Industria() {
		
		this.updateChecker = new UpdateChecker();
		
		ServerSetup.constructMod();
		
	}
	
	public UpdateChecker getUpdateChecker() {
		return updateChecker;
	}
	
}
