package de.m_marvin.industria.core.electrics.engine.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.IElectric;
import de.m_marvin.industria.core.util.SyncRequestType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent.Context;

public class SSyncComponentsPackage {
	
	public final ChunkPos chunkPos;
	public final Map<Object, IElectric<?, ?, ?>> components;
	public final SyncRequestType request;
	
	public SSyncComponentsPackage(Set<Component<?, ?, ?>> components, ChunkPos targetChunk, SyncRequestType request) {
		this.chunkPos = targetChunk;
		this.components = new HashMap<>();
		for (Component<?, ?, ?> component : components) {
			this.components.put(component.pos(), component.type());
		}
		this.request = request;
	}
	
	public SSyncComponentsPackage(Component<?, ?, ?> component, ChunkPos targetChunk, SyncRequestType request) {
		this.chunkPos = targetChunk;
		this.components = new HashMap<>();
		this.components.put(component.pos(), component.type());
		this.request = request;
	}
	
	public SSyncComponentsPackage(Map<Object, IElectric<?, ?, ?>> components, ChunkPos targetChunk, SyncRequestType request) {
		this.chunkPos = targetChunk;
		this.components = components;
		this.request = request;
	}

	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public Map<Object, IElectric<?, ?, ?>> getComponents() {
		return components;
	}
	
	public SyncRequestType getRequest() {
		return request;
	}
	
	@SuppressWarnings("unchecked")
	public static void encode(SSyncComponentsPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.components.size());
		for (Entry<Object, IElectric<?, ?, ?>> component : msg.components.entrySet()) {
			CompoundTag posTag = new CompoundTag();
			((IElectric<?, Object, ?>) component.getValue()).serializeNBTPosition(component.getKey(), posTag);
			buff.writeNbt(posTag);
			IElectric.Type type = IElectric.Type.getType(component.getValue());
			ResourceLocation registryName = type.getRegistry().getKey(component.getValue());
			buff.writeEnum(type);
			buff.writeResourceLocation(registryName);
		}
		buff.writeChunkPos(msg.chunkPos);
		buff.writeEnum(msg.request);
	}
	
	public static SSyncComponentsPackage decode(FriendlyByteBuf buff) {
		int componentCount = buff.readInt();
		Map<Object, IElectric<?, ?, ?>> components = new HashMap<>();
		for (int i = 0; i < componentCount; i++) {
			CompoundTag posTag = buff.readNbt();
			IElectric.Type type = buff.readEnum(IElectric.Type.class);
			ResourceLocation registryName = buff.readResourceLocation();
			IElectric<?, ?, ?> componentType = (IElectric<?, ?, ?>) type.getRegistry().getValue(registryName);
			Object position = componentType.deserializeNBTPosition(posTag);
			components.put(position, componentType);
		}
		ChunkPos chunkPos = buff.readChunkPos();
		SyncRequestType request = buff.readEnum(SyncRequestType.class);
		return new SSyncComponentsPackage(components, chunkPos, request);
	}
	
	public static void handle(SSyncComponentsPackage msg, Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientElectricPackageHandler.handleSyncComponentsServer(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
	}
	
}
