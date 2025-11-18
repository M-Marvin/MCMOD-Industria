package de.m_marvin.industria.core.conduits.types.conduits;

import com.google.common.base.Objects;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitState;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class ConduitEntity {
	
	protected Level level;
	protected ConduitPos position;
	protected ConduitState state;
	protected ConduitShape shape;
	protected float length;
	
	public ConduitEntity(ConduitPos position, float length) {
		this.position = position;
		this.length = length;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setConduitState(ConduitState state) {
		this.state = state;
	}
	
	public ConduitState getConduitState() {
		return state;
	}
	
	public ConduitEntity build() {
		this.state.onBuild(this);
		this.shape = this.state.buildShape(this);
		updateShape();
		return this;
	}
	
	public ConduitEntity dismantle() {
		if (this.shape == null) return this;
		this.state.onDismantle(this);
		this.state.dismantleShape(this);
		this.shape = null;
		return this;
	}
	
	public void updateShape() {
		assert this.shape != null : "Can't update un-build conduit!";
		this.state.updateShape(this);
	}
	
	public CompoundTag save() {
		return save(BlockPos.ZERO);
	}
	
	public CompoundTag save(BlockPos relative) {
		if (this.state == null) return null;
		CompoundTag tag = new CompoundTag();
		tag.put("Position", this.position.writeNBT(new CompoundTag(), relative));
		tag.put("Conduit", this.state.writeNbt());
		tag.putFloat("Length", this.length);
		if (this.shape != null) 
			tag.put("Shape", this.shape.save());
		this.saveAdditional(tag);
		return tag;
	}
	
	public static ConduitEntity load(CompoundTag tag) {
		return load(tag, BlockPos.ZERO);
	}
	
	public static ConduitEntity load(CompoundTag tag, BlockPos relative) {
		ConduitState conduitState = ConduitState.loadNbt(tag.getCompound("Conduit"));
		if (conduitState == null)
			return null;
		ConduitPos position = ConduitPos.readNBT(tag.getCompound("Position"), relative);
		float length = tag.getFloat("Length");
		ConduitShape shape = tag.contains("Shape") ? ConduitShape.load(tag.getCompound("Shape")) : null;
		ConduitEntity conduitEntity = conduitState.getConduit().newConduitEntity(position, length);
		conduitEntity.setConduitState(conduitState);
		if (shape != null)
			conduitEntity.setShape(shape);
		conduitEntity.loadAdditional(tag);
		return conduitEntity;
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
	
	public ConduitPos getPosition() {
		return position;
	}
	
	public float getLength() {
		return length;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConduitEntity other) {
			return 	other.getPosition().equals(this.getPosition()) &
					Objects.equal(this.state, other.state);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "ConduitEntity{conduit=" + this.state.toString() + 
				",length=" + this.length +
				",position=" + this.position.toString() + 
				"}";
 	}
	
}
