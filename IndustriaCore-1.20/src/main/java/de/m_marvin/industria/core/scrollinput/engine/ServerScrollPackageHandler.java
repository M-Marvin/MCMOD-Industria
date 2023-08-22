package de.m_marvin.industria.core.scrollinput.engine;

import de.m_marvin.industria.core.scrollinput.engine.network.CScrollInputPackage;
import de.m_marvin.industria.core.scrollinput.type.items.IScrollOverride;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerScrollPackageHandler {
	
	public static void handleScrollPackage(CScrollInputPackage msg, Context context) {

		ServerPlayer player = context.getSender();
		ServerLevel level = player.serverLevel();
		ItemStack heldItem = player.getItemInHand(msg.getHand());
		UseOnContext ctx = new UseOnContext(level, player, msg.getHand(), heldItem, msg.getHitResult());
		
		if (!heldItem.isEmpty() && heldItem.getItem() instanceof IScrollOverride && ((IScrollOverride) heldItem.getItem()).overridesScroll(ctx, heldItem)) {
			
			((IScrollOverride) heldItem.getItem()).onScroll(ctx, msg.getScrollDelta());
			
		}
		
	}
		
}
