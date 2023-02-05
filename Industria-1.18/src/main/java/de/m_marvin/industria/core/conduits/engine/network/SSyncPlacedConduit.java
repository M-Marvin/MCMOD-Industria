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
import net.minecraft.core.BlockPos;
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
	public boolean removed;
	
	public SSyncPlacedConduit(List<PlacedConduit> conduitStates, ChunkPos targetChunk, boolean removed) {
		this.conduitStates = conduitStates;
		this.chunkPos = targetChunk;
		this.removed = removed;
	}
	
	public SSyncPlacedConduit(PlacedConduit conduitState, ChunkPos targetChunk, boolean removed) {
		this.conduitStates = new ArrayList<PlacedConduit>();
		this.conduitStates.add(conduitState);
		this.chunkPos = targetChunk;
		this.removed = removed;
	}
	
	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public static void encode(SSyncPlacedConduit msg, FriendlyByteBuf buff) {
		buff.writeBoolean(msg.removed);
		buff.writeChunkPos(msg.chunkPos);
		buff.writeInt(msg.conduitStates.size());
		for (PlacedConduit conduitState : msg.conduitStates) {
			buff.writeBlockPos(conduitState.getNodeA());
			buff.writeBlockPos(conduitState.getNodeB());
			buff.writeInt(conduitState.getNodesPerBlock());
			buff.writeInt(conduitState.getLength());
			buff.writeInt(conduitState.getConnectionPointA());
			buff.writeInt(conduitState.getConnectionPointB());
			buff.writeResourceLocation(conduitState.getConduit().getRegistryName());
		}
	}
	
	public static SSyncPlacedConduit decode(FriendlyByteBuf buff) {
		boolean removed = buff.readBoolean();
		ChunkPos chunkPos = buff.readChunkPos();
		int count = buff.readInt();
		List<PlacedConduit> conduitStates = new ArrayList<PlacedConduit>();
		for (int i = 0; i < count; i++) {
			BlockPos nodeApos = buff.readBlockPos();
			BlockPos nodeBpos = buff.readBlockPos();
			int nodesPerBlock = buff.readInt();
			int length = buff.readInt();
			int connectionPointA = buff.readInt();
			int connectionPointB = buff.readInt();
			ResourceLocation conduitName = buff.readResourceLocation();
			if (!Conduits.CONDUITS_REGISTRY.get().containsKey(conduitName)) {
				Industria.LOGGER.error("Recived package for unregistered conduit: " + conduitName);
				continue;
			}
			Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(conduitName);
			ConduitPos position = new ConduitPos(nodeApos, nodeBpos, connectionPointA, connectionPointB);
			conduitStates.add(new PlacedConduit(position, conduit, nodesPerBlock, length));
		}
		return new SSyncPlacedConduit(conduitStates, chunkPos, removed);
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
