package de.m_marvin.industria.content.blockentities.machines;

import de.m_marvin.industria.content.blockentities.kinetics.BaseBeltBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ConveyorBeltBlockEntity extends BaseBeltBlockEntity {
	
	public ConveyorBeltBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.CONVEYOR_BELT.get(), pPos, pBlockState);
	}

}
