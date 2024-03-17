package de.m_marvin.spicedebug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.INGCallback;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValue;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;

public class SpiceDebugger2 implements INGCallback {
	
	public static void main(String[] args) {
		
		File wrkspcDir = new File("").getAbsoluteFile();
		File netlistFile = new File(wrkspcDir, "run/netlists/ingame-level-circuit_03.txt");
		
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
			instance.runNetlist(netlist);
			instance.printMap();
			
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
		public int parseEnd;
		public Map<String, Map<String, String>> subCircuits;
		public Map<String, String> node2lane;
	}
	
	public SpiceDebugger2() {
		this.nglink = new NativeNGLink();
		if (!this.nglink.initNGLink(this)) {
			System.err.println("Failed to init NGLink!");
			return;
		}
	}
	
	protected NativeNGLink nglink;
	protected List<Component> components = new ArrayList<>();
	protected Map<String, Double> node2voltage = new HashMap<>();
	
	protected static final Pattern COMPONENT_PATTERN = Pattern.compile("\\* Component (?<type>.{1,})\\{(?<regid>.{1,})\\} (?<position>.{1,})");
	protected static final Pattern CIRCUIT_PATTERN = Pattern.compile("\\* (?<type>[A-Za-z\\- ]{1,}) \\[(?<props>[a-z_ =0-9\\.,]*)\\]");
	protected static final Pattern PROPERTY_PATTERN = Pattern.compile(" {0,1}(?<key>[a-z_]+)=(?<value>[a-z0-9\\.\\-]+)0{0,1},{0,1}");
	protected static final Pattern NODE_PATTERN = Pattern.compile("(node\\|[A-Za-z0-9\\-_]{1,}\\|)");
	protected static final Pattern LANE_PATTERN = Pattern.compile("node\\|[A-Za-z0-9\\-_]{1,}_lid(?<lid>[0-9]{1,})_lnm(?<lnm>[A-Za-z0-9\\-_]{1,})\\|");
	
	public void mapNetlist(String netlist) {
		
		Matcher componentHeaderMatcher = COMPONENT_PATTERN.matcher(netlist);
		
		while (componentHeaderMatcher.find()) {
			
			Component component = new Component();
			component.type = componentHeaderMatcher.group("type");
			component.regid = componentHeaderMatcher.group("regid");
			component.position = componentHeaderMatcher.group("position");
			component.parseStart = componentHeaderMatcher.start();
			component.parseEnd = netlist.length();
			
			System.out.println("Identified component: " + component.type + " of type " + component.regid + " at " + component.position);
			System.out.println("Parsing region: " + component.parseStart);
			
			if (!components.isEmpty()) {
				components.get(components.size() - 1).parseEnd = component.parseStart;
			}
			
			components.add(component);
			
		}
		
		components.stream()
			.forEach(component -> {
				
				String compnet = netlist.substring(component.parseStart, component.parseEnd);
				
				List<String> nodes = NODE_PATTERN.matcher(compnet).results().map(result -> result.group(0)).toList();
				System.out.println("Node identified: " + nodes.size());
				
				component.node2lane = new HashMap<>();
				nodes.forEach(node -> {
					Matcher laneMatch = LANE_PATTERN.matcher(node);
					if (laneMatch.find()) {
						String lane = laneMatch.group("lid") + "_" + laneMatch.group("lnm");
						component.node2lane.put(node, lane);
					} else {
						System.err.println("Invalid nod: " + node);
					}
				});
				
				component.subCircuits = new HashMap<>();
				Matcher subCircuitMatcher = CIRCUIT_PATTERN.matcher(compnet);
				int id = 0;
				while (subCircuitMatcher.find()) {
					String typeName = subCircuitMatcher.group("type");
					String propString = subCircuitMatcher.group("props");
					Map<String, String> properties = new HashMap<>();
					Matcher propMatcher = PROPERTY_PATTERN.matcher(propString);
					while (propMatcher.find()) {
						properties.put(propMatcher.group("key"), propMatcher.group("value"));
					}
					component.subCircuits.put(id++ + "_" + typeName, properties);
				}
				
			});
		
	}
	
	public void printMap() {

		System.out.println("\n=== Netlist Map ===");
		
		components.stream()
			.sorted((a, b) -> a.type.compareTo(b.type))
			.forEach(component -> {

				System.out.println(component.type + "\t\t" + component.position + "\t\t" + component.regid);
				
				component.subCircuits.forEach((name, properties) -> {
					System.out.println("\tCircuit: " + name);
					properties.forEach((key, value) -> {
						System.out.println("\t\t" + key + "\t" + value);
					});
				});
				
				component.node2lane.entrySet().stream()
					.sorted((a, b) -> a.getValue().compareTo(b.getValue()))
					.forEach(entry -> {
						double voltage = this.node2voltage.getOrDefault(entry.getKey(), 0.0);
						System.out.println("\t" + entry.getValue() + "\t" + entry.getKey() + "\t" + String.format("%.03fV", voltage));
					});
				
			});
		
		System.out.println("\n");
		
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
		System.out.println("Received data: " + vectorCount + " items");
		
		for (VectorValue value : vecData.values()) {
			this.node2voltage.put(value.name(), value.realdata());
		}
	}

	@Override
	public void reciveInitData(PlotDescription plotInfo) {}
	
}