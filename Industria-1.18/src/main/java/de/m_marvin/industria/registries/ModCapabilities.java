package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.util.conduit.ConduitWorldStorageCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=Industria.MODID)
public class ModCapabilities {
	
	public static final Capability<ConduitWorldStorageCapability> CONDUIT_HOLDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<ConduitWorldStorageCapability>() {});
	
	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(ConduitWorldStorageCapability.class);
	}

	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=Industria.MODID)
	public class Attachment {
		
		@SubscribeEvent
		public static void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
			event.addCapability(new ResourceLocation(Industria.MODID, "conduits"), new ConduitWorldStorageCapability(event.getObject()));
		}
		
	}
	
}
