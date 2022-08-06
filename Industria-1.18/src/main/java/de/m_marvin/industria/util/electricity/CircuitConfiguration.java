package de.m_marvin.industria.util.electricity;

import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;

public class CircuitConfiguration {
	
	public static final String GND_NODE = "node-gnd";
	
	protected StringBuilder stringBuilder;
	protected int resistorCount = 1;
	protected int sourceCount = 1;
	
	public CircuitConfiguration(String titleInfo) {
		this.stringBuilder = new StringBuilder();
		this.stringBuilder.append(titleInfo + "\n");
	}
	
	public void complete() {
		this.stringBuilder.append(".end\n");
	}
	
	protected String getNodeName(ConnectionPoint node) {
		return "node-" + Integer.toHexString(node.hashCode());
	}
	
	protected void addResistor(String nodeA, String nodeB, float resistance) {
		stringBuilder.append("R" + (resistorCount++) + " " + nodeA + " " + nodeB + " " + resistance + "\n");
	}

	protected void addSource(String nodeP, String nodeN, float voltage) {
		stringBuilder.append("V" + (sourceCount++) + " " + nodeP + " " + nodeN + " " + voltage + "\n");
	}
	
	public void addSerialResistance(ConnectionPoint nodeA, ConnectionPoint nodeB, float resistance) {
		addResistor(getNodeName(nodeA), getNodeName(nodeB), resistance);
	}
	
	public void addParalelResistance(ConnectionPoint node, float resistance) {
		addResistor(getNodeName(node), GND_NODE, resistance);
	}
	
	public void addSource(ConnectionPoint node, float voltage) {
		addSource(getNodeName(node), GND_NODE, voltage);
	}
	
	@Override
	public String toString() {
		return this.stringBuilder.toString();
	}
	
}
