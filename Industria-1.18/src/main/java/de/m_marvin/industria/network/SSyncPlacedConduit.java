package de.m_marvin.industria.network;

import java.util.function.Supplier;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.registries.ModRegistries;
import de.m_marvin.industria.util.conduit.ConduitWorldStorageCapability;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

public class SSyncPlacedConduit {
	
	public PlacedConduit conduitState;
	
	public SSyncPlacedConduit(PlacedConduit conduitState) {
		this.conduitState = conduitState;
	}
		
	public static void encode(SSyncPlacedConduit msg, FriendlyByteBuf buff) {
		buff.writeBlockPos(msg.conduitState.getNodeA());
		buff.writeBlockPos(msg.conduitState.getNodeB());
		buff.writeInt(msg.conduitState.getNodesPerBlock());
		buff.writeInt(msg.conduitState.getConnectionPointA());
		buff.writeInt(msg.conduitState.getConnectionPointB());
		buff.writeResourceLocation(msg.conduitState.getConduit().getRegistryName());
	}
	
	public static SSyncPlacedConduit decode(FriendlyByteBuf buff) {
		BlockPos nodeApos = buff.readBlockPos();
		BlockPos nodeBpos = buff.readBlockPos();
		int nodesPerBlock = buff.readInt();
		int connectionPointA = buff.readInt();
		int connectionPointB = buff.readInt();
		ResourceLocation conduitName = buff.readResourceLocation();
		if (!ModRegistries.CONDUITS.get().containsKey(conduitName)) {
			Industria.LOGGER.error("Recived package for unregistered conduit: " + conduitName);
			return new SSyncPlacedConduit(null);
		}
		Conduit conduit = ModRegistries.CONDUITS.get().getValue(conduitName);
		
		return new SSyncPlacedConduit(new PlacedConduit(nodeApos, connectionPointA, nodeBpos, connectionPointB, conduit, nodesPerBlock));
	}
	
	@SuppressWarnings("resource")
	public static void handle(SSyncPlacedConduit msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			
			if (msg.conduitState != null) {
				ClientLevel level = Minecraft.getInstance().level;
				LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
				if (conduitHolder.isPresent()) {
					conduitHolder.resolve().get().getConduits().add(msg.conduitState);
				}
			}
			
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
