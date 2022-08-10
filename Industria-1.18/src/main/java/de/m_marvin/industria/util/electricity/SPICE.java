package de.m_marvin.industria.util.electricity;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import de.m_marvin.industria.Industria;
import de.m_marvin.nglink.NativeExtractor;
import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValue;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;

public class SPICE {
	
	private static NativeNGLink nglink;
	private static HashMap<String, Double> ngVecData;
	
	static {
		if (!NativeNGLink.loadedSuccessfully()) throw new IllegalStateException("Failed to load natives for nglink in SPICE class!");
		nglink = new NativeNGLink();
		ngVecData = new HashMap<String, Double>();
	}
	
	public static Map<String, Double> vectorData() {
		return ngVecData;
	}
	
	public static void processCircuit(ElectricNetwork circuit) {

		if (!nglink.isInitialized()) {
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
					Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "JNGLINK: " + s);
				}
				
				@Override
				public void detacheNGSpice() {
					Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "JNGLINK: Detaching spice!");
					nglink.detachNGSpice();
				}
			});
		}

		try {
			
			System.out.println(circuit.toString());
			
			ngVecData.clear();
			nglink.initNGSpice(NativeExtractor.findNative("ngspice"));
			nglink.loadCircuit(circuit.toString());
			nglink.execCommand("op");
			nglink.detachNGSpice();
			
		} catch (FileNotFoundException e) {
			Industria.LOGGER.log(org.apache.logging.log4j.Level.ERROR, "Failed to find ngspice native!");
			e.printStackTrace();
		}
		
	}
	
}
