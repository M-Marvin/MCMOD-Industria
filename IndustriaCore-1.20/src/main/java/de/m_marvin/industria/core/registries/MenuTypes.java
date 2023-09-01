package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.electrics.types.containers.PowerSourceContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuTypes {

	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, IndustriaCore.MODID);
	public static void register() {
		MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<MenuType<JunctionBoxContainer<JunctionBoxBlockEntity>>> JUNCTION_BOX = MENU_TYPES.register("junction_box", () -> IForgeMenuType.create(JunctionBoxContainer::new));
	public static final RegistryObject<MenuType<PowerSourceContainer>> POWER_SOURCE = MENU_TYPES.register("power_source", () -> IForgeMenuType.create(PowerSourceContainer::new));
	
}
