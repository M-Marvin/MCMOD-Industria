package de.industria.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockTarCrust extends BlockBase {
	
	public BlockTarCrust() {
		super("tar_crust", Properties.of(Material.DIRT).strength(2F, 100F).sound(SoundType.NETHERRACK).harvestTool(ToolType.SHOVEL).requiresCorrectToolForDrops());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return Block.box(0, -2, 0, 16, 14, 16);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return Block.box(0, 6, 0, 16, 10, 16);
	}
	
	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.1F, 0.2F, 0.1F));
	}
	
}
