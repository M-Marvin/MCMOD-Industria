package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;

import de.redtec.tileentity.TileEntitySignalAntenna;
import de.redtec.util.ISignalConnective;
import de.redtec.util.RedstoneControlSignal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockSignalAntennaConector extends BlockContainerBase implements ISignalConnective {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BlockSignalAntennaConector() {
		super("antenna_conector", Material.IRON, 1, SoundType.METAL);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntitySignalAntenna();
	}
	
	@Override
	public void onReciveSignal(World worldIn, BlockPos pos, RedstoneControlSignal signal, Direction side) {
		
		TileEntity te = worldIn.getTileEntity(pos);
		
		if (te instanceof TileEntitySignalAntenna && !worldIn.isRemote()) {
			
			int range1 = ((TileEntitySignalAntenna) te).getRange();
			ItemStack chanelItem = ((TileEntitySignalAntenna) te).getChanelItem();
			
			List<TileEntity> tileEntitys = new ArrayList<TileEntity>();
			tileEntitys.addAll(worldIn.loadedTileEntityList);
			
			for (TileEntity te2 : tileEntitys) {
				
				if (te2 instanceof TileEntitySignalAntenna && !te2.getPos().equals(te.getPos())) {
					
					TileEntitySignalAntenna antenna = (TileEntitySignalAntenna) te2;
					
					int range2 = antenna.getRange();
					int distance = getDistance(te.getPos(), antenna.getPos());
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
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntitySignalAntenna) {
			
			ItemStack heldItem = player.getHeldItemMainhand();
			
			if (((TileEntitySignalAntenna) te).getChanelItem() == null && !heldItem.isEmpty()) {
				
				ItemStack item = heldItem.copy();
				item.setCount(1);
				((TileEntitySignalAntenna) te).setChanelItem(item);
				heldItem.shrink(1);
				
			} else if (((TileEntitySignalAntenna) te).getChanelItem() != null && player.isSneaking()) {
				
				ItemStack stack = ((TileEntitySignalAntenna) te).getChanelItem();
				((TileEntitySignalAntenna) te).setChanelItem(null);
				BlockPos pos2 = pos.offset(hit.getFace());
				worldIn.addEntity(new ItemEntity(worldIn, pos2.getX() + 0.5F, pos2.getY() + 0.5F, pos2.getZ() + 0.5F, stack));
								
			}
			
		}
		
		return ActionResultType.SUCCESS;
		
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntitySignalAntenna) {
			ItemStack stack = ((TileEntitySignalAntenna) te).getChanelItem();
			if (stack != null ? !stack.isEmpty() : false) worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, stack));
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	public static int getDistance(BlockPos pos1, BlockPos pos2) {
		
		return	Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) +
				Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ());
		
	}

	@Override
	public boolean canConectSignalWire(IWorldReader world, BlockPos pos, Direction side) {
		if (world.getBlockState(pos).getBlock() == this) {
			return side != world.getBlockState(pos).get(FACING);
		} else {
			return false;
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
	
}
