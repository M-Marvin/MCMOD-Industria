package de.m_marvin.industria.core.magnetism.types.blocks;

import org.joml.Vector3f;

import de.m_marvin.industria.core.parametrics.BlockParametricsManager;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public interface IMagneticBlock {

	public default Vec3d getFieldVector(Level level, BlockState state, BlockPos blockPos) {
		Vec3d vector = BlockParametricsManager.getInstance().getParametrics(state.getBlock()).getMagneticVector();
		if (state.getProperties().contains(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			vector = Vec3d.fromVec(facing.getRotation().transform(vector.writeTo(new Vector3f())));
		}
		return vector;
	}
	
	public default double getCoefficient(Level level, BlockState state, BlockPos pos) {
		return BlockParametricsManager.getInstance().getParametrics(state.getBlock()).getMagneticCoefficient();
	}
	
}
