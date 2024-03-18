package de.m_marvin.industria.core.conduits.types.items;

import java.util.List;
import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

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
	
	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		this.conduit.get().appendHoverText(pTooltipComponents, pIsAdvanced);
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}
	
}
