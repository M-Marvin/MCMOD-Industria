package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.content.conduits.InsulatedElectricConduit;
import de.m_marvin.industria.content.conduits.UninsulatedElectricConduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitType;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD,modid=IndustriaCore.MODID)
public class ModConduits {

	public static final ResourceKey<Registry<ConduitType>> CONDUIT_TYPES_KEY = ResourceKey.createRegistryKey(new ResourceLocation(IndustriaCore.MODID, "conduit_types"));
	public static final ResourceKey<Registry<Conduit>> CONDUITS_KEY = ResourceKey.createRegistryKey(new ResourceLocation(IndustriaCore.MODID, "conduits"));
	public static final DeferredRegister<ConduitType> CONDUIT_TYPES = DeferredRegister.create(CONDUIT_TYPES_KEY, IndustriaCore.MODID);
	public static final DeferredRegister<Conduit> CONDUITS = DeferredRegister.create(CONDUITS_KEY, IndustriaCore.MODID);
    public static void register() {
		CONDUITS.register(FMLJavaModLoadingContext.get().getModEventBus());
		CONDUIT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<Conduit> INSULATED_COPPER_WIRE = Conduits.CONDUITS.register("insulated_copper_wire", () -> new InsulatedElectricConduit(Conduits.ELECTRIC_INSULATED_TYPE.get(), ModItems.INSULATED_COPPER_WIRE.get(), new ResourceLocation(IndustriaCore.MODID, "conduit/insulated_copper_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> INSULATED_ALUMINUM_WIRE = Conduits.CONDUITS.register("insulated_aluminum_wire", () -> new InsulatedElectricConduit(Conduits.ELECTRIC_INSULATED_TYPE.get(), ModItems.INSULATED_ALUMINUM_WIRE.get(), new ResourceLocation(IndustriaCore.MODID, "conduit/insulated_aluminum_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> INSULATED_GOLD_WIRE = Conduits.CONDUITS.register("insulated_gold_wire", () -> new InsulatedElectricConduit(Conduits.ELECTRIC_INSULATED_TYPE.get(), ModItems.INSULATED_GOLD_WIRE.get(), new ResourceLocation(IndustriaCore.MODID, "conduit/insulated_gold_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> INSULATED_TIN_WIRE = Conduits.CONDUITS.register("insulated_tin_wire", () -> new InsulatedElectricConduit(Conduits.ELECTRIC_INSULATED_TYPE.get(), ModItems.INSULATED_TIN_WIRE.get(), new ResourceLocation(IndustriaCore.MODID, "conduit/insulated_tin_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> COPPER_WIRE = Conduits.CONDUITS.register("copper_wire", () -> new UninsulatedElectricConduit(Conduits.ELECTRIC_UNINSULATED_TYPE.get(), ModItems.COPPER_WIRE.get(), new ResourceLocation(IndustriaCore.MODID, "conduit/copper_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> ALUMINUM_WIRE = Conduits.CONDUITS.register("aluminum_wire", () -> new UninsulatedElectricConduit(Conduits.ELECTRIC_UNINSULATED_TYPE.get(), ModItems.ALUMINUM_WIRE.get(), new ResourceLocation(IndustriaCore.MODID, "conduit/aluminum_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> GOLD_WIRE = Conduits.CONDUITS.register("gold_wire", () -> new UninsulatedElectricConduit(Conduits.ELECTRIC_UNINSULATED_TYPE.get(), ModItems.GOLD_WIRE.get(), new ResourceLocation(IndustriaCore.MODID, "conduit/gold_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> TIN_WIRE = Conduits.CONDUITS.register("tin_wire", () -> new UninsulatedElectricConduit(Conduits.ELECTRIC_UNINSULATED_TYPE.get(), ModItems.TIN_WIRE.get(), new ResourceLocation(IndustriaCore.MODID, "conduit/tin_wire"), SoundType.WOOL));
	
}
