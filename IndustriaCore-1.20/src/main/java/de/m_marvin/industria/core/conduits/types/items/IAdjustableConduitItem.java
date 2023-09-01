package de.m_marvin.industria.core.conduits.types.items;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.network.CChangeConduitPlacementLengthPackage;
import de.m_marvin.industria.core.scrollinput.type.items.IScrollOverride;
import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public interface IAdjustableConduitItem extends IConduitItem, IScrollOverride {

	@Override
	public default boolean overridesScroll(UseOnContext context, ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("FirstNode");
	}
	
	@Override
	public default void onScroll(UseOnContext context, double delta) {
		CompoundTag itemTag = context.getItemInHand().getOrCreateTag();
		float placementLength = (float) MathUtility.clamp(itemTag.getFloat("Length") + delta * 0.1F, 1F, 3F);
		IndustriaCore.NETWORK.sendToServer(new CChangeConduitPlacementLengthPackage(placementLength));
		context.getPlayer().displayClientMessage(Component.translatable("industriacore.item.info.conduit.changeLength", Math.round(placementLength * 10.0) / 10.0), true);
	}
	
	public default void onChangePlacementLength(ItemStack stack, float length) {
		CompoundTag itemTag = stack.getOrCreateTag();
		itemTag.putFloat("Length", length);
		stack.setTag(itemTag);
	}
	
}
