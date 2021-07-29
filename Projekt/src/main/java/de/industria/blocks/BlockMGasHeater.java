package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMGasHeaterItemRenderer;
import de.industria.tileentity.TileEntityMGasHeater;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

public class BlockMGasHeater extends BlockMultiPart<TileEntityMGasHeater> implements IBAdvancedBlockInfo {
	
	public BlockMGasHeater() {
		super("gas_heater", Material.METAL, 4F, SoundType.METAL, 2, 1, 2);
	}
		
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMGasHeater();
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.maxMB", 10));
			info.add(new TranslationTextComponent("industria.block.info.gasHeater"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMGasHeaterItemRenderer::new;
	}
		
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		BlockPos pos = getInternPartPos(state);
		return pos.equals(BlockPos.ZERO);
	}
	
	@Override
	public int getStackSize() {
		return 1;
	}
	
}
