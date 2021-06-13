package de.industria.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBurnedScrap extends BlockBurnedBlock {
	
	public BlockBurnedScrap() {
		super("burned_scrap", 0.3F, 0.2F);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Block.makeCuboidShape(2, 0, 2, 14, 4, 14);
	}
	
	@Override
	public OffsetType getOffsetType() {
		return OffsetType.XZ;
	}
	
	@Override
	public int getDustDropAmount(World world, BlockPos pos, BlockState state) {
		return 1;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		if (fromPos.equals(pos.down()) && (!worldIn.getBlockState(fromPos).isOpaqueCube(worldIn, fromPos) || worldIn.getBlockState(fromPos).isAir())) {
			dropBlock(worldIn, pos, state);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return (!context.getWorld().getBlockState(context.getPos().down()).isOpaqueCube(context.getWorld(), context.getPos().down()) || context.getWorld().getBlockState(context.getPos().down()).isAir()) ? context.getWorld().getBlockState(context.getPos()) : super.getStateForPlacement(context);
	}
	
}
