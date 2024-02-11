package de.m_marvin.industria.core.conduits.engine;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitHitResult;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class GenricConduitEventListener {
	
	// Shears override to make it possible to break conduits with it 
	// Breaking is done on ServerPackageHandler
	
	@SubscribeEvent
	public static void onItemUsed(PlayerInteractEvent.RightClickItem event) {
		
		Player player = event.getEntity();
		ItemStack itemStack = event.getEntity().getMainHandItem();
		
		if (itemStack.getItem() == Items.SHEARS && event.getLevel().isClientSide()) {
			
			double range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
			Vec3 viewVec = player.getViewVector(0);
			Vec3 eyePos = player.getEyePosition();
			Vec3 rayTarget = eyePos.add(viewVec.multiply(range, range, range));
			ClipContext clipContext = new ClipContext(eyePos, rayTarget, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
			
			ConduitHitResult hitResult = ConduitUtility.clipConduits(event.getLevel(), clipContext, true);
			if (hitResult.isHit()) {
				ConduitEntity conduit = hitResult.getConduitState();
				ConduitUtility.removeConduitFromClient(event.getLevel(), conduit.getPosition(), !player.isCreative());
				
				ItemStack toolItem = event.getEntity().getMainHandItem();
				if (!toolItem.isEmpty() && toolItem.getMaxDamage() != 0) {
					toolItem.hurtAndBreak(1, player, (p) -> {}); // TODO test if damage is visible on client side
				}
				
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
			}
			
		}
		
	}
	
}
