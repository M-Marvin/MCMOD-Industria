package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.containers.PowerSourceJunctionContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Container {

	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, IndustriaCore.MODID);
	public static void register() {
		MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<MenuType<PowerSourceJunctionContainer>> POWER_SOURCE = MENU_TYPES.register("power_source", () -> IForgeMenuType.create(PowerSourceJunctionContainer::new));
	
}
