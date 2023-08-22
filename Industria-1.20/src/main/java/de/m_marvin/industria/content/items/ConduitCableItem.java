package de.m_marvin.industria.content.items;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import net.minecraft.world.item.ItemStack;

public class ConduitCableItem extends AbstractConduitItem {
	
	public ConduitCableItem(Properties properties, Supplier<Conduit> conduit) {
		super(properties, conduit);
	}

	@Override
	public int getMaxPlacingLength(ItemStack stack) {
		return stack.getCount() * Conduit.BLOCKS_PER_WIRE_ITEM;
	}

	@Override
	public void onPlaced(ItemStack stack, int length) {
		int required = (int) Math.ceil(length / (float) Conduit.BLOCKS_PER_WIRE_ITEM);
		stack.shrink(required);
	}
	
}
