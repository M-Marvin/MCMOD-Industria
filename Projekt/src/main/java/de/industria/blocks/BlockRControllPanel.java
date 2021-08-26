package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.renderer.BlockControllPanelItemRenderer;
import de.industria.tileentity.TileEntityControllPanel;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBSignalConnectiveBlock;
import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
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

public class BlockRControllPanel extends BlockContainerBase implements IBSignalConnectiveBlock, IWaterLoggable, IBAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
	protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
	protected static final VoxelShape TOP_AABB = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	public BlockRControllPanel() {
		super("controll_panel", Material.METAL, 2.2F, SoundType.STONE, true);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState replaceState = context.getLevel().getBlockState(context.getClickedPos());
		return this.defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(WATERLOGGED, replaceState.getBlock() == Blocks.WATER);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		switch (state.getValue(FACING)) {
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
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityControllPanel();
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		Direction facing = state.getValue(FACING);
		Direction face = hit.getDirection();
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (face == facing && tileEntity instanceof TileEntityControllPanel) {
			
			int hitX = (int) ((hit.getLocation().x - hit.getBlockPos().getX()) * 16);
			int hitY = (int) ((hit.getLocation().y - hit.getBlockPos().getY()) * 16);
			int hitZ = (int) ((hit.getLocation().z - hit.getBlockPos().getZ()) * 16);
			
			switch (face) {
			case NORTH: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(hitX, hitY, player.isShiftKeyDown(), player.getMainHandItem());
			case SOUTH: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(16 - hitX - 1, hitY, player.isShiftKeyDown(), player.getMainHandItem());
			case EAST: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(hitZ,  hitY, player.isShiftKeyDown(), player.getMainHandItem());
			case WEST: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(16 - hitZ - 1, hitY, player.isShiftKeyDown(), player.getMainHandItem());
			case UP: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(hitX,  hitZ, player.isShiftKeyDown(), player.getMainHandItem());
			case DOWN: return ((TileEntityControllPanel) tileEntity).onClickOnPanel(hitX, 16 - hitZ, player.isShiftKeyDown(), player.getMainHandItem());
			}
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public boolean canConectSignalWire(IWorldReader world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() == this ? state.getValue(FACING) == side.getOpposite() : false;
	}
	
	@Override
	public void onReciveSignal(World worldIn, BlockPos pos, RedstoneControlSignal signal, Direction side) {
		
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof TileEntityControllPanel) {
			
			((TileEntityControllPanel) tileEntity).onSignal(signal);
			
		}
		
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
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
