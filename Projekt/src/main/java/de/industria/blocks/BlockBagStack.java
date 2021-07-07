package de.industria.blocks;

import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBagStack extends BlockFallingBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty BAGS = IntegerProperty.create("bags", 1, 4);
	
	public BlockBagStack(String name, float hardness, float resistance) {
		super(name, Material.MISCELLANEOUS, hardness, resistance, SoundType.SAND, true);
		this.setDefaultState(this.stateContainer.getBaseState().with(BAGS, 4));
	}
	
	@Override
	public MaterialColor getMaterialColor() {
		return MaterialColor.BROWN_TERRACOTTA;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, BAGS);
		super.fillStateContainer(builder);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(BAGS, 1);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,Hand handIn, BlockRayTraceResult hit) {
		if (state.get(BAGS) < 4 && player.getHeldItem(handIn).getItem() == Item.getItemFromBlock(this)) {
			worldIn.setBlockState(pos, state.with(BAGS, state.get(BAGS) + 1));
			if (!player.isCreative()) player.getHeldItem(handIn).shrink(1);
			worldIn.playSound(null, pos, this.getSoundType(state).getPlaceSound(), SoundCategory.BLOCKS, 1F, 0.8F);
			return ActionResultType.SUCCESS;
		}
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn instanceof FallingBlockEntity && state.get(BAGS) < 4) {
			
			BlockState fallingBags = ((FallingBlockEntity) entityIn).getBlockState();
			
			if (fallingBags.getBlock() == this) {
				
				int bagsToStack = Math.min(fallingBags.get(BAGS), 4 - state.get(BAGS));
				worldIn.setBlockState(pos, state.with(BAGS, state.get(BAGS) + bagsToStack));
				
				if (fallingBags.get(BAGS) == bagsToStack) {
					entityIn.remove();
				} else {
					fallingBags = fallingBags.with(BAGS, fallingBags.get(BAGS) - bagsToStack);
					
					if (worldIn.getBlockState(pos.up()).isAir()) {
						worldIn.setBlockState(pos.up(), fallingBags);
						((FallingBlockEntity) entityIn).shouldDropItem = false;
						entityIn.remove();
					}
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState bagsOnTop = worldIn.getBlockState(pos.up());
		if (bagsOnTop.getBlock() == this && state.get(BAGS) != 4) {
			int bagsToStack = Math.min(bagsOnTop.get(BAGS), 4 - state.get(BAGS));
			worldIn.setBlockState(pos, state.with(BAGS, state.get(BAGS) + bagsToStack));
			
			if (bagsOnTop.get(BAGS) == bagsToStack) {
				worldIn.removeBlock(fromPos, false);
			} else {
				worldIn.setBlockState(fromPos, bagsOnTop.with(BAGS, bagsOnTop.get(BAGS) - bagsToStack));
			}
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = Block.makeCuboidShape(2, 0, 0, 14, 4, 16);
		int bags = state.get(BAGS);
		if (bags > 1) shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 4, 3, 16, 8, 15));
		if (bags > 2) shape = VoxelShapes.or(shape, Block.makeCuboidShape(1, 8, 0, 13, 12, 16));
		if (bags > 3) shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 12, 2, 16, 16, 14));
		return VoxelHelper.rotateShape(shape, state.get(FACING));
	}
	
}
