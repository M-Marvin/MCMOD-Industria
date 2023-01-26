package de.m_marvin.industria.core.conduits.registy;

import java.util.function.Supplier;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.registries.ModItems;
import de.m_marvin.industria.core.conduits.types.Conduit;
import de.m_marvin.industria.core.conduits.types.InsulatedElectricConduit;
import de.m_marvin.industria.core.conduits.types.UninsulatedElectricConduit;
import de.m_marvin.industria.core.conduits.types.Conduit.ConduitType;
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
	
	public static final RegistryObject<ConduitType> ELECTRIC_INSULATED_TYPE = CONDUIT_TYPES.register("electric_insulated_type", () -> new ConduitType(0.05F, 1F, 16, 3));
	public static final RegistryObject<ConduitType> ELECTRIC_UNINSULATED_TYPE = CONDUIT_TYPES.register("electric_uninsulated_type", () -> new ConduitType(0.015F, 1F, 128, 1));
	
	public static final RegistryObject<Conduit> NONE = CONDUITS.register("none", () -> new Conduit(new ConduitType(0F, 1F, 0, 1), null, MissingTextureAtlasSprite.getLocation(), SoundType.STONE));
	public static final RegistryObject<Conduit> INSULATED_COPPER_WIRE = CONDUITS.register("insulated_copper_wire", () -> new InsulatedElectricConduit(ELECTRIC_INSULATED_TYPE.get(), ModItems.INSULATED_COPPER_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/insulated_copper_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> INSULATED_ALUMINUM_WIRE = CONDUITS.register("insulated_aluminum_wire", () -> new InsulatedElectricConduit(ELECTRIC_INSULATED_TYPE.get(), ModItems.INSULATED_ALUMINUM_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/insulated_aluminum_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> INSULATED_GOLD_WIRE = CONDUITS.register("insulated_gold_wire", () -> new InsulatedElectricConduit(ELECTRIC_INSULATED_TYPE.get(), ModItems.INSULATED_GOLD_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/insulated_gold_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> INSULATED_TIN_WIRE = CONDUITS.register("insulated_tin_wire", () -> new InsulatedElectricConduit(ELECTRIC_INSULATED_TYPE.get(), ModItems.INSULATED_TIN_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/insulated_tin_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> COPPER_WIRE = CONDUITS.register("copper_wire", () -> new UninsulatedElectricConduit(ELECTRIC_UNINSULATED_TYPE.get(), ModItems.COPPER_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/copper_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> ALUMINUM_WIRE = CONDUITS.register("aluminum_wire", () -> new UninsulatedElectricConduit(ELECTRIC_UNINSULATED_TYPE.get(), ModItems.ALUMINUM_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/aluminum_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> GOLD_WIRE = CONDUITS.register("gold_wire", () -> new UninsulatedElectricConduit(ELECTRIC_UNINSULATED_TYPE.get(), ModItems.GOLD_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/gold_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> TIN_WIRE = CONDUITS.register("tin_wire", () -> new UninsulatedElectricConduit(ELECTRIC_UNINSULATED_TYPE.get(), ModItems.TIN_WIRE.get(), new ResourceLocation(Industria.MODID, "conduit/tin_wire"), SoundType.WOOL));
	
}
