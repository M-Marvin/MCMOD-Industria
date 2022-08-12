package de.m_marvin.industria.util.electricity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.electricity.ElectricNetworkHandlerCapability.Component;

public class ElectricNetwork {
	
	public static final String GND_NODE = "node-gnd";
	
	// Stored results
	protected Map<Long, Double> serialResistance;
	protected Map<ConnectionPoint, Double> paralelResistance;
	protected Map<ConnectionPoint, Double> nodeVoltages;
	protected Set<Component<?, ?, ?>> components;
	
	// Cache for circuit building
	protected String title;
	protected Map<String, ConnectionPoint> hash2node;
	protected StringBuilder stringBuilder;
	protected Set<String> modelMap;
	protected int resistorCount = 1;
	protected int loadCount = 1;
	protected int sourceCount = 1;
	
	public ElectricNetwork(String titleInfo) {
		this.title = titleInfo;
		this.stringBuilder = new StringBuilder();
		this.stringBuilder.append(titleInfo + "\n");
		this.hash2node = new HashMap<String, ConnectionPoint>();
		this.nodeVoltages = new HashMap<ConnectionPoint, Double>();
		this.paralelResistance = new HashMap<ConnectionPoint, Double>();
		this.serialResistance = new HashMap<Long, Double>();
		this.components = new HashSet<Component<?, ?, ?>>();
		this.modelMap = new HashSet<String>();
	}
	
	public Set<Component<?, ?, ?>> getComponents() {
		return components;
	}
	
	public void complete() {
		this.stringBuilder.append(".end\n");
	}
	
	public Map<String, ConnectionPoint> getNodes() {
		return this.hash2node;
	}
	
	protected String getNodeName(ConnectionPoint node) {
		String hashName = "node-" + Integer.toHexString(node.hashCode());
		this.hash2node.put(hashName, node);
		return hashName;
	}
	
	public ConnectionPoint getNode(String hashName) {
		return this.hash2node.get(hashName);
	}
	
	protected void addResistor(String nodeA, String nodeB, double resistance) {
		stringBuilder.append("R" + (resistorCount++) + " " + nodeA + " " + nodeB + " " + resistance + "\n");
	}
	
	protected void addLoad(String nodeA, String nodeB, double current) {
		int id = this.loadCount++;
		String diodeModel = "D";
		String modelName = "M" + Integer.toHexString(diodeModel.hashCode());
		if (this.modelMap.add(diodeModel)) stringBuilder.append(".model " + modelName + " " + diodeModel + "\n");
//		stringBuilder.append("I" + id + "l " + nodeA + " " + nodeB + " " + current + "\n");
//		stringBuilder.append("D" + id + "l " + nodeB + " " + nodeA + " " + modelName + "\n");
		//stringBuilder.append("B" + id + "l " + nodeA + " " + nodeB + " I=" + current + "\n");
	}
	
	protected void addSource(String nodeP, String nodeN, double voltage, double maxCurrent) {
		if (maxCurrent < 0) {
			stringBuilder.append("V" + (sourceCount++) + "v " + nodeP + " " + nodeN + " " + voltage + "\n");
		} else {
			int id = sourceCount++;
			String nodeL1 = "node-limit" + id + "-1";
			String nodeL2 = "node-limit" + id + "-2";
			String diodeModel = "D";
			String modelName = "M" + Integer.toHexString(diodeModel.hashCode());
			if (this.modelMap.add(diodeModel)) stringBuilder.append(".model " + modelName + " " + diodeModel + "\n");
			stringBuilder.append("V" + id + "v " + nodeP + " " + nodeL1 + " " + voltage + "\n");
			
			stringBuilder.append("I" + id + "v1 " + nodeL2 + " " + nodeL1 + " " + maxCurrent + "\n");
			stringBuilder.append("I" + id + "v2 " + nodeL2 + " " + nodeN + " " + maxCurrent + "\n");
			stringBuilder.append("D" + id + "v1 " + nodeL1 + " " + nodeL2 + " " + modelName + "\n");
			stringBuilder.append("D" + id + "v2 " + nodeN + " " + nodeL2 + " " + modelName + "\n");
			
//			stringBuilder.append("I" + id + "v1 " + nodeL2 + " " + nodeL1 + " " + maxCurrent + "\n");
//			stringBuilder.append("D" + id + "v1 " + nodeL1 + " " + nodeL2 + " " + modelName + "\n");
//			stringBuilder.append("I" + id + "v2 " + nodeL2 + " " + nodeN + " " + maxCurrent + "\n");
//			stringBuilder.append("D" + id + "v2 " + nodeN + " " + nodeL2 + " " + modelName + "\n");
		}
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
	
	public void addFixLoad(ConnectionPoint node, double current) {
		addLoad(getNodeName(node), GND_NODE, current);
	}
	
	@Override
	public String toString() {
		return this.stringBuilder.toString();
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
	
	public void reset() {
		this.stringBuilder = new StringBuilder();
		this.stringBuilder.append(title + "\n");
		this.hash2node.clear();
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
