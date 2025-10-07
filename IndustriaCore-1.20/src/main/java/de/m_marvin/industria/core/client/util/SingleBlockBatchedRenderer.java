package de.m_marvin.industria.core.client.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus=Bus.FORGE, modid=IndustriaCore.MODID, value=Dist.CLIENT)
public class SingleBlockBatchedRenderer {
	
	private SingleBlockBatchedRenderer() {}
	
	private static final Supplier<ProfilerFiller> PROFILER = () -> Minecraft.getInstance().getProfiler();
	private static final Supplier<BlockRenderDispatcher> DISPATCHER = () -> Minecraft.getInstance().getBlockRenderer();
	private static final Supplier<RandomSource> RANDOM = () -> Minecraft.getInstance().level.random;
	private static final BufferBuilder INTERMEDIATE_BUFFER = new BufferBuilder(2048);

	private static boolean useOptimization = false;

	private static Matrix4f cameraMatrix;
	private static Matrix4f inverseCameraMatrix;
	
	private static Int2ObjectMap<VertexBuffer> vertexCaches = new Int2ObjectOpenHashMap<VertexBuffer>();
	private static Set<DrawRequest> drawRequests = new HashSet<>();

//	private static int lastCacheSize = 0;
	
	private static record DrawRequest(Matrix4f translation, RenderType type, int modelHash) implements Comparable<DrawRequest> {

		@Override
		public int compareTo(DrawRequest o) {
			return Integer.compare(this.type.hashCode(), o.type.hashCode()); 
		}
		
	}
	
