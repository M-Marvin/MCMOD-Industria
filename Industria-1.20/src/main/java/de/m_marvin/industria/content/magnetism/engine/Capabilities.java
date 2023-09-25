package de.m_marvin.industria.content.magnetism.engine;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ConduitHandlerCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=IndustriaCore.MODID)
public class Capabilities {

	public static final Capability<MagnetismHandlerCapability> MAGNETISM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<MagnetismHandlerCapability>() {});
	
	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(ConduitHandlerCapability.class);
	}

	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=IndustriaCore.MODID)
	public class Attachment {
		
		@SubscribeEvent
		public static void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
			event.addCapability(new ResourceLocation(IndustriaCore.MODID, "magnetism"), new MagnetismHandlerCapability(event.getObject()));
		}
		
	}
	
}
