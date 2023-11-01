package de.m_marvin.industria.content.magnetism.types.blocks;

import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IMagneticBlock {

	public Vec3d getFieldVector(Level level, BlockState state, BlockPos blockPos);

	public boolean isAlternating(Level level, BlockState state, BlockPos blockPos);

}
