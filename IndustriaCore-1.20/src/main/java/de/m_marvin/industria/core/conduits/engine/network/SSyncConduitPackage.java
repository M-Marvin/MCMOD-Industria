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
	
	public final ChunkPos chunkPos;
	public final List<ConduitEntity> conduits;
	public final SyncRequestType request;
	
	public SSyncConduitPackage(List<ConduitEntity> conduitStates, ChunkPos targetChunk, SyncRequestType request) {
		this.conduits = conduitStates;
		this.chunkPos = targetChunk;
		this.request = request;
	}
	
	public SSyncConduitPackage(ConduitEntity conduitState, ChunkPos targetChunk, SyncRequestType request) {
		this.conduits = new ArrayList<ConduitEntity>();
		this.conduits.add(conduitState);
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
		for (ConduitEntity conduitState : msg.conduits) {
			conduitState.getPosition().write(buff);
			buff.writeDouble(conduitState.getLength());
			buff.writeResourceLocation(Conduits.CONDUITS_REGISTRY.get().getKey(conduitState.getConduit()));
			conduitState.getShape().writeUpdateData(buff);
			buff.writeNbt(conduitState.getUpdateTag());
		}
	}
	
	public static SSyncConduitPackage decode(FriendlyByteBuf buff) {
		SyncRequestType status = buff.readEnum(SyncRequestType.class);
		ChunkPos chunkPos = buff.readChunkPos();
		int count = buff.readInt();
		List<ConduitEntity> conduitStates = new ArrayList<ConduitEntity>();
		for (int i = 0; i < count; i++) {
			ConduitPos position = ConduitPos.read(buff);
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
			ConduitEntity conduitState = conduit.newConduitEntity(position, conduit, length);
			conduitState.setShape(shape);
			conduitState.readUpdateTag(buff.readNbt());
			conduitStates.add(conduitState);
		}
		return new SSyncConduitPackage(conduitStates, chunkPos, status);
	}
	
	public static void handle(SSyncConduitPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientConduitPackageHandler.handleSyncConduitsFromServer(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
