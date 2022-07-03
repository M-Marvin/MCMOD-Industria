package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.util.conduit.ConduitHandlerCapability;
import de.m_marvin.industria.util.electricity.ElectricNetworkHandlerCapability;
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
	
	public static final Capability<ConduitHandlerCapability> CONDUIT_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<ConduitHandlerCapability>() {});
	public static final Capability<ElectricNetworkHandlerCapability> ELECTRIC_NETWORK_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<ElectricNetworkHandlerCapability>() {});
	
	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(ConduitHandlerCapability.class);
	}

	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=Industria.MODID)
	public class Attachment {
		
		@SubscribeEvent
		public static void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
			event.addCapability(new ResourceLocation(Industria.MODID, "conduits"), new ConduitHandlerCapability(event.getObject()));
			event.addCapability(new ResourceLocation(Industria.MODID, "electric_networks"), new ElectricNetworkHandlerCapability(event.getObject()));
		}
		
	}
	
}
