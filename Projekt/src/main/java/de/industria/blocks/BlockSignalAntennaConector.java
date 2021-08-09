package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityRSignalAntenna;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBSignalConnectiveBlock;
import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockSignalAntennaConector extends BlockContainerBase implements IBSignalConnectiveBlock, IBAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BlockSignalAntennaConector() {
		super("antenna_conector", Material.METAL, 1.5F, 0.5F, SoundType.METAL);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityRSignalAntenna();
	}
	
	@Override
	public void onReciveSignal(World worldIn, BlockPos pos, RedstoneControlSignal signal, Direction side) {
		
		TileEntity te = worldIn.getBlockEntity(pos);
		
		if (te instanceof TileEntityRSignalAntenna && !worldIn.isClientSide()) {
			
			int range1 = ((TileEntityRSignalAntenna) te).getRange();
			ItemStack chanelItem = ((TileEntityRSignalAntenna) te).getChanelItem();
			
			List<TileEntity> tileEntitys = new ArrayList<TileEntity>();
			tileEntitys.addAll(worldIn.blockEntityList);
			
			for (TileEntity te2 : tileEntitys) {
				
				if (te2 instanceof TileEntityRSignalAntenna && !te2.getBlockPos().equals(te.getBlockPos())) {
					
					TileEntityRSignalAntenna antenna = (TileEntityRSignalAntenna) te2;
					
					int range2 = antenna.getRange();
					int distance = getDistance(te.getBlockPos(), antenna.getBlockPos());
					ItemStack chanelItem2 = antenna.getChanelItem();
					boolean isInRange = distance <= range1 + range2;
					boolean isSameChanel = chanelItem != null && chanelItem2 != null ? chanelItem.equals(chanelItem2, false) : chanelItem == null && chanelItem2 == null;
					
					if (isInRange && isSameChanel) {
						
						antenna.reciveSignal(signal);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

		TileEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof TileEntityRSignalAntenna) {
			
			ItemStack heldItem = player.getMainHandItem();
			
			if (((TileEntityRSignalAntenna) te).getChanelItem() == null && !heldItem.isEmpty()) {
				
				ItemStack item = heldItem.copy();
				item.setCount(1);
				((TileEntityRSignalAntenna) te).setChanelItem(item);
				heldItem.shrink(1);
				
			} else if (((TileEntityRSignalAntenna) te).getChanelItem() != null && player.isShiftKeyDown()) {
				
				ItemStack stack = ((TileEntityRSignalAntenna) te).getChanelItem();
				((TileEntityRSignalAntenna) te).setChanelItem(null);
				BlockPos pos2 = pos.relative(hit.getDirection());
				worldIn.addFreshEntity(new ItemEntity(worldIn, pos2.getX() + 0.5F, pos2.getY() + 0.5F, pos2.getZ() + 0.5F, stack));
				
			}
			
		}
		
		return ActionResultType.SUCCESS;
		
	}
	
	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof TileEntityRSignalAntenna) {
			ItemStack stack = ((TileEntityRSignalAntenna) te).getChanelItem();
			if (stack != null ? !stack.isEmpty() : false) worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, stack));
		}
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	public static int getDistance(BlockPos pos1, BlockPos pos2) {
		
		return	Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) +
				Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ());
		
	}

	@Override
	public boolean canConectSignalWire(IWorldReader world, BlockPos pos, Direction side) {
		if (world.getBlockState(pos).getBlock() == this) {
			return side != world.getBlockState(pos).getValue(FACING);
		} else {
			return false;
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
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.antenna"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
}
