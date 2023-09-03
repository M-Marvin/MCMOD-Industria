package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.container.PortableCoalGeneratorContainer;
import de.m_marvin.industria.content.container.PortableFuelGeneratorContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Industria.MODID);
	public static void register() {
		MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<MenuType<PortableFuelGeneratorContainer>> 	PORTABLE_FUEL_GENERATOR 	= MENU_TYPES.register("portable_fuel_generator", () -> IForgeMenuType.create(PortableFuelGeneratorContainer::new));
	public static final RegistryObject<MenuType<PortableCoalGeneratorContainer>> 	PORTABLE_COAL_GENERATOR 	= MENU_TYPES.register("portable_coal_generator", () -> IForgeMenuType.create(PortableCoalGeneratorContainer::new));
	
}
