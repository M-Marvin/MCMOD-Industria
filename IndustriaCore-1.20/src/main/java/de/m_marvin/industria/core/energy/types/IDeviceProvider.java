package de.m_marvin.industria.core.energy.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IDeviceProvider {
	
	default public void onDeviceChange(Level level, BlockPos pos, BlockState stateOld, BlockState state) {
		if (stateOld == null)
			onDeviceInstalled(level, pos, state);
	}
	
	default public void onDeviceInstalled(Level level, BlockPos pos, BlockState sate) {}
	
}
