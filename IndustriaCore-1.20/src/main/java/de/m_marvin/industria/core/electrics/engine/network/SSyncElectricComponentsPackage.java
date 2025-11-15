package de.m_marvin.industria.core.electrics.engine.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * Tells the client about the creation or removal of components
 */
public class SSyncElectricComponentsPackage {
	
	public final ChunkPos chunkPos;
	public final Collection<ElectricReference> components;
	public final SyncRequestType request;
	
	public SSyncElectricComponentsPackage(Collection<ElectricReference> components, ChunkPos targetChunk, SyncRequestType request) {
		this.chunkPos = targetChunk;
		this.components = components.stream().distinct().toList();
		this.request = request;
	}
	
	public SSyncElectricComponentsPackage(ElectricComponent<?, ?> component, ChunkPos targetChunk, SyncRequestType request) {
		this.chunkPos = targetChunk;
		this.components = new HashSet<>();
		this.components.add(component.reference());
		this.request = request;
	}

	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public Collection<ElectricReference> getComponents() {
		return components;
	}
	
	public SyncRequestType getRequest() {
		return request;
	}
	
	public static void encode(SSyncElectricComponentsPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.components.size());
		for (ElectricReference component : msg.components)
			component.writeBuff(buff);
		buff.writeChunkPos(msg.chunkPos);
		buff.writeEnum(msg.request);
	}
	
	public static SSyncElectricComponentsPackage decode(FriendlyByteBuf buff) {
		int componentCount = buff.readInt();
		Set<ElectricReference> components = new HashSet<>();
		for (int i = 0; i < componentCount; i++)
			components.add(ElectricReference.readBuff(buff));
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
