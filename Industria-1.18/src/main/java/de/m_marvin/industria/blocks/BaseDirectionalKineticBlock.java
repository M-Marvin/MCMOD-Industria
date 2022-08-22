package de.m_marvin.industria.blocks;

import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseDirectionalKineticBlock extends DirectionalKineticBlock {

	public BaseDirectionalKineticBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public abstract Axis getRotationAxis(BlockState state);

	@Override
	public abstract boolean hasShaftTowards(final LevelReader world, final BlockPos pos, final BlockState state, final Direction face);

}
