package de.m_marvin.industria.content.blockentities.machines;

import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PortableCoalGeneratorBlockEntity extends BlockEntity {

	public PortableCoalGeneratorBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.PORTABLE_COAL_GENERATOR.get(), pPos, pBlockState);
	}

}
