package de.m_marvin.industria.core.conduits.engine.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.conduits.engine.ClientConduitPackageHandler;
import de.m_marvin.industria.core.conduits.registry.Conduits;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/*
 * Sends existing conduits from server to client to sync the conduits
 */
public class SSyncPlacedConduit {
	
	public ChunkPos chunkPos;
	public List<PlacedConduit> conduitStates;
	public Status status;
	public boolean triggerEvents;
	public boolean eventDropItems;
	
	public static enum Status {
		REMOVED, ADDED;
	}
	
	public SSyncPlacedConduit(List<PlacedConduit> conduitStates, ChunkPos targetChunk, Status status, boolean triggerEvents, boolean eventDropItems) {
		this.conduitStates = conduitStates;
		this.chunkPos = targetChunk;
		this.status = status;
		this.triggerEvents = triggerEvents;
		this.eventDropItems = eventDropItems;
	}
	
	public SSyncPlacedConduit(PlacedConduit conduitState, ChunkPos targetChunk, Status status, boolean triggerEvents, boolean eventDropItems) {
		this.conduitStates = new ArrayList<PlacedConduit>();
		this.conduitStates.add(conduitState);
		this.chunkPos = targetChunk;
		this.status = status;
		this.triggerEvents = triggerEvents;
		this.eventDropItems = eventDropItems;
	}
	
	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public boolean isTriggerEvents() {
		return triggerEvents;
	}
	
	public boolean isEventDropItems() {
		return eventDropItems;
	}
	
	public static void encode(SSyncPlacedConduit msg, FriendlyByteBuf buff) {
		buff.writeEnum(msg.status);
		buff.writeBoolean(msg.triggerEvents);
		buff.writeBoolean(msg.eventDropItems);
		buff.writeChunkPos(msg.chunkPos);
		buff.writeInt(msg.conduitStates.size());
		for (PlacedConduit conduitState : msg.conduitStates) {
			conduitState.getPosition().write(buff);
			buff.writeDouble(conduitState.getLength());
			buff.writeResourceLocation(conduitState.getConduit().getRegistryName());
			conduitState.getShape().writeUpdateData(buff);
		}
	}
	
	public static SSyncPlacedConduit decode(FriendlyByteBuf buff) {
		Status status = buff.readEnum(Status.class);
		boolean triggerEvents = buff.readBoolean();
		boolean eventDropsItems = buff.readBoolean();
		ChunkPos chunkPos = buff.readChunkPos();
		int count = buff.readInt();
		List<PlacedConduit> conduitStates = new ArrayList<PlacedConduit>();
		for (int i = 0; i < count; i++) {
			ConduitPos position = ConduitPos.read(buff);
			double length = buff.readDouble();
			ResourceLocation conduitName = buff.readResourceLocation();
			ConduitShape shape = new ConduitShape(null, null, 0);
			shape.readUpdateData(buff);
			if (!Conduits.CONDUITS_REGISTRY.get().containsKey(conduitName)) {
				Industria.LOGGER.error("Recived package for unregistered conduit: " + conduitName);
				continue;
			} else if (shape.nodes == null || shape.lastPos == null) {
				Industria.LOGGER.error("Recived package with invalid conduit shape: " + conduitName);
				continue;
			}
			Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(conduitName);
			PlacedConduit conduitState = new PlacedConduit(position, conduit, length);
			conduitState.setShape(shape);
			conduitStates.add(conduitState);
		}
		return new SSyncPlacedConduit(conduitStates, chunkPos, status, triggerEvents, eventDropsItems);
	}
	
	public static void handle(SSyncPlacedConduit msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
				ClientConduitPackageHandler.handleSyncConduitsFromServer(msg, ctx.get());
			});
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
