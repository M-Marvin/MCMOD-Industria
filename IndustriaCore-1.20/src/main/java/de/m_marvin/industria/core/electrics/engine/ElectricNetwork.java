package de.m_marvin.industria.core.electrics.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.IElectric.ICircuitPlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

public class ElectricNetwork {
	
	protected String title;
	protected final Supplier<Level> level;
	protected Set<Component<?, ?, ?>> components = ConcurrentHashMap.newKeySet();
	protected Set<Component<?, ?, ?>> componentsLast = new HashSet<>();
	protected long templateCounter;
	protected StringBuilder circuitBuilder;
	protected String groundNode;
	protected String netList = "";
	protected Map<String, Double> nodeVoltages = Maps.newHashMap();
	
	protected static Object ngLinkLock = new Object();
	
	public ElectricNetwork(Supplier<Level> level, String titleInfo) {
		this.level = level;
		this.title = titleInfo;
	}
	
	public Level getLevel() {
		return level.get();
	}
	
	public CompoundTag saveNBT(ElectricNetworkHandlerCapability handler) {
		CompoundTag tag = new CompoundTag();
		ListTag componentsTag = new ListTag();
		for (Component<?, ?, ?> component : this.components) {
			if (component == null) continue;
			try {
				CompoundTag compTag = new CompoundTag();
				component.serializeNbt(compTag);
				componentsTag.add(compTag);
			} catch (Exception e) {
				IndustriaCore.LOGGER.error("Failed to serialize electric component at " + component.pos() + "!");
				e.printStackTrace();
			}
		}
		tag.put("Components", componentsTag);
		if (!this.isEmpty() && !this.isPlotEmpty() && this.components.size() > 1) {
			String circuitName = handler.saveCircuit(this.netList);
			tag.putString("Circuit", circuitName);
		}
		return tag;
	}
	
	public void loadNBT(ElectricNetworkHandlerCapability handler, CompoundTag tag) {
		ListTag componentsTag = tag.getList("Components", ListTag.TAG_COMPOUND);
		componentsTag.stream().forEach((componentTag) -> {
			this.components.add(Component.deserializeNbt((CompoundTag) componentTag));
		});
		if (tag.contains("Circuit")) {
			String circuitName = tag.getString("Circuit");
			this.netList = handler.loadCircuit(circuitName);
		}
	}
	
	public Set<Component<?, ?, ?>> getComponents() {
		return components;
	}

	public void reset() {
		this.circuitBuilder = new StringBuilder();
		this.netList = "";
		this.groundNode = null;
		this.componentsLast = components;
		this.components = ConcurrentHashMap.newKeySet();
	}
	
	public void plotComponentDescriptor(Component<?, ?, ?> component) {
		this.circuitBuilder.append("\n* Component " + component.type().toString() + " " + component.pos().toString() + "\n");
	}
	
	public void plotTemplate(Component<?, ?, ?> component, ICircuitPlot template) {
		template.prepare(templateCounter++);
		this.circuitBuilder.append(template.plot());
		if (this.groundNode == null) this.groundNode = template.getAnyNode();
	}

	public void complete(long frame) {
		if (!this.circuitBuilder.isEmpty()) {
			String groundResistor = "R0GND " + groundNode + " 0 1";
			this.netList = title + "\n" + circuitBuilder.toString() + "\n" + groundResistor + "\n.end\n";
		} else {
			this.netList = "";
		}
	}
	
	public boolean isPlotEmpty() {
		return this.netList.isEmpty();
	}
	
	public boolean isEmpty() {
		return components.isEmpty();
	}

	public void removeInvalidComponents() {
		List<Component<?, ?, ?>> invalid = new ArrayList<>();
		for (Component<?, ?, ?> component : this.components) {
			if (component == null || component.instance(null) == null) invalid.add(component);
		}
		invalid.forEach(c -> components.remove(c));
	}
	
	@Override
	public String toString() {
		return isPlotEmpty() ? "EMPTY" : (this.netList == null ? this.circuitBuilder.toString() : netList);
	}
	
	public synchronized Map<String, Double> getNodeVoltages() {
		return nodeVoltages;
	}
	
	public String getNetList() {
		return netList == null ? "" : this.netList;
	}
	
	public synchronized double getFloatingNodeVoltage(NodePos node, int laneId, String lane) {
		return this.nodeVoltages.getOrDefault(node.getKeyString(laneId, lane), 0.0);
	}
	
}
