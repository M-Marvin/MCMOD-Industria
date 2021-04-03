package de.redtec.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.RedTec;
import de.redtec.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.redtec.renderer.BlockEnderCoreItemRenderer;
import de.redtec.tileentity.TileEntityEnderCore;
import de.redtec.util.blockfeatures.IAdvancedBlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockEnderCore extends BlockContainerBase implements IAdvancedBlockInfo {
	
	public BlockEnderCore() {
		super("ender_core", Material.IRON, 1F, 1F, SoundType.NETHERITE);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockEnderCoreItemRenderer::new;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = Block.makeCuboidShape(5, 5, 5, 11, 11, 11);
		return VoxelShapes.or(RedTec.reinforced_casing.getShape(null, null, null, null), shape);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityEnderCore();
	}
	
}
