package de.m_marvin.industria.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.IFlexibleConnection.ConnectionPoint;
import de.m_marvin.industria.util.IFlexibleConnection.FlexConnection;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public interface IConduitHolder {
	
	public boolean addConduit(ConnectionPoint nodeA, ConnectionPoint nodeB, Conduit conduit);
	public Optional<FlexConnection> getConduit(ConnectionPoint node);
	public List<FlexConnection> getConduits();
	
	public static class ConduitWorldStorageCapability implements IConduitHolder, ICapabilitySerializable<ListTag> {
		
		private LazyOptional<IConduitHolder> holder = LazyOptional.of(() -> this);
		
		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == ModCapabilities.CONDUIT_HOLDER_CAPABILITY) {
				return holder.cast();
			}
			return LazyOptional.empty();
		}
		
		private List<FlexConnection> conduits = new ArrayList<>();
		
		@Override
		public ListTag serializeNBT() {
			ListTag tag = new ListTag();
			for (FlexConnection con : conduits) {
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
				FlexConnection con = FlexConnection.load(conTag);
				if (con != null) this.conduits.add(con);
			}
		}

		@Override
		public boolean addConduit(ConnectionPoint nodeA, ConnectionPoint nodeB, Conduit conduit) {
			if (getConduit(nodeA).isPresent() || getConduit(nodeB).isPresent()) {
				return false;
			} else {
				FlexConnection connection = new FlexConnection(nodeA.position(), nodeA.connectionId(), nodeB.position(), nodeB.connectionId(), conduit);
				this.conduits.add(connection);
				return true;
			}
		}
		
		@Override
		public Optional<FlexConnection> getConduit(ConnectionPoint node) {
			for (FlexConnection con : this.conduits) {
				if (con.getNodeA().equals(node.position())) {
					return Optional.of(con);
				}
			}
			return Optional.empty();
		}
		
		@Override
		public List<FlexConnection> getConduits() {
			return this.conduits;
		}
		
	}
	
}
