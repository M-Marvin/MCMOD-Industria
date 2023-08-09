package de.m_marvin.industria.core.electrics.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ServerElectricPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server to change a electric cable conduit's lane names
 */
public class CUpdateJunctionLanes {
	
	public NodePos cableNode;
	public String[] laneLabels;
	
	public CUpdateJunctionLanes(NodePos cableNodes, String[] laneLabels) {
		this.cableNode = cableNodes;
		this.laneLabels = laneLabels;
	}
	
	public NodePos getCableNode() {
		return cableNode;
	}
	
	public String[] getLaneLabels() {
		return laneLabels;
	}
	
	public static void encode(CUpdateJunctionLanes msg, FriendlyByteBuf buff) {
		msg.cableNode.write(buff);
		buff.writeInt(msg.laneLabels.length);
		for (String s : msg.laneLabels) buff.writeUtf(s);
	}
	
	public static CUpdateJunctionLanes decode(FriendlyByteBuf buff) {
		NodePos cableNode = NodePos.read(buff);
		String[] laneLabels = new String[buff.readInt()];
		for (int i = 0; i < laneLabels.length; i++) laneLabels[i] = buff.readUtf();
		return new CUpdateJunctionLanes(cableNode, laneLabels);
	}
	
	public static void handle(CUpdateJunctionLanes msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerElectricPackageHandler.handleUpdateJunctionLanes(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
