package de.m_marvin.industria.util.electricity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.electricity.ElectricNetworkHandlerCapability.Component;

public class CircuitConfiguration {
	
	public static final String GND_NODE = "node-gnd";
	
	protected Map<String, ConnectionPoint> hash2node;
	protected Map<Long, Double> serialResistance;
	protected Map<ConnectionPoint, Double> paralelResistance;
	protected Set<Component<?, ?, ?>> components;
	protected StringBuilder stringBuilder;
	protected int resistorCount = 1;
	protected int sourceCount = 1;
	
	public CircuitConfiguration(String titleInfo) {
		this.stringBuilder = new StringBuilder();
		this.stringBuilder.append(titleInfo + "\n");
		this.hash2node = new HashMap<String, ConnectionPoint>();
		this.paralelResistance = new HashMap<ConnectionPoint, Double>();
		this.serialResistance = new HashMap<Long, Double>();
		this.components = new HashSet<Component<?, ?, ?>>();
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
	
	protected void addSource(String nodeP, String nodeN, double voltage, double maxCurrent) {
		if (maxCurrent < 0) {
			stringBuilder.append("V" + (sourceCount++) + " " + nodeP + " " + nodeN + " " + voltage + "\n");
		} else {
			// TODO Current-dependent voltage source
			String deviceName = "B" + (sourceCount++);
			String expr = "min(" + voltage + "," + voltage + " * (" + 8 + "/" + "i(" + "R1" + ")))";
			//stringBuilder.append("V" + (sourceCount++) + " " + nodeP + " " + nodeN + " " + voltage + "\n");
			stringBuilder.append(deviceName + " " + nodeP + " " + nodeN + " V = " + expr + "\n");
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
	
	@Override
	public String toString() {
		return this.stringBuilder.toString();
	}

	public double getSerialResistance(ConnectionPoint nodeA, ConnectionPoint nodeB) {
		return this.serialResistance.getOrDefault((long) (nodeA.hashCode() + nodeB.hashCode()), Double.MAX_VALUE);
	}
	
}
