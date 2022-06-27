package de.m_marvin.industria.client.util;

import org.apache.logging.log4j.Level;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.registries.Conduits;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class RenderUtil {
	
	@SubscribeEvent
	@SuppressWarnings("deprecation")
	public static void onParticleTextureStitch(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location().equals(TextureAtlas.LOCATION_PARTICLES)) {
			Industria.LOGGER.log(Level.DEBUG, "Add conduit textures to particle atlas");
			Conduits.CONDUITS_REGISTRY.get().forEach((conduit) -> event.addSprite(conduit.getTexture()));
		}
	}
	
}
