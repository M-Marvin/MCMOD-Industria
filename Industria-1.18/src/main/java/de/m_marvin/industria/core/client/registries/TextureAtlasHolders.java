package de.m_marvin.industria.core.client.registries;

import com.google.common.base.Function;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.conduits.ConduitTextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TextureAtlasHolders {
	
	public static ConduitTextureManager conduitTextureManager = null;
	
	@SubscribeEvent
	public static void register(RegisterClientReloadListenersEvent event) {
		conduitTextureManager = register(event, ConduitTextureManager::new);
	}
	
	protected static <T extends TextureAtlasHolder> T register(RegisterClientReloadListenersEvent event, Function<TextureManager, T> factory) {
		T textureAtlasHolder = factory.apply(Minecraft.getInstance().getTextureManager());
		event.registerReloadListener(textureAtlasHolder);
		return textureAtlasHolder;
	}
	
}
