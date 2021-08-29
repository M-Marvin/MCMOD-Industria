package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.multipartbuilds.MultipartBuild.MultipartBuildLocation;
import de.industria.renderer.BlockMCoalHeaterItemRenderer;
import de.industria.tileentity.TileEntityMCoalHeater;
import de.industria.typeregistys.MultipartBuildRecipes;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockMCoalHeater extends BlockMultipartBuilded<TileEntityMCoalHeater> implements ISidedInventoryProvider, IBAdvancedBlockInfo {
	
	public BlockMCoalHeater() {
		super("coal_heater", Material.METAL, 4F, SoundType.METAL, 2, 1, 2, () -> MultipartBuildRecipes.COAL_HEATER);
	}
		
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityMCoalHeater();
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.coalHeater"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockMCoalHeaterItemRenderer::new;
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMCoalHeater tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getBlockPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMCoalHeater tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropContents(worldIn, tileEntity.getBlockPos(), (IInventory) tileEntity);
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public ISidedInventory getContainer(BlockState state, IWorld world, BlockPos pos) {
		if (getInternPartPos(state).equals(new BlockPos(0, 0, 0)) || getInternPartPos(state).equals(new BlockPos(1, 0, 0))) {
			TileEntityMCoalHeater tileEntity = getCenterTE(pos, state, world);
			return tileEntity;
		}
		return null;
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

	@Override
	public void storeBuildData(World world, BlockPos pos, BlockState state, MultipartBuildLocation buildData) {
		TileEntityMCoalHeater tileEntity = getCenterTE(pos, state, world);
		tileEntity.storeBuildData(buildData);
	}
	
}
