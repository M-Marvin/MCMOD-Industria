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
		super("item_distributor", Material.METAL, 1.5F, 1F, SoundType.METAL);
		this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, DistributorType.CORE));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(TYPE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		List<BlockPos> distributors = new ArrayList<BlockPos>();
		for (Direction d : Direction.values()) {
			findConnectedDistributors(context.getLevel(), context.getClickedPos().relative(d), distributors);
		}
		if (distributors.size() >= 9) return context.getLevel().getBlockState(context.getClickedPos());
		if (distributors.size() > 0) return this.defaultBlockState().setValue(TYPE, DistributorType.EXTENSION);
		return this.defaultBlockState();
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		List<BlockPos> distributors = new ArrayList<BlockPos>();
		int cores = findConnectedDistributors(world, pos, distributors);
		if (distributors.size() > 0 && cores != 1) {
			BlockPos firstDistributor = distributors.get(0);
			world.setBlockAndUpdate(firstDistributor, world.getBlockState(firstDistributor).setValue(TYPE, DistributorType.CORE));
			for (int i = 1; i < distributors.size(); i++) {
				BlockPos secondDistributor = distributors.get(i);
				TileEntity tileEntity = world.getBlockEntity(secondDistributor);
				if (tileEntity instanceof IInventory) {
					InventoryHelper.dropContents(world, secondDistributor, (IInventory) tileEntity);
				}
				world.setBlockAndUpdate(secondDistributor, world.getBlockState(secondDistributor).setValue(TYPE, DistributorType.EXTENSION));
				world.removeBlockEntity(secondDistributor);
			}
		}
	}
	
	public int findConnectedDistributors(IWorld world, BlockPos pos, List<BlockPos> posList) {
		
		if (posList.contains(pos)) return 0;
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == this && posList.size() < 9) {
			posList.add(pos);
			int coreCount = 0;
			if (state.getValue(TYPE) == DistributorType.CORE) coreCount = 1;
			for (Direction d : Direction.values()) {
				coreCount += findConnectedDistributors(world, pos.relative(d), posList);
			}
			return coreCount;
		}
		return 0;
		
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.getValue(TYPE) == DistributorType.CORE;
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityItemDistributor();
	}
	
	@Override
	public ISidedInventory getContainer(BlockState state, IWorld world, BlockPos pos) {
		if (state.getValue(TYPE) == DistributorType.CORE) {
			return (ISidedInventory) world.getBlockEntity(pos);
		} else {

			List<BlockPos> distributors = new ArrayList<BlockPos>();
			findConnectedDistributors(world, pos, distributors);
			for (BlockPos distributor : distributors) {
				BlockState distributorState = world.getBlockState(distributor);
				if (distributorState.getBlock() == this ? distributorState.getValue(TYPE) == DistributorType.CORE : false) {
					return (ISidedInventory) world.getBlockEntity(distributor);
				}
			}
			
		}
		return null;
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (state.getValue(TYPE) == DistributorType.CORE) {
			TileEntity tileEntity = worldIn.getBlockEntity(pos);
			if (tileEntity instanceof INamedContainerProvider) {
				if (!worldIn.isClientSide()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
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
		public String getSerializedName() {
			return name;
		}
		
	}
	
}
