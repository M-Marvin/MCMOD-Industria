package de.m_marvin.industria.core.registries;

import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.ssdplugins.engine.StructureDataPlugin;
import de.m_marvin.industria.core.ssdplugins.types.ConduitDataPlugin;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=IndustriaCore.MODID)
public class StructureDataPlugins {
	
	public static final ResourceKey<Registry<StructureDataPlugin<?>>> DATA_PLUGINS_KEY = ResourceKey.createRegistryKey(ResourceLocation.tryBuild(IndustriaCore.MODID, "structure_data_plugins"));
	public static final DeferredRegister<StructureDataPlugin<?>> DATA_PLUGINS = DeferredRegister.create(DATA_PLUGINS_KEY, IndustriaCore.MODID);
	public static final Supplier<IForgeRegistry<StructureDataPlugin<?>>> DATA_PLUGINS_REGISTRY = DATA_PLUGINS.makeRegistry(() -> new RegistryBuilder<StructureDataPlugin<?>>().disableSaving());
	
    public static void register(FMLJavaModLoadingContext modctx) {
    	DATA_PLUGINS.register(modctx.getModEventBus());
	}
	
	public static final RegistryObject<ConduitDataPlugin> CONDUTIS = DATA_PLUGINS.register("conduits", ConduitDataPlugin::new);
    
}
