package de.m_marvin.industria.registries;

import java.util.function.Supplier;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.conduits.Conduit.ConduitType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=Industria.MODID)
public class ModRegistries {
	
	public static final Supplier<IForgeRegistry<ConduitType>> CONDUIT_TYPES = () -> RegistryManager.ACTIVE.getRegistry(ConduitType.class);
	public static final Supplier<IForgeRegistry<Conduit>> CONDUITS = () -> RegistryManager.ACTIVE.getRegistry(Conduit.class);
	
	@SubscribeEvent
	public static void register(RegistryEvent.NewRegistry event) {
		new RegistryBuilder<ConduitType>().setName(new ResourceLocation(Industria.MODID, "conduit_types")).setType(ConduitType.class).create();
		new RegistryBuilder<Conduit>().setName(new ResourceLocation(Industria.MODID, "conduits")).setType(Conduit.class).create();
	}
	
}
