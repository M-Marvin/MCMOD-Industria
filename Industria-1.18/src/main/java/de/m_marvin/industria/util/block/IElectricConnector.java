package de.m_marvin.industria.util.block;

import de.m_marvin.industria.util.electricity.IElectric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IElectricConnector extends IConduitConnector, IElectric<BlockState, BlockPos> {
		
}
