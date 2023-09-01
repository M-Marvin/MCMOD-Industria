package de.m_marvin.industria.content.items;

import java.util.function.Supplier;

import de.m_marvin.industria.content.blockentities.ConduitCoilBlockEntity;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ConduitCoilItem extends AbstractConduitBlockItem {
	
	public static final int MAX_WIRES_ON_COIL = 32;
	
	protected Supplier<Item> emptyCoil;
	
	public ConduitCoilItem(Block block, Supplier<Conduit> conduit, Supplier<Item> emptyItem, Properties properties) {
		super(block, conduit, properties);
		this.emptyCoil = emptyItem;
	}
	
	public Item getEmptyCoil() {
		return emptyCoil.get();
	}
	
	@Override
	public int getMaxPlacingLength(ItemStack stack) {
		int length = stack.getOrCreateTag().getInt("WireLength");
		return length * Conduit.BLOCKS_PER_WIRE_ITEM;
	}
	
	@Override
	public void onPlaced(UseOnContext context, int length) {
		ItemStack stack = context.getItemInHand();
		if (stack.getCount() > 1) {
			ItemStack stack2 = stack.copy();
			stack2.shrink(1);
			context.getPlayer().getInventory().placeItemBackInInventory(stack2);
			stack.setCount(1);
		}
		int lengthOnCoil = stack.getOrCreateTag().getInt("WireLength");
		lengthOnCoil -= (int) Math.ceil(length / (float) Conduit.BLOCKS_PER_WIRE_ITEM);
		if (lengthOnCoil <= 0) {
			context.getPlayer().setItemInHand(context.getHand(), new ItemStack(getEmptyCoil()));
		} else {
			stack.getOrCreateTag().putInt("WireLength", lengthOnCoil);
			context.getPlayer().setItemInHand(context.getHand(), stack);
		}
	}
	
	@Override
	protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
		boolean s = super.placeBlock(pContext, pState);
		if (s && pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof ConduitCoilBlockEntity wireCoil) {
			wireCoil.setWireLength(pContext.getItemInHand().getOrCreateTag().getInt("WireLength"));
		}
		return s;
	}
	
	public ItemStack getWithWires(int wireCount) {
		ItemStack stack = getDefaultInstance();
		stack.getOrCreateTag().putInt("WireLength", wireCount);
		return stack;
	}
	
	public ItemStack getFilledInstance() {
		return getWithWires(MAX_WIRES_ON_COIL);
	}
	
}
