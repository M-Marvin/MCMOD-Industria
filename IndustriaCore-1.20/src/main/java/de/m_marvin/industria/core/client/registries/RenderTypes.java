package de.m_marvin.industria.core.client.registries;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class RenderTypes extends RenderStateShard {
	
	// We only extend RenderStateShard because we need access to its constants
	private RenderTypes(String pName, Runnable pSetupState, Runnable pClearState) {
		super(pName, pSetupState, pClearState);
	}

	public static final Function<ResourceLocation, RenderType> SOLID_TEXTURE = Util.memoize(texture -> {
		RenderType.CompositeState compositestate = RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(RENDERTYPE_SOLID_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, true))
				.createCompositeState(true);
		return RenderType.create("solid_textured", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, true, false, compositestate);
	});

	public static final Function<ResourceLocation, RenderType> CUTOUT_MIPPED_TEXTURE = Util.memoize(texture -> {
		RenderType.CompositeState compositestate = RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, true))
				.createCompositeState(true);
		return RenderType.create("solid_textured", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, true, false, compositestate);
	});

	public static final Function<ResourceLocation, RenderType> CUTOUT_TEXTURE = Util.memoize(texture -> {
		RenderType.CompositeState compositestate = RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(RENDERTYPE_CUTOUT_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.createCompositeState(true);
		return RenderType.create("solid_textured", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, true, false, compositestate);
	});

	public static final Function<ResourceLocation, RenderType> TRANSLUCENT_TEXTURE = Util.memoize(texture -> {
		RenderType.CompositeState compositestate = RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, true))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(TRANSLUCENT_TARGET)
				.createCompositeState(true);
		return RenderType.create("solid_textured", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, true, true, compositestate);
	});
	
	public static RenderType solidTexture(ResourceLocation texture) {
		return SOLID_TEXTURE.apply(texture);
	}
	
	public static RenderType cutoutTexture(ResourceLocation texture) {
		return CUTOUT_TEXTURE.apply(texture);
	}
	
	public static RenderType cutoutMippedTexture(ResourceLocation texture) {
		return CUTOUT_MIPPED_TEXTURE.apply(texture);
	}
	
	public static RenderType translucentTexture(ResourceLocation texture) {
		return TRANSLUCENT_TEXTURE.apply(texture);
	}
	
}
