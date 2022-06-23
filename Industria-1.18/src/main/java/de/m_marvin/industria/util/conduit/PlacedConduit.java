package de.m_marvin.industria.util.conduit;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.registries.ModRegistries;
import de.m_marvin.industria.util.types.ConduitPos;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class PlacedConduit {
	
	private ConduitPos position;
	private int nodesPerBlock;
	private Conduit conduit;
	private ConduitShape shape;
	
	public ConduitShape conduitShape;
	
	public PlacedConduit(ConduitPos position, Conduit conduit, int nodesPerBlock) {
		this.position = position;
		this.conduit = conduit;
		this.nodesPerBlock = nodesPerBlock;
	}
	
	public ConduitPos getConduitPosition() {
		return position;
	}
	
	public PlacedConduit build(Level level) {
		this.shape = conduit.buildShape(level, this, nodesPerBlock);
		updateShape(level);
		return this;
	}
	
	public void updateShape(BlockGetter level) {
		this.conduit.updatePhysicalNodes(level, this);
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.put("Position", this.position.writeNBT(new CompoundTag()));
		tag.putString("Conduit", this.conduit.getRegistryName().toString());
		tag.putInt("Nodes", this.nodesPerBlock);
		return tag;
	}
	
	public static PlacedConduit load(CompoundTag tag) {
		ConduitPos position = ConduitPos.readNBT(tag.getCompound("Position"));
		ResourceLocation conduitName = new ResourceLocation(tag.getString("Conduit"));
		Conduit conduit = ModRegistries.CONDUITS.get().getValue(conduitName);
		int nodesPerBlock = tag.getInt("Nodes");
		if (conduit == null) return null;
		return new PlacedConduit(position, conduit, nodesPerBlock);
	}
	
	public ConduitShape getShape() {
		return shape;
	}
	
	public void setShape(ConduitShape shape) {
		this.shape = shape;
	}
	
	public Conduit getConduit() {
		return conduit;
	}
	
	public BlockPos getNodeA() {
		return this.position.getNodeApos();
	}

	public BlockPos getNodeB() {
		return this.position.getNodeBpos();
	}
	
	public int getConnectionPointA() {
		return this.position.getNodeAid();
	}
	
	public int getConnectionPointB() {
		return this.position.getNodeBid();
	}
	
	public int getNodesPerBlock() {
		return nodesPerBlock;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlacedConduit) {
			PlacedConduit other = (PlacedConduit) obj;
			return 	other.getConduitPosition().equals(this.getConduitPosition()) &
					other.conduit == this.conduit;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "PlacedConduit{conduit=" + this.conduit.getRegistryName() + 
				",nodes=" + this.nodesPerBlock +
				",position=" + this.position.toString() + 
				"}";
 	}
	
}
