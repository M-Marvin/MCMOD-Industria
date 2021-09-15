package de.industria.blocks;

import de.industria.util.blockfeatures.IBAreaLamp;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockLuminousAir extends BlockBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BlockLuminousAir() {
		super("luminous_air", Properties.of(Material.AIR).noCollission().noDrops().noCollission().lightLevel((state) -> {return 15;}));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState p_149645_1_) {
		return BlockRenderType.INVISIBLE;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moved) {
		if (!canSurvive(state, world, pos)) {
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
			if (world.getBlockState(pos.north()).getBlock() == this) world.neighborChanged(pos.north(), Blocks.AIR, pos);
			if (world.getBlockState(pos.south()).getBlock() == this) world.neighborChanged(pos.south(), Blocks.AIR, pos);
			if (world.getBlockState(pos.east()).getBlock() == this) world.neighborChanged(pos.east(), Blocks.AIR, pos);
			if (world.getBlockState(pos.west()).getBlock() == this) world.neighborChanged(pos.west(), Blocks.AIR, pos);
			if (world.getBlockState(pos.above()).getBlock() == this) world.neighborChanged(pos.above(), Blocks.AIR, pos);
			if (world.getBlockState(pos.below()).getBlock() == this) world.neighborChanged(pos.below(), Blocks.AIR, pos);
		}
		super.neighborChanged(state, world, pos, neighbor, neighborPos, moved);
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos sourcePos = pos.relative(state.getValue(FACING));
		BlockState sourceState = world.getBlockState(sourcePos);
		return sourceState.getBlock() instanceof IBAreaLamp ? ((IBAreaLamp) sourceState.getBlock()).isLit(sourceState, world, sourcePos) : sourceState.getBlock() == this;
	}
	
}
