package de.industria.blocks;

import java.util.stream.Stream;

import de.industria.tileentity.TileEntityFluidCannister;
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
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockFluidCannister extends BlockContainerBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public BlockFluidCannister() {
		super("cannister", Material.STONE, 2F, 4F, SoundType.STONE);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = this.defaultBlockState().setValue(FACING, context.getPlayer().getDirection());
		if (canSurvive(state, context.getLevel(), context.getClickedPos())) return state;
		return null;
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockState bottomState = world.getBlockState(pos.below());
		return Block.isFaceFull(bottomState.getShape(world, pos), Direction.UP);
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moved) {
		if (!canSurvive(state, world, neighborPos)) {
			world.destroyBlock(pos, true);
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = Stream.of(
				Block.box(2, 0, 1, 14, 13, 15),
				Block.box(7, 13, 2, 9, 15, 9),
				Block.box(6, 13, 10, 10, 16, 14)
				).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();
		return VoxelHelper.rotateShape(shape, state.getValue(FACING));
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return new TileEntityFluidCannister();
	}
	
}
