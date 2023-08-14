package de.m_marvin.industria.core.conduits.engine.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ClientConduitPackageHandler;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/**
 * Tells the client about existing and removed conduits, for example if a chunk gets loaded on client side.
 * It does not trigger any events on the client side and only updates the conduit-list.
 */
public class SSyncConduit {
	
	public ChunkPos chunkPos;
	public List<ConduitEntity> conduits;
	public Status status;
	
	public static enum Status {
		REMOVED, ADDED;
	}
	
	public SSyncConduit(List<ConduitEntity> conduitStates, ChunkPos targetChunk, Status status) {
		this.conduits = conduitStates;
		this.chunkPos = targetChunk;
		this.status = status;
	}
	
	public SSyncConduit(ConduitEntity conduitState, ChunkPos targetChunk, Status status) {
		this.conduits = new ArrayList<ConduitEntity>();
		this.conduits.add(conduitState);
		this.chunkPos = targetChunk;
		this.status = status;
	}
	
	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public static void encode(SSyncConduit msg, FriendlyByteBuf buff) {
		buff.writeEnum(msg.status);
		buff.writeChunkPos(msg.chunkPos);
		buff.writeInt(msg.conduits.size());
		for (ConduitEntity conduitState : msg.conduits) {
			conduitState.getPosition().write(buff);
			buff.writeDouble(conduitState.getLength());
			buff.writeResourceLocation(conduitState.getConduit().getRegistryName());
			conduitState.getShape().writeUpdateData(buff);
			buff.writeNbt(conduitState.getUpdateTag());
		}
	}
	
	public static SSyncConduit decode(FriendlyByteBuf buff) {
		Status status = buff.readEnum(Status.class);
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
		return new SSyncConduit(conduitStates, chunkPos, status);
	}
	
	public static void handle(SSyncConduit msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
				ClientConduitPackageHandler.handleSyncConduitsFromServer(msg, ctx.get());
			});
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
