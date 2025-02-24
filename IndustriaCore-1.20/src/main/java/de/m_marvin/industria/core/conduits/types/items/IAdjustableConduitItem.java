package de.m_marvin.industria.core.conduits.types.items;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.network.CChangeConduitPlacementLengthPackage;
import de.m_marvin.industria.core.scrollinput.engine.ScrollInputListener.ScrollContext;
import de.m_marvin.industria.core.scrollinput.type.items.IScrollOverride;
import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface IAdjustableConduitItem extends IConduitItem, IScrollOverride {

	@Override
	public default boolean overridesScroll(ScrollContext context) {
		return context.getItemInHand().hasTag() && context.getItemInHand().getTag().contains("FirstNode");
	}
	
	@Override
	public default void onScroll(ScrollContext context) {
		CompoundTag itemTag = context.getItemInHand().getOrCreateTag();
		float placementLength = (float) MathUtility.clamp(itemTag.getFloat("Length") + context.getScroll() * 0.1F, 1F, 3F);
		IndustriaCore.NETWORK.sendToServer(new CChangeConduitPlacementLengthPackage(placementLength));
		context.getPlayer().displayClientMessage(Component.translatable("industriacore.item.info.conduit.changeLength", Math.round(placementLength * 10.0) / 10.0), true);
	}
	
	public default void onChangePlacementLength(ItemStack stack, float length) {
		CompoundTag itemTag = stack.getOrCreateTag();
		itemTag.putFloat("Length", length);
		stack.setTag(itemTag);
	}
	
}
