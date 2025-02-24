package de.m_marvin.industria.core.kinetics.types.blockentities;

import de.m_marvin.industria.core.registries.BlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BeltBlockEntity extends SimpleKineticBlockEntity {

	public BeltBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(BlockEntityTypes.BELT.get(), pPos, pBlockState, 0.0);
	}

}