	@SubscribeEvent
	public static void onRenderTick(RenderLevelStageEvent event) {
		
		if (event.getStage() == Stage.AFTER_SKY) {
		
			var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			Vec3 cameraOffset = camera.getPosition();
			cameraMatrix = new Matrix4f();
			cameraMatrix.rotate(Axis.XP.rotationDegrees(camera.getXRot()));
			cameraMatrix.rotate(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
			cameraMatrix.translate((float)- cameraOffset.x, (float)- cameraOffset.y, (float) -cameraOffset.z);
			inverseCameraMatrix = new Matrix4f(cameraMatrix).invert();
			
		} else if (event.getStage() == Stage.AFTER_BLOCK_ENTITIES) {

			PROFILER.get().push(IndustriaCore.MODID + "single_block_batch");
			
//			System.out.println(String.format("RENDER TEST: IN CACHE: %d, DRAWS: %s, UPLOADS: %d", vertexCaches.size(), drawRequests.size(), lastCacheSize - vertexCaches.size()));
//			lastCacheSize = vertexCaches.size();
			
			processDraws();
			
			drawRequests.clear();
			
			vertexCaches.values().forEach(VertexBuffer::close);
			vertexCaches.clear();

			PROFILER.get().pop();
			
		}
		
	}
	
	public static void reloadConfig() {
		useOptimization = Config.USE_SINGLE_BATCHED_BLOCK_RENDERER.get();
	}
	
	public static void renderBlock(BlockState state, PoseStack poseStack, @Nullable MultiBufferSource buffer, int packedLight, int packedOverlay, int svar) {
		
		BakedModel model = DISPATCHER.get().getBlockModel(state);	
		for (net.minecraft.client.renderer.RenderType rt : model.getRenderTypes(state, RANDOM.get(), ModelData.EMPTY))
			if (useOptimization)
				renderToCache(state, model, poseStack, rt, packedLight, packedOverlay, svar);
			else if (buffer != null)
				DISPATCHER.get().getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), state, model, 1F, 1F, 1F, packedLight, packedOverlay, ModelData.EMPTY, rt);
		
	}
	
	private static void renderToCache(BlockState state, BakedModel model, PoseStack poseStack, RenderType rt, int packedLight, int packedOverlay, int svar) {

		RenderType flushingRenderType = RenderTypeHelper.getEntityRenderType(rt, false);
		int hash = Objects.hash(state, rt, packedLight, packedOverlay, svar);
		
		VertexBuffer cache = vertexCaches.get(hash);
		
		if (cache == null) {
			
			cache = new VertexBuffer(Usage.DYNAMIC);
			
			INTERMEDIATE_BUFFER.begin(flushingRenderType.mode(), flushingRenderType.format());
			
			PoseStack test = new PoseStack();
			test.last().pose().mul(cameraMatrix);
			test.last().normal().mul(cameraMatrix.normal(new Matrix3f()));
			
			DISPATCHER.get().getModelRenderer().renderModel(test.last(), INTERMEDIATE_BUFFER, state, model, 1F, 1F, 1F, packedLight, packedOverlay, ModelData.EMPTY, rt);
			
			cache.bind();
			cache.upload(INTERMEDIATE_BUFFER.end());
			INTERMEDIATE_BUFFER.clear();
			
			vertexCaches.put(hash, cache);
			
		}

		Matrix4f worldTranslation = poseStack.last().pose().mul(inverseCameraMatrix, new Matrix4f());
		
		drawRequests.add(new DrawRequest(worldTranslation, flushingRenderType, hash));
		
	}
	
	public static void processDraws() {
		
		RenderType activeType = null;
		
		for (DrawRequest request : drawRequests.stream().sorted().toList()) {

			if (activeType != request.type) {
				if (activeType != null)
					activeType.clearRenderState();
				activeType = request.type;
				activeType.setupRenderState();
				
				setupShaderState(RenderSystem.getShader(), RenderSystem.getProjectionMatrix(), request.type.mode());
				
			}

			VertexBuffer cached = vertexCaches.get(request.modelHash);
			
			Matrix4f translation = new Matrix4f(RenderSystem.getModelViewMatrix()).mul(request.translation);
			
			setupShaderDynamicState(RenderSystem.getShader(), translation);
			
			cached.bind();
			cached.draw();
			
		}
		
		if (activeType != null)
			activeType.clearRenderState();
		
	}
	
	private static void setupShaderDynamicState(ShaderInstance pShader, Matrix4f pModelViewMatrix) {

		if (pShader.MODEL_VIEW_MATRIX != null) {
				pShader.MODEL_VIEW_MATRIX.set(pModelViewMatrix);
		}

		pShader.apply();
		
	}
	
	private static void setupShaderState(ShaderInstance pShader, Matrix4f pProjectionMatrix, VertexFormat.Mode mode) {
		for(int i = 0; i < 12; ++i) {
			int j = RenderSystem.getShaderTexture(i);
			pShader.setSampler("Sampler" + i, j);
		}
		
		if (pShader.PROJECTION_MATRIX != null) {
			pShader.PROJECTION_MATRIX.set(pProjectionMatrix);
		}
		
		if (pShader.INVERSE_VIEW_ROTATION_MATRIX != null) {
			pShader.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
		}
		
		if (pShader.COLOR_MODULATOR != null) {
			pShader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
		}
		
		if (pShader.GLINT_ALPHA != null) {
			pShader.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
		}
		
//		if (pShader.FOG_START != null) {
//			pShader.FOG_START.set(RenderSystem.getShaderFogStart());
//		}
//		
//		if (pShader.FOG_END != null) {
//			pShader.FOG_END.set(RenderSystem.getShaderFogEnd());
//		}
//		
//		if (pShader.FOG_COLOR != null) {
//			pShader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
//		}
//		
//		if (pShader.FOG_SHAPE != null) {
//			pShader.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
//		}
		/**
		 * Fog does not work with this rendering method, but is not relevant for block entities
		 * anyway, since they have a limited render distance.
		 */
		if (pShader.FOG_COLOR != null) {
			pShader.FOG_COLOR.set(new float[] {0, 0, 0, 0});
		}
		
		if (pShader.TEXTURE_MATRIX != null) {
			pShader.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
		}
		
		if (pShader.GAME_TIME != null) {
			pShader.GAME_TIME.set(RenderSystem.getShaderGameTime());
		}
		
		if (pShader.SCREEN_SIZE != null) {
			Window window = Minecraft.getInstance().getWindow();
			pShader.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
		}
		
		if (pShader.LINE_WIDTH != null && (mode == VertexFormat.Mode.LINES || mode == VertexFormat.Mode.LINE_STRIP)) {
			pShader.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
		}
		
		RenderSystem.setupShaderLights(pShader);
		pShader.apply();
	}
	
}
