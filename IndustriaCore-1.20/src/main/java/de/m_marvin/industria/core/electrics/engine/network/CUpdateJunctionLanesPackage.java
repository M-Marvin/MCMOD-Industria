package de.m_marvin.industria.core.electrics.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ServerElectricPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server that a power source block has been edited
 */
public class CUpdateJunctionLanesPackage {
	
	public final boolean internalNode;
	public final NodePos cableNode;
	public final String[] laneLabels;
	
	public CUpdateJunctionLanesPackage(NodePos cableNode, String[] laneLabels, boolean internalNode) {
		this.cableNode = cableNode;
		this.laneLabels = laneLabels;
		this.internalNode = internalNode;
	}
	
	public boolean isInternalNode() {
		return this.internalNode;
	}
	
	public NodePos getCableNode() {
		return cableNode;
	}
	
	public String[] getLaneLabels() {
		return laneLabels;
	}
	
	public static void encode(CUpdateJunctionLanesPackage msg, FriendlyByteBuf buff) {
		buff.writeBoolean(msg.isInternalNode());
		msg.cableNode.write(buff);
		buff.writeInt(msg.laneLabels.length);
		for (String s : msg.laneLabels) buff.writeUtf(s);
	}
	
	public static CUpdateJunctionLanesPackage decode(FriendlyByteBuf buff) {
		boolean internal = buff.readBoolean();
		NodePos cableNode = NodePos.read(buff);
		String[] laneLabels = new String[buff.readInt()];
		for (int i = 0; i < laneLabels.length; i++) laneLabels[i] = buff.readUtf();
		return new CUpdateJunctionLanesPackage(cableNode, laneLabels, internal);
	}
	
	public static void handle(CUpdateJunctionLanesPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerElectricPackageHandler.handleUpdateJunctionLanes(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
