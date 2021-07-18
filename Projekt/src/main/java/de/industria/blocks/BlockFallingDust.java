package de.industria.blocks;

import java.util.Random;

import de.industria.fluids.util.GasFluid;
import de.industria.util.handler.UtilHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
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
import net.minecraft.world.server.ServerWorld;

public class BlockFallingDust extends BlockFallingBase {
	
	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 16);
	
	public BlockFallingDust(String name) {
		super(name, Material.DIRT, 0.8F, 0.4F, SoundType.SAND);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(LAYERS);
		super.createBlockStateDefinition(builder);
	}
	 
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Block.box(0, 0, 0, 16, state.getValue(LAYERS), 16);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (handIn == Hand.MAIN_HAND) {
			
			ItemStack heldStack = player.getMainHandItem();
			
			if (heldStack.getItem() == this.getCloneItemStack(worldIn, pos, state).getItem()) {
				
				int layers = state.getValue(LAYERS);
				
				if (layers < 16) {
					
					if (!worldIn.isClientSide()) {
						
						state = state.setValue(LAYERS, ++layers);
						worldIn.setBlockAndUpdate(pos, state);
						worldIn.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1, 1);
						
						if (!player.isCreative()) heldStack.shrink(1);
						
					}
					
					worldIn.getBlockTicks().scheduleTick(pos, this, 1);
					
					return ActionResultType.SUCCESS;
					
				}
				
			}
			
		}
		
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn instanceof FallingBlockEntity) {
			
			BlockState layerState = ((FallingBlockEntity) entityIn).getBlockState();
			
			if (layerState.getBlock() == this) {
				
				int layers = state.getValue(LAYERS);
				int fallingLayers = layerState.getValue(LAYERS);
				layers += fallingLayers;
				int remainingLayers = layers > 16 ? layers % 16 : 0;
				layers -= remainingLayers;
				
				worldIn.setBlockAndUpdate(pos, state.setValue(LAYERS, Math.min(16, layers)));
				if (remainingLayers > 0) {
					BlockState topReplaceState = worldIn.getBlockState(pos.above());
					if (topReplaceState.isAir()) {
						worldIn.setBlockAndUpdate(pos.above(), this.defaultBlockState().setValue(LAYERS, Math.min(16, remainingLayers)));
					}
				}
				
				((FallingBlockEntity) entityIn).dropItem = false;
				entityIn.remove();
				
			}
			
			worldIn.getBlockTicks().scheduleTick(pos, this, 1);
			
		}
		
		super.entityInside(state, worldIn, pos, entityIn);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		BlockState neighborState = worldIn.getBlockState(fromPos);
		BlockState stateDown = null;
		BlockState stateUp = null;
		if (pos.above().equals(fromPos)) {
			stateUp = neighborState;
			stateDown = state;
		} else if (pos.below().equals(fromPos)) {
			stateDown = neighborState;
			stateUp = state;
		}
		
		if (neighborState.getBlock() == this && stateUp != null && stateDown != null) {
			
			int layersUp = stateUp.getValue(LAYERS);
			int layersDown = stateDown.getValue(LAYERS);
			
			if (layersDown < 16) {
				
				layersDown += layersUp;
				int remainingLayers = layersDown > 16 ? layersDown % 16 : 0;
				layersDown -= remainingLayers;
				
				stateDown = stateDown.setValue(LAYERS, layersDown);
				stateUp = remainingLayers > 0 ? stateUp.setValue(LAYERS, remainingLayers) : Blocks.AIR.defaultBlockState();
				
				if (pos.above().equals(fromPos)) {
					worldIn.setBlock(fromPos, stateUp, 18);
					worldIn.setBlock(pos, stateDown, 18);
					worldIn.updateNeighborsAt(pos, this);
					this.updateSideFlow(stateDown, worldIn, pos);
					return;
				} else if (pos.below().equals(fromPos)) {
					worldIn.setBlock(fromPos, stateDown, 18);
					worldIn.setBlock(pos, stateUp, 18);
					worldIn.updateNeighborsAt(fromPos, this);
					this.updateSideFlow(stateDown, worldIn, fromPos);
					return;
				}
				
			}
			
		}
		
		worldIn.getBlockTicks().scheduleTick(pos, this, 1);
		
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		this.updateSideFlow(state, worldIn, pos);
		super.tick(state, worldIn, pos, rand);
	}
	
	@SuppressWarnings("deprecation")
	protected void updateSideFlow(BlockState state, World world, BlockPos pos) {
		
		int layers = state.getValue(LAYERS);
		
		if (world.random.nextBoolean()) {
			
			for (Direction d : UtilHelper.sortRandom(Direction.class, Direction.values(), world.random)) {
				
				if (d.getAxis() != Axis.Y) {

					BlockPos pos2 = pos.relative(d);
					BlockState state2 = world.getBlockState(pos2);
					
					if (state2.getBlock() == this) {
						
						int layers2 = state2.getValue(LAYERS);
						int diff = layers2 - layers;
						int transfer = (int) Math.floor(diff / 2);
						if (transfer < 2) transfer = 0;
						
						if (transfer != 0) {
							
							world.setBlock(pos2, state2.setValue(LAYERS, layers2 + -transfer), 18);
							layers += transfer;
							state = state.setValue(LAYERS, layers);
							world.setBlockAndUpdate(pos, state);
							
							world.updateNeighborsAt(pos2, this);
							
						}
						
					} else if (state2.isAir()) {
						
						int diff = layers;
						int transfer = (int) Math.floor(diff / 2);
						if (transfer < 3) transfer = 0;
						
						if (transfer != 0) {
							layers -= transfer;
							state = state.setValue(LAYERS, layers);
							world.setBlockAndUpdate(pos, state);
							world.setBlockAndUpdate(pos2, this.defaultBlockState().setValue(LAYERS, transfer));

							world.updateNeighborsAt(pos2, this);
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
		return state.getValue(LAYERS) < 6 && useContext.getItemInHand().getItem() != Item.byBlock(this);
	}
	
	@Override
	public boolean canBeReplaced(BlockState state, Fluid fluid) {
		return state.getValue(LAYERS) < 14 && !(fluid.getFluid() instanceof GasFluid);
	}
	
}
