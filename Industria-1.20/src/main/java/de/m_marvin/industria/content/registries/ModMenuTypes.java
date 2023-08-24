package de.m_marvin.industria.content.registries;

import de.m_marvin.industria.content.Industria;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModMenuTypes {

	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Industria.MODID);
	public static void register() {
		MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	//public static final RegistryObject<MenuType<JunctionBoxContainer>> JUNCTION_BOX = MENU_TYPES.register("junction_box", () -> IForgeMenuType.create(JunctionBoxContainer::new));
	
}
