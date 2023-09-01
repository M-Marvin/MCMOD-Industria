package de.m_marvin.industria.core.electrics.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.IElectric.ICircuitPlot;
import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.INGCallback;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValue;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class ElectricNetwork implements INGCallback {
	
	protected String title;
	protected Set<Component<?, ?, ?>> components = new HashSet<>();
	//protected long lastUpdated;
	protected int templateCounter;
	protected StringBuilder circuitBuilder;
	protected String netList = "";
	protected NativeNGLink nglink;
	protected Map<String, Double> nodeVoltages = Maps.newHashMap();
	
	protected Object ngLinkLock = new Object();
	
	public ElectricNetwork(String titleInfo) {
		this.title = titleInfo;
		this.nglink = new NativeNGLink();
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
			this.components.add(Component.deserializeNbt(handler.getLevel(), (CompoundTag) componentTag));
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
		if (this.nglink.isNGSpiceAttached()) this.nglink.detachNGSpice();
		this.circuitBuilder = new StringBuilder();
		this.netList = null;
		this.components.clear();
	}
	
	public void plotTemplate(Component<?, ?, ?> component, ICircuitPlot template) {
		template.prepare(templateCounter++);
		this.circuitBuilder.append(template.plot()); // TODO Model filtering
		this.components.add(component);
	}

	public void complete(long frame) {
		if (!this.circuitBuilder.isEmpty()) {
			this.netList = title + "\n" + circuitBuilder.toString() + ".end\n";
		} else {
			this.netList = "";
		}
		//this.lastUpdated = frame;
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
			if (component == null || component.instance() == null) invalid.add(component);
		}
		invalid.forEach(c -> components.remove(c));
	}
	
	@Override
	public String toString() {
		return isPlotEmpty() ? "EMPTY" : (this.netList == null ? this.circuitBuilder.toString() : netList);
	}

//	public boolean updatedInFrame(long frame) {
//		return this.lastUpdated == frame;
//	}

	public void terminateExecution() {
		synchronized (ngLinkLock) {
			if (this.nglink.isInitialized()) {
				this.nglink.detachNGLink();
			}
		}
	}
	
	public NativeNGLink getNglink() {
		return nglink;
	}
	
	public void updateSimulation() {
		synchronized (ngLinkLock) {
			try {
				if (!this.netList.isEmpty()) {
					if (!this.nglink.isInitialized()) {
						if (!nglink.initNGLink(this)) {
							IndustriaCore.LOGGER.log(org.apache.logging.log4j.Level.ERROR, "Failed to start nglink! Electric simulation abborted!");
							return;
						}
					}
					if (!this.nglink.initNGSpice()) {
						IndustriaCore.LOGGER.warn("Failed to start electric simulation! Failed to init SPICE!");
						return;
					}
					if (!this.nglink.loadCircuit(this.netList)) {
						IndustriaCore.LOGGER.warn("Failed to start electric simulation! Failed to load circuit!");
						return;
					}
					this.nglink.execCommand("op");
				}
			} catch (Exception e) {
				IndustriaCore.LOGGER.error("Error when accessing nglink!");
				e.printStackTrace();
			}
		}
	}
	
	public double getFloatingNodeVoltage(NodePos node, int laneId, String lane) {
		return this.nodeVoltages.getOrDefault(node.getKeyString(laneId, lane), 0.0);
	}
	
	@Override
	public void log(String s) {
		if (Config.SPICE_DEBUG_LOGGING.get()) IndustriaCore.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, s);
	}
	
	@Override
	public void detacheNGSpice() {
		IndustriaCore.LOGGER.log(org.apache.logging.log4j.Level.WARN, "SPICE-Engine requested detachment, this could lead to undefined behavior!");
		this.nglink.detachNGSpice();
	}
 
	@Override
	public void reciveVecData(VectorValuesAll vecData, int vectorCount) {
		this.nodeVoltages.clear();
		for (int i = 0; i < vectorCount; i++) {
			VectorValue value = vecData.values()[i];
			this.nodeVoltages.put(value.name(), value.realdata());
		}
	}

	@Override
	public void reciveInitData(PlotDescription plotInfo) {}

}
