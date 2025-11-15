package de.m_marvin.industria.core.electrics.engine.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.util.types.PowerNetState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the client that components need to be updated with new node voltages
 */
public class SUpdateElectricNetworkPackage {
	
	private final String dataList;
	private final Collection<ElectricReference> components;
	private final double maxPower;
	private final double currentConsumtion;
	private final double currentProduction;
	private final PowerNetState state;
	
	public SUpdateElectricNetworkPackage(ElectricNetwork network) {
		this.dataList = network.printDataList();
		this.components = network.listComponents().stream().map(ElectricComponent::reference).toList();
		this.state = network.getState();
		this.maxPower = network.getMaxPower();
		this.currentConsumtion = network.getCurrentConsumtion();
		this.currentProduction = network.getCurrentProduction();
	}

	public SUpdateElectricNetworkPackage(Collection<ElectricComponent<?, ?>> components, ElectricNetwork network) {
		this.components = network.listComponents().stream().filter(components::contains).map(ElectricComponent::reference).toList();
		this.dataList = network.printDataList();
		this.state = network.getState();
		this.maxPower = network.getMaxPower();
		this.currentConsumtion = network.getCurrentConsumtion();
		this.currentProduction = network.getCurrentProduction();
	}
	
	public SUpdateElectricNetworkPackage(Set<ElectricReference> components, String dataList, PowerNetState state, double maxPower, double currentProduction, double currentConsumtion) {
		this.dataList = dataList;
		this.components = components;
		this.state = state;
		this.maxPower = maxPower;
		this.currentProduction = currentProduction;
		this.currentConsumtion = currentConsumtion;
	}

	public Collection<ElectricReference> getComponents() {
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
	
	public static void encode(SUpdateElectricNetworkPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.components.size());
		for (ElectricReference component : msg.components)
			component.writeBuff(buff);
		buff.writeUtf(msg.dataList);
		buff.writeEnum(msg.state);
		buff.writeDouble(msg.maxPower);
		buff.writeDouble(msg.currentProduction);
		buff.writeDouble(msg.currentConsumtion);
	}
	
	public static SUpdateElectricNetworkPackage decode(FriendlyByteBuf buff) {
		int componentCount = buff.readInt();
		Set<ElectricReference> components = new HashSet<>();
		for (int i = 0; i < componentCount; i++)
			components.add(ElectricReference.readBuff(buff));
		String dataList = buff.readUtf();
		PowerNetState state = buff.readEnum(PowerNetState.class);
		double maxPower = buff.readDouble();
		double currentProduction = buff.readDouble();
		double currentConsumtion = buff.readDouble();
		return new SUpdateElectricNetworkPackage(components, dataList, state, maxPower, currentProduction, currentConsumtion);
	}
	
	public static void handle(SUpdateElectricNetworkPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientElectricPackageHandler.handleUpdateNetwork(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
