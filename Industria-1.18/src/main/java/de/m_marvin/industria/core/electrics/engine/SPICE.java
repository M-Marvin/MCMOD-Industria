package de.m_marvin.industria.core.electrics.engine;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.Config;
import de.m_marvin.nglink.NativeExtractor;
import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValue;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class SPICE {
	
	private static boolean callbackInitialized;
	private static NativeNGLink nglink;
	private static HashMap<String, Double> ngVecData;
	
	static {
		if (!NativeNGLink.loadedSuccessfully()) {
			throw new IllegalStateException("Failed to load natives for nglink in SPICE class!");
		}
		nglink = new NativeNGLink();
		callbackInitialized = false;
		ngVecData = new HashMap<String, Double>();
	}
	
	@SubscribeEvent
	public static void onWorldUnloadEvent(WorldEvent.Unload event) {
		resetNativeLib();
	}
	
	public static Map<String, Double> vectorData() {
		return ngVecData;
	}
	
	public static void resetNativeLib() {
		callbackInitialized = false;
	}
	
	public static void makeReady() throws FileNotFoundException {

		if (!nglink.isInitialized() || !callbackInitialized) {
			nglink.initNGLink(new NativeNGLink.NGCallback() {
				@Override
				public void reciveVecData(VectorValuesAll vecData, int vectorCount) {
					for (VectorValue value : vecData.values()) {
						ngVecData.put(value.name(), value.realdata());
					}
				}
				
				@Override
				public void reciveInitData(PlotDescription plotInfo) {}
				
				@Override
				public void log(String s) {
					if (Config.SPICE_DEBUG_LOGGING.get()) Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "JNGLINK: " + s);
				}
				
				@Override
				public void detacheNGSpice() {
					if (Config.SPICE_DEBUG_LOGGING.get()) Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "JNGLINK: Detaching spice!");
					nglink.detachNGSpice();
				}
			});
			callbackInitialized = true;
			
		}
		
	}
	
	public static boolean initSpice() {
		try {
			nglink.initNGSpice(NativeExtractor.findNative("ngspice"));
			return true;
		} catch (FileNotFoundException e) {
			Industria.LOGGER.log(org.apache.logging.log4j.Level.FATAL, "Failed to find ngspice native!");
			e.printStackTrace();
			return false;
		}
	}
	
	public static void loadCircuit(String circuit) {
		nglink.loadCircuit(circuit);
	}
	
	public static void detacheSpice() {
		nglink.detachNGSpice();
	}
	
//	public static void processCircuit(ElectricNetwork circuit) {
//		
//		if (circuit.isPlotEmpty()) {
//			
//			if (Config.SPICE_DEBUG_LOGGING.get()) Industria.LOGGER.log(org.apache.logging.log4j.Level.WARN, "JNGLINK: Detected spice-call with empty circuit, aborted to prevent crash!");
//			return;
//			
//		}
//		
//		//makeReady();
//		
//		try {
//			
//			if (Config.SPICE_DEBUG_LOGGING.get()) Industria.LOGGER.log(Level.DEBUG, "SPICE circuit result:\n" + circuit.toString() + "\nStarting simmulation ...");
//			
//			ngVecData.clear();
//			nglink.initNGSpice(NativeExtractor.findNative("ngspice"));
//			nglink.loadCircuit(circuit.toString());
//			nglink.execCommand("op");
//			nglink.detachNGSpice();
//			
//			if (Config.SPICE_DEBUG_LOGGING.get())Industria.LOGGER.log(Level.DEBUG, "Done");
//			
//		} catch (FileNotFoundException e) {
//			Industria.LOGGER.log(org.apache.logging.log4j.Level.FATAL, "Failed to find ngspice native!");
//			e.printStackTrace();
//		}
//		
//	}
	
}
