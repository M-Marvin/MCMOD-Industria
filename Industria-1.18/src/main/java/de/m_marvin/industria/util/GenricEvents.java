package de.m_marvin.industria.util;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.network.CBreakConduitPackage;
import de.m_marvin.industria.util.conduit.ConduitHitResult;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class GenricEvents {
	
	/* Schears override to make it possible to break conduits with it 
	 * Breaking is done on ServerPackageHandler
	 */
	
	@SubscribeEvent
	public static void onItemUsed(PlayerInteractEvent.RightClickItem event) {
		
		LivingEntity entity = event.getEntityLiving();
		ItemStack itemStack = event.getPlayer().getMainHandItem();
		
		if (itemStack.getItem() == Items.SHEARS && event.getWorld().isClientSide()) {
			float range = 6;
			Vec3 viewVec = entity.getViewVector(0);
			Vec3 eyePos = entity.getEyePosition();
			Vec3 rayTarget = eyePos.add(viewVec.multiply(range, range, range));
			ClipContext clipContext = new ClipContext(eyePos, rayTarget, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
			
			ConduitHitResult hitResult = UtilityHelper.clipConduits(event.getWorld(), clipContext, true);
			if (hitResult.isHit()) {
				PlacedConduit conduit = hitResult.getConduitState();
				UtilityHelper.removeConduit(event.getWorld(), conduit.getConduitPosition(), true);
				Industria.NETWORK.sendToServer(new CBreakConduitPackage(conduit.getConduitPosition(), true));
				
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
			}
			
		}
		
	}
	
}
