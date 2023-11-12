package de.m_marvin.industria.core.magnetism.types.blocks;

import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MagnetBlock extends Block implements IMagneticBlock {

	public MagnetBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return defaultBlockState().setValue(BlockStateProperties.FACING, GameUtility.getFacingDirection(pContext.getPlayer()).getOpposite());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING);
	}
	
	@Override
	public Vec3d getFieldVector(Level level, BlockState state, BlockPos blockPos) {
		switch (state.getValue(BlockStateProperties.FACING)) {
		case NORTH:
			return new Vec3d(0, 0, -1);
		case SOUTH:
			return new Vec3d(0, 0, 1);
		case EAST:
			return new Vec3d(1, 0, 0);
		case WEST:
			return new Vec3d(-1, 0, 0);
		case UP:
			return new Vec3d(0, 1, 0);
		case DOWN:
			return new Vec3d(0, -1, 0);
		default:
			return new Vec3d();
		}
	}

	@Override
	public boolean isAlternating(Level level, BlockState state, BlockPos blockPos) {
		return false;
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.setValue(BlockStateProperties.FACING, pMirror.mirror(pState.getValue(BlockStateProperties.FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(BlockStateProperties.FACING, pRotation.rotate(pState.getValue(BlockStateProperties.FACING)));
	}
	
}
