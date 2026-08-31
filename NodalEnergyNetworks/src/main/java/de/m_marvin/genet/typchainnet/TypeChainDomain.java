package de.m_marvin.genet.typchainnet;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.genet.nodal.Device;
import de.m_marvin.genet.nodal.GridSpace;
import net.minecraft.resources.ResourceLocation;

public class TypeChainDomain extends GridSpace<TypeChain, TypeChain> {
	
	public ResourceLocation getDomainName() {
		return NodeNet.DOMAINS_REGISTRY.get().getKey(this);
	}
	
	public class DomainIntegrator {
		
		public Device<TypeChain, TypeChain> insertDevice(Device<TypeChain, TypeChain> device) {
			try {
				return TypeChainDomain.this.putElement(device);
			} catch (Exception e) {
				NodeNet.LOGGER.warn("exception while inserting device into network domain: %s in %s", device.reference(), TypeChainDomain.this.getDomainName(), e);
				return null;
			}
		}
		
		public Device<TypeChain, TypeChain> removeDevice(TypeChain deviceName) {
			try {
				return TypeChainDomain.this.removeElement(deviceName);
			} catch (Exception e) {
				NodeNet.LOGGER.warn("exception while removing device from network domain: %s in %s", deviceName, TypeChainDomain.this.getDomainName(), e);
				return null;
			}
		}
		
		public Device<TypeChain, TypeChain> getDevice(TypeChain deviceName) {
			try {
				return TypeChainDomain.this.getElementInstance(deviceName);
			} catch (Exception e) {
				NodeNet.LOGGER.warn("exception while querying device from network domain: %s in %s", deviceName, TypeChainDomain.this.getDomainName(), e);
				return null;
			}
		}
		
	}
	
}
