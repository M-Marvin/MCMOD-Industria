package de.redtec.blocks;

import de.redtec.tileentity.TileEntityNComputer;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockNComputer extends BlockMultiPart<TileEntityNComputer> {
	
	public BlockNComputer() {
		super("computer", Material.IRON, 2F, SoundType.METAL, 1, 2, 2);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return getInternPartPos(state).equals(BlockPos.ZERO);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityNComputer();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity instanceof TileEntityNComputer && !worldIn.isRemote()) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
	
}
