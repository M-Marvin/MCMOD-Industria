package de.m_marvin.industria.client.util;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.util.item.IScrollOverride;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientEvents {
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onMouseScrollInput(InputEvent.MouseScrollEvent event) {
		
		ClientLevel level = Minecraft.getInstance().level;
		LocalPlayer player = Minecraft.getInstance().player;
		ItemStack heldItem = player.getMainHandItem();
		
		if (!heldItem.isEmpty() && heldItem.getItem() instanceof IScrollOverride && ((IScrollOverride) heldItem.getItem()).overridesScroll(heldItem)) {
			
			event.setCanceled(true);
			((IScrollOverride) heldItem.getItem()).onScroll(new UseOnContext(level, player, InteractionHand.MAIN_HAND, heldItem, null), event.getScrollDelta());
			
		}
		
	}
	
}
