package de.m_marvin.industria.core.client.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Bus.FORGE, value=Dist.CLIENT)
public class AdvancedBakedAnimation {

	public static class BakedAnimationTextureSprite extends TextureAtlasSprite {
		
		public static class BakedAnimationSpriteContents extends SpriteContents {
			
			private final float factorU;
			private final float factorV;
			private final String animationName;
			
			public BakedAnimationSpriteContents(ResourceLocation name, FrameSize frameSize, NativeImage image,
					AnimationMetadataSection animationMeta, ForgeTextureMetadata forgeMeta,
					float factorU, float factorV, String animationName) {
				super(name, frameSize, image, animationMeta, forgeMeta);
				this.factorU = factorU;
				this.factorV = factorV;
				this.animationName = animationName;
			}
			
			public float getFactorU() {
				return factorU;
			}
			
			public float getFactorV() {
				return factorV;
			}
			
			public String getAnimationName() {
				return animationName;
			}
			
		}
		
		protected BakedAnimationTextureSprite(ResourceLocation pAtlasLocation, BakedAnimationSpriteContents pContents, int pOriginX, int pOriginY, int pX, int pY) {
			super(pAtlasLocation, pContents, pOriginX, pOriginY, pX, pY);
		}
		
		public float getFactorU() {
			return ((BakedAnimationSpriteContents) contents()).getFactorU();
		}
		
		public float getFactorV() {
			return ((BakedAnimationSpriteContents) contents()).getFactorV();
		}
		
		public String getAnimationName() {
			return ((BakedAnimationSpriteContents) contents()).getAnimationName();
		}
		
	}
	
	public static class BakedAnimationTextureAtlasSpriteLoader implements ITextureAtlasSpriteLoader {
		
		public static class BakedAnmiationMetadata {
			
			public static final Section SERIALIZER = new Section();
			
			public static class Section implements MetadataSectionSerializer<BakedAnmiationMetadata> {
				
				@Override
				public String getMetadataSectionName() {
					return "baked_animation";
				}
	
				@Override
				public BakedAnmiationMetadata fromJson(JsonObject pJson) {
					String animationName = pJson.has("animation_name") ? pJson.get("animation_name").getAsString() : "";
					float factorU = pJson.has("factor_U") ? pJson.get("factor_u").getAsFloat() : 1.0f;
					float factorV = pJson.has("factor_v") ? pJson.get("factor_v").getAsFloat() : 1.0f;
					return new BakedAnmiationMetadata(animationName, factorU, factorV);
				}
				
			}
			
			private final String animationName;
			private final float factorU;
			private final float factorV;
			
			public BakedAnmiationMetadata(String animationName, float factorU, float factorV) {
				this.animationName = animationName;
				this.factorU = factorU;
				this.factorV = factorV;
			}
			
			public String getAnimationName() {
				return animationName;
			}
			
			public float getFactorU() {
				return factorU;
			}
			
			public float getFactorV() {
				return factorV;
			}
			
		}
		
		@Override
		public SpriteContents loadContents(ResourceLocation name, Resource resource, FrameSize frameSize,
				NativeImage image, AnimationMetadataSection animationMeta, ForgeTextureMetadata forgeMeta) {
			try {
				Optional<BakedAnmiationMetadata> metadata = resource.metadata().getSection(BakedAnmiationMetadata.SERIALIZER);
				if (metadata.isPresent()) {
					return new BakedAnimationTextureSprite.BakedAnimationSpriteContents(
							name, frameSize, image, animationMeta, forgeMeta,
							metadata.get().getFactorU(),
							metadata.get().getFactorV(),
							metadata.get().getAnimationName()
							);
				}
			} catch (IOException e) {
	            IndustriaCore.LOGGER.error("Unable to get Baked Animation metadata for {}, falling back to vanilla loading", name);
	            e.printStackTrace();
			}
			return new SpriteContents(name, frameSize, image, animationMeta, forgeMeta);
		}

		@Override
		public @NotNull TextureAtlasSprite makeSprite(ResourceLocation atlasName, SpriteContents contents,
				int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel) {
			if (contents instanceof BakedAnimationTextureSprite.BakedAnimationSpriteContents)
				return new BakedAnimationTextureSprite(atlasName, (BakedAnimationTextureSprite.BakedAnimationSpriteContents) contents, atlasWidth, atlasHeight, spriteX, spriteY);			
			return null;
		}
		
	}
	
	private AdvancedBakedAnimation() {}
	
	public static void shiftTextureUV(SimpleBakedModel model, float shiftU, float shiftV, String... animationNames) {
		List<String> textures = Arrays.asList(animationNames);
		shiftTextureUV(model, shiftU, shiftV, textures::contains);
	}
	
	public static void shiftTextureUV(SimpleBakedModel model, float shiftU, float shiftV, Predicate<String> animationNames) {
		for (Direction d : Direction.values()) {
			for (BakedQuad quad : model.getQuads(null, d, null))
				if (quad.getSprite() instanceof BakedAnimationTextureSprite bakedAnimSprite && animationNames.test(bakedAnimSprite.getAnimationName()))
					shiftTextureUV(quad, bakedAnimSprite, shiftU, shiftV);
		}
		for (BakedQuad quad : model.getQuads(null, null, null))
			if (quad.getSprite() instanceof BakedAnimationTextureSprite bakedAnimSprite && animationNames.test(bakedAnimSprite.getAnimationName()))
				shiftTextureUV(quad, bakedAnimSprite, shiftU, shiftV);
	}
	
	private static final ByteBuffer CONVERSION_BUFFER = ByteBuffer.allocate(4);
	private static final Map<BakedQuad, float[]> CACHED_DEFAULT_UV = new HashMap<>(); 
	
	private static float[] getUVDefault(BakedQuad quad) {
		float[] UV = CACHED_DEFAULT_UV.get(quad);
		if (UV == null) {
			int[] data = quad.getVertices();
			int vertecies = data.length / 8;
			UV = new float[vertecies * 2];
			for (int i = 0; i < vertecies; i++) {
				UV[i * 2 + 0] = CONVERSION_BUFFER.putInt(0, data[i * 8 + 4]).getFloat(0);
				UV[i * 2 + 1] = CONVERSION_BUFFER.putInt(0, data[i * 8 + 5]).getFloat(0);
			}
			CACHED_DEFAULT_UV.put(quad, UV);	
		}
		return UV;
	}
	
	public static void shiftTextureUV(BakedQuad quad, BakedAnimationTextureSprite sprite, float shiftU, float shiftV) {
		
		int[] data = quad.getVertices();
		int vertecies = data.length / 8;
		float[] defaultUV = getUVDefault(quad);
		float U = (sprite.getU1() - sprite.getU0()) * shiftU * sprite.getFactorU();
		float V = (sprite.getV1() - sprite.getV0()) * shiftV * sprite.getFactorV();
		
		for (int i = 0; i < vertecies; i++) {
			data[i * 8 + 4] = CONVERSION_BUFFER.putFloat(0, defaultUV[i * 2 + 0] + U).getInt(0);
			data[i * 8 + 5] = CONVERSION_BUFFER.putFloat(0, defaultUV[i * 2 + 1] + V).getInt(0);
		}
		
	}
	
	@SubscribeEvent
	public static void onResourceReload(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new PreparableReloadListener() {
			@Override
			public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager,
					ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor,
					Executor pGameExecutor) {
				
				// Clear the UV state cache on reloading the assets
				CACHED_DEFAULT_UV.clear();
				IndustriaCore.LOGGER.debug("Cleared baked animation UV cache!");
				return null;
			}
		});
	}
	
}
