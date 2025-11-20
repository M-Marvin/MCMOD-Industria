package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.conduits.InsulatedElectricConduit;
import de.m_marvin.industria.content.conduits.UninsulatedElectricConduit;
import de.m_marvin.industria.core.conduits.types.ConduitBehavior.Properties;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModConduits {

	public static final DeferredRegister<Conduit> CONDUITS = DeferredRegister.create(Conduits.CONDUITS_KEY, Industria.MODID);
    public static void register() {
		CONDUITS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<Conduit> INSULATED_COPPER_WIRE 		= CONDUITS.register("insulated_copper_wire", () -> new InsulatedElectricConduit(Properties.of().thickness(3).stiffness(0.8F).soundType(SoundType.WOOL), 10E-6));
	public static final RegistryObject<Conduit> INSULATED_ALUMINUM_WIRE 	= CONDUITS.register("insulated_aluminum_wire", () -> new InsulatedElectricConduit(Properties.of().thickness(3).stiffness(0.8F).soundType(SoundType.WOOL), 10E-6));
	public static final RegistryObject<Conduit> INSULATED_GOLD_WIRE 		= CONDUITS.register("insulated_gold_wire", () -> new InsulatedElectricConduit(Properties.of().thickness(3).stiffness(0.8F).soundType(SoundType.WOOL), 10E-6));
	public static final RegistryObject<Conduit> INSULATED_TIN_WIRE 			= CONDUITS.register("insulated_tin_wire", () -> new InsulatedElectricConduit(Properties.of().thickness(3).stiffness(0.8F).soundType(SoundType.WOOL), 10E-6));
	public static final RegistryObject<Conduit> COPPER_WIRE 				= CONDUITS.register("copper_wire", () -> new UninsulatedElectricConduit(Properties.of().thickness(1).stiffness(1.0F).soundType(SoundType.METAL), 10E-6));
	public static final RegistryObject<Conduit> ALUMINUM_WIRE 				= CONDUITS.register("aluminum_wire", () -> new UninsulatedElectricConduit(Properties.of().thickness(1).stiffness(1.0F).soundType(SoundType.METAL), 10E-6));
	public static final RegistryObject<Conduit> GOLD_WIRE 					= CONDUITS.register("gold_wire", () -> new UninsulatedElectricConduit(Properties.of().thickness(1).stiffness(1.0F).soundType(SoundType.METAL), 10E-6));
	public static final RegistryObject<Conduit> TIN_WIRE 					= CONDUITS.register("tin_wire", () -> new UninsulatedElectricConduit(Properties.of().thickness(1).stiffness(1.0F).soundType(SoundType.METAL), 10E-6));
	
}
