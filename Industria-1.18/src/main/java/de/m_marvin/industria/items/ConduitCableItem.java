package de.m_marvin.industria.items;

import java.util.function.Supplier;

import de.m_marvin.industria.conduits.Conduit;
import net.minecraft.world.item.ItemStack;

public class ConduitCableItem extends AbstractConduitItem {
	
	public static final int BLOCKS_PER_WIRE_ITEM = 2;
	
	public ConduitCableItem(Properties properties, Supplier<Conduit> conduit) {
		super(properties, conduit);
	}

	@Override
	public int getMaxPlacingLength(ItemStack stack) {
		return stack.getCount() * BLOCKS_PER_WIRE_ITEM;
	}

	@Override
	public void onPlaced(ItemStack stack, int length) {
		int required = (int) Math.ceil(length / (float) BLOCKS_PER_WIRE_ITEM);
		stack.shrink(required);
	}
	
}
