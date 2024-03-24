package de.m_marvin.spicedebug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.m_marvin.commandlineparser.CommandLineParser;
import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.INGCallback;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValue;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;

public class SpiceDebugger implements INGCallback {
	
	public static void main(String[] args) {
		
		CommandLineParser parser = new CommandLineParser();
		parser.addOption("net", "", "Specifies the netlist to execute");
		parser.addOption("cmd", "op", "The SPICE command to execute");
		parser.addOption("help", false, "Prints this help info");
		
		parser.parseInput(args);
		
		if (parser.getFlag("help") || args.length == 0) {
			System.out.println(parser.printHelp());
			return;
		}
		
		File netlistFile = new File(parser.getOption("net")).getAbsoluteFile();
		String spiceCmd = parser.getOption("cmd");

		System.out.println("netlist: " + netlistFile.getPath());
		System.out.println("command: " + spiceCmd);
		
		runSolver(netlistFile, spiceCmd);
		
	}
	
	public static void runSolver(File netlistFile, String cmd) {
		
		if (!netlistFile.isFile()) {
			System.err.println("netlist file not found: " + netlistFile);
			return;
		}
		
		try {
			
			InputStream netlistStream = new FileInputStream(netlistFile);
			String netlist = new String(netlistStream.readAllBytes());
			netlistStream.close();
			
			SpiceDebugger instance = new SpiceDebugger();
			instance.mapNetlist(netlist);
			instance.runNetlist(netlist, cmd);
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
	
	public SpiceDebugger() {
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
	protected static final Pattern LANE_PATTERN = Pattern.compile("node\\|[A-Za-z0-9\\-_]{1,}_lid(?<lid>[0-9]{1,})_lnm(?<lnm>[A-Za-z0-9\\-_]{0,})\\|");
	
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
				
				component.node2lane = new HashMap<>();
				nodes.forEach(node -> {
					Matcher laneMatch = LANE_PATTERN.matcher(node);
					if (laneMatch.find()) {
						String lane = laneMatch.group("lid") + "_" + laneMatch.group("lnm");
						component.node2lane.put(node, lane);
					} else {
						System.err.println("Invalid node: " + node);
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

	private static final Pattern FILTER_NODE_PATTERN = Pattern.compile("(?:N[0-9_]{5,})|(?:node\\|[A-Za-z0-9_\\-]{1,}\\|)");
	private static final Pattern FILTER_GROUND_PATTERN = Pattern.compile("R0GND (node\\|[A-Za-z0-9_\\-]{1,}\\|) 0 1");
	
	private String filterSingularMatrixNodes(String netlist) {
		
		Optional<String> groundNode = netlist.lines().map(line -> {
			Matcher nodeMatcher = FILTER_GROUND_PATTERN.matcher(line);
			return nodeMatcher.find() ? nodeMatcher.group(1) : null;
		}).filter(s -> s != null).findAny();
		
		if (groundNode.isEmpty()) return null;
		
		List<List<String>> lineNodes = netlist.lines().map(line -> {
			Matcher nodeMatcher = FILTER_NODE_PATTERN.matcher(line);
			return nodeMatcher.results().map(MatchResult::group).toList();
		}).toList();
		
		List<String> connectedNodes = new ArrayList<>();
		findConnected(connectedNodes, groundNode.get(), lineNodes);

		StringBuilder filterList = new StringBuilder();
		List<String> lines = netlist.lines().toList();
		for (int i = 0; i < lineNodes.size(); i++) {
			boolean isSingular = lineNodes.get(i).size() > 0 && lineNodes.get(i).stream().filter(node -> connectedNodes.contains(node)).count() == 0;
			if (isSingular) continue;
			filterList.append(lines.get(i) + "\n");
		}
		
		return filterList.toString();
		
	}
	
	private void findConnected(List<String> connectedList, String current, List<List<String>> nodeGroups) {
		Set<String> foundNodes = new HashSet<>();
		nodeGroups.stream().filter(group -> group.contains(current)).forEach(group -> group.stream().filter(node -> !connectedList.contains(node)).forEach(foundNodes::add));
		if (!foundNodes.isEmpty()) {
			connectedList.addAll(foundNodes);
			for (String node : foundNodes) {
				findConnected(connectedList, node, nodeGroups);
			}
		}
	}
	
	public void runNetlist(String netlist, String cmd) {
		
		netlist = filterSingularMatrixNodes(netlist);
		
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
		
		if (!this.nglink.execCommand(cmd)) {
			System.err.println("Failed to execute '" + cmd + "' command!");
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
