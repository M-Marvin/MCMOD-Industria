package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.conduits.Conduit.ConduitType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=Industria.MODID)
public class Conduits {
	
	public static final ConduitType DEFAULT_CONDUIT_TYPE = new ConduitType(0.03F, 1F, 20, 4).setRegistryName(new ResourceLocation(Industria.MODID, "default_type"));
	
	@SubscribeEvent
	public static void registerConduitTypes(RegistryEvent.Register<ConduitType> event) {
		var reg = event.getRegistry();
		reg.register(DEFAULT_CONDUIT_TYPE);
	}
	
	public static final Conduit NONE = new Conduit(new ConduitType(0F, 1F, 0, 1), new ResourceLocation(Industria.MODID, "invalid")).setRegistryName(new ResourceLocation(Industria.MODID, "default_conduit"));
	public static final Conduit DEFAULT_CONDUIT = new Conduit(DEFAULT_CONDUIT_TYPE, new ResourceLocation(Industria.MODID, "textures/conduit/test_conduit.png")).setRegistryName(new ResourceLocation(Industria.MODID, "default_conduit"));
	
	@SubscribeEvent
	public static void registerConduits(RegistryEvent.Register<Conduit> event) {
		var reg = event.getRegistry();
		reg.register(NONE);
		reg.register(DEFAULT_CONDUIT);
	}
	
}
