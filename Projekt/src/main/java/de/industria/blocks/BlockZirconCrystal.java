package de.industria.blocks;

import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockZirconCrystal extends BlockBase implements IWaterLoggable {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public BlockZirconCrystal() {
		super("zircon_crystal", Material.STONE, 0F, 2F, SoundType.GLASS);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, FACING);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
		if (!canSurvive(state, context.getLevel(), context.getClickedPos())) {
			for (Direction d : Direction.values()) {
				state = state.setValue(FACING, d);
				if (canSurvive(state, context.getLevel(), context.getClickedPos())) {
					BlockState replaceState = context.getLevel().getBlockState(context.getClickedPos());
					return state.setValue(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
				}
			}
			return context.getLevel().getBlockState(context.getClickedPos());
		} else {
			BlockState replaceState = context.getLevel().getBlockState(context.getClickedPos());
			return state.setValue(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos basePos = pos.relative(state.getValue(FACING));
		BlockState baseState = world.getBlockState(basePos);
		return Block.isShapeFullBlock(baseState.getShape(world, basePos)) && !baseState.isAir();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean p_220069_6_) {
		if (neighborPos.equals(pos.relative(state.getValue(FACING)))) {
			if (!canSurvive(state, world, pos)) world.destroyBlock(pos, true);
		}
		super.neighborChanged(state, world, pos, neighbor, neighborPos, p_220069_6_);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = Block.box(3, 3, 0, 13, 13, 8);
		if (state.getValue(FACING) == Direction.DOWN) {
			shape = Block.box(3, 0, 3, 13, 8, 13);
		} else if (state.getValue(FACING) == Direction.UP) {
			shape = Block.box(3, 8, 3, 13, 16, 13);
		}
		return VoxelHelper.rotateShape(shape, state.getValue(FACING));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}
	
}
