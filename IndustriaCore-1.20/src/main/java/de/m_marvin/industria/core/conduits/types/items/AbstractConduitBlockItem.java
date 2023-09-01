package de.m_marvin.industria.core.conduits.types.items;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public abstract class AbstractConduitBlockItem extends BlockItem implements IAdjustableConduitItem {
	
	private Supplier<Conduit> conduit;
	
	public AbstractConduitBlockItem(Block block, Supplier<Conduit> conduit, Properties properties) {
		super(block, properties);
		this.conduit = conduit;
	}
	
	@Override
	public Conduit getConduit() {
		return this.conduit.get();
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		CompoundTag itemTag = context.getItemInHand().getOrCreateTag();
		if (context.getPlayer().isShiftKeyDown() && (itemTag.contains("FirstNode") || itemTag.contains("Length"))) 
			return onUsePlacement(context);
		if (context.getPlayer().isShiftKeyDown()) return super.useOn(context);
		return onUsePlacement(context);
	}
	
}
