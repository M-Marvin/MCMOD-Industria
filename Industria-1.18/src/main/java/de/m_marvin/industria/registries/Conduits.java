package de.m_marvin.industria.registries;

import java.util.function.Supplier;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.conduits.Conduit.ConduitType;
import de.m_marvin.industria.conduits.IsolatedElectricConduit;
import de.m_marvin.industria.conduits.UnisolatedElectricConduit;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
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
	
	public static final RegistryObject<ConduitType> ELECTRIC_ISOLATED_TYPE = CONDUIT_TYPES.register("electric_isolated_type", () -> new ConduitType(0.05F, 1F, 16, 4));
	public static final RegistryObject<ConduitType> ELECTRIC_UNISOLATED_TYPE = CONDUIT_TYPES.register("electric_unisolated_type", () -> new ConduitType(0.015F, 1F, 128, 2));
	
	public static final RegistryObject<Conduit> NONE = CONDUITS.register("none", () -> new Conduit(new ConduitType(0F, 1F, 0, 1), null, MissingTextureAtlasSprite.getLocation(), SoundType.STONE));
	public static final RegistryObject<Conduit> ISOLATED_COPPER_WIRE = CONDUITS.register("isolated_copper_wire", () -> new IsolatedElectricConduit(ELECTRIC_ISOLATED_TYPE.get(), ModItems.ISOLATED_COPPER_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/isolated_copper_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> UNISOLATED_COPPER_WIRE = CONDUITS.register("unisolated_copper_wire", () -> new UnisolatedElectricConduit(ELECTRIC_UNISOLATED_TYPE.get(), ModItems.UNISOLATED_COPPER_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/unisolated_copper_wire"), SoundType.WOOL));
	
}
