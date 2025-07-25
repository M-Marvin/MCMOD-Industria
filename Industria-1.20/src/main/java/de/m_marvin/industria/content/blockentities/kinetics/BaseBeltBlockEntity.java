package de.m_marvin.industria.content.blockentities.kinetics;

import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BaseBeltBlockEntity extends BeltBlockEntity {

	public BaseBeltBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.BASE_BELT.get(), pPos, pBlockState);
	}

	public BaseBeltBlockEntity(BlockEntityType<?> pType,  BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}
	
}
