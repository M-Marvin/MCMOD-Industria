package de.redtec.blocks;

import de.redtec.tileentity.TileEntityMSteamGenerator;
import de.redtec.tileentity.TileEntityMSteamGenerator.TEPart;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BlockMSteamGenerator extends BlockMultiPart {
	
	public BlockMSteamGenerator() {
		super("steam_generator", Material.IRON, 8F, SoundType.METAL, 3, 3, 2);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return TEPart.hasTileEntity(getInternPartPos(state));
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMSteamGenerator();
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityMSteamGenerator(TEPart.fromPosition(getInternPartPos(state)));
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
}
