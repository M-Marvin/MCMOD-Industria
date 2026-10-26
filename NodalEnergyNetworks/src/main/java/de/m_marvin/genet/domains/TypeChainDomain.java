package de.m_marvin.genet.domains;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.genet.network.nodal.Circuit;
import de.m_marvin.genet.network.nodal.CircuitSpace;
import de.m_marvin.genet.network.nodal.Device;
import de.m_marvin.genet.network.nodal.Grid;
import de.m_marvin.genet.network.nodal.GridSpace;
import de.m_marvin.genet.network.nodal.SimulationEngine;
import net.minecraft.resources.ResourceLocation;

public class TypeChainDomain extends GridSpace<TypeChain, TypeChain> {
	
	private SimulationEngine<TypeChain, TypeChain> simulator;
	
	public ResourceLocation getDomainName() {
		return NodeNet.DOMAINS_REGISTRY.get().getKey(this);
	}
	
	public void setSimulator(SimulationEngine<TypeChain, TypeChain> simulator) {
		this.simulator = simulator;
	}
	
	public void resetDomain() {
		this.simulator = null;
		resetNetworkSpace();
	}
	
	public SimulationEngine<TypeChain, TypeChain> getSimulator() {
		return simulator;
	}
	
	public void tick() {
		finalizeQueuedChanges();
		
		if (this.simulator != null)
			getNetworks()
				.map(Grid::getCircuits)
				.flatMap(CircuitSpace::getNetworks)
				.map(Circuit::getNodal)
				.forEach(network -> this.simulator.submit(network, 20));
	}
	
	public class DomainIntegrator {
		
		public Device<TypeChain, TypeChain> insertDevice(Device<TypeChain, TypeChain> device) {
			try {
				TypeChainDomain.this.insertElements(device);
				return null; // TODO probably remove return value
			} catch (Exception e) {
				NodeNet.LOGGER.warn("exception while inserting device into network domain: {} in {}", device.name(), TypeChainDomain.this.getDomainName(), e);
				return null;
			}
		}
		
		public Device<TypeChain, TypeChain> removeDevice(TypeChain deviceName) {
			try {
				TypeChainDomain.this.removeElements(deviceName);
				return null; // TODO probably remove return value
			} catch (Exception e) {
				NodeNet.LOGGER.warn("exception while removing device from network domain: {} in {}", deviceName, TypeChainDomain.this.getDomainName(), e);
				return null;
			}
		}
		
		public Device<TypeChain, TypeChain> getDevice(TypeChain deviceName) {
			try {
				return TypeChainDomain.this.getElementInstance(deviceName);
			} catch (Exception e) {
				NodeNet.LOGGER.warn("exception while querying device from network domain: {} in {}", deviceName, TypeChainDomain.this.getDomainName(), e);
				return null;
			}
		}
		
	}
	
}
