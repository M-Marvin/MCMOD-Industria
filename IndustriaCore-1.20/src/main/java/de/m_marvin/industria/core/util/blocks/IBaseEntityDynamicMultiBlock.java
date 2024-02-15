package de.m_marvin.industria.core.util.blocks;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBaseEntityDynamicMultiBlock {

	public List<BlockPos> findMultiBlockEntityBlocks(Level level, BlockPos pos, BlockState state);
	
}
