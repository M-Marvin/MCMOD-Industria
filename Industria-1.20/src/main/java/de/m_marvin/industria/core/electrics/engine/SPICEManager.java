package de.m_marvin.industria.core.electrics.engine;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;
import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValue;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;
import net.minecraft.client.telemetry.events.WorldUnloadEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class SPICEManager {
	
	private final static List<SPICEEngine> instances = new ArrayList<>();
	
	public static class SPICEEngine {
		
		protected final NativeNGLink nglink = new NativeNGLink();
		
		protected SPICEEngine() {}
		
		public void terminate() {
			
		}
		
		public void startup() {
			
		}
		
		public boolean isRunning() {
			return false;
		}
		
	}
	
	public static SPICEEngine getNewEngine() {
		SPICEEngine engine = new SPICEEngine();
		instances.add(engine);
		return engine;
	}
	
	public static void discardEngine(SPICEEngine engine) {
		if (engine.isRunning()) engine.terminate();
		instances.remove(engine);
	}
	
	public static void terminateEngines() {
		for (SPICEEngine engine : instances) {
			engine.terminate();
		}
		instances.clear();
	}
	
}
