package de.m_marvin.industria.core.energy.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockDeviceProvider extends IDeviceProvider {
	
	public boolean hasDeviceConnection(Level level, BlockPos pos, BlockState state, Direction side);
	
}
