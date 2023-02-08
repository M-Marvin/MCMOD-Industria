package de.m_marvin.industria.core.electrics.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

public class ElectricNetwork {
	
	public static final String GND_NODE = "0";

	@FunctionalInterface
	public static interface NetListBuilder {
		public String build(ElectricNetwork net, String id, String... param);
	}
	
	// param: 0 node+ 1 node- 2 current 3 voltage
	public static final NetListBuilder SOURCE_BUILDER = (net, id, param) -> {
		String md = net.addModel("sw vt=0 vh=0.001 ron=0.001 roff=10e9");
		return    "I1"+id+" N1"+id+" N3"+id+" "+param[2]+"\n"
				+ "I2"+id+" N1"+id+" "+param[1]+" "+param[2]+"\n"
				+ "V1"+id+" N2"+id+" N3"+id+" "+param[3]+"\n"
				+ "S1"+id+" "+param[0]+" N2"+id+" N2"+id+" "+param[0]+" "+md+"\n"
				+ "S2"+id+" N1"+id+" N3"+id+" N3"+id+" N1"+id+" "+md+"\n"
				+ "S3"+id+" N1"+id+" "+param[1]+" "+param[1]+" N1"+id+" "+md+"\n";
	};
	
	// param: 0 nodeA 1 nodeB 2 resistance
	public static final NetListBuilder RESISTANCE_BUILDER = (net, id, param) -> {
		return    "R1"+id+" "+param[0]+" "+param[1]+" "+param[2]+"\n";
	};
	
	// param 0 node+ 1 node- 2 current
	public static final NetListBuilder LOAD_BUILDER = (net, id, param) -> {
		String md = net.addModel("sw vt=0 vh=0.001 ron=0.001 roff=10e9");
		return    "I1"+id+" "+param[0]+" "+param[1]+" "+param[2]+"\n"
				+ "S1"+id+" "+param[0]+" "+param[1]+" "+param[1]+" "+param[0]+" "+md+"\n";
	};
	
	// Stored results
	protected Map<Long, Double> serialResistance;
	protected Map<NodePos, Double> parallelResistance;
	protected Map<NodePos, Double> nodeVoltages;
	protected Set<Component<?, ?, ?>> components;
	protected long lastUpdated;
	
