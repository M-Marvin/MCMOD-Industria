package de.m_marvin.industria.core.conduits.engine.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ClientConduitPackageHandler;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

/**
 * Tells the client about existing and removed conduits, for example if a chunk gets loaded on client side.
 * It does not trigger any events on the client side and only updates the conduit-list.
 */
public class SSyncConduitPackage {
	
	// TODO rewrite sync packages, especially conduits, allow for post creation update packages
	
	public final ChunkPos chunkPos;
	public final List<ConduitEntity> conduits;
	public final SyncRequestType request;
	
	public SSyncConduitPackage(List<ConduitEntity> conduitEntitys, ChunkPos targetChunk, SyncRequestType request) {
		this.conduits = conduitEntitys;
		this.chunkPos = targetChunk;
		this.request = request;
	}
	
	public SSyncConduitPackage(ConduitEntity conduitEntity, ChunkPos targetChunk, SyncRequestType request) {
		this.conduits = new ArrayList<ConduitEntity>();
		this.conduits.add(conduitEntity);
		this.chunkPos = targetChunk;
		this.request = request;
	}
	
	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public SyncRequestType getRquest() {
		return request;
	}
	
	public static void encode(SSyncConduitPackage msg, FriendlyByteBuf buff) {
		buff.writeEnum(msg.request);
		buff.writeChunkPos(msg.chunkPos);
		buff.writeInt(msg.conduits.size());
		for (ConduitEntity conduitEntity : msg.conduits) {
			conduitEntity.getPosition().writeBuff(buff);
			buff.writeDouble(conduitEntity.getLength());
			buff.writeResourceLocation(Conduits.CONDUITS_REGISTRY.get().getKey(conduitEntity.getConduit()));
			conduitEntity.getShape().writeUpdateData(buff);
			buff.writeNbt(conduitEntity.getUpdateTag());
		}
	}
	
	public static SSyncConduitPackage decode(FriendlyByteBuf buff) {
		SyncRequestType status = buff.readEnum(SyncRequestType.class);
		ChunkPos chunkPos = buff.readChunkPos();
		int count = buff.readInt();
		List<ConduitEntity> conduitEntitys = new ArrayList<ConduitEntity>();
		for (int i = 0; i < count; i++) {
			ConduitPos position = ConduitPos.readBuff(buff);
			double length = buff.readDouble();
			ResourceLocation conduitName = buff.readResourceLocation();
			ConduitShape shape = new ConduitShape(null, null, 0);
			shape.readUpdateData(buff);
			if (!Conduits.CONDUITS_REGISTRY.get().containsKey(conduitName)) {
				IndustriaCore.LOGGER.error("Recived package for unregistered conduit: " + conduitName);
				continue;
			} else if (shape.nodes == null || shape.lastPos == null) {
				IndustriaCore.LOGGER.error("Recived package with invalid conduit shape: " + conduitName);
				continue;
			}
			Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(conduitName);
			ConduitEntity conduitEntity = conduit.newConduitEntity(position, conduit, length);
			conduitEntity.setShape(shape);
			conduitEntity.readUpdateTag(buff.readNbt());
			conduitEntitys.add(conduitEntity);
		}
		return new SSyncConduitPackage(conduitEntitys, chunkPos, status);
	}
	
	public static void handle(SSyncConduitPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientConduitPackageHandler.handleSyncConduitsFromServer(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
