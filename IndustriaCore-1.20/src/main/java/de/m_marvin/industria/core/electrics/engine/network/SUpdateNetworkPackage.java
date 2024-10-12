package de.m_marvin.industria.core.electrics.engine.network;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the client that a networks needs to be updated
 */
public class SUpdateNetworkPackage {
	
	public final String dataList;
	public final Set<Component<?, ?, ?>> components;
	
	public SUpdateNetworkPackage(Set<Component<?, ?, ?>> components, String dataList) {
		this.dataList = dataList;
		this.components = components;
	}
	
	public Set<Component<?, ?, ?>> getComponents() {
		return components;
	}
	
	public String getDataList() {
		return dataList;
	}
	
	public static void encode(SUpdateNetworkPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.components.size());
		for (Component<?, ?, ?> component : msg.components) {
			CompoundTag componentTag = new CompoundTag();
			component.serializeNbt(componentTag);
			buff.writeNbt(componentTag);
		}
		buff.writeUtf(msg.dataList);
	}
	
	public static SUpdateNetworkPackage decode(FriendlyByteBuf buff) {
		int componentCount = buff.readInt();
		Set<Component<?, ?, ?>> components = new HashSet<>();
		for (int i = 0; i < componentCount; i++) {
			CompoundTag componentTag = buff.readNbt();
			Component<?, ?, ?> component = Component.deserializeNbt(componentTag);
			components.add(component);
		}
		String dataList = buff.readUtf();
		return new SUpdateNetworkPackage(components, dataList);
	}
	
	public static void handle(SUpdateNetworkPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientElectricPackageHandler.handleUpdateNetwork(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
