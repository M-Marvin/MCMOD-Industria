package de.m_marvin.industria.registries;

import java.util.function.Supplier;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.conduits.Conduit.ConduitType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=Industria.MODID)
public class Conduits {
	
	public static final ResourceKey<Registry<ConduitType>> CONDUIT_TYPES_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Industria.MODID, "conduit_types"));
	public static final ResourceKey<Registry<Conduit>> CONDUITS_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Industria.MODID, "conduits"));
	public static final DeferredRegister<ConduitType> CONDUIT_TYPES = DeferredRegister.create(CONDUIT_TYPES_KEY, Industria.MODID);
	public static final DeferredRegister<Conduit> CONDUITS = DeferredRegister.create(CONDUITS_KEY, Industria.MODID);
    public static final Supplier<IForgeRegistry<ConduitType>> CONDUIT_TYPES_REGISTRY = CONDUIT_TYPES.makeRegistry(ConduitType.class, () -> new RegistryBuilder<ConduitType>().disableSaving());
    public static final Supplier<IForgeRegistry<Conduit>> CONDUITS_REGISTRY = CONDUITS.makeRegistry(Conduit.class, () -> new RegistryBuilder<Conduit>().disableSaving());
	public static void register() {
		CONDUITS.register(FMLJavaModLoadingContext.get().getModEventBus());
		CONDUIT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<ConduitType> DEFAULT_CONDUIT_TYPE = CONDUIT_TYPES.register("default_type", () -> new ConduitType(0.03F, 1F, 20, 4));
	
	public static final RegistryObject<Conduit> NONE = CONDUITS.register("none", () -> new Conduit(new ConduitType(0F, 1F, 0, 1), MissingTextureAtlasSprite.getLocation()));
	public static final RegistryObject<Conduit> DEFAULT_CONDUIT = CONDUITS.register("default_conduit", () -> new Conduit(DEFAULT_CONDUIT_TYPE.get(), new ResourceLocation(Industria.MODID, "conduit/test_conduit")));
	
}
