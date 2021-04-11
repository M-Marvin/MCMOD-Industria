package de.industria.blocks;

import de.industria.tileentity.TileEntityRedstoneReciver;
import de.industria.util.blockfeatures.ISignalConnectiveBlock;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockRRedstoneReciver extends BlockContainerBase implements ISignalConnectiveBlock {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty TRANSIVER_MODE = BooleanProperty.create("transiver_mode");
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
	
	public BlockRRedstoneReciver() {
		super("redstone_reciver", Material.ROCK, 2.5F, 1.5F, SoundType.STONE);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TRANSIVER_MODE, false).with(POWERED, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, TRANSIVER_MODE, POWERED);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		if (state.get(TRANSIVER_MODE)) {
			
			boolean power = worldIn.isBlockPowered(pos);
			boolean powered = state.get(POWERED);
			
			TileEntity te = worldIn.getTileEntity(pos);
			
			if (te instanceof TileEntityRedstoneReciver && power != powered) {
				
				worldIn.setBlockState(pos, state.with(POWERED, power));
				
				if (((TileEntityRedstoneReciver) te).getChanelItem() != null) {
					
					RedstoneControlSignal signal = new RedstoneControlSignal(((TileEntityRedstoneReciver) te).getChanelItem(), power);
					this.sendSignal(worldIn, pos, signal);
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntityRedstoneReciver) {
			ItemStack stack = ((TileEntityRedstoneReciver) te).getChanelItem();
			if (stack != null ? !stack.isEmpty() : false) worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, stack));
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	public void onReciveSignal(World worldIn, BlockPos pos, RedstoneControlSignal signal, Direction side) {
		
		TileEntity te = worldIn.getTileEntity(pos);
		BlockState state = worldIn.getBlockState(pos);
		
		if (te instanceof TileEntityRedstoneReciver && !state.get(TRANSIVER_MODE)) {
			
			ItemStack filter = ((TileEntityRedstoneReciver) te).getChanelItem();
			
			if (ItemStackHelper.isItemStackItemEqual(filter, signal.getChanelItem(), false)) {
				
				if (state.getBlock() instanceof BlockRRedstoneReciver) {
					
					((BlockRRedstoneReciver) state.getBlock()).triggerRemote(worldIn, pos, signal.isPowered());
					
				}
				
			}
			
		}
		
	}
	
	public void triggerRemote(World world, BlockPos pos, boolean powered) {
		
		BlockState state = world.getBlockState(pos);
		
		if (state.getBlock() == this ? !state.get(TRANSIVER_MODE) : false) {
			
			world.setBlockState(pos, state.with(POWERED, powered));
			world.notifyNeighborsOfStateExcept(pos.offset(state.get(FACING)), this, state.get(FACING).getOpposite());
						
		}
		
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntityRedstoneReciver) {
			
			if (player.isSneaking()) {
				
				worldIn.setBlockState(pos, state.with(TRANSIVER_MODE, !state.get(TRANSIVER_MODE)));
				
				return ActionResultType.CONSUME;
				
			} else {
				
				if (!worldIn.isRemote()) NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);
				return ActionResultType.CONSUME;
				
			}
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}
	
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return 0;
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.get(POWERED) && !blockState.get(TRANSIVER_MODE) ? 15 : 0;
	}

	@Override
	public boolean canConectSignalWire(IWorldReader world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() == this ? state.get(FACING) == side.getOpposite() : false;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityRedstoneReciver();
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
