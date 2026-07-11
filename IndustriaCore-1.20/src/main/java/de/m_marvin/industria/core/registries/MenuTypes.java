package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxMenu;
import de.m_marvin.industria.core.electrics.types.containers.VoltageSourceMenu;
import de.m_marvin.industria.core.kinetics.types.containers.MotorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuTypes {

	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, IndustriaCore.MODID);
	public static void register(FMLJavaModLoadingContext modctx) {
		MENU_TYPES.register(modctx.getModEventBus());
	}
	
	public static final RegistryObject<MenuType<JunctionBoxMenu<JunctionBoxBlockEntity>>> JUNCTION_BOX = 	MENU_TYPES.register("junction_box", () -> IForgeMenuType.create(JunctionBoxMenu::new));
	public static final RegistryObject<MenuType<VoltageSourceMenu>> VOLTAGE_SOURCE = 						MENU_TYPES.register("voltage_source", () -> IForgeMenuType.create(VoltageSourceMenu::new));
	public static final RegistryObject<MenuType<MotorMenu>> MOTOR = 										MENU_TYPES.register("motor", () -> IForgeMenuType.create(MotorMenu::new));
	
}
