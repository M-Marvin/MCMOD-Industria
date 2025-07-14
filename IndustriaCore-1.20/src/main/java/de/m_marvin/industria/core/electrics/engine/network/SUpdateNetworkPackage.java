package de.m_marvin.industria.core.electrics.engine.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.util.types.PowerNetState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the client that components need to be updated with new node voltages
 */
public class SUpdateNetworkPackage {
	
	public final String dataList;
	public final Collection<ElectricComponent<?, Object, ?>> components;
	public final double maxPower;
	public final double currentConsumtion;
	public final double currentProduction;
	public final PowerNetState state;
	
	public SUpdateNetworkPackage(ElectricNetwork network) {
		this.dataList = network.printDataList();
		this.components = network.listComponents();
		this.state = network.getState();
		this.maxPower = network.getMaxPower();
		this.currentConsumtion = network.getCurrentConsumtion();
		this.currentProduction = network.getCurrentProduction();
	}
	
	public SUpdateNetworkPackage(Set<ElectricComponent<?, Object, ?>> components, String dataList, PowerNetState state, double maxPower, double currentProduction, double currentConsumtion) {
		this.dataList = dataList;
		this.components = components;
		this.state = state;
		this.maxPower = maxPower;
		this.currentProduction = currentProduction;
		this.currentConsumtion = currentConsumtion;
	}

	public Collection<ElectricComponent<?, Object, ?>> getComponents() {
		return components;
	}
	
	public String getDataList() {
		return dataList;
	}
	
	public PowerNetState getState() {
		return state;
	}

	public double getMaxPower() {
		return maxPower;
	}
	
	public double getCurrentConsumtion() {
		return currentConsumtion;
	}
	
	public double getCurrentProduction() {
		return currentProduction;
	}
	
	public static void encode(SUpdateNetworkPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.components.size());
		for (ElectricComponent<?, ?, ?> component : msg.components) {
			CompoundTag componentTag = new CompoundTag();
			component.serializeNbt(componentTag);
			buff.writeNbt(componentTag);
		}
		buff.writeUtf(msg.dataList);
		buff.writeEnum(msg.state);
		buff.writeDouble(msg.maxPower);
		buff.writeDouble(msg.currentProduction);
		buff.writeDouble(msg.currentConsumtion);
	}
	
	public static SUpdateNetworkPackage decode(FriendlyByteBuf buff) {
		int componentCount = buff.readInt();
		Set<ElectricComponent<?, Object, ?>> components = new HashSet<>();
		for (int i = 0; i < componentCount; i++) {
			CompoundTag componentTag = buff.readNbt();
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ElectricComponent<?, Object, ?> component = new ElectricComponent(null, null, null);
			component.deserializeNbt(componentTag);
			components.add(component);
		}
		String dataList = buff.readUtf();
		PowerNetState state = buff.readEnum(PowerNetState.class);
		double maxPower = buff.readDouble();
		double currentProduction = buff.readDouble();
		double currentConsumtion = buff.readDouble();
		return new SUpdateNetworkPackage(components, dataList, state, maxPower, currentProduction, currentConsumtion);
	}
	
	public static void handle(SUpdateNetworkPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientElectricPackageHandler.handleUpdateNetwork(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
