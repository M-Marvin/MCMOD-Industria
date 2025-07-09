package de.m_marvin.industria.core.electrics.types.conduits;

import java.util.Optional;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.contraptions.ContraptionUtility;
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
	default NodePos[] getElectricConnections(Level level, ConduitPos pos, ConduitEntity instance) {
		return new NodePos[] { pos.getNodeA(), pos.getNodeB() };
	}
	
	@Override
	default double getCurrentPower(Level level, ConduitPos pos, ConduitEntity instance) {
		return 0;
	}
	
	@Override
	default double getMaxPowerGeneration(Level level, ConduitPos pos, ConduitEntity instance) {
		return 0;
	}
	
	public int getWireCount();
	
	@Override
	default ChunkPos getAffectedChunk(Level level, ConduitPos position) {
		BlockPos middlePos = MathUtility.getMiddleBlock(
				ContraptionUtility.ensureWorldBlockCoordinates(level, position.getNodeApos(), position.getNodeApos()),
				ContraptionUtility.ensureWorldBlockCoordinates(level, position.getNodeApos(), position.getNodeApos()));
		return new ChunkPos(middlePos);
	}
	
	@Override
	default Optional<ConduitEntity> getInstance(Level level, ConduitPos pos) {
		return ConduitUtility.getConduit(level, pos);
	}
	
	@Override
	default boolean isInstanceValid(Level level, ConduitEntity instance) {
		return true;
	}
	
}
