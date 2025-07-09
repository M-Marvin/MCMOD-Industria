package de.m_marvin.industria.core.electrics.engine.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
import de.m_marvin.industria.core.electrics.engine.ElectricHandlerCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.util.types.PowerNetState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the client that components need to be updated with new node voltages
 */
public class SUpdateElectricNetworkPackage {
	
	public final String dataList;
	public final Collection<ElectricComponent<?, Object, ?>> components;
	public final PowerNetState state;
	
	public SUpdateElectricNetworkPackage(ElectricNetwork network) {
		this.dataList = network.printDataList();
		this.components = network.listComponents();
		this.state = network.getState();;
	}
	
	public SUpdateElectricNetworkPackage(Collection<ElectricComponent<?, Object, ?>> components, String dataList, PowerNetState state) {
		this.dataList = dataList;
		this.components = components;
		this.state = state;
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
	
	public static void encode(SUpdateElectricNetworkPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.components.size());
		for (ElectricComponent<?, ?, ?> component : msg.components) {
			CompoundTag componentTag = new CompoundTag();
			component.serializeNbt(componentTag);
			buff.writeNbt(componentTag);
		}
		buff.writeUtf(msg.dataList);
		buff.writeEnum(msg.state);
	}
	
	public static SUpdateElectricNetworkPackage decode(FriendlyByteBuf buff) {
		int componentCount = buff.readInt();
		Set<ElectricComponent<?, Object, ?>> components = new HashSet<>();
		for (int i = 0; i < componentCount; i++) {
			CompoundTag componentTag = buff.readNbt();
//			ElectricComponent<?, ?, ?> component = ElectricComponent.deserializeNbt(componentTag);
//			components.add(component);
		}
		String dataList = buff.readUtf();
		PowerNetState state = buff.readEnum(PowerNetState.class);
		return new SUpdateElectricNetworkPackage(components, dataList, state);
	}
	
	public static void handle(SUpdateElectricNetworkPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientElectricPackageHandler.handleUpdateNetwork(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
