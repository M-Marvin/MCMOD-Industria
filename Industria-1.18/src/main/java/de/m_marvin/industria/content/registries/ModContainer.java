package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.content.container.JunctionBoxContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainer {

	private static final DeferredRegister<MenuType<?>> CONTAINER = DeferredRegister.create(ForgeRegistries.CONTAINERS, IndustriaCore.MODID);
	public static void register() {
		CONTAINER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<MenuType<JunctionBoxContainer>> JUNCTION_BOX = CONTAINER.register("junction_box", () -> IForgeMenuType.create(JunctionBoxContainer::new));
	
}
