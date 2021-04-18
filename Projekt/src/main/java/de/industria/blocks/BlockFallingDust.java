package de.industria.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFallingDust extends BlockFallingBase {
	
	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);
	
	public BlockFallingDust(String name) {
		super(name, Material.SAND, 2F, 3F, SoundType.SAND);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(LAYERS);
		super.fillStateContainer(builder);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Block.makeCuboidShape(0, 0, 0, 16, state.get(LAYERS) * 2, 16);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (handIn == Hand.MAIN_HAND) {
			
			ItemStack heldStack = player.getHeldItemMainhand();
			
			if (heldStack.getItem() == this.getItem(worldIn, pos, state).getItem()) {
				
				int layers = state.get(LAYERS);
				
				if (layers < 8) {
					
					if (!worldIn.isRemote()) {
						
						state = state.with(LAYERS, ++layers);
						worldIn.setBlockState(pos, state);
						worldIn.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1, 1);
						
					}
					
					updateSideFlow(state, worldIn, pos);
					
					return ActionResultType.SUCCESS;
					
				}
				
			}
			
		}
		
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn instanceof FallingBlockEntity) {
			
			BlockState layerState = ((FallingBlockEntity) entityIn).getBlockState();
			
			if (layerState.getBlock() == this) {
				
				int layers = state.get(LAYERS);
				int fallingLayers = layerState.get(LAYERS);
				layers += fallingLayers;
				int remainingLayers = layers > 8 ? layers % 8 : 0;
				layers -= remainingLayers;
				
				worldIn.setBlockState(pos, state.with(LAYERS, Math.min(8, layers)));
				if (remainingLayers > 0) {
					BlockState topReplaceState = worldIn.getBlockState(pos.up());
					if (topReplaceState.isAir()) {
						worldIn.setBlockState(pos.up(), this.getDefaultState().with(LAYERS, Math.min(8, remainingLayers)));
					}
				}
				
				((FallingBlockEntity) entityIn).shouldDropItem = false;
				entityIn.remove();
				
			}
			
			this.updateSideFlow(state, worldIn, pos);
			
		}
		
		super.onEntityCollision(state, worldIn, pos, entityIn);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		BlockState neighborState = worldIn.getBlockState(fromPos);
		BlockState stateDown = null;
		BlockState stateUp = null;
		if (pos.up().equals(fromPos)) {
			stateUp = neighborState;
			stateDown = state;
		} else if (pos.down().equals(fromPos)) {
			stateDown = neighborState;
			stateUp = state;
		}
		
		if (neighborState.getBlock() == this && stateUp != null && stateDown != null) {
			
			int layersUp = stateUp.get(LAYERS);
			int layersDown = stateDown.get(LAYERS);
			
			if (layersDown < 8) {
				
				layersDown += layersUp;
				int remainingLayers = layersDown > 8 ? layersDown % 8 : 0;
				layersDown -= remainingLayers;
				
				stateDown = stateDown.with(LAYERS, layersDown);
				stateUp = remainingLayers > 0 ? stateUp.with(LAYERS, remainingLayers) : Blocks.AIR.getDefaultState();
				
				if (pos.up().equals(fromPos)) {
					worldIn.setBlockState(fromPos, stateUp);
					worldIn.setBlockState(pos, stateDown);
				} else if (pos.down().equals(fromPos)) {
					worldIn.setBlockState(fromPos, stateDown);
					worldIn.setBlockState(pos, stateUp);
				}
				
			}
			
		}
		
		updateSideFlow(state, worldIn, pos);
		
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}
	
	protected void updateSideFlow(BlockState state, World world, BlockPos pos) {
		
		int layers = state.get(LAYERS);
		
		for (Direction d : Direction.values()) {
			
			if (d.getAxis() != Axis.Y) {

				BlockPos pos2 = pos.offset(d);
				BlockState state2 = world.getBlockState(pos2);
				
				if (state2.getBlock() == this) {
					
					int layers2 = state2.get(LAYERS);
					int diff = layers2 - layers;
					int transfer = (int) Math.floor(diff / 2);
					
					world.setBlockState(pos2, state2.with(LAYERS, layers2 + -transfer), 16);
					layers += transfer;
					state = state.with(LAYERS, layers);
					world.setBlockState(pos, state);
					
				}
				
			}
			
		}
		
	}
	
}
