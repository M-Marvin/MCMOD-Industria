package de.m_marvin.industria.core.conduits.engine.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.engine.ClientConduitPackageHandler;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

/**
 * Tells the client about existing and removed conduits, for example if a chunk gets loaded on client side.
 * It does not trigger any events on the client side and only updates the conduit-list.
 */
public class SSyncAddedConduits {
	
	public final ChunkPos chunkPos;
	public final List<ConduitEntity> conduits;
	
	public SSyncAddedConduits(List<ConduitEntity> conduitEntitys, ChunkPos targetChunk) {
		this.conduits = conduitEntitys;
		this.chunkPos = targetChunk;
	}
	
	public SSyncAddedConduits(ConduitEntity conduitEntity, ChunkPos targetChunk) {
		this.conduits = new ArrayList<ConduitEntity>();
		this.conduits.add(conduitEntity);
		this.chunkPos = targetChunk;
	}
	
	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public static void encode(SSyncAddedConduits msg, FriendlyByteBuf buff) {
		buff.writeChunkPos(msg.chunkPos);
		buff.writeInt(msg.conduits.size());
		for (ConduitEntity conduitEntity : msg.conduits)
			buff.writeNbt(conduitEntity.save());
	}
	
	public static SSyncAddedConduits decode(FriendlyByteBuf buff) {
		ChunkPos chunkPos = buff.readChunkPos();
		int count = buff.readInt();
		List<ConduitEntity> conduitEntitys = new ArrayList<ConduitEntity>();
		for (int i = 0; i < count; i++)
			conduitEntitys.add(ConduitEntity.load(buff.readNbt()));
		return new SSyncAddedConduits(conduitEntitys, chunkPos);
	}
	
	public static void handle(SSyncAddedConduits msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientConduitPackageHandler.handleSyncAddedConduitsFromServer(msg, ctx.get());
			ctx.get().setPacketHandled(true);
		});
		
	}
	
}
