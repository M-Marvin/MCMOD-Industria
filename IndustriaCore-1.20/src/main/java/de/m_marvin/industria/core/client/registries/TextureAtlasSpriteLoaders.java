package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.util.ShiftedTextureAnimation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterTextureAtlasSpriteLoadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class TextureAtlasSpriteLoaders {
	
	@SubscribeEvent
	public static void onRegisterSTextureAtlasSpriteLoaders(RegisterTextureAtlasSpriteLoadersEvent event) {
		
		event.register("shifted_texture", new ShiftedTextureAnimation.ShiftedTextureAnimationTextureAtlasSpriteLoader());
		
	}
	
}