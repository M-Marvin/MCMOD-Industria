package de.m_marvin.industria.content.blocks.kinetics;

import de.m_marvin.industria.content.blockentities.kinetics.BaseSimpleKineticBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blocks.ShortShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BaseShortShaftBlock extends ShortShaftBlock {

	public BaseShortShaftBlock(boolean isLong, Properties pProperties) {
		super(isLong, pProperties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new BaseSimpleKineticBlockEntity(pPos, pState);
	}
	
}
