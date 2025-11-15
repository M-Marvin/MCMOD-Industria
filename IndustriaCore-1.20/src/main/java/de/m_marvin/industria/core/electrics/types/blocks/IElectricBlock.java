package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.Optional;

import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.electrics.types.IElectric;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface IElectricBlock extends IConduitConnector, IElectric<BlockState, Block> {
	
	@Override
	default void serializeNBT(BlockState instance, CompoundTag nbt) {
		nbt.put("State", NbtUtils.writeBlockState(instance));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	default BlockState deserializeNBT(CompoundTag nbt) {
		return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound("State"));
	}

	@Override
	default boolean isWire() {
		return false;
	}

	@Override
	default ChunkPos getAffectedChunk(Level level, ElectricReference reference) {
		return new ChunkPos(reference.block());
	}
	
	@Override
	default Optional<BlockState> getInstance(Level level, ElectricReference reference) {
		BlockState state = level.getBlockState(reference.block());
		if (state.isAir()) return Optional.empty();
		return Optional.of(level.getBlockState(reference.block()));
	}
	
	@Override
	default boolean isInstanceValid(Level level, BlockState instance) {
		return !instance.isAir();
	}
	
}
