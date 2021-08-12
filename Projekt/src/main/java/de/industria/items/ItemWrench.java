package de.industria.items;

import java.util.Collection;

import de.industria.blocks.BlockMultiPart;
import de.industria.typeregistys.ModTabs;
import de.industria.typeregistys.ModToolType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWrench extends ItemToolBase {

	public ItemWrench() {
		super("wrench", 1.5F, 0.5F, ModToolType.WRENCH, ItemTier.IRON, new Properties().tab(ModTabs.TOOLS).stacksTo(1).defaultDurability(640));
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		
		if (context.getPlayer().mayBuild()) {
			World world = context.getLevel();
			BlockPos pos = context.getClickedPos();
			BlockState state = world.getBlockState(pos);
			BlockState stateOld = state;
			
			Collection<Property<?>> props = state.getProperties();
			for (Property<?> prop : props) {
				if (prop instanceof DirectionProperty) {
					boolean horizontal = !((DirectionProperty) prop).getPossibleValues().contains(Direction.UP);
					state = state.setValue((DirectionProperty) prop, rotate(state.getValue((DirectionProperty) prop), context.getPlayer().isShiftKeyDown(), horizontal));
				}
			}
			
			if (state.canSurvive(world, pos) && !(state.getBlock() instanceof BlockMultiPart)) {

				world.setBlock(pos, state, 2);
				
				if (!stateOld.equals(state) && !context.getPlayer().isCreative()) {
					stack.setDamageValue(stack.getDamageValue() + 1);
					if (stack.getDamageValue() > stack.getMaxDamage()) {
						context.getPlayer().setItemInHand(context.getHand(), ItemStack.EMPTY);
					}
				}
				
				return !stateOld.equals(state) ? ActionResultType.SUCCESS : ActionResultType.PASS;
				
			}
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	protected Direction rotate(Direction facing, boolean shift, boolean horizontal) {
		if (shift) {
			return facing.getOpposite();
		} else {
			if (facing == Direction.UP) {
				facing = Direction.DOWN;
			} else if (facing == Direction.DOWN) {
				facing = Direction.NORTH;
			} else if (facing == Direction.NORTH) {
				facing = Direction.SOUTH;
			} else if (facing == Direction.SOUTH) {
				facing = Direction.EAST;
			} else if (facing == Direction.EAST) {
				facing = Direction.WEST;
			} else if (facing == Direction.WEST) {
				facing = horizontal ? Direction.NORTH : Direction.UP;
			}
		}
		return facing;
	}
	
	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		int dammage = itemStack.getDamageValue() + 1;
		if (dammage > this.getMaxDamage(itemStack)) {
			return ItemStack.EMPTY.copy();
		} else {
			ItemStack itemStack2 = itemStack.copy();
			itemStack2.setDamageValue(dammage);
			return itemStack2;
		}
	}
	
	@Override
	public boolean isRepairable(ItemStack stack) {
		return true;
	}
	
}
