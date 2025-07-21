package de.m_marvin.industria.content.blocks.fences;

import de.m_marvin.industria.content.registries.ModTags;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModularFencePostBlock extends Block {
	
	public static final BooleanProperty IS_TOP = BooleanProperty.create("is_top");
	public static final BooleanProperty HAS_BASE = BooleanProperty.create("has_base");
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public static final VoxelShape SHAPE_POST = VoxelShapeUtility.box(5, 0, 5, 11, 16, 11);
	public static final VoxelShape SHAPE_POST_TOP = VoxelShapeUtility.box(5, 0, 5, 11, 11, 11);
	
	public ModularFencePostBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(this.stateDefinition.any().setValue(IS_TOP, false).setValue(HAS_BASE, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(IS_TOP, HAS_BASE, FACING);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return pState.getValue(IS_TOP) ? SHAPE_POST_TOP : SHAPE_POST;
	}

	public static boolean isMeshBlock(BlockGetter pLevel, BlockPos pPos) {
		BlockState state = pLevel.getBlockState(pPos);
		return state.is(ModTags.Blocks.MODULAR_FENCE_MESHES);
	}

	public static boolean isPostBlock(BlockGetter pLevel, BlockPos pPos) {
		BlockState state = pLevel.getBlockState(pPos);
		return state.is(ModTags.Blocks.MODULAR_FENCE_POSTS);
	}

	public static boolean isBaseBlock(BlockGetter pLevel, BlockPos pPos) {
		BlockState state = pLevel.getBlockState(pPos);
		return state.is(ModTags.Blocks.MODULAR_FENCE_BASES);
	}
	
	public static boolean isDirectionalFenceBlock(BlockGetter pLevel, BlockPos pPos) {
		BlockState state = pLevel.getBlockState(pPos);
		return	state.is(ModTags.Blocks.MODULAR_FENCE_POSTS) ||
				state.is(ModTags.Blocks.MODULAR_FENCE_BASES);
	}
	
	public static Direction getPlacementFacing(BlockPlaceContext pContext) {
		BlockPos above = pContext.getClickedPos().above();
		BlockPos below = pContext.getClickedPos().below();
		if (isDirectionalFenceBlock(pContext.getLevel(), above)) {
			return pContext.getLevel().getBlockState(above).getValue(FACING);
		}
		if (isDirectionalFenceBlock(pContext.getLevel(), below))
			return pContext.getLevel().getBlockState(below).getValue(FACING);
		return pContext.getHorizontalDirection();
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState();
		state = state.setValue(IS_TOP, !isPostBlock(pContext.getLevel(), pContext.getClickedPos().relative(Direction.UP)));
		return state.setValue(FACING, getPlacementFacing(pContext));
	}
	
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
		boolean isFence = isPostBlock(pLevel, pNeighborPos);
		boolean isBase = isBaseBlock(pLevel, pNeighborPos);
		if (pPos.relative(Direction.UP).equals(pNeighborPos) && pState.getValue(IS_TOP) && isFence)
			pLevel.setBlockAndUpdate(pPos, pState.setValue(IS_TOP, !isFence));
		if (pPos.relative(Direction.DOWN).equals(pNeighborPos) && pState.getValue(HAS_BASE) != isBase)
			pLevel.setBlockAndUpdate(pPos, pState.setValue(HAS_BASE, isBase));
	}
	
}
