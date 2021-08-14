package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockEnderCoreItemRenderer;
import de.industria.tileentity.TileEntityEnderCore;
import de.industria.typeregistys.ModItems;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
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

public class BlockEnderCore extends BlockContainerBase implements IBAdvancedBlockInfo {
	
	public BlockEnderCore() {
		super("ender_core", Material.METAL, 1F, 1F, SoundType.NETHERITE_BLOCK);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
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
		VoxelShape shape = Block.box(5, 5, 5, 11, 11, 11);
		return VoxelShapes.or(ModItems.reinforced_casing.getShape(null, null, null, null), shape);
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityEnderCore();
	}
	
}
