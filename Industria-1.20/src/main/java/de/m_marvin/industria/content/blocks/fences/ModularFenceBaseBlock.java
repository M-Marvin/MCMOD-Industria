package de.m_marvin.industria.content.blocks.fences;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModularFenceBaseBlock extends Block {
	
	public static final BooleanProperty IS_TALL = BooleanProperty.create("is_tall");
	public static final BooleanProperty HAS_POST = BooleanProperty.create("has_post");
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static final VoxelShape SHAPE_CENTER = box(3, 0, 3, 13, 9, 13);
	public static final VoxelShape SHAPE_CENTER_TALL = box(3, 0, 3, 13, 16, 13);
	public static final VoxelShape SHAPE_PLANE_NORTH = box(3, 0, 0, 13, 9, 8);
	public static final VoxelShape SHAPE_PLANE_SOUTH = box(3, 0, 8, 13, 9, 16);
	public static final VoxelShape SHAPE_PLANE_EAST = box(8, 0, 3, 16, 9, 13);
	public static final VoxelShape SHAPE_PLANE_WEST = box(0, 0, 3, 8, 9, 13);
	public static final VoxelShape SHAPE_CENTER_POST = box(3, 0, 2, 13, 11, 14);
	public static final VoxelShape SHAPE_CENTER_POST_R = box(2, 0, 3, 14, 11, 13);
	public static final VoxelShape SHAPE_PLANE_NORTH_TALL = box(3, 0, 0, 13, 16, 8);
	public static final VoxelShape SHAPE_PLANE_SOUTH_TALL = box(3, 0, 8, 13, 16, 16);
	public static final VoxelShape SHAPE_PLANE_EAST_TALL = box(8, 0, 3, 16, 16, 13);
	public static final VoxelShape SHAPE_PLANE_WEST_TALL = box(0, 0, 3, 8, 16, 13);
	public static final VoxelShape SHAPE_CENTER_POST_TALL = box(3, 0, 2, 13, 16, 14);
	public static final VoxelShape SHAPE_CENTER_POST_TALL_R = box(2, 0, 3, 14, 16, 13);
	
	public ModularFenceBaseBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(this.stateDefinition.any().setValue(HAS_POST, false).setValue(IS_TALL, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(HAS_POST, IS_TALL, NORTH, SOUTH, EAST, WEST, FACING);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		VoxelShape shape = Shapes.empty();
		if (pState.getValue(IS_TALL)) {
			if (pState.getValue(NORTH))
				shape = Shapes.or(shape, SHAPE_PLANE_NORTH_TALL);
			if (pState.getValue(SOUTH))
				shape = Shapes.or(shape, SHAPE_PLANE_SOUTH_TALL);
			if (pState.getValue(EAST))
				shape = Shapes.or(shape, SHAPE_PLANE_EAST_TALL);
			if (pState.getValue(WEST))
				shape = Shapes.or(shape, SHAPE_PLANE_WEST_TALL);
			if (pState.getValue(HAS_POST)) {
				shape = Shapes.or(shape, pState.getValue(FACING).getAxis() == Axis.X ? SHAPE_CENTER_POST_TALL : SHAPE_CENTER_POST_TALL_R);
			} else {
				shape = Shapes.or(shape, SHAPE_CENTER_TALL);
			}
		} else {
			if (pState.getValue(NORTH))
				shape = Shapes.or(shape, SHAPE_PLANE_NORTH);
			if (pState.getValue(SOUTH))
				shape = Shapes.or(shape, SHAPE_PLANE_SOUTH);
			if (pState.getValue(EAST))
				shape = Shapes.or(shape, SHAPE_PLANE_EAST);
			if (pState.getValue(WEST))
				shape = Shapes.or(shape, SHAPE_PLANE_WEST);
			if (pState.getValue(HAS_POST)) {
				shape = Shapes.or(shape, pState.getValue(FACING).getAxis() == Axis.X ? SHAPE_CENTER_POST : SHAPE_CENTER_POST_R);
			} else {
				shape = Shapes.or(shape, SHAPE_CENTER);
			}
		}
		return shape;
	}
	
	public static boolean canConnectToBlock(BlockGetter pLevel, BlockPos pPos, Direction pFace) {
		return	ModularFencePostBlock.isBaseBlock(pLevel, pPos) ||
				ModularFencePostBlock.isMeshBlock(pLevel, pPos) ||
				pLevel.getBlockState(pPos).isFaceSturdy(pLevel, pPos, pFace);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState();
		state = state.setValue(NORTH, canConnectToBlock(pContext.getLevel(), pContext.getClickedPos().relative(Direction.NORTH), Direction.NORTH));
		state = state.setValue(SOUTH, canConnectToBlock(pContext.getLevel(), pContext.getClickedPos().relative(Direction.SOUTH), Direction.SOUTH));
		state = state.setValue(EAST, canConnectToBlock(pContext.getLevel(), pContext.getClickedPos().relative(Direction.EAST), Direction.EAST));
		state = state.setValue(WEST, canConnectToBlock(pContext.getLevel(), pContext.getClickedPos().relative(Direction.WEST), Direction.WEST));
		state = state.setValue(HAS_POST, 
				ModularFencePostBlock.isPostBlock(pContext.getLevel(), pContext.getClickedPos().relative(Direction.UP)) ||
				(ModularFencePostBlock.isBaseBlock(pContext.getLevel(), pContext.getClickedPos().relative(Direction.UP)) && 
				 pContext.getLevel().getBlockState(pContext.getClickedPos().relative(Direction.UP)).getValue(HAS_POST)));
		state = state.setValue(IS_TALL, ModularFencePostBlock.isBaseBlock(pContext.getLevel(), pContext.getClickedPos().relative(Direction.UP)));
		return state.setValue(FACING, ModularFencePostBlock.getPlacementFacing(pContext));
	}
	
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
		boolean isBase = ModularFencePostBlock.isBaseBlock(pLevel, pNeighborPos);
		boolean isPost = ModularFencePostBlock.isPostBlock(pLevel, pNeighborPos);
		boolean isPostBase = ModularFencePostBlock.isBaseBlock(pLevel, pNeighborPos) && pLevel.getBlockState(pNeighborPos).getValue(HAS_POST);
		boolean canConnect = canConnectToBlock(pLevel, pNeighborPos, Direction.NORTH);
		if (pPos.relative(Direction.NORTH).equals(pNeighborPos) && pState.getValue(NORTH) != canConnect)
			pLevel.setBlockAndUpdate(pPos, pState = pState.setValue(NORTH, canConnect));
		canConnect = canConnectToBlock(pLevel, pNeighborPos, Direction.SOUTH);
		if (pPos.relative(Direction.SOUTH).equals(pNeighborPos) && pState.getValue(SOUTH) != canConnect)
			pLevel.setBlockAndUpdate(pPos, pState = pState.setValue(SOUTH, canConnect));
		canConnect = canConnectToBlock(pLevel, pNeighborPos, Direction.EAST);
		if (pPos.relative(Direction.EAST).equals(pNeighborPos) && pState.getValue(EAST) != canConnect)
			pLevel.setBlockAndUpdate(pPos, pState = pState.setValue(EAST, canConnect));
		canConnect = canConnectToBlock(pLevel, pNeighborPos, Direction.WEST);
		if (pPos.relative(Direction.WEST).equals(pNeighborPos) && pState.getValue(WEST) != canConnect)
			pLevel.setBlockAndUpdate(pPos, pState = pState.setValue(WEST, canConnect));
		if (pPos.relative(Direction.UP).equals(pNeighborPos) && pState.getValue(HAS_POST) != (isPost || isPostBase))
			pLevel.setBlockAndUpdate(pPos, pState = pState.setValue(HAS_POST, (isPost || isPostBase)));
		if (pPos.relative(Direction.UP).equals(pNeighborPos) && pState.getValue(IS_TALL) != isBase)
			pLevel.setBlockAndUpdate(pPos, pState = pState.setValue(IS_TALL, isBase));
	}
	
}
