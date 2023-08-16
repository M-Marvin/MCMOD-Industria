package de.m_marvin.industria.core.electrics.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ServerElectricPackageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server to change a electric cable conduit's lane names
 */
public class CUpdateJunctionLanesPackage {
	
	public final int internalNode;
	public final BlockPos blockPos;
	public final NodePos cableNode;
	public final String[] laneLabels;
	
	public CUpdateJunctionLanesPackage(NodePos cableNode, String[] laneLabels) {
		this.cableNode = cableNode;
		this.blockPos = cableNode.getBlock();
		this.laneLabels = laneLabels;
		this.internalNode = -1;
	}

	public CUpdateJunctionLanesPackage(int internalNode, BlockPos blockPos, String[] laneLabels) {
		this.cableNode = null;
		this.blockPos = blockPos;
		this.internalNode = internalNode;
		this.laneLabels = laneLabels;
	}
	
	public boolean isInternalNode() {
		return this.cableNode == null;
	}
	
	public int getInternalNode() {
		return internalNode;
	}
	
	public BlockPos getBlockPos() {
		return blockPos;
	}
	
	public NodePos getCableNode() {
		return cableNode;
	}
	
	public String[] getLaneLabels() {
		return laneLabels;
	}
	
	public static void encode(CUpdateJunctionLanesPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.isInternalNode() ? msg.internalNode : -1);
		if (!msg.isInternalNode()) msg.cableNode.write(buff);
		buff.writeBlockPos(msg.blockPos);
		buff.writeInt(msg.laneLabels.length);
		for (String s : msg.laneLabels) buff.writeUtf(s);
	}
	
	public static CUpdateJunctionLanesPackage decode(FriendlyByteBuf buff) {
		int internalNode = buff.readInt();
		NodePos cableNode = internalNode >= 0 ? null : NodePos.read(buff);
		BlockPos blockPos = buff.readBlockPos();
		String[] laneLabels = new String[buff.readInt()];
		for (int i = 0; i < laneLabels.length; i++) laneLabels[i] = buff.readUtf();
		return internalNode >= 0 ? new CUpdateJunctionLanesPackage(internalNode, blockPos, laneLabels) : new CUpdateJunctionLanesPackage(cableNode, laneLabels);
	}
	
	public static void handle(CUpdateJunctionLanesPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ServerElectricPackageHandler.handleUpdateJunctionLanes(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
