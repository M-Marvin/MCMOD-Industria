package de.m_marvin.industria.core.electrics.engine;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.electrics.events.ElectricNetworkEvent;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.engine.network.SUpdateElectricNetworkPackage;
import de.m_marvin.industria.core.electrics.types.IElectric.ICircuitPlot;
import de.m_marvin.industria.core.util.ConditionalExecutor;
import de.m_marvin.industria.core.util.types.PowerNetState;
import de.m_marvin.industria.core.util.ufns.SynchronizedFunctionalNetworkSpace;
import de.m_marvin.unimap.api.MultiBiMap;
import de.m_marvin.unimap.impl.HashMultiBiMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

public class ElectricNetwork extends SynchronizedFunctionalNetworkSpace.SynchronizedFunctionalNetwork<ElectricNetwork, Object, ElectricComponent<?, Object, ?>, NodePos> {
	
	private final Supplier<Level> level;
	
	private MultiBiMap<Integer, NodePos> ref2nodeMap = new HashMultiBiMap<Integer, NodePos>();
	private Map<String, Double> nodeVoltages = Maps.newHashMap();
	private double maxPower;
	private double currentConsumtion;
	private double currentProduction;
	private PowerNetState state = PowerNetState.ACTIVE;
	
	private long templateCounter;
	private StringBuilder circuitBuilder;
	private String groundNode;
	private String netList = "";

	public ElectricNetwork(Supplier<Level> level) {
		this.level = level;
		resetNetlist(); // When loading the chunk (and as such initially constructing the network) the new network has to be in reset state
	}
	
	public Level getLevel() {
		return level.get();
	}
	
	@Override
	public void serializeNbt(CompoundTag nbt) {
		super.serializeNbt(nbt);
		
		ListTag nodesNbt = new ListTag();
		for (var e : this.ref2nodeMap.entrySet()) {
			CompoundTag nodeNbt = new CompoundTag();
			nodeNbt.putInt("RefId", e.getKey());
			nodeNbt.put("Node", e.getValue().writeNBT(new CompoundTag()));
			nodesNbt.add(nodeNbt);
		}
		nbt.put("Nodes", nodesNbt);
		
		CompoundTag voltagesNbt = new CompoundTag();
		for (var e : this.nodeVoltages.entrySet()) {
			voltagesNbt.putDouble(e.getKey(), e.getValue());
		}
		nbt.put("Voltages", voltagesNbt);
		
		nbt.putDouble("MaxPower", this.maxPower);
		nbt.putDouble("CurrentConsumtion", this.currentConsumtion);
		nbt.putDouble("CurrentProduction", this.currentProduction);
		nbt.putString("State", this.state.name().toLowerCase());
	}
	
	@Override
	public void deserializeNbt(CompoundTag nbt) {
		super.deserializeNbt(nbt);

		ListTag nodesNbt = nbt.getList("Nodes", 10);
		this.ref2nodeMap.clear();
		for (int i = 0; i < nodesNbt.size(); i++) {
			CompoundTag nodeNbt = nodesNbt.getCompound(i);
			int refId = nodeNbt.getInt("RefId");
			NodePos node = NodePos.readNBT(nodeNbt.getCompound("Node"));
			this.ref2nodeMap.put(refId, node);
		}
		
		CompoundTag voltagesNbt = nbt.getCompound("Voltages");
		this.nodeVoltages.clear();
		for (String node : voltagesNbt.getAllKeys()) {
			this.nodeVoltages.put(node, voltagesNbt.getDouble(node));
		}
		
		this.maxPower = nbt.getDouble("MaxPower");
		this.currentConsumtion = nbt.getDouble("CurrentConsumtion");
		this.currentProduction = nbt.getDouble("CurrentProduction");
		this.state = PowerNetState.valueOf(nbt.getString("State").toUpperCase());
	}
	
	@Override
	public void afterChange() {
		if (!getLevel().isClientSide())
			recomputeElectrics();
	}

	@Override
	public void onUpdate() {
		if (!getLevel().isClientSide()) {
			resetNetlist();
			recomputeElectrics();
		}
	}
	
	protected void resetNetlist() {
		this.circuitBuilder = new StringBuilder();
		this.netList = "";
		this.groundNode = null;
	}
	
	protected void plotComponentDescriptor(ElectricComponent<?, ?, ?> component) {
		this.circuitBuilder.append("\n* Component " + component.type().toString() + " " + component.reference().toString() + "\n");
	}
	
	protected void plotTemplate(ElectricComponent<?, ?, ?> component, ICircuitPlot template) {
		template.prepare(templateCounter++);
		this.circuitBuilder.append(template.plot());
		if (this.groundNode == null) this.groundNode = template.getAnyNode();
	}
	
