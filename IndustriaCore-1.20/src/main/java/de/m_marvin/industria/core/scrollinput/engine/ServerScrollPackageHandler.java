package de.m_marvin.industria.core.scrollinput.engine;

import de.m_marvin.industria.core.scrollinput.engine.ScrollInputListener.ScrollContext;
import de.m_marvin.industria.core.scrollinput.engine.network.CScrollInputPackage;
import de.m_marvin.industria.core.scrollinput.type.items.IScrollOverride;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerScrollPackageHandler {
	
	public static void handleScrollPackage(CScrollInputPackage msg, Context ctx) {

		ServerPlayer player = ctx.getSender();
		ServerLevel level = player.serverLevel();
		ItemStack heldItem = player.getItemInHand(msg.getHand());
		ScrollContext context = new ScrollContext(level, player, msg.getHand(), heldItem, msg.getHitResult(), msg.getScrollDelta());
		
		if (!heldItem.isEmpty() && heldItem.getItem() instanceof IScrollOverride && ((IScrollOverride) heldItem.getItem()).overridesScroll(context)) {
			((IScrollOverride) heldItem.getItem()).onScroll(context);
		}
		
	}
		
}
