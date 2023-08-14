package de.m_marvin.industria.core.conduits.types.conduits;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.IElectric;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface IElectricConduit extends IElectric<ConduitEntity, ConduitPos, Conduit> {
	
	@Override
	default void serializeNBT(ConduitEntity instance, ConduitPos position, CompoundTag nbt) {
		nbt.put("Position", position.writeNBT(new CompoundTag()));
	}

	@Override
	default ConduitEntity deserializeNBTInstance(CompoundTag nbt) {
		return ConduitEntity.load(nbt.getCompound("State"));
	}
	
	@Override
	default ConduitPos deserializeNBTPosition(CompoundTag nbt) {
		return ConduitPos.readNBT(nbt.getCompound("Position"));
	}
	
	@Override
	default NodePos[] getConnections(Level level, ConduitPos pos, ConduitEntity instance) {
		return new NodePos[] { pos.getNodeA(), pos.getNodeB() };
	}
	
	public int getWireCount();
	
}
