package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.electrics.screens.JunctionBoxScreen;
import de.m_marvin.industria.core.client.electrics.screens.PowerSourceScreen;
import de.m_marvin.industria.core.electrics.types.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import de.m_marvin.industria.core.registries.MenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Screens {
	
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		//MenuScreens.register(Container.JUNCTION_BOX.get(), JunctionBoxScreen::new);
		MenuScreens.register(MenuTypes.JUNCTION_BOX.get(), new ScreenConstructor<JunctionBoxContainer<JunctionBoxBlockEntity>, JunctionBoxScreen<JunctionBoxBlockEntity, JunctionBoxContainer<JunctionBoxBlockEntity>>>() {
		@Override
		public JunctionBoxScreen<JunctionBoxBlockEntity, JunctionBoxContainer<JunctionBoxBlockEntity>> create(
			JunctionBoxContainer<JunctionBoxBlockEntity> pMenu, Inventory pInventory, Component pTitle) {
				return new JunctionBoxScreen<JunctionBoxBlockEntity, JunctionBoxContainer<JunctionBoxBlockEntity>>(pMenu, pInventory, pTitle);
			}
		});
		MenuScreens.register(MenuTypes.POWER_SOURCE.get(), PowerSourceScreen::new);
	}
	
}
