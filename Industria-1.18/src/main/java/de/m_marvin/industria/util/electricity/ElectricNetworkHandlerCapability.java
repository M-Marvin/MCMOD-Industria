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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
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
	
	public static final float MAX_RESISTANCE = 100000000;
	
	public static int debug = 0;
	
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
	private HashMap<Object, Component<?, ?, ?>> pos2componentMap = new HashMap<Object, Component<?, ?, ?>>();
	private HashMap<ConnectionPoint, NodeInfo> node2stateMap = new HashMap<ConnectionPoint, NodeInfo>();
	private HashMap<ConnectionPoint, Set<Component<?, ?, ?>>> node2componentMap = new HashMap<ConnectionPoint, Set<Component<?, ?, ?>>>();
	private HashMap<ConnectionPoint, Float> voltageItterationCache = new HashMap<ConnectionPoint, Float>();
	private long frame;
 	
	@Override
	public ListTag serializeNBT() {
		ListTag nbt = new ListTag();
		for (Component<?, ?, ?> component : this.pos2componentMap.values()) {
			CompoundTag componentNbt = new CompoundTag();
			component.serializeNbt(componentNbt);
			nbt.add(componentNbt);
		}
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "Saved " + nbt.size() + " electric components");
		return nbt;
	}
	
	@Override
	public void deserializeNBT(ListTag nbt) {
		this.pos2componentMap.clear();
		this.node2stateMap.clear();
		this.node2componentMap.clear();
		this.voltageItterationCache.clear();
		
		for (int i = 0; i < nbt.size(); i++) {
			CompoundTag componentNbt = nbt.getCompound(i);
			Component<?, ?, ?> component = Component.deserializeNbt(componentNbt);
			if (component != null) this.addToNetwork(component);
		}
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "Loaded " + this.pos2componentMap.size() + "/" + nbt.size() + " electric components");
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
		if (!event.getWorld().isClientSide()) {
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
	
	public static record Component<I, P, T>(P pos, IElectric<I, P, T> type, I instance) {
		public void serializeNbt(CompoundTag nbt) {
			IElectric.Type componentType = IElectric.Type.getType(this.type);
			this.type.serializeNBT(instance, pos, nbt);
			nbt.putString("Type", this.type.getRegistryName().toString());
			nbt.putString("ComponentType", componentType.name().toLowerCase());
		}
		public static <I, P, T> Component<I, P, T> deserializeNbt(CompoundTag nbt) {
			IElectric.Type componentType = IElectric.Type.valueOf(nbt.getString("ComponentType").toUpperCase());
			ResourceLocation typeName = new ResourceLocation(nbt.getString("Type"));
			Object typeObject = componentType.getRegistry().getValue(typeName);
			if (typeObject instanceof IElectric) {
				@SuppressWarnings("unchecked")
				IElectric<I, P, T> type = (IElectric<I, P, T>) typeObject;
				I instance = type.deserializeNBTInstance(nbt);
				P position = type.deserializeNBTPosition(nbt);
				return new Component<I, P, T>(position, type, instance);
			}
			return null;
		}
		public float getGeneratedVoltage(ConnectionPoint node, float load) {
			return type.getGeneratedVoltage(instance, node, load);
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
	public static class NodeInfo { float voltage; float current; float resistance; float load; long frame; }
	
	public void tick() {
		
	}
	
	public <I, P, T> void removeComponent(P pos, I state) {
		if (this.pos2componentMap.containsKey(pos)) {
			Component<?, ?, ?> component = removeFromNetwork(pos);
			updateNetwork(component.getNodes(this.level)[0]);
		}
	}
	
	public <I, P, T> void addComponent(P pos, IElectric<I, P, T> type, I instance) {
		Component<?, ?, ?> component = this.pos2componentMap.get(pos);
		if (component != null) {
			if (component.type.equals(type) && component.instance.equals(instance)) {
				return;
			} else {
				removeFromNetwork(pos);
			}
		}
		Component<I, P, T> component2 = new Component<I, P, T>(pos, type, instance);
		addToNetwork(component2);
		updateNetwork(component2.getNodes(this.level)[0]);
	}
	
	public <I, P, T> void addToNetwork(Component<I, P, T> component) {
		this.pos2componentMap.put(component.pos, component);
		for (ConnectionPoint node : component.type().getConnections(this.level, component.pos, component.instance())) {
			Set<Component<?, ?, ?>> componentSet = this.node2componentMap.getOrDefault(node, new HashSet<Component<?, ?, ?>>());
			componentSet.add(component);
			this.node2componentMap.put(node, componentSet);
			this.node2stateMap.putIfAbsent(node, new NodeInfo());
		}
	}
	
	public <P> Component<?, ?, ?> removeFromNetwork(P pos) {
		Component<?, ?, ?> component = this.pos2componentMap.remove(pos);
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
		// TODO Automatic recalculating
	}
	
	// TODO Incomplete
	public void calculateNetwork(Object position) {
		
		Component<?, ?, ?> component = this.pos2componentMap.get(position);
		Set<Component<?, ?, ?>> generators = scannNetworkGenerators(component);
		
		
		System.out.println("Found " + generators.size() + " generators");
		
		node2stateMap.forEach((node, info) -> {
			info.voltage = 0;
			info.load = Float.MAX_VALUE;
		});
		voltageItterationCache.clear();
		
		System.out.println("TEST");
		
		float itterations = 1; // TODO itterations
		
		for (int i = 0; i < itterations; i++) {
			
			node2stateMap.forEach((node, info) -> {
				info.voltage = 0;
			});
			
			for (Component<?, ?, ?> generatorComponent : generators) {
				
				ConnectionPoint[] nodes = generatorComponent.getNodes(this.level);
				
				for (ConnectionPoint node : nodes) {
					
					if (generatorComponent.getGeneratedVoltage(node, Float.MAX_VALUE) <= 0) continue;
					
					calculateNetworkResistance(generatorComponent, node);
					
					calculateNetworkVoltages(generatorComponent, node, (float) (i / itterations));
					
				}
				
			}
			
			
			for (Component<?, ?, ?> generatorComponent : generators) {

				debug++;
				ConnectionPoint[] nodes = generatorComponent.getNodes(this.level);
				for (ConnectionPoint node : nodes) {
					
					if (generatorComponent.getGeneratedVoltage(node, Float.MAX_VALUE) <= 0) continue;
					
					calculateNetworkLoadResistance(generatorComponent, node, 0.5F); // TODO tolerance
					
				}
				
			}
			
			debug = 0;
		}

		this.node2stateMap.forEach((node, info) -> {
			
			this.level.setBlock(node.position.offset(0, 3, 0), Blocks.WARPED_SIGN.defaultBlockState(), 3);
			BlockEntity e =  this.level.getBlockEntity(node.position.offset(0, 3, 0));
			if (e instanceof SignBlockEntity) ((SignBlockEntity) e).setMessage(node.connectionId, new TextComponent(info.voltage + "V"));
			
			
		});
		
		for (Component<?, ?, ?> component2 : generators) {

			this.level.setBlock(((BlockPos) component2.pos).offset(0, 1, 0), Blocks.DARK_OAK_SIGN.defaultBlockState(), 3);
			BlockEntity e =  this.level.getBlockEntity(((BlockPos) component2.pos).offset(0, 1, 0));
			if (e instanceof SignBlockEntity) ((SignBlockEntity) e).setMessage(0, new TextComponent(this.node2stateMap.get(component2.getNodes(level)[0]).load + "R"));
			if (e instanceof SignBlockEntity) ((SignBlockEntity) e).setMessage(1, new TextComponent(this.node2stateMap.get(component2.getNodes(level)[1]).load + "R"));
			if (e instanceof SignBlockEntity) ((SignBlockEntity) e).setMessage(2, new TextComponent(this.node2stateMap.get(component2.getNodes(level)[2]).load + "R"));
		}
		
		System.out.println("Done");
//		
//		this.node2stateMap.forEach((node, info) -> {
//			BlockPos pos = node.position.offset(0, 3, 0);
//			this.level.setBlock(pos, Blocks.WARPED_SIGN.defaultBlockState(), 3);
//			BlockEntity e = this.level.getBlockEntity(pos);
//			if (e instanceof SignBlockEntity) ((SignBlockEntity) e).setMessage(0, new String)
//		})
		
		System.out.println("Resulting generator states:");
		generators.forEach((generator) -> {
			for (ConnectionPoint node : generator.getNodes(this.level)) {
				NodeInfo info = this.node2stateMap.get(node);
				float energy = Math.round((info.voltage / info.load) * 100) / 100F;

				System.out.println(generator.pos + " N " + node.offset);
				//System.out.println("Voltage " + info.voltage);
				System.out.println("Load " + info.load);
				//System.out.println("-> Energy " + energy);
				if (energy > 0) {
				}
			}
		});
		
	}
	
	/*
	 * Searches and collects all components that generate a voltage
	 */
	public Set<Component<?, ?, ?>> scannNetworkGenerators(Component<?, ?, ?> component) {
		newFrame();
		Set<Component<?, ?, ?>> generators = new HashSet<Component<?, ?, ?>>();
		scannNetworkGeneratorsP(component, generators);
		return generators;
	}
	protected void scannNetworkGeneratorsP(Component<?, ?, ?> currentComponent, Set<Component<?, ?, ?>> generators)  {
		
		ConnectionPoint[] nodes = currentComponent.getNodes(this.level);
		for (ConnectionPoint node : nodes) {
			
			NodeInfo info = this.node2stateMap.get(node);
			if (info.frame == this.frame) continue;
			info.frame = this.frame;
			info.voltage = 0; // Prepare for recalculating voltages
			
			if (currentComponent.getGeneratedVoltage(node, Float.MAX_VALUE) > 0) generators.add(currentComponent);
			
			this.node2componentMap.get(node).forEach((component) -> {
				
				if (!component.equals(currentComponent)) scannNetworkGeneratorsP(component, generators);
				
			});
			
		}
	}
	
	// FIXME Not working
	public void calculateNetworkLoadResistance(@Nullable Component<? , ?, ?> excludingComponent, ConnectionPoint node, float t) { // TODO wrong paramter name
		newFrame();
		float load = calculateNetworkLoadResistanceP(excludingComponent, node, t);
		//System.out.println("LOAD " + node.position + " > " + load);
		
	}
	private float calculateNetworkLoadResistanceP(@Nullable Component<? , ?, ?> excludingComponent, ConnectionPoint node, float t) {
		
		NodeInfo info = this.node2stateMap.get(node);
		
		if (info.frame == this.frame) return Float.MAX_VALUE;
		
		info.load = Float.MAX_VALUE;
		info.frame = this.frame;
		
		for (Component<?, ?, ?> component : this.node2componentMap.get(node)) {
			if (component.equals(excludingComponent)) continue;
			
			ConnectionPoint[] nodes = component.getNodes(this.level);
			for (ConnectionPoint node2 : nodes) {
				//if (node2.equals(node) && excludingComponent == component) continue;
				
				NodeInfo info2 = this.node2stateMap.get(node2);
				
//				if (info2.frame == this.frame) continue;
//				info2.frame = this.frame;
				
				if (node2.equals(node)) {
					float paralelResistance = component.getParalelResistance(node2);
					info.load = 1 / (1 / info.load + 1 / paralelResistance);
				} else {
					
					float serialResistance = component.getSerialResistance(node, node2);
					float paralelResistance = component.getParalelResistance(node2);
					
					float networkResistance = calculateNetworkLoadResistanceP(component, node2, t);
					float nodeResistance = 1 / (1 / networkResistance + 1 / paralelResistance);
					
					float expectedVoltage = nodeResistance <= MAX_RESISTANCE ? (info.voltage * nodeResistance) / (nodeResistance + serialResistance) : info.voltage;
					
					//System.out.println("XX" + serialResistance + " " + info2.voltage + " > " + expectedVoltage);
					
					if (info2.voltage - expectedVoltage > t) {
						
//						System.out.println(info2.resistance + " " + serialResistance);
						//System.out.println("XXIst " + info2.voltage + " " + node.position);
						//System.out.println("XXSolte " + expectedVoltage);
						//System.out.println(serialResistance);
						
						//this.level.setBlock(node2.position.offset(0, 4, 0), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
						
						return Float.MAX_VALUE;
					}
					
					level.setBlock(node2.position.above(4 + debug), Blocks.RED_WOOL.defaultBlockState(), 3);	
					
					//float serialResistance = component.getSerialResistance(node, node2);
					float pathResistance = nodeResistance + serialResistance;
					info.load = 1 / (1 / info.load + 1 / pathResistance);
				}
				
			}
		}
		
		return info.load;
		
	}
	
	/*
	 * Recalculates all wire and consumer resistances from the "sight" of the given component/node
	 */
	public void calculateNetworkResistance(@Nullable Component<?, ?, ?> component, ConnectionPoint node) {
		newFrame();
		calculateNetworkResistanceP(component, node);
	}
	private float calculateNetworkResistanceP(@Nullable Component<?, ?, ?> excludingComponent, ConnectionPoint node) {
		
		NodeInfo info = this.node2stateMap.get(node);
		
		if (info.frame == this.frame) return Float.MAX_VALUE;
		
		info.resistance = Float.MAX_VALUE;
		info.frame = this.frame;
		
		for (Component<?, ?, ?> component : this.node2componentMap.get(node)) {
			if (component.equals(excludingComponent)) continue;
			
			ConnectionPoint[] nodes = component.getNodes(this.level);
			for (ConnectionPoint node2 : nodes) {
				
				NodeInfo info2 = this.node2stateMap.get(node2);
				
				if (node2.equals(node)) {
					float paralelResistance = component.getParalelResistance(node2);
					info.resistance = 1 / (1 / info.resistance + 1 / paralelResistance);
				} else {
					float networkResistance = calculateNetworkResistanceP(component, node2);
					float paralelResistance = component.getParalelResistance(node2);
					float nodeResistance = 1 / (1 / networkResistance + 1 / paralelResistance);
					
					info2.resistance = nodeResistance;
					
					float serialResistance = component.getSerialResistance(node, node2);
					float pathResistance = nodeResistance + serialResistance;
					info.resistance = 1 / (1 / info.resistance + 1 / pathResistance);
				}
				
			}
		}
		
		return info.resistance;
		
	}
	
	/*
	 * Recalculates all voltages caused by the given generator component
	 */
	public void calculateNetworkVoltages(Component<?, ?, ?> component, ConnectionPoint node, float itterationProgress) {
		newFrame();
		
		float f = 1 - itterationProgress;
		
		// Set generated voltage from generator
		NodeInfo info = this.node2stateMap.get(node);
		float generatedVoltage = component.getGeneratedVoltage(node, info.load);
		float lastItterationVoltage = voltageItterationCache.getOrDefault(node, generatedVoltage);
		generatedVoltage = lastItterationVoltage + (generatedVoltage - lastItterationVoltage) * 1; // DEBUG
		
		if (generatedVoltage < info.voltage) return;
		info.voltage = generatedVoltage;
		
		calculateNetworkVoltagesP(component, node, 1 - itterationProgress);
	}
	private void calculateNetworkVoltagesP(Component<?, ?, ?> excludingComponent, ConnectionPoint node, float voltageRisingSpeed) {
				
		NodeInfo info = this.node2stateMap.get(node);
		
		if (info.frame == this.frame) return;	
		info.frame = this.frame;
		
		for (Component<?, ?, ?> component : this.node2componentMap.get(node)) {
			if (component.equals(excludingComponent)) continue;
			
			ConnectionPoint[] nodes = component.getNodes(this.level);
			for (ConnectionPoint node2 : nodes) {
				if (node2.equals(node)) continue;
				
				NodeInfo info2 = this.node2stateMap.get(node2);
				
				float serialResistance = component.getSerialResistance(node, node2);
				float paralelResistance = info2.resistance;
				float voltage = info.voltage;
				float transmittedVoltage = paralelResistance <= MAX_RESISTANCE ? (voltage * paralelResistance) / (serialResistance + paralelResistance) : voltage;
				
				if (transmittedVoltage >= info2.voltage) {
					
					float old = info2.voltage;
					info2.voltage = transmittedVoltage;
					
					calculateNetworkVoltagesP(component, node2, voltageRisingSpeed);
				}
				
			}
			
		}
		
	}
	
	private void newFrame() {
		long newFrame = this.frame;
		while (newFrame == this.frame) newFrame = this.level.random.nextLong();
		this.frame = newFrame;
	}
	
}
