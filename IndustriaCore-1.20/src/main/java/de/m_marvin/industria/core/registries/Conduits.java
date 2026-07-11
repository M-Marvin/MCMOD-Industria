package de.m_marvin.industria.core.registries;

import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitBehavior.Properties;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.electrics.types.conduits.ElectricConduit;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=IndustriaCore.MODID)
public class Conduits {

	public static final ResourceKey<Registry<Conduit>> CONDUITS_KEY = ResourceKey.createRegistryKey(ResourceLocation.tryBuild(IndustriaCore.MODID, "conduits"));
	public static final DeferredRegister<Conduit> CONDUITS = DeferredRegister.create(CONDUITS_KEY, IndustriaCore.MODID);
    public static final Supplier<IForgeRegistry<Conduit>> CONDUITS_REGISTRY = Conduits.CONDUITS.makeRegistry(() -> new RegistryBuilder<Conduit>().disableSaving());
    
    public static void register(FMLJavaModLoadingContext modctx) {
		CONDUITS.register(modctx.getModEventBus());
	}
	
	public static final RegistryObject<Conduit> NONE = 				CONDUITS.register("none", () -> new Conduit(Properties.of().segmentLength(1).soundType(SoundType.STONE).validNodes(NodeTypes.ALL)));
	public static final RegistryObject<Conduit> ELECTRIC_CONDUIT =	CONDUITS.register("electric_conduit", () -> new ElectricConduit(Properties.of().soundType(SoundType.WOOL).validNodes(NodeTypes.ELECTRIC), 2, 0.1));
	
}
