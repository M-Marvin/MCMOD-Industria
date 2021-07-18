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
		super(name, Material.DECORATION, hardness, resistance, SoundType.SAND, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(BAGS, 4));
	}
	
	@Override
	public MaterialColor defaultMaterialColor() {
		return MaterialColor.TERRACOTTA_BROWN;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, BAGS);
		super.createBlockStateDefinition(builder);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(BAGS, 1);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,Hand handIn, BlockRayTraceResult hit) {
		if (state.getValue(BAGS) < 4 && player.getItemInHand(handIn).getItem() == Item.byBlock(this)) {
			worldIn.setBlockAndUpdate(pos, state.setValue(BAGS, state.getValue(BAGS) + 1));
			if (!player.isCreative()) player.getItemInHand(handIn).shrink(1);
			worldIn.playSound(null, pos, this.getSoundType(state).getPlaceSound(), SoundCategory.BLOCKS, 1F, 0.8F);
			return ActionResultType.SUCCESS;
		}
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		if (entityIn instanceof FallingBlockEntity && state.getValue(BAGS) < 4 && entityIn.isAlive()) {
			
			BlockState fallingBags = ((FallingBlockEntity) entityIn).getBlockState();
			
			if (fallingBags.getBlock() == this) {
				
				int bagsToStack = Math.min(fallingBags.getValue(BAGS), 4 - state.getValue(BAGS));
				worldIn.setBlockAndUpdate(pos, state.setValue(BAGS, state.getValue(BAGS) + bagsToStack));
				
				if (fallingBags.getValue(BAGS) == bagsToStack) {
					entityIn.remove();
				} else {
					fallingBags = fallingBags.setValue(BAGS, fallingBags.getValue(BAGS) - bagsToStack);
					
					if (worldIn.getBlockState(pos.above()).isAir()) {
						worldIn.setBlockAndUpdate(pos.above(), fallingBags);
						((FallingBlockEntity) entityIn).dropItem = false;
						entityIn.remove();
					}
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState bagsOnTop = worldIn.getBlockState(pos.above());
		if (bagsOnTop.getBlock() == this && state.getValue(BAGS) != 4) {
			int bagsToStack = Math.min(bagsOnTop.getValue(BAGS), 4 - state.getValue(BAGS));
			worldIn.setBlockAndUpdate(pos, state.setValue(BAGS, state.getValue(BAGS) + bagsToStack));
			
			if (bagsOnTop.getValue(BAGS) == bagsToStack) {
				worldIn.removeBlock(fromPos, false);
			} else {
				worldIn.setBlockAndUpdate(fromPos, bagsOnTop.setValue(BAGS, bagsOnTop.getValue(BAGS) - bagsToStack));
			}
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = Block.box(2, 0, 0, 14, 4, 16);
		int bags = state.getValue(BAGS);
		if (bags > 1) shape = VoxelShapes.or(shape, Block.box(0, 4, 3, 16, 8, 15));
		if (bags > 2) shape = VoxelShapes.or(shape, Block.box(1, 8, 0, 13, 12, 16));
		if (bags > 3) shape = VoxelShapes.or(shape, Block.box(0, 12, 2, 16, 16, 14));
		return VoxelHelper.rotateShape(shape, state.getValue(FACING));
	}
	
}
