package de.m_marvin.industria.util.electricity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.block.IElectricConnector;
import de.m_marvin.industria.util.conduit.ConduitEvent;
import de.m_marvin.industria.util.conduit.ConduitEvent.ConduitPlaceEvent;
import de.m_marvin.industria.util.conduit.IElectricConduit;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ElectricNetworkHandlerCapability implements ICapabilitySerializable<ListTag> {
	
	/* Capability handling */
	
	private LazyOptional<ElectricNetworkHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private Level level;
	private HashMap<Object, Component<?, ?>> pos2componentMap = new HashMap<Object, Component<?, ?>>();
	private HashMap<ConnectionPoint, NodeInfo> node2stateMap = new HashMap<ConnectionPoint, NodeInfo>();
	private HashMap<ConnectionPoint, Set<Component<?, ?>>> node2componentMap = new HashMap<ConnectionPoint, Set<Component<?, ?>>>();
	private long frame;
 	
	@Override
	public ListTag serializeNBT() {
		ListTag nbt = new ListTag();
		
		return nbt;
	}

	@Override
	public void deserializeNBT(ListTag nbt) {
		
	}
	
	public ElectricNetworkHandlerCapability(Level level) {
		this.level = level;
	}
	
	/* Event handling */
	
	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		ServerLevel level = (ServerLevel) event.world;
		LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		if (networkHandler.isPresent()) {
			networkHandler.resolve().get().tick();
		}
	}
	
	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void onClientWorldTick(ClientTickEvent event) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null) {
			LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
			if (networkHandler.isPresent()) {
				networkHandler.resolve().get().tick();
			}
		}
	}
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		ServerLevel level = (ServerLevel) event.getWorld();
		LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		if (networkHandler.isPresent()) {
			if (event.getState().getBlock() instanceof IElectricConnector) {
				IElectricConnector block = (IElectricConnector) event.getState().getBlock();
				networkHandler.resolve().get().addComponent(event.getPos(), block, event.getState());
			} else {
				networkHandler.resolve().get().removeComponent(event.getPos(), event.getState());
			}
		}
	}
	
	@SubscribeEvent
	public static void onConduitStateChange(ConduitEvent event) {
		if (!event.getLevel().isClientSide()) {
			ServerLevel level = (ServerLevel) event.getLevel();
			LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
			if (networkHandler.isPresent()) {
				if (event.getConduitState().getConduit() instanceof IElectricConduit && event instanceof ConduitPlaceEvent) {
					IElectricConduit conduit = (IElectricConduit) event.getConduitState().getConduit();
					networkHandler.resolve().get().addComponent(event.getPosition(), conduit, event.getConduitState());
				} else {
					networkHandler.resolve().get().removeComponent(event.getPosition(), event.getConduitState());
				}
			}
		}
	}
	
	/* ElectricNetwork handling */
	
	public static record Component<I, P>(P pos, IElectric<I, P> type, I instance) {
		public float getGeneratedVoltage(ConnectionPoint node) {
			return type.getGeneratedVoltage(instance, node);
		}
		public float getSerialResistance(ConnectionPoint node1, ConnectionPoint node2) {
			return type.getSerialResistance(instance, node1, node2);
		}
		public float getParalelResistance(ConnectionPoint node) {
			return type.getParalelResistance(instance, node);
		}
		public ConnectionPoint[] getNodes(Level level) {
			return type.getConnections(level, pos, instance);
		}
	}
	public static class NodeInfo { float voltage; float current; float resistance; long frame; }
	
	public void tick() {
		
	}
	
	public <I, P> void removeComponent(P pos, I state) {
		if (this.pos2componentMap.containsKey(pos)) {
			Component<?, ?> component = removeFromNetwork(pos);
			updateNetwork(component.getNodes(this.level)[0]);
		}
	}
	
	public <I, P> void addComponent(P pos, IElectric<I, P> type, I instance) {
		Component<?, ?> component = this.pos2componentMap.get(pos);
		if (component != null) {
			if (component.type.equals(type) && component.instance.equals(instance)) {
				return;
			} else {
				removeFromNetwork(pos);
			}
		}
		Component<I, P> component2 = new Component<I, P>(pos, type, instance);
		addToNetwork(pos, component2);
		updateNetwork(component2.getNodes(this.level)[0]);
	}
	
	public <I, P> void addToNetwork(P pos, Component<I, P> component) {
		this.pos2componentMap.put(pos, component);
		for (ConnectionPoint node : component.type().getConnections(this.level, pos, component.instance())) {
			Set<Component<?, ?>> componentSet = this.node2componentMap.getOrDefault(node, new HashSet<Component<?, ?>>());
			componentSet.add(component);
			this.node2componentMap.put(node, componentSet);
			this.node2stateMap.putIfAbsent(node, new NodeInfo());
		}
	}
	
	public <P> Component<?, ?> removeFromNetwork(P pos) {
		Component<?, ?> component = this.pos2componentMap.remove(pos);
		if (component != null) {
			Set<ConnectionPoint> unusedNodes = new HashSet<ConnectionPoint>();
			this.node2componentMap.forEach((node, componentSet) -> {
				if (componentSet.remove(component) && componentSet.isEmpty()) unusedNodes.add(node);
			});
			unusedNodes.forEach(this.node2stateMap::remove);
		}
		return component;
	}
	
	public void updateNetwork(ConnectionPoint node) {

		//updateNodeTree(null, node, 0);
	}
	
	private void updateNodeTree(@Nullable Component<?, ?> lastComponent, ConnectionPoint node, float lastVoltage) {
		
//		NodeInfo info = this.node2stateMap.get(node);
//		if (info == null || (info.voltage >= lastVoltage && info.frame == this.frame)) return;
//		
//		info.frame = this.frame;
//		info.voltage = lastVoltage;
//		for (Component<?, ?> component : this.node2componentMap.get(node)) {
//			if (component.equals(lastComponent)) continue;
//			
//			ConnectionPoint[] nodes = component.getNodes(level);
//			for (ConnectionPoint node2 : nodes) {
//				if (node2.equals(node)) continue;	
//				 
//				float serialResistance = component.getSerialResistance(node, node2);
//				float paralelResistance = getParalelResistance(node2);
//				float voltageTransfered = paralelResistance * lastVoltage / (paralelResistance + serialResistance);
//				float voltageGenerated = getGeneratedVoltage(node2);
//				float nodeVoltage = Math.max(voltageTransfered, voltageGenerated);
//				
//				updateNodeTree(component, node2, nodeVoltage);
//			}
//		}
		
		// TODO Updates only voltage
		
	}
	
	public void calculateNetwork(Object position) {
		
		Component<?, ?> component = this.pos2componentMap.get(position);
		Set<Component<?, ?>> generators = scannNetworkGenerators(component);
		
		System.out.println("Found " + generators.size() + " generators");
		
		generators.forEach((generatorComponent) -> {
			
			ConnectionPoint[] nodes = generatorComponent.getNodes(this.level);
			for (ConnectionPoint node : nodes) {
				
				if (generatorComponent.getGeneratedVoltage(node) <= 0) continue;
				
				calculateNetworkResistance(generatorComponent, node);
				calculateNetworkVoltages(generatorComponent, node);
				
			}
			
		});
		
		System.out.println("Done");
		
	}
	
	public Set<Component<?, ?>> scannNetworkGenerators(Component<?, ?> component) {
		newFrame();
		Set<Component<?, ?>> generators = new HashSet<Component<?, ?>>();
		scannNetworkGeneratorsP(component, generators);
		return generators;
	}
	protected void scannNetworkGeneratorsP(Component<?, ?> currentComponent, Set<Component<?, ?>> generators)  {
		
		ConnectionPoint[] nodes = currentComponent.getNodes(this.level);
		for (ConnectionPoint node : nodes) {
			
			NodeInfo info = this.node2stateMap.get(node);
			if (info.frame == this.frame) continue;
			info.frame = this.frame;
			info.voltage = 0; // Prepare for recalculating voltages
			
			if (currentComponent.getGeneratedVoltage(node) > 0) generators.add(currentComponent);
			
			this.node2componentMap.get(node).forEach((component) -> {
				
				if (!component.equals(currentComponent)) scannNetworkGeneratorsP(component, generators);
				
			});
			
		}
	}
	
	public void calculateNetworkResistance(@Nullable Component<? , ?> component, ConnectionPoint node) {
		newFrame();
		calculateNetworkResistanceP(component, node);
	}
	private float calculateNetworkResistanceP(@Nullable Component<?, ?> excludingComponent, ConnectionPoint node) {
		
		NodeInfo info = this.node2stateMap.get(node);
		
		if (info.frame == this.frame) return Float.MAX_VALUE;
		
		info.resistance = Float.MAX_VALUE;
		info.frame = this.frame;
		
		for (Component<?, ?> component : this.node2componentMap.get(node)) {
			if (component.equals(excludingComponent)) continue;
			
			ConnectionPoint[] nodes = component.getNodes(this.level);
			for (ConnectionPoint node2 : nodes) {
				if (node2.equals(node) && excludingComponent != null) continue;
				
				float networkResistance = calculateNetworkResistanceP(component, node2);
				float paralelResistance = getParalelResistance(node2);
				float serialResistance = component.getSerialResistance(node, node2);
				float pathResistance = 1 / (1 / networkResistance + 1 / paralelResistance) + serialResistance;
				info.resistance = 1 / (1 / info.resistance + 1 / pathResistance);
				
			}
		}
		
		return info.resistance;
		
	}
	
	public void calculateNetworkVoltages(Component<?, ?> component, ConnectionPoint node) {
		newFrame();
		this.node2stateMap.get(node).voltage = component.getGeneratedVoltage(node);
		calculateNetworkVoltagesP(component, node);
	}
	private void calculateNetworkVoltagesP(Component<?, ?> excludingComponent, ConnectionPoint node) {
		
		NodeInfo info = this.node2stateMap.get(node);
		
		if (info.frame == this.frame) return;
		
		info.frame = this.frame;
		
		for (Component<?, ?> component : this.node2componentMap.get(node)) {
			if (component.equals(excludingComponent)) continue;
			
			ConnectionPoint[] nodes = component.getNodes(this.level);
			for (ConnectionPoint node2 : nodes) {
				if (node2.equals(node) && excludingComponent != null) continue;
				
				float serialResistance = component.getSerialResistance(node, node2);
				float paralelResistance = this.node2stateMap.get(node2).resistance;
				float voltage = this.node2stateMap.get(node).voltage;
				
				float transmittedVoltage = (voltage * paralelResistance) / (serialResistance + paralelResistance);
				float generatedVoltage = getGeneratedVoltage(node2);
				
				if (transmittedVoltage >= generatedVoltage) {
					info.voltage = transmittedVoltage;
					calculateNetworkVoltagesP(component, node2);
				}
				
			}
			
		}
		
	}
	
	private float getParalelResistance(ConnectionPoint node) {
		float resistance = Float.MAX_VALUE;
		for (Component<?, ?> component : this.node2componentMap.get(node)) {
			float r = component.getParalelResistance(node);
			resistance = 1 / (1 / resistance + 1 / r);
		}
		return resistance;
	}
	
	private float getGeneratedVoltage(ConnectionPoint node) {
		float voltage = 0;
		for (Component<?, ?> component : this.node2componentMap.get(node)) {
			voltage += component.getGeneratedVoltage(node);
		}
		return voltage;
	}
	
	private void newFrame() {
		long newFrame = this.frame;
		while (newFrame == this.frame) newFrame = this.level.random.nextLong();
		this.frame = newFrame;
	}
	
}
