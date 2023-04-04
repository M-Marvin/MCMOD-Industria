package de.m_marvin.industria.core.conduits.types.conduits;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import de.m_marvin.industria.core.electrics.types.IElectric;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface IElectricConduit extends IElectric<PlacedConduit, ConduitPos, Conduit> {
	
	@Override
	default void serializeNBT(PlacedConduit instance, ConduitPos position, CompoundTag nbt) {
		nbt.put("Position", position.writeNBT(new CompoundTag()));
	}

	@Override
	default PlacedConduit deserializeNBTInstance(CompoundTag nbt) {
		return PlacedConduit.load(nbt.getCompound("State"));
	}
	
	@Override
	default ConduitPos deserializeNBTPosition(CompoundTag nbt) {
		return ConduitPos.readNBT(nbt.getCompound("Position"));
	}
	
	@Override
	default NodePos[] getConnections(Level level, ConduitPos pos, PlacedConduit instance) {
		return new NodePos[] { new NodePos(pos.getNodeApos(), 0), new NodePos(pos.getNodeBpos(), 1) };
	}
	
}
