package de.m_marvin.industria.content.blocks.machines;

import de.m_marvin.industria.content.blockentities.machines.PortableCoalGeneratorBlockEntity;
import de.m_marvin.industria.core.util.blocks.BaseEntityMultiBlock;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PortableCoalGeneratorBlock extends BaseEntityMultiBlock {

	public PortableCoalGeneratorBlock(Properties pProperties) {
		super(pProperties, 2, 1, 1);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.LIT);
		super.createBlockStateDefinition(pBuilder);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		if (getMBPos(pState).equals(new Vec3i(0, 0, 0))) {
			return new PortableCoalGeneratorBlockEntity(pPos, pState);
		}
		return null;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
}
