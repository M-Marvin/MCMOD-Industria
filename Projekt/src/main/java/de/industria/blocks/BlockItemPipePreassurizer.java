package de.industria.blocks;

import de.industria.tileentity.TileEntityItemPipePreassurizer;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockItemPipePreassurizer extends BlockContainerBase {
	
	public static final VoxelShape SHAPE_HORIZONTAL = VoxelShapes.or(
			Block.makeCuboidShape(0, 13, 0, 16, 16, 16),
			Block.makeCuboidShape(0, 0, 0, 16, 3, 16),
			Block.makeCuboidShape(13, 3, 0, 16, 13, 16),
			Block.makeCuboidShape(0, 3, 0, 3, 13, 16)
			);
	public static final VoxelShape SHAPE_VERTICAL = VoxelShapes.or(
			Block.makeCuboidShape(0, 0, 13, 16, 16, 16),
			Block.makeCuboidShape(0, 0, 0, 16, 16, 3),
			Block.makeCuboidShape(13, 0, 3, 16, 16, 13),
			Block.makeCuboidShape(0, 0, 3, 3, 16, 13)
			);
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BlockItemPipePreassurizer() {
		super("item_pipe_preassurizer", Material.IRON, 2F, 2F, SoundType.METAL);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityItemPipePreassurizer();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(FACING).getAxis().isVertical()) {
			return SHAPE_VERTICAL;
		}
		return VoxelHelper.rotateShape(SHAPE_HORIZONTAL, state.get(FACING));
	}
	
}
