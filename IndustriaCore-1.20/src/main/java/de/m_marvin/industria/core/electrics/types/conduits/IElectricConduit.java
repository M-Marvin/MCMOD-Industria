package de.m_marvin.industria.core.electrics.types.conduits;

import java.util.Optional;

import de.m_marvin.industria.core.conduits.ConduitUtility;
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

public interface IElectricConduit extends IElectric<ConduitEntity, Conduit> {
	
	@Override
	default void serializeNBT(ConduitEntity instance, CompoundTag nbt) {
		nbt.put("State", instance.save());
	}
	
	@Override
	default ConduitEntity deserializeNBT(CompoundTag nbt) {
		return ConduitEntity.load(nbt.getCompound("State"));
	}
	
	@Override
	default NodePos[] getElectricConnections(Level level, ElectricReference reference, ConduitEntity instance) {
		return new NodePos[] { reference.conduit().getNodeA(), reference.conduit().getNodeB() };
	}
	
	@Override
	default double getCurrentPower(Level level, ElectricReference reference, ConduitEntity instance) {
		return 0;
	}
	
	@Override
	default double getMaxPowerGeneration(Level level, ElectricReference reference, ConduitEntity instance) {
		return 0;
	}
	
	public int getWireCount();
	
	@Override
	default ChunkPos getAffectedChunk(Level level, ElectricReference reference) {
		BlockPos middlePos = MathUtility.getMiddleBlock(
				ContraptionUtility.ensureWorldBlockCoordinates(level, reference.conduit().getNodeApos(), reference.conduit().getNodeApos()),
				ContraptionUtility.ensureWorldBlockCoordinates(level, reference.conduit().getNodeApos(), reference.conduit().getNodeApos()));
		return new ChunkPos(middlePos);
	}
	
	@Override
	default Optional<ConduitEntity> getInstance(Level level, ElectricReference reference) {
		return ConduitUtility.getConduit(level, reference.conduit());
	}
	
	@Override
	default boolean isInstanceValid(Level level, ConduitEntity instance) {
		return true;
	}
	
}
