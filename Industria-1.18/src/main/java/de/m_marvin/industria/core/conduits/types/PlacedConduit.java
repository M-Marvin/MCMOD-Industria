package de.m_marvin.industria.core.conduits.types;

import de.m_marvin.industria.core.conduits.registry.Conduits;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class PlacedConduit {
	
	private ConduitPos position;
	private int length;
	private int nodesPerBlock;
	private Conduit conduit;
	private ConduitShape shape;
	
	public ConduitShape conduitShape;
	
	public PlacedConduit(ConduitPos position, Conduit conduit, int nodesPerBlock, int length) {
		this.position = position;
		this.conduit = conduit;
		this.nodesPerBlock = nodesPerBlock;
		this.length = length;
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
		Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(conduitName);
		int nodesPerBlock = tag.getInt("Nodes");
		int length = tag.getInt("Length");
		if (conduit == null) return null;
		return new PlacedConduit(position, conduit, nodesPerBlock, length);
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

	public ConduitPos getPosition() {
		return position;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getNodesPerBlock() {
		return nodesPerBlock;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlacedConduit) {
			PlacedConduit other = (PlacedConduit) obj;
			return 	other.getPosition().equals(this.getPosition()) &
					other.conduit == this.conduit;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "PlacedConduit{conduit=" + this.conduit.getRegistryName() + 
				",nodes=" + this.nodesPerBlock +
				",length=" + this.length +
				",position=" + this.position.toString() + 
				"}";
 	}
	
}
