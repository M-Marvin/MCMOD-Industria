package de.m_marvin.industria.core.kinetics.types.items;

import de.m_marvin.industria.core.compound.types.blocks.CompoundBlock;
import de.m_marvin.industria.core.compound.types.items.CompoundableBlockItem;
import de.m_marvin.industria.core.kinetics.types.blocks.BeltBlock;
import de.m_marvin.industria.core.registries.Tags;
import de.m_marvin.industria.core.scrollinput.engine.ScrollInputListener.ScrollContext;
import de.m_marvin.industria.core.scrollinput.type.items.IScrollOverride;
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

public class BeltItem<T extends BeltBlock> extends CompoundableBlockItem implements IScrollOverride {
	
	protected final T belt;
	
	public BeltItem(T block, Properties pProperties) {
		super(block, pProperties);
		this.belt = block;
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
			if (firstAxis != secondAxis)
				return InteractionResult.FAIL;
			
			// Remove first position
			tag.remove("FirstPos");
			tag.remove("Axis");
			stack.setTag(tag.isEmpty() ? null : tag);
			
			return tryPlaceBetween(pContext, firstPos, secondPos, firstAxis);
		}
		
	}

	protected InteractionResult tryPlaceBetween(UseOnContext context, BlockPos pos1, BlockPos pos2, Axis axis) {
		
		BlockPos diff = pos2.subtract(pos1);
		// Ends have to be on same plane
		if (axis.choose(diff.getX(), diff.getY(), diff.getZ()) > 0)
			return InteractionResult.FAIL;
		// Ends have to be horizontal, vertical or diagonal (45 degree) to another
		int diff1 = Math.abs(axis.choose(diff.getY(), diff.getZ(), diff.getX()));
		int diff2 = Math.abs(axis.choose(diff.getZ(), diff.getX(), diff.getY()));
		if (diff1 != diff2 && diff1 != 0 && diff2 != 0)
			return InteractionResult.FAIL;
		
		// Number of blocks to place
		int blocks = Math.max(diff1, diff2) + 1;
		
		// Verify required number of items
		if (context.getItemInHand().getCount() < blocks && !context.getPlayer().isCreative())
			return InteractionResult.FAIL;
		
		DiagonalDirection direction = DiagonalDirection.getNearest(diff.getX(), diff.getY(), diff.getZ());
		DiagonalPlanarDirection orientation = direction.onPlanarWithAxis(axis);
		BlockState middleState = this.belt.defaultBlockState()
				.setValue(BeltBlock.AXIS, axis)
				.setValue(BeltBlock.IS_END, false)
				.setValue(BeltBlock.ORIENTATION, orientation);
		
		// Check if middle blocks can be placed
		for (int i = 1; i < blocks - 1; i++) {
			BlockPos pos = pos1.offset(direction.getNormal().x * i, direction.getNormal().y * i, direction.getNormal().z * i);
			
			if (!tryPlaceCompound(context.getLevel(), pos, middleState, true))
				return InteractionResult.FAIL;
		}
		
		// CHeck if end blocks can be placed
		BlockState endState1 = middleState.setValue(BeltBlock.IS_END, true);
		BlockState endState2 = endState1.setValue(BeltBlock.ORIENTATION, orientation.getOposite());
		if (!tryPlaceCompound(context.getLevel(), pos1, endState1, true)) return InteractionResult.FAIL;
		if (!tryPlaceCompound(context.getLevel(), pos2, endState2, true)) return InteractionResult.FAIL;
		
		// Place middle blocks
		for (int i = 1; i < blocks - 1; i++) {
			BlockPos pos = pos1.offset(direction.getNormal().x * i, direction.getNormal().y * i, direction.getNormal().z * i);
			
			tryPlaceCompound(context.getLevel(), pos, middleState, false);
		}
		
		// Place end blocks
		tryPlaceCompound(context.getLevel(), pos1, endState1, false);
		tryPlaceCompound(context.getLevel(), pos2, endState2, false);
		
		// Consume items
		if (!context.getPlayer().isCreative())
			context.getItemInHand().shrink(blocks);
		
		return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
		
	}
	
	@Override
	public boolean overridesScroll(ScrollContext context) {
		CompoundTag tag =  context.getItemInHand().getTag();
		return tag != null && tag.contains("FirstPos");
	}

	@Override
	public void onScroll(ScrollContext context) {}
	
}
