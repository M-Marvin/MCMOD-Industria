package de.m_marvin.industria.core.conduits.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.engine.ServerConduitPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Gets send if the player scrolls when placing an conduit to update the length placed by the server.
 **/
public class CChangeConduitPlacementLengthPackage {
	
	public final float placementLength;
	
	public CChangeConduitPlacementLengthPackage(float placementLength) {
		this.placementLength = placementLength;
	}
		
	public float getPlacementLength() {
		return placementLength;
	}
	
	public static void encode(CChangeConduitPlacementLengthPackage msg, FriendlyByteBuf buff) {
		buff.writeFloat(msg.placementLength);
	}
	
	public static CChangeConduitPlacementLengthPackage decode(FriendlyByteBuf buff) {
		float placementLength = buff.readFloat();
		return new CChangeConduitPlacementLengthPackage(placementLength);
	}
	
	public static void handle(CChangeConduitPlacementLengthPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerConduitPackageHandler.handleChangePlacementLength(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
