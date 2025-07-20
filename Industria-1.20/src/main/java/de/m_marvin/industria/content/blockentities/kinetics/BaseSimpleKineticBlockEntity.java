package de.m_marvin.industria.content.blockentities.kinetics;

import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.kinetics.types.blockentities.SimpleKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BaseSimpleKineticBlockEntity extends SimpleKineticBlockEntity {

	public BaseSimpleKineticBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	public BaseSimpleKineticBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, double rotationalOffset) {
		super(pType, pPos, pBlockState, rotationalOffset);
	}

	public BaseSimpleKineticBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.BASE_SIMPLE_KINETIC.get(), pPos, pBlockState);
	}

	public BaseSimpleKineticBlockEntity(BlockPos pPos, BlockState pBlockState, double rotationalOffset) {
		super(ModBlockEntityTypes.BASE_SIMPLE_KINETIC.get(), pPos, pBlockState, rotationalOffset);
	}

}
