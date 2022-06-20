package de.m_marvin.industria.util.conduit;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;

public class ConduitPos {
	
	private BlockPos nodeApos;
	private BlockPos nodeBpos;
	private int nodeAid;
	private int nodeBid;
	
	public ConduitPos(BlockPos nodeApos, BlockPos nodeBpos, int nodeAid, int nodeBid) {
		super();
		this.nodeApos = nodeApos;
		this.nodeBpos = nodeBpos;
		this.nodeAid = nodeAid;
		this.nodeBid = nodeBid;
	}
	
	public BlockPos getNodeApos() {
		return nodeApos;
	}
	public void setNodeApos(BlockPos nodeApos) {
		this.nodeApos = nodeApos;
	}
	public BlockPos getNodeBpos() {
		return nodeBpos;
	}
	public void setNodeBpos(BlockPos nodeBpos) {
		this.nodeBpos = nodeBpos;
	}
	public int getNodeAid() {
		return nodeAid;
	}
	public void setNodeAid(int nodeAid) {
		this.nodeAid = nodeAid;
	}
	public int getNodeBid() {
		return nodeBid;
	}
	public void setNodeBid(int nodeBid) {
		this.nodeBid = nodeBid;
	}
	
	public void write(FriendlyByteBuf buff) {
		buff.writeBlockPos(nodeApos);
		buff.writeBlockPos(nodeBpos);
		buff.writeInt(nodeAid);
		buff.writeInt(nodeBid);
	}
	
	public static ConduitPos read(FriendlyByteBuf buff) {
		BlockPos nodeApos = buff.readBlockPos();
		BlockPos nodeBpos = buff.readBlockPos();
		int nodeAid = buff.readInt();
		int nodeBid = buff.readInt();
		return new ConduitPos(nodeApos, nodeBpos, nodeAid, nodeBid);
	}
	
	public CompoundTag writeNBT(CompoundTag nbt) {
		nbt.put("PosA", NbtUtils.writeBlockPos(this.nodeApos));
		nbt.put("PosB", NbtUtils.writeBlockPos(this.nodeBpos));
		nbt.putInt("IdA", this.nodeAid);
		nbt.putInt("IdB", this.nodeBid);
		return nbt;
	}
	
	public static ConduitPos readNBT(CompoundTag nbt) {
		return new ConduitPos(NbtUtils.readBlockPos(nbt.getCompound("PosA")), NbtUtils.readBlockPos(nbt.getCompound("PosB")), nbt.getInt("IdA"), nbt.getInt("IdB"));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConduitPos) {
			ConduitPos other = (ConduitPos) obj;
			return 	other.nodeApos.equals(nodeApos) &&
					other.nodeBpos.equals(nodeBpos) &&
					other.nodeAid == nodeAid &&
					other.nodeBid == nodeBid;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "ConduitPos{A=[" + this.nodeAid + "@" + this.nodeApos.getX() + "," + nodeApos.getY() + "," + nodeApos.getZ() + "],B=[" + this.nodeBid + "@" + this.nodeBpos.getX() + "," + nodeBpos.getY() + "," + nodeBpos.getZ() + "]}";
	}
	
}
