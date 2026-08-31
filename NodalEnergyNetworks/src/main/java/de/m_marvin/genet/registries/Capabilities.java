package de.m_marvin.genet.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ConduitHolderCapability;
import de.m_marvin.industria.core.contraptions.engine.ContraptionHandlerCapability;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability;
import de.m_marvin.industria.core.kinetics.engine.KineticNetworkSpaceCapability;
import de.m_marvin.industria.core.magnetism.engine.MagnetismHandlerCapability;
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
	
	public static final Capability<E> CONDUIT_HOLDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<ConduitHolderCapability>() {});
	
	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(ConduitHolderCapability.class);
	}

	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=IndustriaCore.MODID)
	public class Attachment {
		
		@SubscribeEvent
		public static void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
			event.addCapability(ResourceLocation.tryBuild(IndustriaCore.MODID, "conduits"), new ConduitHolderCapability(event.getObject()));
			event.addCapability(ResourceLocation.tryBuild(IndustriaCore.MODID, "electrics"), new ElectricNetworkSpaceCapability(event.getObject()));
			event.addCapability(ResourceLocation.tryBuild(IndustriaCore.MODID, "contraption"), new ContraptionHandlerCapability(event.getObject()));
			event.addCapability(ResourceLocation.tryBuild(IndustriaCore.MODID, "magnetism"), new MagnetismHandlerCapability(event.getObject()));
			event.addCapability(ResourceLocation.tryBuild(IndustriaCore.MODID, "kinetics"), new KineticNetworkSpaceCapability(event.getObject()));
		}
		
	}
	
}