	public void recomputeElectrics() {
		
		// Only rebuild netlist if it has been reset befpre
		if (this.netList.isEmpty() && this.groundNode == null && this.circuitBuilder != null) {
			
			for (var component : listComponents()) {
				plotComponentDescriptor(component);
				component.plotCircuit(getLevel(), this, template -> plotTemplate(component, template));
			}
			
			String groundResistor = "R0GND " + this.groundNode + " 0 1";
			this.netList = filterSingularMatrixNodes(String.format("%s\n%s\n\n%s", "Ingame circuit #" + hashCode(), circuitBuilder.toString(), groundResistor));
			
		}
		
		if (this.isTripped()) {
			this.nodeVoltages.clear();

			// Send update to clients
			IndustriaCore.NETWORK.send(ElectricUtility.TRACKING_NETWORK.with(() -> this), new SUpdateElectricNetworkPackage(this));
			
			// Notify components
			updateComponents();
			
		} else {

			// No simulation if empty
			if (isEmpty() || this.groundNode == null) return;
			
			ElectricNetworkSpaceCapability.getSimulationProcessor().processNetwork(this).thenAcceptAsync(state -> {
				
				if (!state) {
					tripFuse();
					MinecraftForge.EVENT_BUS.post(new ElectricNetworkEvent.FuseTripedEvent(getLevel(), this));
					return;
				}

				// Recalculate network global variables
				this.maxPower = 0;
				this.currentConsumtion = 0;
				this.currentProduction = 0;
				for (ElectricComponent<?, ?, ?> c : listComponents()) {
					this.maxPower += c.getMaxPowerGeneration(getLevel());
					double p = c.getCurrentPower(getLevel());
					if (p > 0) {
						this.currentProduction += p;
					} else {
						this.currentConsumtion += -p;
					}
				}
				
				if (this.currentConsumtion > this.currentProduction) {
					this.nodeVoltages.clear();
					setState(PowerNetState.INACTIVE);
				} else {
					setState(PowerNetState.ACTIVE);
				}
				
				// Send update to clients
				IndustriaCore.NETWORK.send(ElectricUtility.TRACKING_NETWORK.with(() -> this), new SUpdateElectricNetworkPackage(this));
				
			}, ConditionalExecutor.SERVER_TICK_EXECUTOR);
			
		}
		
	}
	
	public void updateComponents() {
		for (var c : listComponents()) {
			c.onNetworkChange(getLevel());
		}
	}
	
	@Override
	protected synchronized void afterPutComponent(int refId) {
		this.ref2nodeMap.remove(refId);
		resetNetlist();
	}

	@Override
	protected synchronized void afterRemoveComponent(int refId) {
		this.ref2nodeMap.remove(refId);
		resetNetlist();
	}

	@Override
	protected synchronized void afterParametrizedConnection(int refId1, int refId2, NodePos parameter) {
		this.ref2nodeMap.put(refId1, parameter);
		this.ref2nodeMap.put(refId2, parameter);
	}
	
	public synchronized Collection<ElectricComponent<?, ?, ?>> findComponentsOnNode(NodePos node) {
		List<ElectricComponent<?, ?, ?>> components = new ArrayList<>();
		for (Integer refId : this.ref2nodeMap.getKeys(node)) {
			ElectricComponent<?, ?, ?> component = this.components.get(refId.intValue());
			if (component != null) components.add(component);
		}
		return components;
	}

	@Override
	protected synchronized void afterIntegrateNetwork(IntSet refIds, ElectricNetwork other) {
		for (int refId : refIds) {
			if (other.ref2nodeMap.containsKey(refId))
				this.ref2nodeMap.put(refId, other.ref2nodeMap.get(refId));
		}
		// This is mainly for the client, on the server the node voltages get overridden after an update anyway
		this.nodeVoltages.putAll(other.nodeVoltages);
		resetNetlist();
	}
	
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
			if (isSingular)
				continue;
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
	
	public boolean isPlotEmpty() {
		return this.netList.isEmpty();
	}
	
	public boolean isEmpty() {
		return components.isEmpty();
	}
	
	@Override
	public String toString() {
		return isPlotEmpty() ? "EMPTY" : (this.netList == null ? this.circuitBuilder.toString() : netList);
	}
	
	public synchronized Map<String, Double> getNodeVoltages() {
		return nodeVoltages;
	}

