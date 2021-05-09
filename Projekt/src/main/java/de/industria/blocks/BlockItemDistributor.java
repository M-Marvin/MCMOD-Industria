package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityItemDistributor;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import net.minecraft.block.Block;
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
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockItemDistributor extends BlockContainerBase implements IAdvancedBlockInfo, ISidedInventoryProvider {
	
	public static final EnumProperty<DistributorType> TYPE = EnumProperty.create("type", DistributorType.class);
	
	public BlockItemDistributor() {
		super("item_distributor", Material.IRON, 1.5F, 1F, SoundType.METAL);
		this.setDefaultState(this.stateContainer.getBaseState().with(TYPE, DistributorType.CORE));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(TYPE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		List<BlockPos> distributors = new ArrayList<BlockPos>();
		for (Direction d : Direction.values()) {
			findConnectedDistributors(context.getWorld(), context.getPos().offset(d), distributors);
		}
		if (distributors.size() >= 9) return context.getWorld().getBlockState(context.getPos());
		if (distributors.size() > 0) return this.getDefaultState().with(TYPE, DistributorType.EXTENSION);
		return this.getDefaultState();
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		List<BlockPos> distributors = new ArrayList<BlockPos>();
		int cores = findConnectedDistributors(world, pos, distributors);
		if (distributors.size() > 0 && cores != 1) {
			BlockPos firstDistributor = distributors.get(0);
			world.setBlockState(firstDistributor, world.getBlockState(firstDistributor).with(TYPE, DistributorType.CORE));
			for (int i = 1; i < distributors.size(); i++) {
				BlockPos secondDistributor = distributors.get(i);
				TileEntity tileEntity = world.getTileEntity(secondDistributor);
				if (tileEntity instanceof IInventory) {
					InventoryHelper.dropInventoryItems(world, secondDistributor, (IInventory) tileEntity);
				}
				world.setBlockState(secondDistributor, world.getBlockState(secondDistributor).with(TYPE, DistributorType.EXTENSION));
				world.removeTileEntity(secondDistributor);
			}
		}
	}
	
	public int findConnectedDistributors(IWorld world, BlockPos pos, List<BlockPos> posList) {
		
		if (posList.contains(pos)) return 0;
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == this && posList.size() < 9) {
			posList.add(pos);
			int coreCount = 0;
			if (state.get(TYPE) == DistributorType.CORE) coreCount = 1;
			for (Direction d : Direction.values()) {
				coreCount += findConnectedDistributors(world, pos.offset(d), posList);
			}
			return coreCount;
		}
		return 0;
		
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.get(TYPE) == DistributorType.CORE;
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityItemDistributor();
	}
	
	@Override
	public ISidedInventory createInventory(BlockState state, IWorld world, BlockPos pos) {
		if (state.get(TYPE) == DistributorType.CORE) {
			return (ISidedInventory) world.getTileEntity(pos);
		} else {

			List<BlockPos> distributors = new ArrayList<BlockPos>();
			findConnectedDistributors(world, pos, distributors);
			for (BlockPos distributor : distributors) {
				BlockState distributorState = world.getBlockState(distributor);
				if (distributorState.getBlock() == this ? distributorState.get(TYPE) == DistributorType.CORE : false) {
					return (ISidedInventory) world.getTileEntity(distributor);
				}
			}
			
		}
		return null;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (state.get(TYPE) == DistributorType.CORE) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof INamedContainerProvider) {
				if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.itemDistributor"));
		};
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
	public static enum DistributorType implements IStringSerializable {
		
		EXTENSION("extension"),CORE("core");
		
		protected String name;
		
		DistributorType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		@Override
		public String getString() {
			return name;
		}
		
	}
	
}
