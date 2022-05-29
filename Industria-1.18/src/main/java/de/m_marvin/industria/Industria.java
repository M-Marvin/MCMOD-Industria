package de.m_marvin.industria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.m_marvin.industria.util.Events;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("industria")
public class Industria {
	
	public static final String MODID = "industria";
	public static final Logger LOGGER = LogManager.getLogger();
	
	public Industria() {
		
		//inecraftForge.EVENT_BUS.register(new Events());
				
	}
	
}
