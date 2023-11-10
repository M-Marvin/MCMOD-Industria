package de.m_marvin.industria.content.magnetism;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.magnetism.engine.MagnetismHandlerCapability;
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
public class Capabilities {

	public static final Capability<MagnetismHandlerCapability> MAGNETISM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<MagnetismHandlerCapability>() {});
	
	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(MagnetismHandlerCapability.class);
	}

	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=Industria.MODID)
	public class Attachment {
		
		@SubscribeEvent
		public static void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
			event.addCapability(new ResourceLocation(IndustriaCore.MODID, "magnetism"), new MagnetismHandlerCapability(event.getObject()));
		}
		
	}
	
}
