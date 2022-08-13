package de.m_marvin.industria.util.electricity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.electricity.ElectricNetworkHandlerCapability.Component;

public class ElectricNetwork {
	
	public static final String GND_NODE = "0";

	@FunctionalInterface
	public static interface NetListBuilder {
		public String build(ElectricNetwork net, String id, String... param);
	}
	
	// param: 0 node+ 1 node- 2 current 3 voltage
	public static final NetListBuilder SOURCE_BUILDER = (net, id, param) -> {
		String md = net.addModel("sw vt=0 vh=0.001 ron=0.001 roff=10e9");
		return    "V1"+id+" N1"+id+" N2"+id+" "+param[3]+"\n"
				+ "I1"+id+" N3"+id+" N2"+id+" "+param[2]+"\n"
				+ "S1"+id+" N2"+id+" N3"+id+" N2"+id+" N3"+id+" "+md+"\n"
				+ "I2"+id+" N3"+id+" "+param[1]+" "+param[2]+"\n"
				+ "S3"+id+" "+param[1]+" N3"+id+" "+param[1]+" N3"+id+" "+md+"\n"
				+ "S4"+id+" N1"+id+" "+param[0]+" N1"+id+" "+param[0]+" "+md+"\n";
	};
	
	// param: 0 nodeA 1 nodeB 2 resistance
	public static final NetListBuilder RESISTANCE_BUILDER = (net, id, param) -> {
		return    "R1"+id+" "+param[0]+" "+param[1]+" "+param[2]+"\n";
	};
	
	// param 0 node+ 1 node- 2 current
	public static final NetListBuilder LOAD_BUILDER = (net, id, param) -> {
		return    "I1"+id+" "+param[0]+" "+param[1]+" "+param[2]+"\n";
	};
	
	// Stored results
	protected Map<Long, Double> serialResistance;
	protected Map<ConnectionPoint, Double> paralelResistance;
	protected Map<ConnectionPoint, Double> nodeVoltages;
	protected Set<Component<?, ?, ?>> components;
	
	// Cache for circuit building
	protected String title;
	protected Set<Set<String>> connectedNodes;
	protected Map<String, Set<ConnectionPoint>> hash2node;
	protected StringBuilder stringBuilder;
	protected String netList;
	protected Set<String> modelMap;
	protected int resistorCount = 1;
	protected int loadCount = 1;
	protected int sourceCount = 1;
	
	public ElectricNetwork(String titleInfo) {
		this.title = titleInfo;
		this.stringBuilder = new StringBuilder();
		this.stringBuilder.append(titleInfo + "\n");
		this.hash2node = new HashMap<String, Set<ConnectionPoint>>();
		this.nodeVoltages = new HashMap<ConnectionPoint, Double>();
		this.paralelResistance = new HashMap<ConnectionPoint, Double>();
		this.serialResistance = new HashMap<Long, Double>();
		this.components = new HashSet<Component<?, ?, ?>>();
		this.modelMap = new HashSet<String>();
		this.connectedNodes = new HashSet<Set<String>>();
	}
	
	public Set<Component<?, ?, ?>> getComponents() {
		return components;
	}
	
	public Map<String, Set<ConnectionPoint>> getNodes() {
		return this.hash2node;
	}
	
	protected String getNodeName(ConnectionPoint node) {
		String hashName = "node-" + Integer.toHexString(node.hashCode());
		Set<ConnectionPoint> nodes = this.hash2node.getOrDefault(hashName, new HashSet<ConnectionPoint>());
		nodes.add(node);
		this.hash2node.put(hashName, nodes);
		return hashName;
	}
	
	public Set<ConnectionPoint> getNodes(String hashName) {
		return this.hash2node.get(hashName);
	}
	
	protected String addModel(String model) {
		String modelName = "M" + Integer.toHexString(model.hashCode());
		if (this.modelMap.add(model)) stringBuilder.append(".model " + modelName + " " + model + "\n");
		return modelName;
	}
	
