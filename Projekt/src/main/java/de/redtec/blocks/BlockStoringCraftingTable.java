package de.redtec.blocks;

import de.redtec.tileentity.TileEntityStoringCraftingTable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockStoringCraftingTable extends BlockContainerBase {

	public BlockStoringCraftingTable() {
		super("storing_crafting_table", Material.ROCK, 2.5F, SoundType.NETHERITE);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityStoringCraftingTable();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntityStoringCraftingTable) {
			if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityStoringCraftingTable) tileEntity, pos);
			return ActionResultType.CONSUME;
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) te);
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
}
