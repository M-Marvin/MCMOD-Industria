package de.m_marvin.industria.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.client.rendering.util.ClientPackageHandler;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.ModRegistries;
import de.m_marvin.industria.util.conduit.PlacedConduit;
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
	
	public SSyncPlacedConduit(List<PlacedConduit> conduitStates, ChunkPos targetChunk) {
		this.conduitStates = conduitStates;
		this.chunkPos = targetChunk;
	}
	
	public SSyncPlacedConduit(PlacedConduit conduitState, ChunkPos targetChunk) {
		this.conduitStates = new ArrayList<PlacedConduit>();
		this.conduitStates.add(conduitState);
		this.chunkPos = targetChunk;
	}
	
	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public static void encode(SSyncPlacedConduit msg, FriendlyByteBuf buff) {
		buff.writeChunkPos(msg.chunkPos);
		buff.writeInt(msg.conduitStates.size());
		for (PlacedConduit conduitState : msg.conduitStates) {
			buff.writeBlockPos(conduitState.getNodeA());
			buff.writeBlockPos(conduitState.getNodeB());
			buff.writeInt(conduitState.getNodesPerBlock());
			buff.writeInt(conduitState.getConnectionPointA());
			buff.writeInt(conduitState.getConnectionPointB());
			buff.writeResourceLocation(conduitState.getConduit().getRegistryName());
		}
	}
	
	public static SSyncPlacedConduit decode(FriendlyByteBuf buff) {
		ChunkPos chunkPos = buff.readChunkPos();
		int count = buff.readInt();
		List<PlacedConduit> conduitStates = new ArrayList<PlacedConduit>();
		for (int i = 0; i < count; i++) {
			BlockPos nodeApos = buff.readBlockPos();
			BlockPos nodeBpos = buff.readBlockPos();
			int nodesPerBlock = buff.readInt();
			int connectionPointA = buff.readInt();
			int connectionPointB = buff.readInt();
			ResourceLocation conduitName = buff.readResourceLocation();
			if (!ModRegistries.CONDUITS.get().containsKey(conduitName)) {
				Industria.LOGGER.error("Recived package for unregistered conduit: " + conduitName);
				continue;
			}
			Conduit conduit = ModRegistries.CONDUITS.get().getValue(conduitName);
			conduitStates.add(new PlacedConduit(nodeApos, connectionPointA, nodeBpos, connectionPointB, conduit, nodesPerBlock));
		}
		return new SSyncPlacedConduit(conduitStates, chunkPos);
	}
	
	public static void handle(SSyncPlacedConduit msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
				ClientPackageHandler.handleSyncConduitsFromServer(msg, ctx.get());
			});
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}