package de.m_marvin.industria.client.util;

import org.apache.logging.log4j.Level;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.registries.ModRegistries;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RenderUtil {
	
	@SuppressWarnings("deprecation")
	public static void onParticleTextureStitch(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location().equals(TextureAtlas.LOCATION_PARTICLES)) {
			Industria.LOGGER.log(Level.DEBUG, "Add conduit textures to particle atlas");
			ModRegistries.CONDUITS.get().forEach((conduit) -> event.addSprite(conduit.getTexture()));
		}
	}
	
}