	// Cache for circuit building
	protected String title;
	protected Set<Set<String>> connectedNodes;
	protected Map<String, Set<NodePos>> hash2node;
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
		this.hash2node = new HashMap<String, Set<NodePos>>();
		this.nodeVoltages = new HashMap<NodePos, Double>();
		this.parallelResistance = new HashMap<NodePos, Double>();
		this.serialResistance = new HashMap<Long, Double>();
		this.components = new HashSet<Component<?, ?, ?>>();
		this.modelMap = new HashSet<String>();
		this.connectedNodes = new HashSet<Set<String>>();
	}
	
	public CompoundTag saveNBT() {
		CompoundTag tag = new CompoundTag();
		CompoundTag serialResistanceTag = new CompoundTag();
		this.serialResistance.forEach((key, value) -> serialResistanceTag.putDouble(String.valueOf(key), value));
		CompoundTag parallelResistanceTag = new CompoundTag();
		this.parallelResistance.forEach((key, value) -> {if (key != null) parallelResistanceTag.putDouble(key.getKeyString(), value);});
		CompoundTag nodeVoltagesTag = new CompoundTag();
		this.nodeVoltages.forEach((key, value) -> {if (key != null) nodeVoltagesTag.putDouble(key.getKeyString(), value);});
		ListTag componentsTag = new ListTag();
		this.components.forEach((component) -> {
			CompoundTag componentTag = new CompoundTag();
			component.serializeNbt(componentTag);
			componentsTag.add(componentTag);
		});
		tag.put("SerialResistance", serialResistanceTag);
		tag.put("ParallelResistance", parallelResistanceTag);
		tag.put("NodeVoltages", nodeVoltagesTag);
		tag.put("Components", componentsTag);
		return tag;
	}
	
	public void loadNBT(Level level, CompoundTag tag) {
		CompoundTag serialResistanceTag = tag.getCompound("SerialResistance");
		serialResistanceTag.getAllKeys().forEach((key) -> {
			this.serialResistance.put(Long.valueOf(key), serialResistanceTag.getDouble(key));
		});
		CompoundTag parallelResistanceTag = tag.getCompound("ParallelResistance");// FIXME 
		parallelResistanceTag.getAllKeys().forEach((key) -> {
			this.parallelResistance.put(NodePos.getFromKeyString(key), parallelResistanceTag.getDouble(key));
		});
		CompoundTag nodeVoltagesTag = tag.getCompound("NodeVoltages");
		nodeVoltagesTag.getAllKeys().forEach((key) -> {
			this.nodeVoltages.put(NodePos.getFromKeyString(key), nodeVoltagesTag.getDouble(key));
		});
		ListTag componentsTag = tag.getList("Components", 10);
		componentsTag.stream().forEach((componentTag) -> {
			this.components.add(Component.deserializeNbt((CompoundTag) componentTag));
		});
	}
	
	public Set<Component<?, ?, ?>> getComponents() {
		return components;
	}
	
	public Map<String, Set<NodePos>> getNodes() {
		return this.hash2node;
	}
	
	protected String getNodeName(NodePos node) {
		String hashName = "node-" + Integer.toHexString(node.hashCode());
		Set<NodePos> nodes = this.hash2node.getOrDefault(hashName, new HashSet<NodePos>());
		nodes.add(node);
		this.hash2node.put(hashName, nodes);
		return hashName;
	}
	
	public Set<NodePos> getNodes(String hashName) {
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
			List<Set<String>> stream = connectedNodes.stream().filter((set) -> !set.equals(nodeSetB.get())).toList();
			connectedNodes.clear();
			stream.forEach(connectedNodes::add);
		}
	}
	
	protected void addLoad(String nodeA, String nodeB, double current) {
		stringBuilder.append(LOAD_BUILDER.build(this, "-" + loadCount++ + "l", nodeA, nodeB, String.valueOf(current)));
	}
	
	protected void addResistor(String nodeA, String nodeB, double resistance) {
		if (resistance == 0) {
			combineNodes(nodeA, nodeB);
		} else {
			stringBuilder.append(RESISTANCE_BUILDER.build(this, "-" + resistorCount++ + "r", nodeA, nodeB, String.valueOf(resistance)));
		}
	}
	
	protected void addSource(String nodeP, String nodeN, double voltage, double maxCurrent) {
		if (voltage > 0) stringBuilder.append(SOURCE_BUILDER.build(this, "-" + sourceCount++ + "s", nodeP, nodeN, String.valueOf(maxCurrent), String.valueOf(voltage)));
	}
	
	public void addSerialResistance(NodePos nodeA, NodePos nodeB, double resistance) {
		this.serialResistance.put((long) (nodeA.hashCode() + nodeB.hashCode()), resistance);
		addResistor(getNodeName(nodeA), getNodeName(nodeB), resistance);
	}
	
	public void addParallelResistance(NodePos node, double resistance) {
		this.parallelResistance.put(node, 1 / (1 / this.parallelResistance.getOrDefault(node, 0D) + 1 / resistance));
		addResistor(getNodeName(node), GND_NODE, resistance);
	}
	
	public void addSource(NodePos node, double voltage, double maxCurrent) {
		addSource(getNodeName(node), GND_NODE, voltage, maxCurrent);
	}
	
	public void addLoad(NodePos node, double current) {
		addLoad(getNodeName(node), GND_NODE, current);
	}
	
	public boolean isPlotEmpty() {
		return sourceCount + loadCount + resistorCount == 0;
	}
	
	public boolean isEmpty() {
		return components.isEmpty();
	}
	
	@Override
	public String toString() {
		return isPlotEmpty() ? "EMPTY" : (this.netList == null ? this.stringBuilder.toString() : netList);
	}
	
	public double getSerialResistance(NodePos nodeA, NodePos nodeB) {
		return this.serialResistance.getOrDefault((long) (nodeA.hashCode() + nodeB.hashCode()), Double.MAX_VALUE);
	}

	public double getParallelResistance(NodePos node) {
		return this.parallelResistance.getOrDefault(node, Double.MAX_VALUE);
	}
	
	public Map<NodePos, Double> getNodeVoltages() {
		return this.nodeVoltages;
	}
	
	public double getVoltage(NodePos node) {
		return this.nodeVoltages.getOrDefault(node, 0D);
	}

	public void complete(long frame) {
		this.stringBuilder.append(".end\n");
		this.netList = this.stringBuilder.toString();
		this.connectedNodes.forEach((nodeSet) -> {
			Set<NodePos> mapedNodes = new HashSet<NodePos>();
			String nodeName = nodeSet.stream().findAny().get() + "-com";
			nodeSet.forEach((node) -> {
				mapedNodes.addAll(getNodes(node));
				netList = netList.replace(node, nodeName);
			});
			hash2node.put(nodeName, mapedNodes);
		});
		this.lastUpdated = frame;
	}
	
	public void reset() {
		this.stringBuilder = new StringBuilder();
		this.stringBuilder.append(title + "\n");
		this.netList = null;
		this.hash2node.clear();
		this.connectedNodes.clear();
		this.nodeVoltages.clear();
		this.parallelResistance .clear();
		this.serialResistance.clear();
		this.components.clear();
		this.modelMap.clear();
		this.resistorCount = 0;
		this.sourceCount = 0;
		this.loadCount = 0;
	}

	public boolean updatedInFrame(long frame) {
		return this.lastUpdated == frame;
	}
	
}
