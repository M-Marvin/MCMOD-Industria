package de.m_marvin.industria.core.scrollinput.engine;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.scrollinput.type.items.IScrollOverride;
import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ScrollInputListener {
	
	@SubscribeEvent
	public static void onMouseScrollInput(InputEvent.MouseScrollingEvent event) {
		performScrollOverrides(event, InteractionHand.MAIN_HAND);
		performScrollOverrides(event, InteractionHand.OFF_HAND);
	}
	
	@SuppressWarnings("resource")
	protected static void performScrollOverrides(InputEvent.MouseScrollingEvent event, InteractionHand hand) {
		
		ClientLevel level = Minecraft.getInstance().level;
		Player player = Minecraft.getInstance().player;
		ItemStack heldItem = player.getItemInHand(hand);
		UseOnContext result = MathUtility.raycastBlockClick(level, player, hand, player.getBlockReach());
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