package de.m_marvin.industria.core.conduits.types.items;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public abstract class AbstractConduitItem extends Item implements IAdjustableConduitItem {
	
	private Supplier<Conduit> conduit;
	
	public AbstractConduitItem(Properties properties, Supplier<Conduit> conduit) {
		super(properties);
		this.conduit = conduit;
	}
	
	public Conduit getConduit() {
		return conduit.get();
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		return onUsePlacement(context);
	}
	
}
