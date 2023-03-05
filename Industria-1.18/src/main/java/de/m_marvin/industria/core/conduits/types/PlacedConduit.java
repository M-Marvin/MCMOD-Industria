package de.m_marvin.industria.core.conduits.types;

import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;

import de.m_marvin.industria.core.conduits.engine.ConduitHandlerCapability.ContraptionAttachment;
import de.m_marvin.industria.core.conduits.registry.Conduits;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.physics.PhysicUtility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PlacedConduit {
	
	private ConduitPos position;
	private double length;
	private Conduit conduit;
	private ConduitShape shape;
	
	public PlacedConduit(ConduitPos position, Conduit conduit, double length) {
		this.position = position;
		this.conduit = conduit;
		this.length = length;
	}
	
	public PlacedConduit build(Level level) {
		this.shape = conduit.buildShape(level, this);
		updateShape(level);
		if (!level.isClientSide) {
			ServerShip contraptionA = (ServerShip) PhysicUtility.getContraptionOfBlock(level, this.getPosition().getNodeApos());
			ServerShip contraptionB = (ServerShip) PhysicUtility.getContraptionOfBlock(level, this.getPosition().getNodeBpos());
			if (contraptionA != null) ContraptionAttachment.attachIfMissing(level, contraptionA).notifyNewConduit(this, 0);
			if (contraptionB != null) ContraptionAttachment.attachIfMissing(level, contraptionB).notifyNewConduit(this, 1);
		}
		return this;
	}
	
	public PlacedConduit dismantle(Level level) {
		this.conduit.dismantleShape(level, this);
		this.shape = null;
		return this;
	}
	
	public void updateShape(Level level) {
		assert this.shape != null : "Can't update un-build conduit!";
		this.conduit.updatePhysicalNodes(level, this);
	}
	
	public boolean updateContraptions(Level level, PhysShip contraption, int nodeId) {
		if (this.shape == null) return false;
		this.conduit.updateContraptionForces(level, contraption, this, nodeId);
		return true;
	}
	
	public CompoundTag save() {
		CompoundTag tag = new CompoundTag();
		tag.put("Position", this.position.writeNBT(new CompoundTag()));
		tag.putString("Conduit", this.conduit.getRegistryName().toString());
		tag.putDouble("Length", this.length);
		if (this.shape != null) tag.put("Shape", this.shape.save());
		return tag;
	}
	
	public static PlacedConduit load(CompoundTag tag) {
		ResourceLocation conduitName = new ResourceLocation(tag.getString("Conduit"));
		Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(conduitName);
		if (conduit == null) return null;
		ConduitPos position = ConduitPos.readNBT(tag.getCompound("Position"));
		double length = tag.getDouble("Length");
		ConduitShape shape = tag.contains("Shape") ? ConduitShape.load(tag.getCompound("Shape")) : null;
		if (shape == null) return null;
		PlacedConduit state = new PlacedConduit(position, conduit, length);
		state.setShape(shape);
		return state;
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
