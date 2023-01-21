package de.m_marvin.industria.blocks;

import com.simibubi.create.content.contraptions.components.motor.CreativeMotorBlock;
import com.simibubi.create.foundation.block.ITE;

import de.m_marvin.industria.blockentities.MotorBlockEntity;
import de.m_marvin.industria.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ElectricMotorBlock extends BaseDirectionalKineticBlock implements EntityBlock, ITE<MotorBlockEntity> {

	public ElectricMotorBlock(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(BlockStateProperties.FACING).getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == getRotationAxis(state);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new MotorBlockEntity(pPos, pState);
	}

	@Override
	public Class<MotorBlockEntity> getTileEntityClass() {
		return MotorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends MotorBlockEntity> getTileEntityType() {
		return ModBlockEntities.MOTOR.get();
	}
	
}
