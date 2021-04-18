package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockMCoalHeaterItemRenderer;
import de.industria.tileentity.TileEntityMCoalHeater;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
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
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockMCoalHeater extends BlockMultiPart<TileEntityMCoalHeater> implements ISidedInventoryProvider, IAdvancedBlockInfo {
	
	public BlockMCoalHeater() {
		super("coal_heater", Material.ROCK, 4F, SoundType.STONE, 2, 1, 2);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		BlockPos ipos = getInternPartPos(state);
		Direction facing = state.get(FACING);
		
		if (ipos.equals(new BlockPos(1, 1, 0))) {
			return Block.makeCuboidShape(0, 0, 0, 16, 8, 16);
		} else if (ipos.equals(new BlockPos(0, 1, 0))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(8, 0, 0, 16, 8, 16), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 0))) {
			return Block.makeCuboidShape(0, 0, 0, 16, 8, 16);
		} else if (ipos.equals(new BlockPos(1, 2, 0))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(0, 0, 14, 16, 16, 16), facing);
		} else if (ipos.equals(new BlockPos(0, 2, 0))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(4, 0, 14, 16, 16, 16), facing);
		} else if (ipos.equals(new BlockPos(1, 2, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(1, 0, 0, 16, 16, 1), Block.makeCuboidShape(0, 0, 0, 1, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(15, 0, 0, 16, 16, 16), Block.makeCuboidShape(0, 0, 0, 15, 16, 1)), facing);
		} else if (ipos.equals(new BlockPos(1, 2, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(1, 0, 15, 16, 16, 16), Block.makeCuboidShape(0, 0, 0, 1, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 2, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(15, 0, 0, 16, 16, 16), Block.makeCuboidShape(0, 0, 15, 15, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(1, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(1, 0, 0, 16, 16, 1), Block.makeCuboidShape(0, 0, 0, 1, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 1, 1))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(15, 0, 0, 16, 16, 16), Block.makeCuboidShape(0, 0, 0, 15, 16, 1)), facing);
		} else if (ipos.equals(new BlockPos(1, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(1, 0, 15, 16, 16, 16), Block.makeCuboidShape(0, 0, 0, 1, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(2, 1, 2))) {
			return VoxelHelper.rotateShape(VoxelShapes.or(Block.makeCuboidShape(15, 0, 0, 16, 16, 16), Block.makeCuboidShape(0, 0, 15, 15, 16, 16)), facing);
		} else if (ipos.equals(new BlockPos(0, 2, 2))) {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(0, 0, 0, 16, 16, 5), facing);
		}
		
		return Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
		
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
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
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntityMCoalHeater tileEntity = getCenterTE(pos, state, worldIn);
		if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntityMCoalHeater tileEntity = getCenterTE(pos, state, worldIn);
		if (tileEntity != null) InventoryHelper.dropInventoryItems(worldIn, tileEntity.getPos(), (IInventory) tileEntity);
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos) {
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
	
}
