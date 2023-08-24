package de.m_marvin.industria.core.client.conduits;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConduitTextureManager extends TextureAtlasHolder {

	public static final int TEXTURE_MAP_WIDTH = 64;
	public static final int TEXTURE_MAP_HEIGHT = 32;
	
	public static final ResourceLocation LOCATION_CONDUITS = new ResourceLocation(IndustriaCore.MODID, "textures/atlas/conduits.png");
	
	private static ConduitTextureManager instance;

	@SubscribeEvent
	public static void addReloadListener(RegisterClientReloadListenersEvent event) {
		instance = new ConduitTextureManager(Minecraft.getInstance().getTextureManager());
		event.registerReloadListener(instance);
	}
	
	public static ConduitTextureManager getInstance() {
		return instance;
	}
	
	public ConduitTextureManager(TextureManager textureManager) {
		super(textureManager, LOCATION_CONDUITS, new ResourceLocation(IndustriaCore.MODID, "conduits"));
	}
	
	public TextureAtlasSprite get(Conduit conduit) {
		return this.getSprite(conduit.getTexture());
	}
	
}
