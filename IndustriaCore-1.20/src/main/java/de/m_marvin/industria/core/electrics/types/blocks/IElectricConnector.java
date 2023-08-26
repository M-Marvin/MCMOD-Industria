package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.Optional;

import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.electrics.types.IElectric;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface IElectricConnector extends IConduitConnector, IElectric<BlockState, BlockPos, Block> {
	
	@Override
	default void serializeNBTInstance(BlockState instance, CompoundTag nbt) {
		nbt.put("State", NbtUtils.writeBlockState(instance));
	}
	
	@Override
	default void serializeNBTPosition(BlockPos position, CompoundTag nbt) {
		nbt.put("Position", NbtUtils.writeBlockPos(position));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	default BlockState deserializeNBTInstance(CompoundTag nbt) {
		return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound("State"));
	}

	@Override
	default BlockPos deserializeNBTPosition(CompoundTag nbt) {
		return NbtUtils.readBlockPos(nbt.getCompound("Position"));
	}
	
	@Override
	default boolean isWire() {
		return false;
	}

	@Override
	default ChunkPos getAffectedChunk(Level level, BlockPos position) {
		return new ChunkPos(position);
	}
	
	@Override
	default Optional<BlockState> getInstance(Level level, BlockPos pos) {
		return Optional.of(level.getBlockState(pos));
	}
	
}
