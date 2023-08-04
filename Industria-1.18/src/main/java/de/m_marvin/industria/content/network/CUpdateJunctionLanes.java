package de.m_marvin.industria.core.conduits.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.engine.ServerConduitPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server that a conduit got placed by a client
 */
public class CUpdateJunctionLanes {
	
	public String[] laneLabelsUp;
	public String[] laneLabelsDown;
	public String[] laneLabelsLeft;
	public String[] laneLabelsRight;
	
	public CUpdateJunctionLanes(String[] laneLabelsUp, String[] laneLabelsDown, String[] laneLabelsLeft, String[] laneLabelsRight) {
		this.placementLength = placementLength;
	}
		
	public float getPlacementLength() {
		return placementLength;
	}
	
	public static void encode(CUpdateJunctionLanes msg, FriendlyByteBuf buff) {
		buff.writeFloat(msg.placementLength);
	}
	
	public static CUpdateJunctionLanes decode(FriendlyByteBuf buff) {
		float placementLength = buff.readFloat();
		return new CUpdateJunctionLanes(placementLength);
	}
	
	public static void handle(CUpdateJunctionLanes msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerConduitPackageHandler.handleChangePlacementLength(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
