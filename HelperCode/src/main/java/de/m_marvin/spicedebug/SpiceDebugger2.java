package de.m_marvin.spicedebug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.INGCallback;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;

public class SpiceDebugger2 implements INGCallback {
	
	public static void main(String[] args) {
		
		File wrkspcDir = new File("").getAbsoluteFile();
		File netlistFile = new File(wrkspcDir, "run/netlists/ingame-level-circuit_01.txt");
		
		System.out.println("Debug Netlist: " + netlistFile);
		
		runSolver(netlistFile);
		
	}
	
	public static void runSolver(File netlistFile) {
		
		if (!netlistFile.isFile()) {
			System.err.println("Netlist file not found: " + netlistFile);
		}
		
		try {
			
			InputStream netlistStream = new FileInputStream(netlistFile);
			String netlist = new String(netlistStream.readAllBytes());
			netlistStream.close();
			
			SpiceDebugger2 instance = new SpiceDebugger2();
			instance.mapNetlist(netlist);
			//instance.runNetlist(netlist);
			
		} catch (IOException e) {
			System.err.println("Failed to read netlist file!");
			e.printStackTrace();
		}
		
	}
	
	
	protected static class Component {
		public String type;
		public String regid;
		public String position;
		public int parseStart;
	}
	
	public SpiceDebugger2() {
		this.nglink = new NativeNGLink();
		if (!this.nglink.initNGLink(this)) {
			System.err.println("Failed to init NGLink!");
			return;
		}
	}
	
	protected NativeNGLink nglink;
	
	
	Pattern COMPONENT_PATTERN = Pattern.compile("\\* Component (?<type>.{1,})\\{(?<regid>.{1,})\\} (?<position>.{1,})");
	public void mapNetlist(String netlist) {
		
		Matcher componentHeaderMatcher = COMPONENT_PATTERN.matcher(netlist);
		
		List<Component> components = new ArrayList<>();
		while (componentHeaderMatcher.find()) {
			
			Component component = new Component();
			component.type = componentHeaderMatcher.group("type");
			component.regid = componentHeaderMatcher.group("regid");
			component.position = componentHeaderMatcher.group("position");
			component.parseStart = componentHeaderMatcher.start();
			
			System.out.println("Identified component: " + component.type + " of type " + component.regid + " at " + component.position);
			System.out.println("Parsing region: " + component.parseStart);
			
			components.add(component);
			
		}
		
		
	}
	
	public void runNetlist(String netlist) {
		if (!this.nglink.isNGSpiceAttached()) {
			if (!this.nglink.initNGSpice()) {
				System.err.println("Failed to init ngspice!");
				return;
			}
			System.out.println("SPICE initialized");
		}
		
		if (!this.nglink.loadCircuit(netlist)) {
			System.err.println("Failed to load netlist!");
			return;
		}
		
		if (!this.nglink.execCommand("op")) {
			System.err.println("Failed to execute op command!");
			return;
		}
		
		this.nglink.execCommand("quit");
	}
	
	@Override
	public void log(String s) {
		System.out.println("[SPICE] " + s);
	}

	@Override
	public void detacheNGSpice() {
		System.out.println("Spice terminated");
	}
	
	@Override
	public void reciveVecData(VectorValuesAll vecData, int vectorCount) {
		System.out.println("Received data: " + vectorCount);
		// TODO
	}

	@Override
	public void reciveInitData(PlotDescription plotInfo) {}
	
}