	protected void combineNodes(String nodeA, String nodeB) {
		Optional<Set<String>> nodeSetA = connectedNodes.stream().filter((nodeSet) -> nodeSet.contains(nodeA)).findAny();
		Optional<Set<String>> nodeSetB = connectedNodes.stream().filter((nodeSet) -> nodeSet.contains(nodeB)).findAny();
		if (nodeSetA.equals(nodeSetB)) {
			if (nodeSetA.isEmpty()) {
				Set<String> nodeSet = new HashSet<String>();
				nodeSet.add(nodeA);
				nodeSet.add(nodeB);
				connectedNodes.add(nodeSet);
			}
		} else if (nodeSetA.isEmpty()) {
			nodeSetB.get().add(nodeA);
		} else if (nodeSetB.isEmpty()) {
			nodeSetA.get().add(nodeB);
		} else {
			nodeSetB.get().stream().forEach(nodeSetA.get()::add);
			connectedNodes.remove(nodeSetB.get());
		}
	}
	
	protected void addLoad(String nodeA, String nodeB, double current) {
		stringBuilder.append(LOAD_BUILDER.build(this, loadCount++ + "-l", nodeA, nodeB, String.valueOf(current)));
	}
	
	protected void addResistor(String nodeA, String nodeB, double resistance) {
		if (resistance == 0) {
			combineNodes(nodeA, nodeB);
		} else {
			stringBuilder.append(RESISTANCE_BUILDER.build(this, resistorCount++ + "-r", nodeA, nodeB, String.valueOf(resistance)));
		}
	}
	
	protected void addSource(String nodeP, String nodeN, double voltage, double maxCurrent) {
		stringBuilder.append(SOURCE_BUILDER.build(this, sourceCount++ + "-s", nodeP, nodeN, String.valueOf(maxCurrent), String.valueOf(voltage)));
	}
	
	public void addSerialResistance(ConnectionPoint nodeA, ConnectionPoint nodeB, double resistance) {
		this.serialResistance.put((long) (nodeA.hashCode() + nodeB.hashCode()), resistance);
		addResistor(getNodeName(nodeA), getNodeName(nodeB), resistance);
	}
	
	public void addParalelResistance(ConnectionPoint node, double resistance) {
		this.paralelResistance.put(node, 1 / (1 / this.paralelResistance.getOrDefault(node, 0D) + 1 / resistance));
		addResistor(getNodeName(node), GND_NODE, resistance);
	}
	
	public void addSource(ConnectionPoint node, double voltage, double maxCurrent) {
		addSource(getNodeName(node), GND_NODE, voltage, maxCurrent);
	}
	
	public void addLoad(ConnectionPoint node, double current) {
		addLoad(getNodeName(node), GND_NODE, current);
	}
	
	@Override
	public String toString() {
		return this.netList == null ? this.stringBuilder.toString() : netList;
	}

	public double getSerialResistance(ConnectionPoint nodeA, ConnectionPoint nodeB) {
		return this.serialResistance.getOrDefault((long) (nodeA.hashCode() + nodeB.hashCode()), Double.MAX_VALUE);
	}

	public double getParalelResistance(ConnectionPoint node) {
		return this.paralelResistance.getOrDefault(node, Double.MAX_VALUE);
	}
	
	public double getVoltage(ConnectionPoint node) {
		return this.nodeVoltages.getOrDefault(node, 0D);
	}

	public void complete() {
		this.stringBuilder.append(".end\n");
		this.netList = this.stringBuilder.toString();
		this.connectedNodes.forEach((nodeSet) -> {
			Set<ConnectionPoint> mapedNodes = new HashSet<ConnectionPoint>();
			String nodeName = nodeSet.stream().findAny().get() + "-com";
			nodeSet.forEach((node) -> {
				mapedNodes.addAll(getNodes(node));
				netList = netList.replace(node, nodeName);
			});
			hash2node.put(nodeName, mapedNodes);
		});
	}
	
	public void reset() {
		this.stringBuilder = new StringBuilder();
		this.stringBuilder.append(title + "\n");
		this.netList = null;
		this.hash2node.clear();
		this.connectedNodes.clear();
		this.nodeVoltages.clear();
		this.paralelResistance .clear();
		this.serialResistance.clear();
		this.components.clear();
		this.modelMap.clear();
		this.resistorCount = 1;
		this.sourceCount = 1;
		this.loadCount = 1;
	}
	
}
