package de.m_marvin.industria.core.kinetics.engine.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import de.m_marvin.industria.core.kinetics.engine.ClientKineticPackageHandler;
import de.m_marvin.industria.core.kinetics.engine.KineticNetworkSpaceCapability.KineticComponent;
import de.m_marvin.industria.core.kinetics.engine.KineticNetwork;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.util.types.PowerNetState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the client that a networks needs to be updated with new node voltages
 */
public class SUpdateKineticNetworkPackage {
	
	private final Collection<KineticComponent> components;
	private final Map<KineticReference, Double> speedMap;
	private final double networkSpeed;
	private final double maxPower;
	private final double currentProduction;
	private final double currentConsumtion;
	private final PowerNetState state;
	
	public SUpdateKineticNetworkPackage(KineticNetwork network) {
		this.components = network.listComponents();
		this.speedMap = network.getSpeedMap();
		this.maxPower = network.getMaxPower();
		this.currentConsumtion = network.getCurrentConsumtion();
		this.currentProduction = network.getCurrentProduction();
		this.networkSpeed = network.getNetworkSpeed();
		this.state = network.getState();
	}

	public SUpdateKineticNetworkPackage(Collection<KineticComponent> components, KineticNetwork network) {
		this.components = network.listComponents().stream().filter(components::contains).toList();
		this.speedMap = network.getSpeedMap();
		this.maxPower = network.getMaxPower();
		this.currentConsumtion = network.getCurrentConsumtion();
		this.currentProduction = network.getCurrentProduction();
		this.networkSpeed = network.getNetworkSpeed();
		this.state = network.getState();
	}
	
	public SUpdateKineticNetworkPackage(Collection<KineticComponent> components, Map<KineticReference, Double> speedMap, double speed, double maxPower, double currentProduction, double currentConsumtion, PowerNetState state) {
		this.components = components;
		this.speedMap = speedMap;
		this.networkSpeed = speed;
		this.maxPower = maxPower;
		this.currentProduction = currentProduction;
		this.currentConsumtion = currentConsumtion;
		this.state = state;
	}
	
	public Collection<KineticComponent> getComponents() {
		return components;
	}
	
	public Map<KineticReference, Double> getSpeedMap() {
		return speedMap;
	}
	
	public double getMaxPower() {
		return maxPower;
	}
	
	public double getCurrentProduction() {
		return currentProduction;
	}
	
	public double getCurrentConsumtion() {
		return currentConsumtion;
	}
	
	public double getSpeed() {
		return networkSpeed;
	}
	
	public PowerNetState getState() {
		return state;
	}
	
	public static void encode(SUpdateKineticNetworkPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.components.size());
		for (KineticComponent component : msg.components) {
			CompoundTag componentTag = new CompoundTag();
			component.serializeNbt(componentTag);
			buff.writeNbt(componentTag);
		}
		buff.writeInt(msg.speedMap.size());
		for (var e : msg.speedMap.entrySet()) {
			buff.writeNbt(e.getKey().writeNbt());
			buff.writeDouble(e.getValue());
		}
		buff.writeDouble(msg.networkSpeed);
		buff.writeDouble(msg.maxPower);
		buff.writeDouble(msg.currentProduction);
		buff.writeDouble(msg.currentConsumtion);
		buff.writeEnum(msg.state);
	}
	
	public static SUpdateKineticNetworkPackage decode(FriendlyByteBuf buff) {
		int componentCount = buff.readInt();
		Set<KineticComponent> components = new HashSet<>();
		for (int i = 0; i < componentCount; i++) {
			CompoundTag componentTag = buff.readNbt();
			KineticComponent component = new KineticComponent(null, null, null);
			component.deserializeNbt(componentTag);
			components.add(component);
		}
		int entryCount = buff.readInt();
		Map<KineticReference, Double> speedMap = new HashMap<KineticReference, Double>();
		for (int i = 0; i < entryCount; i++) {
			KineticReference reference = KineticReference.readNbt(buff.readNbt());
			double speed = buff.readDouble();
			speedMap.put(reference, speed);
		}
		double speed = buff.readDouble();
		double maxPower = buff.readDouble();
		double currentProduction = buff.readDouble();
		double currentConsumtion= buff.readDouble();
		PowerNetState state = buff.readEnum(PowerNetState.class);
		return new SUpdateKineticNetworkPackage(components, speedMap, speed, maxPower, currentProduction, currentConsumtion, state);
	}
	
	public static void handle(SUpdateKineticNetworkPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientKineticPackageHandler.handleUpdateNetwork(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
