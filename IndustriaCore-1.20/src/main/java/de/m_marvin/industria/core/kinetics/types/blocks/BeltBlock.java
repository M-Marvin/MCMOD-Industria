package de.m_marvin.industria.core.kinetics.types.blocks;

import de.m_marvin.industria.core.compound.types.blocks.CompoundBlock;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility.ShapeType;
import de.m_marvin.industria.core.util.types.DiagonalDirection;
import de.m_marvin.industria.core.util.types.DiagonalPlanarDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeltBlock extends BaseEntityBlock implements IKineticBlock {
	
	public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;
	public static final EnumProperty<DiagonalPlanarDirection> ORIENTATION = Blocks.PROP_PLANAR_ORIENTATION;
	public static final BooleanProperty IS_END = Blocks.PROP_IS_END;

	public static final VoxelShape SHAPE_STRAIGHT = Shapes.or(VoxelShapeUtility.box(1, 3, 0, 15, 4, 16), VoxelShapeUtility.box(1, 12, 0, 15, 13, 16));
	public static final VoxelShape SHAPE_STRAIGHT_END = Shapes.or(VoxelShapeUtility.box(1, 3, 4, 15, 4, 16), VoxelShapeUtility.box(1, 12, 4, 15, 13, 16), VoxelShapeUtility.box(1, 4, 3, 15, 12, 4));
	public static final VoxelShape SHAPE_SLOPE = Shapes.or(
			// TOP
			VoxelShapeUtility.box(1, 6, 0, 15, 8, 2),
			VoxelShapeUtility.box(1, 8, 2, 15, 10, 4),
			VoxelShapeUtility.box(1, 11.5F, 4, 15, 12, 6),
			VoxelShapeUtility.box(1, 10, 4, 15, 12, 4.5F),
			VoxelShapeUtility.box(1, 12, 6, 15, 14, 8),
			VoxelShapeUtility.box(1, 14, 8, 15, 16, 10),
			VoxelShapeUtility.box(1, 16, 10, 15, 18, 12),
			VoxelShapeUtility.box(1, 18, 12, 15, 20, 14),
			VoxelShapeUtility.box(1, 20, 14, 15, 22, 16),
			// BOTTOM
			VoxelShapeUtility.box(1, -6, 0, 15, -4, 2),
			VoxelShapeUtility.box(1, -4, 2, 15, -2, 4),
			VoxelShapeUtility.box(1, -2, 4, 15, 0, 6),
			VoxelShapeUtility.box(1, 0, 6, 15, 2, 8),
			VoxelShapeUtility.box(1, 2, 8, 15, 4, 10),
			VoxelShapeUtility.box(1, 4, 10, 15, 4.5F, 12),
			VoxelShapeUtility.box(1, 4, 11.5F, 15, 6, 12),
			VoxelShapeUtility.box(1, 6, 12, 15, 8, 14),
			VoxelShapeUtility.box(1, 8, 14, 15, 10, 16)
			);
	public static final VoxelShape SHAPE_SLOPE_END = Shapes.or(
			// TOP
			VoxelShapeUtility.box(1, 4, 4, 15, 12, 4.5F),
			VoxelShapeUtility.box(1, 11.5F, 4, 15, 12, 6),
			VoxelShapeUtility.box(1, 12, 6, 15, 14, 8),
			VoxelShapeUtility.box(1, 14, 8, 15, 16, 10),
			VoxelShapeUtility.box(1, 16, 10, 15, 18, 12),
			VoxelShapeUtility.box(1, 18, 12, 15, 20, 14),
			VoxelShapeUtility.box(1, 20, 14, 15, 22, 16),
			// BOTTOM
			VoxelShapeUtility.box(1, 4, 4, 15, 4.5F, 12),
			VoxelShapeUtility.box(1, 4, 11.5F, 15, 6, 12),
			VoxelShapeUtility.box(1, 6, 12, 15, 8, 14),
			VoxelShapeUtility.box(1, 8, 14, 15, 10, 16)
			);
	
	public BeltBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(AXIS);
		pBuilder.add(ORIENTATION);
		pBuilder.add(IS_END);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		return state.getValue(AXIS) != Axis.Y && state.getValue(ORIENTATION).isDiagonal();
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		
		return VoxelShapeUtility.stateCachedShape(ShapeType.MISC, pState, () -> {
			
			Axis axis = pState.getValue(AXIS);
			DiagonalPlanarDirection orientation = pState.getValue(ORIENTATION);
			
			if (orientation.isDiagonal()) {
				
				VoxelShape shape = pState.getValue(IS_END) ? SHAPE_SLOPE_END : SHAPE_SLOPE;
				
				int angle = orientation.getAngleFromPositiveX() - 45;
				if (axis == Axis.Z) angle = -angle + 90;
				if (axis == Axis.Y) angle -= 90;
				
				return VoxelShapeUtility.transformation()
						.centered()
						.rotateFromAxisX(axis)
						.rotateAround(axis, -angle)
						.uncentered()
						.transform(shape);
				
			} else {

				VoxelShape shape = pState.getValue(IS_END) ? SHAPE_STRAIGHT_END : SHAPE_STRAIGHT;

				int angle = orientation.getAngleFromPositiveX();
				if (axis == Axis.Y) angle -= 90;
				
				return VoxelShapeUtility.transformation()
						.centered()
						.rotateFromAxisX(axis)
						.rotateAround(axis, angle)
						.uncentered()
						.transform(shape);
				
			}
			
		});
		
	}
	
	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		DiagonalDirection direction1 = DiagonalDirection.fromPlanarAndAxis(pState.getValue(ORIENTATION), pState.getValue(AXIS));
		DiagonalDirection direction2 = direction1.getOposite();
		
		BlockPos pos1 = pPos.offset(direction1.getNormal().x, direction1.getNormal().y, direction1.getNormal().z);
		boolean valid1 = CompoundBlock.performOnAllAndCombine(pLevel, pos1, 
				() -> isValidConnectedBelt(pLevel.getBlockState(pos1), direction2), 
				(compound, part) -> isValidConnectedBelt(part.getState(), direction2), 
				CompoundBlock::trueIfAny);
		
		if (!valid1) return false;
		
		if (!pState.getValue(IS_END)) {
			BlockPos pos2 = pPos.offset(direction2.getNormal().x, direction2.getNormal().y, direction2.getNormal().z);
			boolean valid2 = CompoundBlock.performOnAllAndCombine(pLevel, pos2, 
					() -> isValidConnectedBelt(pLevel.getBlockState(pos2), direction1), 
					(compound, part) -> isValidConnectedBelt(part.getState(), direction1), 
					CompoundBlock::trueIfAny);
			
			return valid2;
		}
		
		return true;
	}
	
	public boolean isValidConnectedBelt(BlockState pState, DiagonalDirection direction) {
		if (pState.getBlock() instanceof BeltBlock) {
			DiagonalDirection d = DiagonalDirection.fromPlanarAndAxis(pState.getValue(ORIENTATION), pState.getValue(AXIS));
			if (direction == d) return true;
			if (direction == d.getOposite() && !pState.getValue(IS_END)) return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {		
		DiagonalDirection direction1 = DiagonalDirection.fromPlanarAndAxis(pState.getValue(ORIENTATION), pState.getValue(AXIS));
		BlockPos pos1 = pPos.offset(direction1.getNormal().x, direction1.getNormal().y, direction1.getNormal().z);
		DiagonalDirection direction2 = direction1.getOposite();
		BlockPos pos2 = pPos.offset(direction2.getNormal().x, direction2.getNormal().y, direction2.getNormal().z);
		
		if (pNeighborPos.equals(pos1) || (!pState.getValue(IS_END) && pNeighborPos.equals(pos2)))
			pLevel.scheduleTick(pPos, this, 1);
		
		super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		updateDiagonalBelts(pState, pLevel, pPos);
		super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
	}
	
	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!canSurvive(pState, pLevel, pPos))
			pLevel.destroyBlock(pPos, true);
	}
	
	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {}
	
	public void updateDiagonalBelts(BlockState pState, Level pLevel, BlockPos pPos) {
		DiagonalDirection direction1 = DiagonalDirection.fromPlanarAndAxis(pState.getValue(ORIENTATION), pState.getValue(AXIS));
		BlockPos pos1 = pPos.offset(direction1.getNormal().x, direction1.getNormal().y, direction1.getNormal().z);
		BlockState state1 = pLevel.getBlockState(pos1);
		pLevel.scheduleTick(pos1, state1.getBlock(), 1);
		
		if (!pState.getValue(IS_END)) {
			DiagonalDirection direction2 = direction1.getOposite();
			BlockPos pos2 = pPos.offset(direction2.getNormal().x, direction2.getNormal().y, direction2.getNormal().z);
			BlockState state2 = pLevel.getBlockState(pos2);
			pLevel.scheduleTick(pos2, state2.getBlock(), 1);
		}
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new BeltBlockEntity(pPos, pState);
	}

	@Override
	public TransmissionNode[] getTransmissionNodes(LevelAccessor level, BlockPos pos, BlockState state) {
		Axis axis = state.getValue(AXIS);
		DiagonalPlanarDirection orientation = state.getValue(ORIENTATION);
		DiagonalDirection connectDirection = DiagonalDirection.fromPlanarAndAxis(orientation, axis);
		if (state.getValue(IS_END)) {
			return new TransmissionNode[] {
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, connectDirection, BELT),
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, null, BELT_ATTACHMENT)
			};
		} else {
			return new TransmissionNode[] {
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, connectDirection, BELT),
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, connectDirection.getOposite(), BELT),
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, null, BELT_ATTACHMENT)
			};
		}
	}

}
