package de.m_marvin.industria.core.conduits.types.conduits;

import java.util.Optional;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.IElectric;
import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public interface IElectricConduit extends IElectric<ConduitEntity, ConduitPos, Conduit> {
	
	@Override
	default void serializeNBTPosition(ConduitPos position, CompoundTag nbt) {
		nbt.put("Position", position.writeNBT(new CompoundTag()));
	}
	
	@Override
	default void serializeNBTInstance(ConduitEntity instance, CompoundTag nbt) {
		nbt.put("State", instance.save());
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
	
	@Override
	default ChunkPos getChunkPos(ConduitPos position) {
		BlockPos pos = MathUtility.getMiddleBlock(position.getNodeApos(), position.getNodeBpos());
		return new ChunkPos(pos);
	}
	
	@Override
	default Optional<ConduitEntity> getInstance(Level level, ConduitPos pos) {
		return ConduitUtility.getConduit(level, pos);
	}
	
}
