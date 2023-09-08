package de.m_marvin.industria.core.util.blocks;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockEntity<T extends BlockEntity> extends BlockEntity {
	
	private final BlockEntityType<T> type;
	
	public MultiBlockEntity(BlockEntityType<T> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		this.type = pType;
	}
	
	public T getMaster() {
		BlockState state = getBlockState();
		if (state.getBlock() instanceof BaseEntityMultiBlock multiBlock) {
			BlockPos masterPos = multiBlock.getMasterBlockEntityBlock(state, this.worldPosition);
			Optional<T> blockEntity = this.level.getBlockEntity(masterPos, this.type);
			if (blockEntity.isPresent()) return blockEntity.get();
		}
		return null;
	}
	
	public boolean isMaster() {
		BlockState state = getBlockState();
		if (state.getBlock() instanceof BaseEntityMultiBlock multiBlock) {
			return multiBlock.getMasterBlockEntityBlock(state, worldPosition).equals(worldPosition);
		}
		return false;
	}
	
}
