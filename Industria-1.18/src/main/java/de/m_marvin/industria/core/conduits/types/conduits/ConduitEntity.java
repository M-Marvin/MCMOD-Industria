package de.m_marvin.industria.core.conduits.types.conduits;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ConduitEntity {
	
	protected ConduitPos position;
	protected double length;
	protected Conduit conduit;
	protected ConduitShape shape;
	
	public ConduitEntity(ConduitPos position, Conduit conduit, double length) {
		this.position = position;
		this.conduit = conduit;
		this.length = length;
	}
	
	public ConduitEntity build(Level level) {
		this.conduit.onBuild(level, position, this);
		this.shape = conduit.buildShape(level, this);
		updateShape(level);
		// TODO Remove if not able to fix
//		if (!level.isClientSide) {
//			ServerShip contraptionA = (ServerShip) PhysicUtility.getContraptionOfBlock(level, this.getPosition().getNodeApos());
//			ServerShip contraptionB = (ServerShip) PhysicUtility.getContraptionOfBlock(level, this.getPosition().getNodeBpos());
//			if (contraptionA != null) ContraptionAttachment.attachIfMissing(contraptionA).notifyNewConduit(level, this, 0);
//			if (contraptionB != null) ContraptionAttachment.attachIfMissing(contraptionB).notifyNewConduit(level, this, 1);
//		}
		return this;
	}
	
	public ConduitEntity dismantle(Level level) {
		this.conduit.onDismantle(level, position, this);
		this.conduit.dismantleShape(level, this);
		this.shape = null;
		return this;
	}
	
	public void updateShape(Level level) {
		assert this.shape != null : "Can't update un-build conduit!";
		this.conduit.updatePhysicalNodes(level, this);
	}
	
	// TODO Remove if not able to fix
//	public boolean updateContraptions(Level level, PhysShip contraption, int nodeId) {
//		if (this.shape == null) return false;
//		this.conduit.updateContraptionForces(level, contraption, this, nodeId);
//		return true;
//	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.put("Position", this.position.writeNBT(new CompoundTag()));
		tag.putString("Conduit", this.conduit.getRegistryName().toString());
		tag.putDouble("Length", this.length);
		if (this.shape != null) tag.put("Shape", this.shape.save());
		this.saveAdditional(tag);
		return tag;
	}
	
	public static ConduitEntity load(CompoundTag tag) {
		ResourceLocation conduitName = new ResourceLocation(tag.getString("Conduit"));
		Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(conduitName);
		if (conduit == null) return null;
		ConduitPos position = ConduitPos.readNBT(tag.getCompound("Position"));
		double length = tag.getDouble("Length");
		ConduitShape shape = tag.contains("Shape") ? ConduitShape.load(tag.getCompound("Shape")) : null;
		if (shape == null) return null;
		ConduitEntity state = conduit.newConduitEntity(position, conduit, length);
		state.setShape(shape);
		state.loadAdditional(tag);
		return state;
	}
	
	public void saveAdditional(CompoundTag tag) {};
	public void loadAdditional(CompoundTag tag) {};
	public CompoundTag getUpdateTag() { return new CompoundTag(); };
	public void readUpdateTag(CompoundTag tag) {};
	
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
		if (obj instanceof ConduitEntity other) {
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
