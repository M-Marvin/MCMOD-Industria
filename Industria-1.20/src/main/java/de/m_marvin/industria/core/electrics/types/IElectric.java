package de.m_marvin.industria.core.electrics.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public interface IElectric<I, P, T> {
	
	public default void updateNetwork(Level level, P position) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		handler.updateNetwork(position);
	}
	
	public default void onNetworkNotify(Level level, I instance, P position) {}
	
	public void neighborRewired(Level level, I instance, P position, Component<?, ?, ?> neighbor);
	public void plotCircuit(Level level, I instance, P position, ElectricNetwork circuit, Consumer<CircuitTemplate> plotter);
	public void serializeNBT(I instance, P position, CompoundTag nbt);
	public I deserializeNBTInstance(CompoundTag nbt);
	public P deserializeNBTPosition(CompoundTag nbt);
	
	public NodePos[] getConnections(Level level, P pos, I instance);
	public String[] getWireLanes(P pos, I instance, NodePos node);
	public void setWireLanes(P pos, I instance, NodePos node, String[] laneLabels);
	public boolean isWire();
	
	public static enum Type {
		
		BLOCK(() -> ForgeRegistries.BLOCKS),CONDUIT(Conduits.CONDUITS_REGISTRY::get);
		
		private Supplier<IForgeRegistry<?>> registry;
		
		private Type(Supplier<IForgeRegistry<?>> registry) {
			this.registry = registry;
		}
		
		@SuppressWarnings("unchecked")
		public IForgeRegistry<Object> getRegistry() {
			return (IForgeRegistry<Object>) this.registry.get();
		}
		
		public static Type getType(Object type) {
			if (type instanceof Block) return BLOCK;
			if (type instanceof Conduit) return CONDUIT;
			return BLOCK;
		}
		
	}
	
}