package de.m_marvin.industria.core.electrics.types;

import java.util.HashSet;
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
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class ElectricNetwork implements INGCallback {
	
	protected String title;
	protected Map<NodePos, Double> nodeVoltages = Maps.newHashMap();
	protected Set<Component<?, ?, ?>> components = new HashSet<>();
	protected long lastUpdated;
	protected int templateCounter;
	protected StringBuilder circuitBuilder;
	protected String netList = "";
	protected NativeNGLink nglink;
	protected long timeLine;
	protected long simulationStart;
	
	public ElectricNetwork(String titleInfo) {
		this.title = titleInfo;
		this.nglink = new NativeNGLink();
	}
	
	public CompoundTag saveNBT(ElectricNetworkHandlerCapability handler) {
		CompoundTag tag = new CompoundTag();
		ListTag componentsTag = new ListTag();
		for (Component<?, ?, ?> component : this.components) {
			try {
				CompoundTag compTag = new CompoundTag();
				component.serializeNbt(compTag);
				componentsTag.add(compTag);
			} catch (Exception e) {
				System.err.println("Failed to serialize electric component at " + component.pos() + "!");
				e.printStackTrace();
			}
		}
		tag.put("Components", componentsTag);
		if (!this.isEmpty() && !this.isPlotEmpty()) {
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
		this.nodeVoltages.clear();
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
		this.lastUpdated = frame;
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

	public boolean updatedInFrame(long frame) {
		return this.lastUpdated == frame;
	}

	public void startExecution() {
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
			this.timeLine = 0;
			this.simulationStart = System.currentTimeMillis();
		}
	}
	
	public void terminateExecution() {
		if (this.nglink.isInitialized()) {
			this.nglink.detachNGLink();
		}
	}
	
	public boolean isExecutionActive() {
		return this.nglink.isNGSpiceAttached();
	}
	
	public NativeNGLink getNglink() {
		return nglink;
	}
	
	public long getTimeLine() {
		return timeLine;
	}
	
	public long getSimulationStart() {
		return simulationStart;
	}
	
	public void setTimeLine(long timeLine) {
		this.timeLine = timeLine;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reciveInitData(PlotDescription plotInfo) {}
	
	
	
	
	
	
	
	
	public Map<NodePos, Double> getNodeVoltages() {
		return this.nodeVoltages;
	}
	
	public double getVoltage(NodePos node) {
		return this.nodeVoltages.getOrDefault(node, 0D);
	}

}
