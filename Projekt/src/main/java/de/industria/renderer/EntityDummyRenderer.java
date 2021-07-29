package de.industria.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityDummyRenderer extends EntityRenderer<Entity> {

	public EntityDummyRenderer(EntityRendererManager manager) {
		super(manager);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ResourceLocation getTextureLocation(Entity p_110775_1_) {
		return AtlasTexture.LOCATION_BLOCKS;
	}
	
}
