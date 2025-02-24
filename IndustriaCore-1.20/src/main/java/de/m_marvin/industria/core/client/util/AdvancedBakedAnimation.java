package de.m_marvin.industria.core.client.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Bus.FORGE, value=Dist.CLIENT)
public class AdvancedBakedAnimation {

	private AdvancedBakedAnimation() {}
	
	public static void shiftTextureUV(SimpleBakedModel model, float shiftU, float shiftV, ResourceLocation... texture) {
		List<ResourceLocation> textures = Arrays.asList(texture);
		shiftTextureUV(model, shiftU, shiftV, textures::contains);
	}
	
	public static void shiftTextureUV(SimpleBakedModel model, float shiftU, float shiftV, Predicate<ResourceLocation> textures) {
		for (Direction d : Direction.values()) {
			for (BakedQuad quad : model.getQuads(null, d, null))
				if (textures.test(quad.getSprite().contents().name()))
					shiftTextureUV(quad, quad.getSprite(), shiftU, shiftV);
		}
		for (BakedQuad quad : model.getQuads(null, null, null))
			if (textures.test(quad.getSprite().contents().name()))
				shiftTextureUV(quad, quad.getSprite(), shiftU, shiftV);
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
	
	public static void shiftTextureUV(BakedQuad quad, TextureAtlasSprite sprite, float shiftU, float shiftV) {
		
		int[] data = quad.getVertices();
		int vertecies = data.length / 8;
		float[] defaultUV = getUVDefault(quad);
		float U = (sprite.getU1() - sprite.getU0()) * shiftU;
		float V = (sprite.getV1() - sprite.getV0()) * shiftV;
		
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