	public synchronized Map<String, Double> getNodeVoltages(Level level, ElectricComponent<?, Object, ?> component) {
		return Stream
				.of(component.getNodes(level))
				.flatMap(node -> {
					String[] lanes = component.getWireLanes(level, node);
					return IntStream.range(0, lanes.length)
							.mapToObj(i -> getNodeKeyString(node, i, lanes[i]));
				})
				.collect(Collectors.toMap(Functions.identity(), this.nodeVoltages::get));
	}
	
	public synchronized boolean parseDataList(String dataList) {
		this.nodeVoltages.clear();
		Stream.of(dataList.split("\n"))
			.map(s -> s.split("\t"))
			.filter(s -> s.length == 2)
			.forEach(s -> this.nodeVoltages.put(s[0], Double.valueOf(s[1].split(" V")[0])));
		return this.nodeVoltages.size() > 0;
	}
	
	public void setMaxPower(double maxPower) {
		this.maxPower = maxPower;
	}
	
	public double getMaxPower() {
		return maxPower;
	}
	
	public void setCurrentConsumtion(double currentConsumtion) {
		this.currentConsumtion = currentConsumtion;
	}
	
	public double getCurrentConsumtion() {
		return currentConsumtion;
	}
	
	public void setCurrentProduction(double currentProduction) {
		this.currentProduction = currentProduction;
	}
	
	public double getCurrentProduction() {
		return currentProduction;
	}
	
	public void tripFuse() {
		setState(PowerNetState.FAILED);
	}
	
	public void setState(PowerNetState state) {
		this.state = state;
		updateComponents();
	}
	
	public PowerNetState getState() {
		return state;
	}
	
	public boolean isTripped() {
		return this.state == PowerNetState.FAILED;
	}
	
	public boolean isOnline() {
		return this.state == PowerNetState.ACTIVE;
	}
	
	public String getNetList() {
		return netList == null ? "" : this.netList;
	}
	
	public String printDataList() {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, Double> e : this.nodeVoltages.entrySet()) {
			sb.append(e.getKey()).append("\t").append(e.getValue()).append("\n");
		}
		return sb.toString();
	}
	
	public synchronized Optional<Double> getFloatingNodeVoltage(NodePos node, int laneId, String lane) {
		String nodeName = getNodeKeyString(node, laneId, lane);
		if (!this.nodeVoltages.containsKey(nodeName)) return Optional.empty();
		return Optional.of(isOnline() ? this.nodeVoltages.get(nodeName) : 0.0);
	}

	public synchronized Optional<Double> getFloatingLocalNodeVoltage(BlockPos position, String lane, int group) {
		String nodeName = getLocalNodeKeyString(position, lane, group);
		if (!this.nodeVoltages.containsKey(nodeName)) return Optional.empty();
		return Optional.of(isOnline() ? this.nodeVoltages.get(nodeName) : 0.0);
	}
																	/*     |----TEMPLATE----|     |------EXTERNAL--------|     |-------------INTERNAL-----------| */
	private static final Pattern FILTER_NODE_PATTERN = Pattern.compile("(?:N[0-9_]{3,}_[0-9]+)|(?:node\\|[A-Za-z0-9_~]+\\|)|(?:intnode\\|[A-Za-z0-9_~]+\\|_[0-9]+)");
	private static final Pattern FILTER_GROUND_PATTERN = Pattern.compile("R0GND ([^ ]+) 0 1");
	
	public static String getLocalNodeKeyString(BlockPos position, String laneName, int group) {
		return ("IntNode|pos" + position.getX() + "_" + position.getY() + "_" + position.getZ() + "_lnm" + laneName + "|_" + group)
				.toLowerCase()
				.replace('-', '~'); // '-' would be interpreted as mathematical operator, '~' not
	}
	
	public static String getNodeKeyString(NodePos node, int laneId, String laneName) {
		return ("Node|pos" + node.getBlock().getX() + "_" + node.getBlock().getY() + "_" + node.getBlock().getZ() + "_id" + node.getNode() + "_lid" + laneId + "_lnm" + laneName + "|")
				.toLowerCase()
				.replace('-', '~'); // '-' would be interpreted as mathematical operator, '~' not
	}
	
	public static NodePos getNodeFromKeyString(String keyString) {
		try {
			Properties props = new Properties();
			props.load(new StringReader(keyString));
			BlockPos position = BlockPos.of(Long.valueOf(props.getProperty("pos")));
			int node = Integer.valueOf(props.getProperty("id"));
			return new NodePos(position, node);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getLaneFromKeyString(String keyString) {
		try {
			Properties props = new Properties();
			props.load(new StringReader(keyString));
			return props.getProperty("lane");
		} catch (Exception e) {
			return "";
		}
	}
	
}
