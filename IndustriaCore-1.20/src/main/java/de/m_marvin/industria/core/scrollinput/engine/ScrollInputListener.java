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
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ScrollInputListener {
	
	public static class ScrollContext extends UseOnContext {
		
		private final HitResult hitResult;
		private final double scroll;
		
		public ScrollContext(Level pLevel, Player pPlayer, InteractionHand pHand, ItemStack pItemStack, HitResult pHitResult, double pScroll) {
			super(pLevel, pPlayer, pHand, pItemStack, pHitResult instanceof BlockHitResult blockHit ? blockHit : null);
			this.hitResult = pHitResult;
			this.scroll = pScroll;
		}
		
		public HitResult getAllHitResult() {
			return hitResult;
		}
		
		public double getScroll() {
			return scroll;
		}
		
	}
	
	@SubscribeEvent
	public static void onMouseScrollInput(InputEvent.MouseScrollingEvent event) {
		performScrollOverrides(event, InteractionHand.MAIN_HAND);
		performScrollOverrides(event, InteractionHand.OFF_HAND);
	}

	@SuppressWarnings("resource")
	protected static void performScrollOverrides(InputEvent.MouseScrollingEvent event, InteractionHand hand) {
		
		Player player = Minecraft.getInstance().player;
		ItemStack heldItem = player.getItemInHand(hand);
		
		if (!heldItem.isEmpty() && heldItem.getItem() instanceof IScrollOverride scrollItem) {

			ClientLevel level = Minecraft.getInstance().level;
			HitResult hit = MathUtility.getPlayerPOVHitResultWithEntitites(level, player, Fluid.NONE, player.getBlockReach());
			ScrollContext context = new ScrollContext(level, player, hand, heldItem, hit, event.getScrollDelta());
			
			if (scrollItem.overridesScroll(context)) {
				event.setCanceled(true);
				((IScrollOverride) heldItem.getItem()).onScroll(context);
			}
			
		}
		
	}
	

}