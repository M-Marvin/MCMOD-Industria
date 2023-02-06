package de.m_marvin.industria.core.conduits.types;

import de.m_marvin.industria.core.conduits.registry.Conduits;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PlacedConduit {
	
	private ConduitPos position;
	private double length;
	private Conduit conduit;
	private ConduitShape shape;
	
	public ConduitShape conduitShape;
	
	public PlacedConduit(ConduitPos position, Conduit conduit, double length) {
		this.position = position;
		this.conduit = conduit;
		this.length = length;
	}
	
	public PlacedConduit build(Level level) {
		this.shape = conduit.buildShape(level, this, length);
		updateShape(level);
		return this;
	}
	
	public void updateShape(Level level) {
		this.conduit.updatePhysicalNodes(level, this);
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.put("Position", this.position.writeNBT(new CompoundTag()));
		tag.putString("Conduit", this.conduit.getRegistryName().toString());
		tag.putDouble("Length", this.length);
		return tag;
	}
	
	public static PlacedConduit load(CompoundTag tag) {
		ConduitPos position = ConduitPos.readNBT(tag.getCompound("Position"));
		ResourceLocation conduitName = new ResourceLocation(tag.getString("Conduit"));
		Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(conduitName);
		double length = tag.getDouble("Length");
		if (conduit == null) return null;
		return new PlacedConduit(position, conduit, length);
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
	
	public double getLength() {
		return length;
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
				",length=" + this.length +
				",position=" + this.position.toString() + 
				"}";
 	}

	public int getNodeCount() {
		return 2;
	}
	
}
