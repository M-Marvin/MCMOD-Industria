package de.m_marvin.industria.core.electrics.types.blocks;

import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.electrics.types.IElectric;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface IElectricConnector extends IConduitConnector, IElectric<BlockState, BlockPos, Block> {

	@Override
	default void serializeNBT(BlockState instance, BlockPos position, CompoundTag nbt) {
		nbt.put("State", NbtUtils.writeBlockState(instance));
		nbt.put("Position", NbtUtils.writeBlockPos(position));
	}

	@Override
	default BlockState deserializeNBTInstance(CompoundTag nbt) {
		return NbtUtils.readBlockState(nbt.getCompound("State"));
	}

	@Override
	default BlockPos deserializeNBTPosition(CompoundTag nbt) {
		return NbtUtils.readBlockPos(nbt.getCompound("Position"));
	}
	
	@Override
	default boolean isWire() {
		return false;
	}
	
}
