package de.m_marvin.industria.content.blocks.fences;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModularFenceMeshBlock extends Block {
	
	public static enum Mesh implements StringRepresentable {
		NONE("none"),
		MESH("mesh"),
		POST("post");

		private final String name;
		
		private Mesh(String name) {
			this.name = name;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
	
	public static final BooleanProperty IS_TOP = BooleanProperty.create("is_top");
	public static final BooleanProperty HAS_BASE = BooleanProperty.create("has_base");
	public static final EnumProperty<Mesh> NORTH = EnumProperty.create("north", Mesh.class);
	public static final EnumProperty<Mesh> SOUTH = EnumProperty.create("south", Mesh.class);
	public static final EnumProperty<Mesh> EAST = EnumProperty.create("east", Mesh.class);
	public static final EnumProperty<Mesh> WEST = EnumProperty.create("west", Mesh.class);
	
	public static final VoxelShape SHAPE_PLANE_NORTH = box(7, 0, 0, 9, 16, 8);
	public static final VoxelShape SHAPE_PLANE_SOUTH = box(7, 0, 8, 9, 16, 16);
	public static final VoxelShape SHAPE_PLANE_EAST = box(8, 0, 7, 16, 16, 9);
	public static final VoxelShape SHAPE_PLANE_WEST = box(0, 0, 7, 8, 16, 9);
	public static final VoxelShape SHAPE_PLANE_NORTH_LONG = box(7, 0, -9, 9, 16, 8);
	public static final VoxelShape SHAPE_PLANE_SOUTH_LONG = box(7, 0, 8, 9, 16, 24);
	public static final VoxelShape SHAPE_PLANE_EAST_LONG = box(8, 0, 7, 24, 16, 9);
	public static final VoxelShape SHAPE_PLANE_WEST_LONG = box(-8, 0, 7, 8, 16, 9);
	public static final VoxelShape SHAPE_NONE = box(7, 0, 7, 9, 16, 9);
	
	public ModularFenceMeshBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(this.stateDefinition.any().setValue(IS_TOP, false).setValue(HAS_BASE, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(IS_TOP, HAS_BASE, NORTH, SOUTH, EAST, WEST);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		VoxelShape shape = SHAPE_NONE;
		Mesh type = pState.getValue(NORTH);
		if (type != Mesh.NONE)
			shape = Shapes.or(shape, type == Mesh.MESH ? SHAPE_PLANE_NORTH : SHAPE_PLANE_NORTH_LONG);
		type = pState.getValue(SOUTH);
		if (type != Mesh.NONE)
			shape = Shapes.or(shape, type == Mesh.MESH ? SHAPE_PLANE_SOUTH : SHAPE_PLANE_SOUTH_LONG);
		type = pState.getValue(EAST);
		if (type != Mesh.NONE)
			shape = Shapes.or(shape, type == Mesh.MESH ? SHAPE_PLANE_EAST : SHAPE_PLANE_EAST_LONG);
		type = pState.getValue(WEST);
		if (type != Mesh.NONE)
			shape = Shapes.or(shape, type == Mesh.MESH ? SHAPE_PLANE_WEST : SHAPE_PLANE_WEST_LONG);
		return shape;
	}
	
	public static Mesh getMeshType(BlockGetter pLevel, BlockPos pPos) {
		if (ModularFencePostBlock.isBaseBlock(pLevel, pPos)) {
			if (ModularFencePostBlock.isPostBlock(pLevel, pPos.above())) return Mesh.POST;
			return Mesh.MESH;
		}
		
		if (ModularFencePostBlock.isMeshBlock(pLevel, pPos)) return Mesh.MESH;
		if (ModularFencePostBlock.isPostBlock(pLevel, pPos)) return Mesh.POST;
		return Mesh.NONE;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = defaultBlockState();
		state = state.setValue(NORTH, getMeshType(pContext.getLevel(), pContext.getClickedPos().north()));
		state = state.setValue(SOUTH, getMeshType(pContext.getLevel(), pContext.getClickedPos().south()));
		state = state.setValue(EAST, getMeshType(pContext.getLevel(), pContext.getClickedPos().east()));
		state = state.setValue(WEST, getMeshType(pContext.getLevel(), pContext.getClickedPos().west()));
		state = state.setValue(HAS_BASE, ModularFencePostBlock.isBaseBlock(pContext.getLevel(), pContext.getClickedPos().below()));
		return state;
	}
	
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
		Mesh type = getMeshType(pLevel, pNeighborPos);
		boolean isBase = ModularFencePostBlock.isBaseBlock(pLevel, pNeighborPos);
		if (pPos.relative(Direction.NORTH).equals(pNeighborPos) && pState.getValue(NORTH) != type)
			pLevel.setBlockAndUpdate(pPos, pState.setValue(NORTH, type));
		if (pPos.relative(Direction.SOUTH).equals(pNeighborPos) && pState.getValue(SOUTH) != type)
			pLevel.setBlockAndUpdate(pPos, pState.setValue(SOUTH, type));
		if (pPos.relative(Direction.EAST).equals(pNeighborPos) && pState.getValue(EAST) != type)
			pLevel.setBlockAndUpdate(pPos, pState.setValue(EAST, type));
		if (pPos.relative(Direction.WEST).equals(pNeighborPos) && pState.getValue(WEST) != type)
			pLevel.setBlockAndUpdate(pPos, pState.setValue(WEST, type));
		if (pPos.below().equals(pNeighborPos) && pState.getValue(HAS_BASE) != isBase)
			pLevel.setBlockAndUpdate(pPos, pState.setValue(HAS_BASE, isBase));
	}
	
}
