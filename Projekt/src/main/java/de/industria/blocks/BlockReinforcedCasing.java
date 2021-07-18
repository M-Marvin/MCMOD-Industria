package de.industria.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockReinforcedCasing extends BlockBase {

	public BlockReinforcedCasing() {
		super("reinforced_casing", Material.METAL, 3F, 6F, SoundType.NETHERITE_BLOCK);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = VoxelShapes.empty();
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(0, 0, 0, 3, 3, 3), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(13, 0, 0, 16, 3, 3), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(0, 13, 0, 3, 16, 3), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(13, 13, 0, 16, 16, 3), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(0, 0, 13, 3, 3, 16), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(13, 0, 13, 16, 3, 16), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(0, 13, 13, 3, 16, 16), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(13, 13, 13, 16, 16, 16), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(0, 3, 0, 2, 13, 2), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(14, 3, 0, 16, 13, 2), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(14, 3, 14, 16, 13, 16), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(0, 3, 14, 2, 13, 16), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(3, 0, 0, 13, 2, 2), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(3, 14, 0, 13, 16, 2), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(3, 14, 14, 13, 16, 16), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(3, 0, 14, 13, 2, 16), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(0, 0, 3, 2, 2, 13), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(0, 14, 3, 2, 16, 13), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(14, 14, 3, 16, 16, 13), IBooleanFunction.OR);
		shape = VoxelShapes.joinUnoptimized(shape, Block.box(14, 0, 3, 16, 2, 13), IBooleanFunction.OR);
		return shape;
	}

}
