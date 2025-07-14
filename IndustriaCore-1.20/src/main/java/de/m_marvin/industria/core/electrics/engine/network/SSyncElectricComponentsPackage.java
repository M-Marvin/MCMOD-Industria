package de.m_marvin.industria.core.electrics.engine.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * Tells the client about the creation or removal of components
 */
public class SSyncElectricComponentsPackage {
	
	public final ChunkPos chunkPos;
	public final Set<ElectricComponent<?, Object, ?>> components;
	public final SyncRequestType request;
	
	public SSyncElectricComponentsPackage(Collection<ElectricComponent<?, Object, ?>> components, ChunkPos targetChunk, SyncRequestType request) {
		this.chunkPos = targetChunk;
		this.components = new HashSet<ElectricComponent<?,Object,?>>();
		this.components.addAll(components);
		this.request = request;
	}
	
	public SSyncElectricComponentsPackage(ElectricComponent<?, Object, ?> component, ChunkPos targetChunk, SyncRequestType request) {
		this.chunkPos = targetChunk;
		this.components = new HashSet<>();
		this.components.add(component);
		this.request = request;
	}

	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public Collection<ElectricComponent<?, Object, ?>> getComponents() {
		return components;
	}
	
	public SyncRequestType getRequest() {
		return request;
	}
	
	public static void encode(SSyncElectricComponentsPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.components.size());
		for (ElectricComponent<?, ?, ?> component : msg.components) {
			CompoundTag componentTag = new CompoundTag();
			component.serializeNbt(componentTag);
			buff.writeNbt(componentTag);
		}
		buff.writeChunkPos(msg.chunkPos);
		buff.writeEnum(msg.request);
	}
	
	public static SSyncElectricComponentsPackage decode(FriendlyByteBuf buff) {
		int componentCount = buff.readInt();
		Set<ElectricComponent<?, Object, ?>> components = new HashSet<>();
		for (int i = 0; i < componentCount; i++) {
			CompoundTag componentTag = buff.readNbt();
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ElectricComponent<?, Object, ?> component = new ElectricComponent(null, null, null);
			component.deserializeNbt(componentTag);
			components.add(component);
		}
		ChunkPos chunkPos = buff.readChunkPos();
		SyncRequestType request = buff.readEnum(SyncRequestType.class);
		return new SSyncElectricComponentsPackage(components, chunkPos, request);
	}
	
	public static void handle(SSyncElectricComponentsPackage msg, Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientElectricPackageHandler.handleSyncComponentsServer(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
	}
	
}
