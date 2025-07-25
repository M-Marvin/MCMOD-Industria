package de.m_marvin.industria.content.blocks.kinetics;

import de.m_marvin.industria.content.blockentities.kinetics.BaseBeltBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blocks.BeltBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BaseBeltBlock extends BeltBlock {

	public BaseBeltBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new BaseBeltBlockEntity(pPos, pState);
	}
	
}
