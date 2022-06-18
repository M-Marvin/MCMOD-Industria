package de.m_marvin.industria.util.conduit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.network.SSyncPlacedConduit;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.block.IConduitConnector.ConnectionPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ConduitWorldStorageCapability implements ICapabilitySerializable<ListTag> {
	
	private LazyOptional<ConduitWorldStorageCapability> holder = LazyOptional.of(() -> this);
	
	@SubscribeEvent
	public static void onClientWorldTick(ClientTickEvent event) {
		
		@SuppressWarnings("resource")
		ClientLevel level = Minecraft.getInstance().level;
		
		if (level != null) {
			LazyOptional<ConduitWorldStorageCapability> conduitCap = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
			if (conduitCap.isPresent()) {
				conduitCap.resolve().get().update(level);
			}
		}
		
	}
	
	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		
		Level level = event.world;
		
		LazyOptional<ConduitWorldStorageCapability> conduitCap = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitCap.isPresent()) {
			conduitCap.resolve().get().update(level);
		}
		
	}
	
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
	
	public boolean addConduit(Level level, ConnectionPoint nodeA, ConnectionPoint nodeB, Conduit conduit, int nodesPerBlock) {
		if (getConduit(nodeA).isPresent() || getConduit(nodeB).isPresent()) {
			return false;
		} else {
			PlacedConduit conduitState = new PlacedConduit(nodeA.position(), nodeA.connectionId(), nodeB.position(), nodeB.connectionId(), conduit, nodesPerBlock).build(level);
			this.conduits.add(conduitState);
			Industria.NETWORK.send(PacketDistributor.DIMENSION.with(() -> level.dimension()), new SSyncPlacedConduit(conduitState));
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
	
	public void update(BlockGetter level) {
		
		for (PlacedConduit conduit : this.getConduits()) {
			conduit.updateShape(level);
		}
		
	}
	
}
