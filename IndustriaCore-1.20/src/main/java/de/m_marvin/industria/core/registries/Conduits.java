package de.m_marvin.industria.core.registries;

import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitType;
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

	public static final ResourceKey<Registry<ConduitType>> CONDUIT_TYPES_KEY = ResourceKey.createRegistryKey(new ResourceLocation(IndustriaCore.MODID, "conduit_types"));
	public static final ResourceKey<Registry<Conduit>> CONDUITS_KEY = ResourceKey.createRegistryKey(new ResourceLocation(IndustriaCore.MODID, "conduits"));
	public static final DeferredRegister<ConduitType> CONDUIT_TYPES = DeferredRegister.create(CONDUIT_TYPES_KEY, IndustriaCore.MODID);
	public static final DeferredRegister<Conduit> CONDUITS = DeferredRegister.create(CONDUITS_KEY, IndustriaCore.MODID);
	public static final Supplier<IForgeRegistry<ConduitType>> CONDUIT_TYPES_REGISTRY = Conduits.CONDUIT_TYPES.makeRegistry(() -> new RegistryBuilder<ConduitType>().disableSaving());
    public static final Supplier<IForgeRegistry<Conduit>> CONDUITS_REGISTRY = Conduits.CONDUITS.makeRegistry(() -> new RegistryBuilder<Conduit>().disableSaving());
	
    public static void register() {
		CONDUITS.register(FMLJavaModLoadingContext.get().getModEventBus());
		CONDUIT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<ConduitType> ELECTRIC_INSULATED_TYPE = CONDUIT_TYPES.register("electric_insulated_type", () -> new ConduitType(0.0005F, 1F, 16, 3));
	public static final RegistryObject<ConduitType> ELECTRIC_UNINSULATED_TYPE = CONDUIT_TYPES.register("electric_uninsulated_type", () -> new ConduitType(0.00015F, 1F, 128, 1));
	
	public static final RegistryObject<Conduit> NONE = 				CONDUITS.register("none", () -> new Conduit(new ConduitType(0F, 1F, 0, 1), null, new ResourceLocation("missingno"), SoundType.STONE, NodeTypes.ALL));
	public static final RegistryObject<Conduit> ELECTRIC_CONDUIT =	CONDUITS.register("electric_conduit", () -> new ElectricConduit(ELECTRIC_INSULATED_TYPE.get(), Items.ELECTRIC_WIRE.get(), new ResourceLocation("missingno"), SoundType.STONE, 2, 0.1));
	
}
