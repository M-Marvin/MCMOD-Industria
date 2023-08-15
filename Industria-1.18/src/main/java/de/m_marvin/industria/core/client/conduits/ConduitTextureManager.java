package de.m_marvin.industria.core.client.conduits;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

public class ConduitTextureManager extends TextureAtlasHolder {

	public static final int TEXTURE_MAP_WIDTH = 64;
	public static final int TEXTURE_MAP_HEIGHT = 32;
	
	public static final ResourceLocation LOCATION_CONDUITS = new ResourceLocation(IndustriaCore.MODID, "textures/atlas/conduits.png");
	
	public ConduitTextureManager(TextureManager textureManager) {
		super(textureManager, LOCATION_CONDUITS, new ResourceLocation(IndustriaCore.MODID, "conduits"));
	}
	
	public TextureAtlasSprite get(Conduit conduit) {
		return this.getSprite(Conduits.CONDUITS_REGISTRY.get().getKey(conduit));
	}
	
}
