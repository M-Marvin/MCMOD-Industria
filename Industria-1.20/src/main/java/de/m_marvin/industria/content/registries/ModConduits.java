package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.conduits.InsulatedElectricConduit;
import de.m_marvin.industria.content.conduits.UninsulatedElectricConduit;
import de.m_marvin.industria.core.conduits.types.ConduitType;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModConduits {

	public static final DeferredRegister<ConduitType> CONDUIT_TYPES = DeferredRegister.create(Conduits.CONDUIT_TYPES_KEY, Industria.MODID);
	public static final DeferredRegister<Conduit> CONDUITS = DeferredRegister.create(Conduits.CONDUITS_KEY, Industria.MODID);
    public static void register() {
		CONDUITS.register(FMLJavaModLoadingContext.get().getModEventBus());
		CONDUIT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<Conduit> INSULATED_COPPER_WIRE 		= CONDUITS.register("insulated_copper_wire", () -> new InsulatedElectricConduit(Conduits.ELECTRIC_INSULATED_TYPE.get(), 0.001, ModItems.INSULATED_COPPER_WIRE.get(), new ResourceLocation(Industria.MODID, "insulated_copper_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> INSULATED_ALUMINUM_WIRE 	= CONDUITS.register("insulated_aluminum_wire", () -> new InsulatedElectricConduit(Conduits.ELECTRIC_INSULATED_TYPE.get(), 0.001, ModItems.INSULATED_ALUMINUM_WIRE.get(), new ResourceLocation(Industria.MODID, "insulated_aluminum_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> INSULATED_GOLD_WIRE 		= CONDUITS.register("insulated_gold_wire", () -> new InsulatedElectricConduit(Conduits.ELECTRIC_INSULATED_TYPE.get(), 0.001, ModItems.INSULATED_GOLD_WIRE.get(), new ResourceLocation(Industria.MODID, "insulated_gold_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> INSULATED_TIN_WIRE 			= CONDUITS.register("insulated_tin_wire", () -> new InsulatedElectricConduit(Conduits.ELECTRIC_INSULATED_TYPE.get(), 0.001, ModItems.INSULATED_TIN_WIRE.get(), new ResourceLocation(Industria.MODID, "insulated_tin_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> COPPER_WIRE 				= CONDUITS.register("copper_wire", () -> new UninsulatedElectricConduit(Conduits.ELECTRIC_UNINSULATED_TYPE.get(), 0.001, ModItems.COPPER_WIRE.get(), new ResourceLocation(Industria.MODID, "copper_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> ALUMINUM_WIRE 				= CONDUITS.register("aluminum_wire", () -> new UninsulatedElectricConduit(Conduits.ELECTRIC_UNINSULATED_TYPE.get(), 0.001, ModItems.ALUMINUM_WIRE.get(), new ResourceLocation(Industria.MODID, "aluminum_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> GOLD_WIRE 					= CONDUITS.register("gold_wire", () -> new UninsulatedElectricConduit(Conduits.ELECTRIC_UNINSULATED_TYPE.get(), 0.001, ModItems.GOLD_WIRE.get(), new ResourceLocation(Industria.MODID, "gold_wire"), SoundType.WOOL));
	public static final RegistryObject<Conduit> TIN_WIRE 					= CONDUITS.register("tin_wire", () -> new UninsulatedElectricConduit(Conduits.ELECTRIC_UNINSULATED_TYPE.get(), 0.001, ModItems.TIN_WIRE.get(), new ResourceLocation(Industria.MODID, "tin_wire"), SoundType.WOOL));
	
}
