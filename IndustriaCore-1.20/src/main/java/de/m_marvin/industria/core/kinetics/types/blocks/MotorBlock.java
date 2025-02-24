package de.m_marvin.industria.core.kinetics.types.blocks;

import de.m_marvin.industria.core.kinetics.types.blockentities.MotorBlockEntity;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MotorBlock extends BaseEntityBlock implements IKineticBlock {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public static final VoxelShape SHAPE = Shapes.or(VoxelShapeUtility.box(6, 6, 0, 10, 10, 1), VoxelShapeUtility.box(3, 3, 1, 13, 13, 15));
	public static final VoxelShape SHAPE_HORIZONTAL = Shapes.or(VoxelShapeUtility.box(0, 0, 3, 16, 3, 13), VoxelShapeUtility.box(6, 6, 0, 10, 10, 1), VoxelShapeUtility.box(3, 3, 1, 13, 13, 15));
	
	public MotorBlock(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}
	
	@Override
	public TransmissionNode[] getTransmissionNodes(LevelAccessor level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(FACING);
		return new TransmissionNode[] {
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, facing.getAxis(), null, facing.getAxisDirection(), SHAFT)
		};
	}
	
	@Override
	public double getTorque(LevelAccessor level, BlockPos pos, int partId, BlockState state) {
		if (level.getBlockEntity(pos) instanceof MotorBlockEntity motor) {
			return motor.getSourceTorque();
		}
		return 0.0;
	}
	
	@Override
	public double getSourceSpeed(LevelAccessor level, BlockPos pos, int partId, BlockState state) {
		if (level.getBlockEntity(pos) instanceof MotorBlockEntity motor) {
			return motor.getSourceRPM();
		}
		return 0.0;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING);
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return VoxelShapeUtility.stateCachedShape(ShapeType.MISC, pState, () -> {
			Direction facing = pState.getValue(FACING);
			return VoxelShapeUtility.transformation()
					.centered()
					.rotateFromNorth(facing)
					.uncentered()
					.transform(facing.getAxis() == Axis.Y ? SHAPE : SHAPE_HORIZONTAL);
		});
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction facing = pContext.getNearestLookingDirection();
		return this.defaultBlockState().setValue(FACING, facing);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new MotorBlockEntity(pPos, pState);
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.setValue(FACING, pMirror.mirror(pState.getValue(FACING)));
	}
	
}
