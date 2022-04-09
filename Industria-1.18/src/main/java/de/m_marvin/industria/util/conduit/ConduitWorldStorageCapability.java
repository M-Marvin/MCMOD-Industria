package de.m_marvin.industria.util.conduit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.conduit.IFlexibleConnection.ConnectionPoint;
import de.m_marvin.industria.util.conduit.IFlexibleConnection.PlacedConduit;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ConduitWorldStorageCapability implements ICapabilitySerializable<ListTag> {
	
	private LazyOptional<ConduitWorldStorageCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ModCapabilities.CONDUIT_HOLDER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private List<PlacedConduit> conduits = new ArrayList<>();
	
	@Override
	public ListTag serializeNBT() {
		ListTag tag = new ListTag();
		for (PlacedConduit con : conduits) {
			CompoundTag conTag = con.save();
			if (conTag != null) tag.add(conTag);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(ListTag tag) {
		this.conduits.clear();
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag conTag = tag.getCompound(i);
			PlacedConduit con = PlacedConduit.load(conTag);
			if (con != null) this.conduits.add(con);
		}
	}
	
	public boolean addConduit(BlockGetter level, ConnectionPoint nodeA, ConnectionPoint nodeB, Conduit conduit) {
		if (getConduit(nodeA).isPresent() || getConduit(nodeB).isPresent()) {
			return false;
		} else {
			PlacedConduit connection = new PlacedConduit(nodeA.position(), nodeA.connectionId(), nodeB.position(), nodeB.connectionId(), conduit).build(level);
			this.conduits.add(connection);
			return true;
		}
	}
	
	public Optional<PlacedConduit> getConduit(ConnectionPoint node) {
		for (PlacedConduit con : this.conduits) {
			if (con.getNodeA().equals(node.position())) {
				return Optional.of(con);
			}
		}
		return Optional.empty();
	}
	
	public List<PlacedConduit> getConduits() {
		return this.conduits;
	}
	
}
