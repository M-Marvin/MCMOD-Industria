package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityPreassurePipeItemTerminal;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockPreassurePipeItemTerminal extends BlockContainerBase implements IAdvancedBlockInfo {
	
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
	public static final BooleanProperty INPUT = BooleanProperty.create("input");
	
	public BlockPreassurePipeItemTerminal() {
		super("preassure_pipe_item_terminal", Material.IRON, 2F, 2F, SoundType.METAL);
		this.setDefaultState(this.stateContainer.getBaseState().with(INPUT, false));
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		worldIn.setBlockState(pos, state.with(INPUT, !state.get(INPUT)));
		return ActionResultType.SUCCESS;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, INPUT);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityPreassurePipeItemTerminal();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(FACING).getAxis().isVertical()) {
			return SHAPE_VERTICAL;
		}
		return VoxelHelper.rotateShape(SHAPE_HORIZONTAL, state.get(FACING));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.preassurePipeTerminal"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}
	
}
