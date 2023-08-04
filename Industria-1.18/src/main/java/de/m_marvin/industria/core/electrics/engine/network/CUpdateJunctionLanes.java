package de.m_marvin.industria.core.electrics.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ServerElectricPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server that a conduit got placed by a client
 */
public class CUpdateJunctionLanes {
	
	public NodePos wireNode;
	public String[] laneLabels;
	
	public CUpdateJunctionLanes(String[] laneLabels, NodePos wireNode) {
		this.laneLabels = laneLabels;
		this.wireNode = wireNode;
	}
	
	public String[] getLaneLabels() {
		return laneLabels;
	}
	
	public NodePos getWireNode() {
		return wireNode;
	}
	
	public static void encode(CUpdateJunctionLanes msg, FriendlyByteBuf buff) {
		msg.wireNode.write(buff);
		buff.writeInt(msg.laneLabels.length);
		for (String s : msg.laneLabels) buff.writeUtf(s);
	}
	
	public static CUpdateJunctionLanes decode(FriendlyByteBuf buff) {
		NodePos wireNode = NodePos.read(buff);
		String[] laneLabels = new String[buff.readInt()];
		for (int i = 0; i < laneLabels.length; i++) laneLabels[i] = buff.readUtf();
		return new CUpdateJunctionLanes(laneLabels, wireNode);
	}
	
	public static void handle(CUpdateJunctionLanes msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerElectricPackageHandler.handleUpdateJunctionLanes(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
