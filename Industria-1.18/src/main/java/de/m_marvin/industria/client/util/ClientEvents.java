package de.m_marvin.industria.client.util;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.item.IScrollOverride;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientEvents {
	
	@SubscribeEvent
	public static void onMouseScrollInput(InputEvent.MouseScrollEvent event) {
		performScrollOverrides(event, InteractionHand.MAIN_HAND);
		performScrollOverrides(event, InteractionHand.OFF_HAND);
	}
	
	@SuppressWarnings("resource")
	protected static void performScrollOverrides(InputEvent.MouseScrollEvent event, InteractionHand hand) {
		
		ClientLevel level = Minecraft.getInstance().level;
		LocalPlayer player = Minecraft.getInstance().player;
		ItemStack heldItem = player.getItemInHand(hand);
		UseOnContext result = UtilityHelper.raycastBlockClick(level, player, hand, player.getReachDistance());
		BlockHitResult hitResult = null;
		if (result != null) {
			hitResult = result.getHitResult();
		}
		UseOnContext context = new UseOnContext(level, player, hand, heldItem, hitResult);
		
		if (!heldItem.isEmpty() && heldItem.getItem() instanceof IScrollOverride && ((IScrollOverride) heldItem.getItem()).overridesScroll(context, heldItem)) {
			
			event.setCanceled(true);
			((IScrollOverride) heldItem.getItem()).onScroll(context, event.getScrollDelta());
			
		}
		
	}
	
}
