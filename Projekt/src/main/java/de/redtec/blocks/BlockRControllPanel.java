package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.redtec.renderer.BlockControllPanelItemRenderer;
import de.redtec.tileentity.TileEntityControllPanel;
import de.redtec.util.blockfeatures.IAdvancedBlockInfo;
import de.redtec.util.blockfeatures.ISignalConnectiveBlock;
import de.redtec.util.types.RedstoneControlSignal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockRControllPanel extends BlockContainerBase implements ISignalConnectiveBlock, IWaterLoggable, IAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape EAST_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_OPEN_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape BOTTOM_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
	protected static final VoxelShape TOP_AABB = Block.makeCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	public BlockRControllPanel() {
		super("controll_panel", Material.IRON, 2.2F, SoundType.STONE, true);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState replaceState = context.getWorld().getBlockState(context.getPos());
		return this.getDefaultState().with(FACING, context.getFace()).with(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		switch (state.get(FACING)) {
		case NORTH: return NORTH_OPEN_AABB;
		case SOUTH: return SOUTH_OPEN_AABB;
		case EAST: return EAST_OPEN_AABB;
		case WEST: return WEST_OPEN_AABB;
		case DOWN: return TOP_AABB;
		case UP: return BOTTOM_AABB;
		default: return NORTH_OPEN_AABB;
		}
		
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityControllPanel();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		Direction facing = state.get(FACING);
		Direction face = hit.getFace();
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (face == facing && tileEntity instanceof TileEntityControllPanel && !worldIn.isRemote()) {
			
			int hitX = (int) ((hit.getHitVec().x - hit.getPos().getX()) * 16);
			int hitY = (int) ((hit.getHitVec().y - hit.getPos().getY()) * 16);
			int hitZ = (int) ((hit.getHitVec().z - hit.getPos().getZ()) * 16);
			
			switch (face) {
			case NORTH: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(hitX, hitY, player.isSneaking(), player.getHeldItemMainhand());
			case SOUTH: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(16 - hitX - 1, hitY, player.isSneaking(), player.getHeldItemMainhand());
			case EAST: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(hitZ,  hitY, player.isSneaking(), player.getHeldItemMainhand());
			case WEST: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(16 - hitZ - 1, hitY, player.isSneaking(), player.getHeldItemMainhand());
			case UP: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(hitX,  hitZ, player.isSneaking(), player.getHeldItemMainhand());
			case DOWN: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(hitX, 16 - hitZ, player.isSneaking(), player.getHeldItemMainhand());
			}
			
		}
		
		return ActionResultType.CONSUME;
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (tileEntity instanceof TileEntityControllPanel) {

			ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1);
			CompoundNBT compound = ((TileEntityControllPanel) tileEntity).saveToNBT(new CompoundNBT());
			
			if (!compound.isEmpty()) {
				stack.setTagInfo("BlockEntityTag", compound);
			}
			
			ItemEntity item = new ItemEntity(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, stack);
			item.setDefaultPickupDelay();
			worldIn.addEntity(item);
			
		}
		
		super.onBlockHarvested(worldIn, pos, state, player);
		
	}
	
	@Override
	public boolean canConectSignalWire(IWorldReader world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() == this ? state.get(FACING) == side.getOpposite() : false;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, net.minecraft.loot.LootContext.Builder builder) {
		return new ArrayList<ItemStack>();
	}

	@Override
	public void onReciveSignal(World worldIn, BlockPos pos, RedstoneControlSignal signal, Direction side) {
		
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (tileEntity instanceof TileEntityControllPanel) {
			
			((TileEntityControllPanel) tileEntity).onSignal(signal);
			
		}
		
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluid().getDefaultState() : Fluids.EMPTY.getDefaultState();
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockControllPanelItemRenderer::new;
	}
	
}
