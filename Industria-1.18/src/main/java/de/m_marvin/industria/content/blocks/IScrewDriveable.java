package de.m_marvin.industria.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public interface IScrewDriveable {
	
	public default boolean isAdjustable(BlockState targetBlock, Direction face, Vec3 position) {
		return false;
	}
	
	public default InteractionResult onScrewDriveAdjusting(BlockState targetBlock, UseOnContext context, double delta) {
		return InteractionResult.PASS;
	}
	
	public default InteractionResult onScrewDrived(BlockState targetedBlock, UseOnContext context) {
		BlockState rotatedBlock = getRotatedBlockState(targetedBlock, context.getClickedFace());
		if (!rotatedBlock.canSurvive(context.getLevel(), context.getClickedPos())) {
			return InteractionResult.PASS;
		}
		
//		if (targetedBlock.getBlock() instanceof IRotate) {
//			KineticTileEntity.switchToBlockState(context.getLevel(), context.getClickedPos(),
//					Block.updateFromNeighbourShapes(rotatedBlock, (LevelAccessor) context.getLevel(), context.getClickedPos()));
//			final BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
//			if (te instanceof GeneratingKineticTileEntity) {
//				((GeneratingKineticTileEntity) te).reActivateSource = true;
//			}
//		} else {
//			context.getLevel().setBlock(context.getClickedPos(), rotatedBlock, 3);
//		}
		
		
		
		if (context.getLevel().getBlockState(context.getClickedPos()) != targetedBlock) {
			
		}
		return InteractionResult.SUCCESS;
	}

	public default InteractionResult onSneakScrewDrived(BlockState targetedBlock, UseOnContext context) {
		return pickupBlock(context.getClickedPos(), targetedBlock, context);
	}
	
	public static InteractionResult pickupBlock(BlockPos position, BlockState state, UseOnContext context) {
		if (context.getLevel().isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
			Block.getDrops(state, (ServerLevel) context.getLevel(), position, context.getLevel().getBlockEntity(position), context.getPlayer(), context.getItemInHand()).
				forEach(itemStack -> context.getPlayer().getInventory().placeItemBackInInventory(itemStack));
		}
		state.spawnAfterBreak((ServerLevel) context.getLevel(), position, ItemStack.EMPTY);
		context.getLevel().destroyBlock(position, false);
		return InteractionResult.SUCCESS;
	}
	
	public static BlockState getRotatedBlockState(BlockState originalState, Direction rotatePlaneFace) {
		
//		if (originalState.hasProperty(BlockStateProperties.FACING)) {
//			return originalState.setValue(BlockStateProperties.FACING, DirectionHelper.rotateAround(originalState.getValue(BlockStateProperties.FACING), rotatePlaneFace.getAxis()));
//		}
//		if (originalState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
//			return originalState.setValue(BlockStateProperties.HORIZONTAL_FACING, originalState.getValue(BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
//		}
//		if (originalState.hasProperty(BlockStateProperties.AXIS)) {
//			return originalState.setValue(BlockStateProperties.AXIS, DirectionHelper.rotateAround(VoxelShaper.axisAsFace(originalState.getValue(BlockStateProperties.AXIS)), rotatePlaneFace.getAxis()).getAxis());
//		}
		return originalState;
	}
	
}
