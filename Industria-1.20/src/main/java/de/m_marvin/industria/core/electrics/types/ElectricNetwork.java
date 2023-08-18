package de.m_marvin.industria.core.electrics.types;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.IElectric.ICircuitPlot;
import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.INGCallback;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

public class ElectricNetwork implements INGCallback {
	
	protected String title;
	protected Map<NodePos, Double> nodeVoltages = Maps.newHashMap();
	protected Set<Component<?, ?, ?>> components = new HashSet<>();
	protected long lastUpdated;
	protected int templateCounter;
	protected StringBuilder circuitBuilder;
	protected String netList;
	protected NativeNGLink nglink;
	
	public ElectricNetwork(String titleInfo) {
		this.title = titleInfo;
		this.nglink = new NativeNGLink();
	}
	
	public CompoundTag saveNBT() {
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
		return tag;
	}
	
	public void loadNBT(Level level, CompoundTag tag) {
		ListTag componentsTag = tag.getList("Components", ListTag.TAG_COMPOUND);
		componentsTag.stream().forEach((componentTag) -> {
			this.components.add(Component.deserializeNbt(level, (CompoundTag) componentTag));
		});
	}
	
	public Set<Component<?, ?, ?>> getComponents() {
		return components;
	}
	
	public void plotTemplate(Component<?, ?, ?> component, ICircuitPlot template) {
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
		this.lastUpdated = frame;
	}
	
	public void reset() {
		if (this.nglink.isNGSpiceAttached()) this.nglink.detachNGSpice();
		this.circuitBuilder = new StringBuilder();
		this.circuitBuilder.append(title + "\n");
		this.netList = null;
		this.nodeVoltages.clear();
		this.components.clear();
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
			
			System.out.println("TODO Start simulation loop!");
			// TODO
			
		}
	}
	
	public void terminate() {
		if (this.nglink.isInitialized()) {
			this.nglink.detachNGLink();
		}
	}

	@Override
	public void log(String s) {
		// TODO Debug print config
		IndustriaCore.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, s);
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
	public void reciveInitData(PlotDescription plotInfo) {
		// TODO Auto-generated method stub
		
	}
	
}
