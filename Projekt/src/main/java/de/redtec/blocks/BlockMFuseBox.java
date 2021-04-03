package de.redtec.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.redtec.tileentity.TileEntityMFuseBox;
import de.redtec.util.blockfeatures.IAdvancedBlockInfo;
import de.redtec.util.blockfeatures.IElectricConnectiveBlock;
import de.redtec.util.handler.ElectricityNetworkHandler;
import de.redtec.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMFuseBox extends BlockContainerBase implements IElectricConnectiveBlock, IAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty ON = BooleanProperty.create("on");
	
	protected static final VoxelShape EAST_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 11.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_OPEN_AABB = Block.makeCuboidShape(5.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 11.0D);
	protected static final VoxelShape NORTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 5.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape BOTTOM_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D);
	protected static final VoxelShape TOP_AABB = Block.makeCuboidShape(0.0D, 5.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	public BlockMFuseBox() {
		super("fuse_box", Material.ROCK, 1.5F, SoundType.METAL);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, ON);
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
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getFace()).with(ON, false);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		TileEntity te = worldIn.getTileEntity(pos);
		
		if (te instanceof TileEntityMFuseBox && handIn == Hand.MAIN_HAND) {

			ItemStack heldStack = player.getHeldItemMainhand();
			ItemStack fuseStack = ((TileEntityMFuseBox) te).getFuse();
			
			if ((heldStack.isEmpty() || !fuseStack.isEmpty()) && player.isSneaking()) {
				
				ItemStack removeStack = ((TileEntityMFuseBox) te).removeFuse();
				BlockPos pos2 = pos.offset(hit.getFace());
				worldIn.addEntity(new ItemEntity(worldIn, pos2.getX() + 0.5F, pos2.getY() + 0.5F, pos2.getZ() + 0.5F, removeStack));
				return ActionResultType.SUCCESS;
				
			} else if (!heldStack.isEmpty() && fuseStack.isEmpty()) {
				
				boolean success = ((TileEntityMFuseBox) te).insertFuse(heldStack);
				if (success && !player.isCreative()) {
					heldStack.shrink(1);
					return ActionResultType.SUCCESS;
				}
				
			} else {
				
				if (((TileEntityMFuseBox) te).canSwitch()) {
					
					worldIn.setBlockState(pos, state.with(ON, !state.get(ON)));
					worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1, 0.5F);
					return ActionResultType.SUCCESS;
					
				}
				
			}
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMFuseBox();
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NoLimit;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side != state.get(FACING);
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.SWITCH;
	}
	
	@Override
	public boolean isSwitchClosed(World worldIn, BlockPos pos, BlockState state) {
		return state.get(ON);
	}
	
	@Override
	public boolean beforNetworkChanges(World world, BlockPos pos, BlockState state, ElectricityNetwork network, int lap) {
		
		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(world);
		
		float needCurrent = 0;
		float produsedCurrent = 0;
		
		for (Direction d : Direction.values()) {
			
			if (this.canConnect(d, world, pos, state)) {
				
				ElectricityNetwork networkState = handler.getNetworkState(world, pos, d);
				
				if (networkState.getCapacity() - networkState.getNeedCurrent() > 0.001F) {
					
					produsedCurrent += networkState.getCapacity() - networkState.getNeedCurrent();
					
				} else if (networkState.getNeedCurrent() - networkState.getCapacity() > 0.001F) {
					
					needCurrent += networkState.getNeedCurrent() - networkState.getCapacity();
					
				}
				
			}
			
		}
		
		int maxCurrent = 0;
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityMFuseBox) {
			maxCurrent = ((TileEntityMFuseBox) te).getMaxCurrent();
		}
		
		float transferingCurrent = Math.min(needCurrent, produsedCurrent);
		boolean overload = transferingCurrent > maxCurrent;
		boolean closed = state.get(ON);
		
		if (closed && overload) {
			world.setBlockState(pos, state.with(ON, false));
			world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1, 0.5F);
			return true;
		}
		
		return false;
		
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntityMFuseBox) {
			ItemStack stack = ((TileEntityMFuseBox) te).getFuse();
			if (stack != null ? !stack.isEmpty() : false) worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, stack));
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("redtec.block.info.fuseBox"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}
	
}
