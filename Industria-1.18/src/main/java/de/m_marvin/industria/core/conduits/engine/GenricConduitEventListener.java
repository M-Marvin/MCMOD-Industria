package de.m_marvin.industria.core.conduits.engine;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class GenricConduitEventListener {
	
	// Shears override to make it possible to break conduits with it 
	// Breaking is done on ServerPackageHandler
	
	@SubscribeEvent
	public static void onItemUsed(PlayerInteractEvent.RightClickItem event) {
		
		LivingEntity entity = event.getEntityLiving();
		ItemStack itemStack = event.getPlayer().getMainHandItem();
		
		if (itemStack.getItem() == Items.SHEARS && event.getWorld().isClientSide()) {
			
			double range = entity.getAttributeValue(ForgeMod.REACH_DISTANCE.get());
			Vec3 viewVec = entity.getViewVector(0);
			Vec3 eyePos = entity.getEyePosition();
			Vec3 rayTarget = eyePos.add(viewVec.multiply(range, range, range));
			ClipContext clipContext = new ClipContext(eyePos, rayTarget, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
			
			ConduitHitResult hitResult = ConduitUtility.clipConduits(event.getWorld(), clipContext, true);
			if (hitResult.isHit()) {
				PlacedConduit conduit = hitResult.getConduitState();
				ConduitUtility.removeConduitFromClient(event.getWorld(), conduit.getConduitPosition(), true);
				
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
			}
			
		}
		
	}
	
}
