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
 * Tells the client about the current state of electric networks on the server to synchronize them
 */
public class SUpdateElectricNetworkPackage {
	
	private final Collection<ElectricReference> components;
	private final PowerNetState state;
	
	public SUpdateElectricNetworkPackage(ElectricNetwork network) {
		this.components = network.listComponents().stream().map(ElectricComponent::reference).toList();
		this.state = network.getState();
	}

	public SUpdateElectricNetworkPackage(Collection<ElectricComponent<?, ?>> components, ElectricNetwork network) {
		this.components = network.listComponents().stream().filter(components::contains).map(ElectricComponent::reference).toList();
		this.state = network.getState();
	}
	
	public SUpdateElectricNetworkPackage(Set<ElectricReference> components, PowerNetState state) {
		this.components = components;
		this.state = state;
	}

	public Collection<ElectricReference> getComponents() {
		return components;
	}
	
	public PowerNetState getState() {
		return state;
	}

	public static void encode(SUpdateElectricNetworkPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.components.size());
		for (ElectricReference component : msg.components)
			component.writeBuff(buff);
		buff.writeEnum(msg.state);
	}
	
	public static SUpdateElectricNetworkPackage decode(FriendlyByteBuf buff) {
		int componentCount = buff.readInt();
		Set<ElectricReference> components = new HashSet<>();
		for (int i = 0; i < componentCount; i++)
			components.add(ElectricReference.readBuff(buff));
		PowerNetState state = buff.readEnum(PowerNetState.class);
		return new SUpdateElectricNetworkPackage(components, state);
	}
	
	public static void handle(SUpdateElectricNetworkPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientElectricPackageHandler.handleUpdateNetwork(msg, ctx.get());
			ctx.get().setPacketHandled(true);
		});
		
	}
	
}
