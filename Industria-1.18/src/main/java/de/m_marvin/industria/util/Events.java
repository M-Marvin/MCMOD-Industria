package de.m_marvin.industria.util;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.conduit.ConduitWorldStorageCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

	@SubscribeEvent
	public static void onClientWorldTick(ClientTickEvent event) {
		
		@SuppressWarnings("resource")
		ClientLevel level = Minecraft.getInstance().level;
		
		if (level != null) {
			LazyOptional<ConduitWorldStorageCapability> conduitCap = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
			if (conduitCap.isPresent()) {
				conduitCap.resolve().get().updateConduitPhysic(level);
			}
		}
		
	}
	
	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		
		Level level = event.world;
		
		LazyOptional<ConduitWorldStorageCapability> conduitCap = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitCap.isPresent()) {
			conduitCap.resolve().get().updateConduitPhysic(level);
		}
		
	}
	
}
