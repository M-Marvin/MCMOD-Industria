package de.m_marvin.industria.content.items.machines;

import de.m_marvin.industria.content.blocks.machines.ConveyorBeltBlock;
import de.m_marvin.industria.core.compound.types.blocks.CompoundBlock;
import de.m_marvin.industria.core.kinetics.types.items.BeltItem;
import de.m_marvin.industria.core.registries.Tags;
import de.m_marvin.industria.core.util.types.DiagonalDirection;
import de.m_marvin.industria.core.util.types.DiagonalPlanarDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

public class ConveyorBeltItem extends BeltItem {

	public ConveyorBeltItem(ConveyorBeltBlock block, Properties pProperties) {
		super(block, pProperties);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		
		ItemStack stack = pContext.getItemInHand();
		
		// Test if first pos already set
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains("FirstPos")) {
			BlockPos pos = pContext.getClickedPos();
			
			// Get state at first position
			BlockState shaftState = CompoundBlock.performOnTargetedAndReturn(pContext.getLevel(), pContext.getClickedPos(), pContext.getPlayer(), 
					() -> pContext.getLevel().getBlockState(pos), 
					(compound, part) -> part.getState());
			
			// Check if valid block
			if (!shaftState.is(Tags.Blocks.BELT_SHAFTS) || !shaftState.hasProperty(BlockStateProperties.AXIS))
				return InteractionResult.FAIL;
			
			if (shaftState.getValue(BlockStateProperties.AXIS).isVertical())
				return InteractionResult.FAIL;
			
			// Set first position
			tag.putString("Axis", shaftState.getValue(BlockStateProperties.AXIS).getName());
			tag.put("FirstPos", NbtUtils.writeBlockPos(pos));
			stack.setTag(tag);
			return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide());
		} else {
			
			// If shift, clear first position
			if (pContext.getPlayer().isShiftKeyDown()) {
				tag.remove("FirstPos");
				tag.remove("Axis");
				stack.setTag(tag.isEmpty() ? null : tag);
				return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide());
			}
			
			// Get first position and axis
			BlockPos firstPos = NbtUtils.readBlockPos(tag.getCompound("FirstPos"));
			Axis firstAxis = Axis.byName(tag.getString("Axis"));
			
			// Get second position and state
			BlockPos secondPos = pContext.getClickedPos();
			BlockState shaftState = CompoundBlock.performOnTargetedAndReturn(pContext.getLevel(), secondPos, pContext.getPlayer(), 
					() -> pContext.getLevel().getBlockState(secondPos), 
					(compound, part) -> part.getState());
			
			// Check if valid block
			if (!shaftState.is(Tags.Blocks.BELT_SHAFTS) || !shaftState.hasProperty(BlockStateProperties.AXIS))
				return InteractionResult.FAIL;
			
			// Check if axis match
			Axis secondAxis = shaftState.getValue(BlockStateProperties.AXIS);
			if (firstAxis != secondAxis || secondAxis.isVertical())
				return InteractionResult.FAIL;
			
			// Remove first position
			tag.remove("FirstPos");
			tag.remove("Axis");
			stack.setTag(tag.isEmpty() ? null : tag);
			
			return tryPlaceBetween(pContext, firstPos, secondPos, firstAxis);
		}
		
	}

	@Override
	protected InteractionResult tryPlaceBetween(UseOnContext context, BlockPos pos1, BlockPos pos2, Axis axis) {
		
		BlockPos diff = pos2.subtract(pos1);
		// Ends have to be on same plane
		if (axis.choose(diff.getX(), diff.getY(), diff.getZ()) > 0)
			return InteractionResult.FAIL;
		// Ends have to be horizontal, vertical or diagonal (45 degree) to another
		int diff1 = Math.abs(axis.choose(diff.getY(), diff.getZ(), diff.getY()));
		int diff2 = Math.abs(axis.choose(diff.getZ(), diff.getX(), diff.getX()));
		if ((diff1 != diff2 && diff1 != 0) || diff2 == 0)
			return InteractionResult.FAIL;
		
		// Number of blocks to place
		int blocks = Math.max(diff1, diff2) + 1;
		
		// Verify required number of items
		if (context.getItemInHand().getCount() < blocks && !context.getPlayer().isCreative())
			return InteractionResult.FAIL;
		
		DiagonalDirection direction = DiagonalDirection.getNearest(diff.getX(), diff.getY(), diff.getZ());
		DiagonalPlanarDirection orientation = direction.onPlanarWithAxis(axis);
		if (orientation == null) return InteractionResult.FAIL;
		
		BlockState middleState = this.belt.defaultBlockState()
				.setValue(ConveyorBeltBlock.AXIS, axis)
				.setValue(ConveyorBeltBlock.IS_END, false)
				.setValue(ConveyorBeltBlock.ORIENTATION, orientation);
		
		// Check if middle blocks can be placed
		for (int i = 1; i < blocks - 1; i++) {
			BlockPos pos = pos1.offset(direction.getNormal().x * i, direction.getNormal().y * i, direction.getNormal().z * i);

			boolean waterlogged = context.getLevel().getFluidState(pos).isSourceOfType(Fluids.WATER);
			if (!tryPlaceCompound(context.getLevel(), pos, middleState.setValue(BlockStateProperties.WATERLOGGED, waterlogged), true))
				return InteractionResult.FAIL;
		}
		
		// CHeck if end blocks can be placed
		boolean waterlogged1 = context.getLevel().getFluidState(pos1).isSourceOfType(Fluids.WATER);
		boolean waterlogged2 = context.getLevel().getFluidState(pos2).isSourceOfType(Fluids.WATER);
		BlockState endState1 = middleState.setValue(BlockStateProperties.WATERLOGGED, waterlogged1).setValue(ConveyorBeltBlock.IS_END, true);
		BlockState endState2 = endState1.setValue(BlockStateProperties.WATERLOGGED, waterlogged2).setValue(ConveyorBeltBlock.ORIENTATION, orientation.getOposite());
		if (!tryPlaceCompound(context.getLevel(), pos1, endState1, true)) return InteractionResult.FAIL;
		if (!tryPlaceCompound(context.getLevel(), pos2, endState2, true)) return InteractionResult.FAIL;
		
		// Place middle blocks
		for (int i = 1; i < blocks - 1; i++) {
			BlockPos pos = pos1.offset(direction.getNormal().x * i, direction.getNormal().y * i, direction.getNormal().z * i);

			boolean waterlogged = context.getLevel().getFluidState(pos).isSourceOfType(Fluids.WATER);
			tryPlaceCompound(context.getLevel(), pos, middleState.setValue(BlockStateProperties.WATERLOGGED, waterlogged), false);
		}
		
		// Place end blocks
		tryPlaceCompound(context.getLevel(), pos1, endState1, false);
		tryPlaceCompound(context.getLevel(), pos2, endState2, false);
		
		// Consume items
		if (!context.getPlayer().isCreative())
			context.getItemInHand().shrink(blocks);
		
		return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
		
	}
	
}
