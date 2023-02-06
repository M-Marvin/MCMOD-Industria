package de.m_marvin.industria.core.conduits.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.engine.ServerConduitPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server that a conduit got placed by a client
 */
public class CChangeConduitPlacementLength {
	
	public float placementLength;
	
	public CChangeConduitPlacementLength(float placementLength) {
		this.placementLength = placementLength;
	}
		
	public float getPlacementLength() {
		return placementLength;
	}
	
	public static void encode(CChangeConduitPlacementLength msg, FriendlyByteBuf buff) {
		buff.writeFloat(msg.placementLength);
	}
	
	public static CChangeConduitPlacementLength decode(FriendlyByteBuf buff) {
		float placementLength = buff.readFloat();
		return new CChangeConduitPlacementLength(placementLength);
	}
	
	public static void handle(CChangeConduitPlacementLength msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerConduitPackageHandler.handleChangePlacementLength(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
