package de.industria.blocks;

import java.util.Random;

import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockIronLadder extends LadderBlock {

	public BlockIronLadder(String name) {
		super(Properties.of(Material.METAL).strength(0.8F).sound(SoundType.METAL).harvestTool(BlockBase.getDefaultToolType(Material.METAL)));
		this.setRegistryName(name);
	}
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		
		BlockState blockstate1 = this.defaultBlockState();
		IWorldReader iworldreader = context.getLevel();
	  	BlockPos blockpos = context.getClickedPos();
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		
		for(Direction direction : context.getNearestLookingDirections()) {
			if (direction.getAxis().isHorizontal()) {
				blockstate1 = blockstate1.setValue(FACING, direction.getOpposite());
				
				if (blockstate1.canSurvive(iworldreader, blockpos)) {
					return blockstate1.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
				}
			}
		}
		
		return null;
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		return findNextAnchor(pos, state, world) != null || super.canSurvive(state, world, pos);
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neghbor, BlockPos neightborPos, boolean moved) {
		if (!canSurvive(state, world, pos)) {
			world.getBlockTicks().scheduleTick(pos, this, 1);
		}
	}
	
	public BlockState updateShape(BlockState state, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
		return state;
	}
	
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!canSurvive(state, world, pos)) world.destroyBlock(pos, true);
	}
	
	public BlockPos findNextAnchor(BlockPos pos, BlockState state, IWorldReader world) {
		for (int i = 1; i < 256; i++) {
			BlockPos posUp = pos.above(i);
			if (posUp.getY() < 256) {
				BlockState stateUp = world.getBlockState(posUp);
				if (stateUp.getBlock() == this ? stateUp.getValue(FACING) == state.getValue(FACING) : false) {
					if (super.canSurvive(stateUp, world, posUp)) return posUp;
				} else {
					break;
				}
			} else {
				break;
			}
		}
		for (int i = 1; i < 256; i++) {
			BlockPos posDown = pos.below(i);
			if (posDown.getY() >= 0) {
				BlockState stateDown = world.getBlockState(posDown);
				if (stateDown.getBlock() == this ? stateDown.getValue(FACING) == state.getValue(FACING) : false) {
					if (super.canSurvive(stateDown, world, posDown)) return posDown;
				} else {
					break;
				}
			} else {
				break;
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace) {
		if (hand == Hand.MAIN_HAND && player.getMainHandItem().getItem() == Item.byBlock(this)) {
			Direction stackDirection = player.getRotationVector().x > 0 ? Direction.DOWN : Direction.UP;
			
			for (int i = 0; i < 256; i++) {
				BlockPos placePos = pos.relative(stackDirection, i);
				BlockState replaceState = world.getBlockState(placePos);
				
				if (replaceState.getBlock() != this) {
					if (replaceState.getBlock().canBeReplaced(replaceState, new BlockItemUseContext(player, hand, player.getMainHandItem(), raytrace))) {
						world.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1, 1);
						world.setBlock(placePos, state.setValue(WATERLOGGED, replaceState.getFluidState().getType() == Fluids.WATER), 2);
						if (!player.isCreative()) player.getMainHandItem().shrink(1);
						return ActionResultType.SUCCESS;
					} else {
						return ActionResultType.PASS;
					}
				}
			}
			
		}
		return ActionResultType.PASS;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return VoxelHelper.rotateShape(Block.box(0, 0, 15, 16, 16, 15.2F), state.getValue(FACING));
	}
	
}
