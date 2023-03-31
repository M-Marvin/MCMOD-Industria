package de.m_marvin.industria.core.electrics.types;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class ElectricNetwork {
	
	public static final String GND_NODE = "0";

	@FunctionalInterface
	public static interface NetListBuilder {
		public String build(ElectricNetwork net, String id, String... param);
	}
	
	// Stored results
	protected String title;
	protected Map<NodePos, Double> nodeVoltages = Maps.newHashMap();
	protected Set<Component<?, ?, ?>> components = new HashSet<>();
	
	//protected Set<Component<?, ?, ?>> components;
	protected long lastUpdated;
	
	protected StringBuilder circuitBuilder;
	protected String netList;
	
	public ElectricNetwork(String titleInfo) {
		this.title = titleInfo;
	}
	
	public CompoundTag saveNBT() {
		CompoundTag tag = new CompoundTag();
//		CompoundTag serialResistanceTag = new CompoundTag();
//		this.serialResistance.forEach((key, value) -> serialResistanceTag.putDouble(String.valueOf(key), value));
//		CompoundTag parallelResistanceTag = new CompoundTag();
//		this.parallelResistance.forEach((key, value) -> {if (key != null) parallelResistanceTag.putDouble(key.getKeyString(), value);});
//		CompoundTag nodeVoltagesTag = new CompoundTag();
//		this.nodeVoltages.forEach((key, value) -> {if (key != null) nodeVoltagesTag.putDouble(key.getKeyString(), value);});
//		ListTag componentsTag = new ListTag();
//		this.components.forEach((component) -> {
//			CompoundTag componentTag = new CompoundTag();
//			component.serializeNbt(componentTag);
//			componentsTag.add(componentTag);
//		});
//		tag.put("SerialResistance", serialResistanceTag);
//		tag.put("ParallelResistance", parallelResistanceTag);
//		tag.put("NodeVoltages", nodeVoltagesTag);
//		tag.put("Components", componentsTag);
		return tag;
	}
	
	public void loadNBT(Level level, CompoundTag tag) {
//		CompoundTag serialResistanceTag = tag.getCompound("SerialResistance");
//		serialResistanceTag.getAllKeys().forEach((key) -> {
//			this.serialResistance.put(Long.valueOf(key), serialResistanceTag.getDouble(key));
//		});
//		CompoundTag parallelResistanceTag = tag.getCompound("ParallelResistance");// FIXME 
//		parallelResistanceTag.getAllKeys().forEach((key) -> {
//			this.parallelResistance.put(NodePos.getFromKeyString(key), parallelResistanceTag.getDouble(key));
//		});
//		CompoundTag nodeVoltagesTag = tag.getCompound("NodeVoltages");
//		nodeVoltagesTag.getAllKeys().forEach((key) -> {
//			this.nodeVoltages.put(NodePos.getFromKeyString(key), nodeVoltagesTag.getDouble(key));
//		});
//		ListTag componentsTag = tag.getList("Components", 10);
//		componentsTag.stream().forEach((componentTag) -> {
//			this.components.add(Component.deserializeNbt((CompoundTag) componentTag));
//		});
	}
	
	public Set<Component<?, ?, ?>> getComponents() {
		return components;
	}
	
//	public Map<String, Set<NodePos>> getNodes() {
//		return this.hash2node;
//	}
	
//	protected String getNodeName(NodePos node) {
//		String hashName = "node-" + Integer.toHexString(node.hashCode());
//		Set<NodePos> nodes = this.hash2node.getOrDefault(hashName, new HashSet<NodePos>());
//		nodes.add(node);
//		this.hash2node.put(hashName, nodes);
//		return hashName;
//	}
	
//	public Set<NodePos> getNodes(String hashName) {
//		return this.hash2node.get(hashName);
//	}
	
//	protected String addModel(String model) {
//		String modelName = "M" + Integer.toHexString(model.hashCode());
//		if (this.modelMap.add(model)) stringBuilder.append(".model " + modelName + " " + model + "\n");
//		return modelName;
//	}
	
//	protected void combineNodes(String nodeA, String nodeB) {
//		Optional<Set<String>> nodeSetA = connectedNodes.stream().filter((nodeSet) -> nodeSet.contains(nodeA)).findAny();
//		Optional<Set<String>> nodeSetB = connectedNodes.stream().filter((nodeSet) -> nodeSet.contains(nodeB)).findAny();
//		if (nodeSetA.equals(nodeSetB)) {
//			if (nodeSetA.isEmpty()) {
//				Set<String> nodeSet = new HashSet<String>();
//				nodeSet.add(nodeA);
//				nodeSet.add(nodeB);
//				connectedNodes.add(nodeSet);
//			}
//		} else if (nodeSetA.isEmpty()) {
//			nodeSetB.get().add(nodeA);
//		} else if (nodeSetB.isEmpty()) {
//			nodeSetA.get().add(nodeB);
//		} else {
//			nodeSetB.get().stream().forEach(nodeSetA.get()::add);
//			List<Set<String>> stream = connectedNodes.stream().filter((set) -> !set.equals(nodeSetB.get())).toList();
//			connectedNodes.clear();
//			stream.forEach(connectedNodes::add);
//		}
//	}
	
	protected int templateCounter;
	
	public void plotTemplate(Component<?, ?, ?> component, CircuitTemplate template) {
		template.prepare(templateCounter++);
		this.circuitBuilder.append(template.plot()); // TODO Model filtering
		this.components.add(component);
	}
	
	
	
	
	
	public boolean isPlotEmpty() {
		return templateCounter == 0;
	}
	
	public boolean isEmpty() {
		return components.isEmpty();
	}
	
	@Override
	public String toString() {
		return isPlotEmpty() ? "EMPTY" : (this.netList == null ? this.circuitBuilder.toString() : netList);
	}
	
	public Map<NodePos, Double> getNodeVoltages() {
		return this.nodeVoltages;
	}
	
	public double getVoltage(NodePos node) {
		return this.nodeVoltages.getOrDefault(node, 0D);
	}

	public void complete(long frame) {
		this.circuitBuilder.append(".end\n");
		this.netList = circuitBuilder.toString();
		
		System.out.println(netList);
		
//		this.connectedNodes.forEach((nodeSet) -> {
//			Set<NodePos> mapedNodes = new HashSet<NodePos>();
//			String nodeName = nodeSet.stream().findAny().get() + "-com";
//			nodeSet.forEach((node) -> {
//				mapedNodes.addAll(getNodes(node));
//				netList = netList.replace(node, nodeName);
//			});
//			hash2node.put(nodeName, mapedNodes);
//		});
		this.lastUpdated = frame;
	}
	
	public void reset() {
		this.circuitBuilder = new StringBuilder();
		this.circuitBuilder.append(title + "\n");
		this.netList = null;
		this.nodeVoltages.clear();
		this.components.clear();
	}

	public boolean updatedInFrame(long frame) {
		return this.lastUpdated == frame;
	}
	
}
