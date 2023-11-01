package de.m_marvin.industria.content.magnetism.types;

import de.m_marvin.industria.content.magnetism.types.blocks.IMagneticBlock;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MagneticFieldInfluence {
	
	public final BlockPos blockPos;
	
	public MagneticFieldInfluence(BlockPos pos) {
		this.blockPos = pos;
	}

	public Vec3d getVektor(Level level) {
		BlockState state = level.getBlockState(blockPos);
		if (state.getBlock() instanceof IMagneticBlock magneticBlock) {
			return magneticBlock.getFieldVector(level, state, this.blockPos);
		}
		return new Vec3d(0, 0, 0);
	}
	
	public boolean isAlternating(Level level) {
		BlockState state = level.getBlockState(blockPos);
		if (state.getBlock() instanceof IMagneticBlock magneticBlock) {
			return magneticBlock.isAlternating(level, state, this.blockPos);
		}
		return false;
	}
	
}
