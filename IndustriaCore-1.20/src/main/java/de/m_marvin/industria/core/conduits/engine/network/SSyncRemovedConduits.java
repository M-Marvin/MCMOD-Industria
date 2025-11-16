package de.m_marvin.industria.core.conduits.engine.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.engine.ClientConduitPackageHandler;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

/**
 * Tells the client about existing and removed conduits, for example if a chunk gets loaded on client side.
 * It does not trigger any events on the client side and only updates the conduit-list.
 */
public class SSyncRemovedConduits {
	
	public final ChunkPos chunkPos;
	public final List<ConduitPos> conduits;

	public SSyncRemovedConduits(List<ConduitPos> conduits, ChunkPos targetChunk) {
		this.conduits = conduits;
		this.chunkPos = targetChunk;
	}
	
	public SSyncRemovedConduits(ConduitPos conduit, ChunkPos targetChunk) {
		this.conduits = new ArrayList<ConduitPos>();
		this.conduits.add(conduit);
		this.chunkPos = targetChunk;
	}
	
	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public static void encode(SSyncRemovedConduits msg, FriendlyByteBuf buff) {
		buff.writeChunkPos(msg.chunkPos);
		buff.writeInt(msg.conduits.size());
		for (ConduitPos conduit : msg.conduits)
			conduit.writeBuff(buff);
	}
	
	public static SSyncRemovedConduits decode(FriendlyByteBuf buff) {
		ChunkPos chunkPos = buff.readChunkPos();
		int count = buff.readInt();
		List<ConduitPos> conduits = new ArrayList<ConduitPos>();
		for (int i = 0; i < count; i++)
			conduits.add(ConduitPos.readBuff(buff));
		return new SSyncRemovedConduits(conduits, chunkPos);
	}
	
	public static void handle(SSyncRemovedConduits msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientConduitPackageHandler.handleSyncRemovedConduitsFromServer(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
