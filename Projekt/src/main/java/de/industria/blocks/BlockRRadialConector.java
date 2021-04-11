package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;

import de.industria.tileentity.TileEntityRLockedCompositeBlock;
import de.industria.util.blockfeatures.IAdvancedStickyBlock;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockRRadialConector extends BlockContainerBase implements IAdvancedStickyBlock {
	
	public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
	public static final BooleanProperty MANUAL_LOCK = BooleanProperty.create("manual_lock");
	private int maxAttachableBlocks = 3000;
	private int maxScannDepth = 3000;
	
	public BlockRRadialConector() {
		super("radial_conector", Material.WOOD, 1.5F, 0.5F, SoundType.WOOD, true);
		this.setDefaultState(this.stateContainer.getBaseState().with(LOCKED, false).with(MANUAL_LOCK, false));
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(LOCKED, MANUAL_LOCK);
	}
	
	public List<BlockPos> getAttachedBlocks(World world, BlockPos pos, BlockPos pistonPos) {
		
		List<BlockPos> list = new ArrayList<BlockPos>();
		if (this.scannAt(list, world, pos, 0, pistonPos)) {
			return list;
		}
		return new ArrayList<BlockPos>();
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean isConectableBlock(BlockState state) {
		
		if (state.isAir()) {
			return false;
		} else if (state.getBlock().getPushReaction(state) == PushReaction.BLOCK) {
			return false;
		} else if (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.LAVA) {
			return false;
		}
		
		return true;
		
	}
	
	@SuppressWarnings("deprecation")
	private boolean scannAt(List<BlockPos> list, World world, BlockPos scannPos, int scanDepth, BlockPos pistonPos) {
		
		BlockState scannState = world.getBlockState(scannPos);
		
		if (list.size() > this.maxAttachableBlocks) return false;
		
		if (isConectableBlock(scannState) && !list.contains(scannPos) && scanDepth <= this.maxScannDepth && world.isBlockLoaded(scannPos) && !scannPos.equals(pistonPos)) {
			
			list.add(scannPos);
			
			for (Direction d : Direction.values()) {
				
				if (!scannAt(list, world, scannPos.offset(d), scanDepth++, pistonPos)) return false;;
				
			}
			
		}
		
		return true;
		
	}
	
	@Override
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		
		TileEntity tileEntity = world.getTileEntity(pos);
		List<BlockPos> connectedBlocks = null;
		
		if (state.get(LOCKED) && tileEntity instanceof TileEntityRLockedCompositeBlock) {
			connectedBlocks = ((TileEntityRLockedCompositeBlock) tileEntity).getConnectedBlocks();
		} else {
			connectedBlocks = getAttachedBlocks(world, pos, pistonStructureHelper.getPistonPos());
		}
		
		if (connectedBlocks.size() > 0) {
			
			for (BlockPos blockPos : connectedBlocks) {
				
				if (blockPos.equals(pistonStructureHelper.getPistonPos())) return false;
				if (!blockPos.equals(pos) && !pistonStructureHelper.getBlocksToMove().contains(blockPos)) pistonStructureHelper.addBlockLine(blockPos, pistonStructureHelper.getMoveDirection());
				
			}
			
			return true;
			
		}
		
		return false;
		
	}
	
	public void setLocked(boolean locked, World world, BlockPos pos, BlockState state) {
		
		if (locked) {
			
			List<BlockPos> blocks = getAttachedBlocks(world, pos, BlockPos.ZERO);
			
			if (blocks.size() > 0) {
				
				world.setBlockState(pos, state.with(LOCKED, true));
				TileEntityRLockedCompositeBlock tileEntity = new TileEntityRLockedCompositeBlock();
				world.setTileEntity(pos, tileEntity);
				tileEntity.validate();
				tileEntity.setPositions(blocks);
				
			}
			
		} else {
			
			world.removeTileEntity(pos);
			world.setBlockState(pos, state.with(LOCKED, false));
			
		}
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		if (!state.get(MANUAL_LOCK)) {
			
			boolean power = worldIn.getRedstonePowerFromNeighbors(pos) > 0 || worldIn.isBlockPowered(pos);
			boolean powered = state.get(LOCKED);
			
			if (power != powered) {
				
				worldIn.setBlockState(pos, state);
				setLocked(power, worldIn, pos, state);
				
			}
			
		}
		
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (player.isSneaking()) {
			
			state = state.with(MANUAL_LOCK, !state.get(MANUAL_LOCK));
			this.setLocked(state.get(MANUAL_LOCK), worldIn, pos, state);
			return ActionResultType.CONSUME;
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.get(LOCKED);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityRLockedCompositeBlock();
	}
	
}
