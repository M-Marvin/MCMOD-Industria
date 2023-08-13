package de.m_marvin.industria.core.conduits.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PlacedConduit {
	
	public static class ConduitStateStorage<T, N extends Tag> {
		
		protected T data;
		protected Function<T, N> serializer;
		protected Function<N, T> deserializer;
		
		public ConduitStateStorage(T initialValue, Function<T, N> serializer, Function<N, T> deserializer) {
			this.data = initialValue;
			this.serializer = serializer;
			this.deserializer = deserializer;
		}
		
		public T getData() {
			return data;
		}
		
		public void setData(T data) {
			this.data = data;
		}
		
		public Tag serialize() {
			return this.serializer.apply(data);
		}
		
		@SuppressWarnings("unchecked")
		public void deserialize(Tag tag) {
			this.data = this.deserializer.apply((N) tag);
		}
		
	}
	
	private Map<String, ConduitStateStorage<?, ? extends Tag>> dataStorages;
	private ConduitPos position;
	private double length;
	private Conduit conduit;
	private ConduitShape shape;
	
	public PlacedConduit(ConduitPos position, Conduit conduit, double length) {
		this.position = position;
		this.conduit = conduit;
		this.length = length;
		this.dataStorages = new HashMap<>();
	}
	
	public void addDataStorage(String name, ConduitStateStorage<?, ? extends Tag> storage) {
		this.dataStorages.put(name, storage);
	}
	
	public void removeDataStorage(String name) {
		this.dataStorages.remove(name);
	}
	
	public ConduitStateStorage<?, ? extends Tag> getDataStorage(String name) {
		return this.dataStorages.get(name);
	}
	
	public Object getStateData(String name) {
		return this.dataStorages.containsKey(name) ? getDataStorage(name).getData() : null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> void setStateData(String name, T value) {
		if (this.dataStorages.containsKey(name)) ((ConduitStateStorage<T, ?>) this.dataStorages.get(name)).setData(value);
	}
	
	public PlacedConduit build(Level level) {
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
	
	public PlacedConduit dismantle(Level level) {
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
		if (this.dataStorages.size() > 0) {
			CompoundTag storages = new CompoundTag();
			for (Entry<String, ConduitStateStorage<?, ? extends Tag>> storage : this.dataStorages.entrySet()) {
				storages.put(storage.getKey(), storage.getValue().serialize());
			}
			tag.put("Storages", storages);
		}
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
		state.conduit.onBuild(null, position, state);
		if (tag.contains("Storages")) {
			CompoundTag storages = tag.getCompound("Storages");
			for (String key : storages.getAllKeys()) {
				ConduitStateStorage<?, ? extends Tag> st = state.getDataStorage(key);
				if (st != null) st.deserialize(storages.get(key));
			}
		}
		return state;
	}
	
	// TODO Server->Client sync of state storages
	
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
