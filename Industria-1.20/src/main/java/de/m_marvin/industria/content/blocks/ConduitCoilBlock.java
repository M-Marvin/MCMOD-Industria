package de.m_marvin.industria.content.blocks;

import de.m_marvin.industria.content.blockentities.ConduitCoilBlockEntity;
import de.m_marvin.industria.content.items.ConduitCoilItem;
import de.m_marvin.industria.content.registries.ModBlockStateProperties;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConduitCoilBlock extends BaseEntityBlock {
	
	public static final VoxelShape SHAPE_0 = Shapes.join(Shapes.or(VoxelShapeUtility.box(0, 0, 0, 16, 16, 2), VoxelShapeUtility.box(0, 0, 14, 16, 16, 16), VoxelShapeUtility.box(5, 5, 2, 11, 11, 14)), VoxelShapeUtility.box(6, 6, 0, 10, 10, 16), BooleanOp.ONLY_FIRST);
	public static final VoxelShape SHAPE_1 = Shapes.join(Shapes.or(VoxelShapeUtility.box(0, 0, 0, 16, 16, 2), VoxelShapeUtility.box(0, 0, 14, 16, 16, 16), VoxelShapeUtility.box(4, 4, 2, 12, 12, 14)), VoxelShapeUtility.box(6, 6, 0, 10, 10, 16), BooleanOp.ONLY_FIRST);
	public static final VoxelShape SHAPE_2 = Shapes.join(Shapes.or(VoxelShapeUtility.box(0, 0, 0, 16, 16, 2), VoxelShapeUtility.box(0, 0, 14, 16, 16, 16), VoxelShapeUtility.box(3, 3, 2, 13, 13, 14)), VoxelShapeUtility.box(6, 6, 0, 10, 10, 16), BooleanOp.ONLY_FIRST);
	public static final VoxelShape SHAPE_3 = Shapes.join(Shapes.or(VoxelShapeUtility.box(0, 0, 0, 16, 16, 2), VoxelShapeUtility.box(0, 0, 14, 16, 16, 16), VoxelShapeUtility.box(2, 2, 2, 14, 14, 14)), VoxelShapeUtility.box(6, 6, 0, 10, 10, 16), BooleanOp.ONLY_FIRST);
	public static final VoxelShape SHAPE_4 = Shapes.join(Shapes.or(VoxelShapeUtility.box(0, 0, 0, 16, 16, 2), VoxelShapeUtility.box(0, 0, 14, 16, 16, 16), VoxelShapeUtility.box(1, 1, 2, 15, 15, 14)), VoxelShapeUtility.box(6, 6, 0, 10, 10, 16), BooleanOp.ONLY_FIRST);
	
	protected final boolean holdsConduits;
	
	public ConduitCoilBlock(Properties pProperties, boolean holdConduits) {
		super(pProperties);
		this.holdsConduits = holdConduits;
	}

	public boolean holdsConduits() {
		return holdsConduits;
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		VoxelShape shape = SHAPE_0;
		if (holdsConduits) {
			switch (pState.getValue(ModBlockStateProperties.LAYERS)) {
			case 1: shape = SHAPE_1; break;
			case 2: shape = SHAPE_2; break;
			case 3: shape = SHAPE_3; break;
			case 4: shape = SHAPE_4; break;
			}
		}
		return VoxelShapeUtility.transformation()
				.centered()
				.rotateFromNorth(Direction.fromAxisAndDirection(pState.getValue(BlockStateProperties.AXIS), AxisDirection.POSITIVE))
				.uncentered()
				.transform(shape);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.AXIS);
		pBuilder.add(ModBlockStateProperties.LAYERS);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState().setValue(BlockStateProperties.AXIS, pContext.getNearestLookingDirection().getAxis());
		if (holdsConduits) {
			int wireLength = Math.max(1, Math.min(4, (int) (pContext.getItemInHand().getOrCreateTag().getInt("WireLength") / (float) ConduitCoilItem.MAX_WIRES_ON_COIL * 4)));
			state = state.setValue(ModBlockStateProperties.LAYERS, wireLength);
		}
		return state;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return holdsConduits ? new ConduitCoilBlockEntity(pPos, pState) : null;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
}
