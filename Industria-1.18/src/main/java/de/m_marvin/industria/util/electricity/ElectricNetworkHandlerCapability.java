package de.m_marvin.industria.util.electricity;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.block.IElectricConnector;
import de.m_marvin.industria.util.conduit.ConduitEvent;
import de.m_marvin.industria.util.conduit.ConduitEvent.ConduitPlaceEvent;
import de.m_marvin.industria.util.conduit.IElectricConduit;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.nglink.NativeExtractor;
import de.m_marvin.nglink.NativeNGLink;
import de.m_marvin.nglink.NativeNGLink.PlotDescription;
import de.m_marvin.nglink.NativeNGLink.VectorValue;
import de.m_marvin.nglink.NativeNGLink.VectorValuesAll;
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
	private NativeNGLink nglink;
	private HashMap<Object, Component<?, ?, ?>> pos2componentMap = new HashMap<Object, Component<?, ?, ?>>();
	private HashMap<ConnectionPoint, Set<Component<?, ?, ?>>> node2componentMap = new HashMap<ConnectionPoint, Set<Component<?, ?, ?>>>();
	
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
		this.node2componentMap.clear();
		
		for (int i = 0; i < nbt.size(); i++) {
			CompoundTag componentNbt = nbt.getCompound(i);
			Component<?, ?, ?> component = Component.deserializeNbt(componentNbt);
			if (component != null) this.addToNetwork(component);
		}
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "Loaded " + this.pos2componentMap.size() + "/" + nbt.size() + " electric components");
	}
	
	public ElectricNetworkHandlerCapability(Level level) {
		this.level = level;
		if (!NativeNGLink.loadedSuccessfully()) throw new IllegalStateException("Failed to load natives for nglink in ElectricityNetworkHandler!");
		this.nglink = new NativeNGLink();
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
		public void plotCircuit(Level level, CircuitConfiguration circuit) {
			type.plotCircuit(level, instance, pos, circuit);
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
		}
	}
	
	public <I, P, T> Component<I, P, T> removeFromNetwork(P pos) {
		@SuppressWarnings("unchecked")
		Component<I, P, T> component = (Component<I, P, T>) this.pos2componentMap.remove(pos);
		for (ConnectionPoint node : component.type().getConnections(this.level, component.pos, component.instance())) {
			Set<Component<?, ?, ?>> componentSet = this.node2componentMap.getOrDefault(node, new HashSet<Component<?, ?, ?>>());
			componentSet.remove(component);
			this.node2componentMap.put(node, componentSet);
		}
		return component;
	}
	
	public <P> void updateNetwork(P position) {
		Component<?, ?, ?> component = this.pos2componentMap.get(position);
		if (component != null) {
			setCallbacks();
			CircuitConfiguration circuit = buildCircuit(component);
			
			try {
				
				Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "Circuit: \n" + circuit.toString());
				nglink.initNGSpice(NativeExtractor.findNative("ngspice"));
				nglink.loadCircuit(circuit.toString());
				nglink.execCommand("tran 1 1");
				nglink.detachNGSpice();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void setCallbacks() {

		if (!nglink.isInitialized()) {
			this.nglink.initNGLink(new NativeNGLink.NGCallback() {
				
				@Override
				public void reciveVecData(VectorValuesAll vecData, int vectorCount) {
					
					System.out.println("RECIVED DATA " + vecData.count());
					for (VectorValue value : vecData.values()) {
						System.out.println("VECTOR " + value.name() + " = " + value.realdata());
					}
					
				}
				
				@Override
				public void reciveInitData(PlotDescription plotInfo) {}
				
				@Override
				public void log(String s) {
					Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "JNGLINK: " + s);
				}
				
				@Override
				public void detacheNGSpice() {
					Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "JNGLINK: Detaching spice!");
					nglink.detachNGSpice();
				}
			});
		}
		
	}
	
	private CircuitConfiguration buildCircuit(Component<?, ?, ?> component) {
		CircuitConfiguration circuit = new CircuitConfiguration("recalc-circuit");
		Set<Component<?, ?, ?>> components = new HashSet<Component<?, ?, ?>>();
		buildCircuit0(component, null, components, circuit);
		circuit.complete();
		return circuit;
	}
	private void buildCircuit0(Component<?, ?, ?> component, ConnectionPoint node, Set<Component<?, ?, ?>> components, CircuitConfiguration circuit) {
		
		if (components.contains(component)) return;
		components.add(component);
		component.plotCircuit(level, circuit);
		
		for (ConnectionPoint node2 : component.getNodes(level)) {
			if (node2.equals(node)) continue; 
			for (Component<?, ?, ?> component2 : this.node2componentMap.get(node2)) {
				buildCircuit0(component2, node2, components, circuit);
			}
		}
		
	}
	
}
