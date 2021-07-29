package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityPipePreassurizer;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

public class BlockPipePreassurizer extends BlockContainerBase implements IBAdvancedBlockInfo {
	
	public static final VoxelShape SHAPE_HORIZONTAL = VoxelShapes.or(
			Block.box(0, 13, 0, 16, 16, 16),
			Block.box(0, 0, 0, 16, 3, 16),
			Block.box(13, 3, 0, 16, 13, 16),
			Block.box(0, 3, 0, 3, 13, 16)
			);
	public static final VoxelShape SHAPE_VERTICAL = VoxelShapes.or(
			Block.box(0, 0, 13, 16, 16, 16),
			Block.box(0, 0, 0, 16, 16, 3),
			Block.box(13, 0, 3, 16, 16, 13),
			Block.box(0, 0, 3, 3, 16, 13)
			);
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BlockPipePreassurizer() {
		super("pipe_preassurizer", Material.METAL, 2F, 2F, SoundType.METAL);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == null ? true : side.getAxis() != state.getValue(FACING).getAxis();
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityPipePreassurizer();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.getValue(FACING).getAxis().isVertical()) {
			return SHAPE_VERTICAL;
		}
		return VoxelHelper.rotateShape(SHAPE_HORIZONTAL, state.getValue(FACING));
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.maxMB", 100));
			info.add(new TranslationTextComponent("industria.block.info.pipePreassurizer"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
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
