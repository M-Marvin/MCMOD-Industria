package de.m_marvin.industria.core.client.gpucaching;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import com.mojang.blaze3d.vertex.VertexFormat;

import de.m_marvin.industria.IndustriaCore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus=Bus.FORGE, modid=IndustriaCore.MODID, value=Dist.CLIENT)
public class RenderTest {

	// FIXME ANIMATED BLOCK RENDERING OPTIMIZATION
	
	@SubscribeEvent
	public static void onRenderTick(RenderLevelStageEvent event) {
		
		if (event.getStage() == Stage.AFTER_BLOCK_ENTITIES) {

//			PoseStack p = event.getPoseStack();
//			p.pushPose();
//			Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//			p.translate(-offset.x, -offset.y + 1, -offset.z);
//			BlockState s = de.m_marvin.industria.core.registries.Blocks.GEAR.get().defaultBlockState();
//			BakedModel model = dispatcher.get().getBlockModel(s);
//			ModelData data = ModelData.EMPTY;
////			for (net.minecraft.client.renderer.RenderType rt : model.getRenderTypes(, random.get(), data))
//				dispatcher.get().getModelRenderer().renderModel(p.last(), Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(RenderType.solid(), false)), s, model, 1F, 1F, 1F, 245, 0, data, RenderType.solid());
//				p.popPose();
				
//			BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//			PoseStack matrixStack = event.getPoseStack();
//			
//			Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//			
//			matrixStack.pushPose();
//			matrixStack.translate(-offset.x, -offset.y, -offset.z);
//			
//			cached = null;
//			
//			int s = 30;
//			for (int x = 0; x < s; x++) {
//				for (int y = 0; y < s; y++) {
//					for (int z = 0; z < s; z++) {
//						matrixStack.pushPose();
//						matrixStack.translate(x, y, z);
//						
//						
//						
//						renderBlock(Blocks.NETHERITE_BLOCK.defaultBlockState(), matrixStack, buffer);
//						
//						
//						
//						matrixStack.popPose();
//					}
//				}
//			}
//			
//			matrixStack.popPose();
			
			processDraws();
			
//			System.out.println("REQUESTS: " + drawRequests.size());
			
			drawRequests.clear();
			
//			System.out.println("CACHED: " + vertexCaches.size());
			
			vertexCaches.values().forEach(v -> v.close());
			vertexCaches.clear();
			
		}
		
	}

	private static Supplier<BlockRenderDispatcher> dispatcher = () -> Minecraft.getInstance().getBlockRenderer();
	private static Supplier<RandomSource> random = () -> Minecraft.getInstance().level.random;
	
	private static Int2ObjectMap<VertexBuffer> vertexCaches = new Int2ObjectOpenHashMap<VertexBuffer>();

	private record DrawRequest(Pose pose, RenderType type, int modelHash) implements Comparable<DrawRequest> {

		@Override
		public int compareTo(DrawRequest o) {
			return Integer.compare(this.type.hashCode(), o.type.hashCode()); 
		}
		
	}
	
	private static Set<DrawRequest> drawRequests = new HashSet<>();
	
	public static void renderInRequest(BlockState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		
		BakedModel model = dispatcher.get().getBlockModel(state);

		for (net.minecraft.client.renderer.RenderType rt : model.getRenderTypes(state, random.get(), ModelData.EMPTY))
			renderToCache(state, model, poseStack, buffer, rt, packedLight, packedOverlay);
		
	}
	
	private static final BufferBuilder cachingBufferBuilder = new BufferBuilder(2048);
	
	private static void renderToCache(BlockState state, BakedModel model, PoseStack poseStack, MultiBufferSource buffer, RenderType rt, int packedLight, int packedOverlay) {
		
		int hash = Objects.hash(state, rt, packedLight, packedOverlay);
		
		if (!vertexCaches.containsKey(hash)) {
			
			VertexBuffer vbuff = new VertexBuffer(Usage.STATIC);;
			
			PoseStack ps = new PoseStack();
			
			cachingBufferBuilder.begin(rt.mode(), rt.format());
			
			dispatcher.get().getModelRenderer().renderModel(ps.last(), cachingBufferBuilder, state, model, 1F, 1F, 1F, packedLight, packedOverlay, ModelData.EMPTY, rt);
			
			vbuff.bind();
			vbuff.upload(cachingBufferBuilder.end());
			cachingBufferBuilder.clear();
			
			vertexCaches.put(hash, vbuff);
			
		}
		
//		dispatcher.getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), state, model, 1F, 1F, 1F, packedLight, packedOverlay, ModelData.EMPTY, rt);
		
		drawRequests.add(new DrawRequest(poseStack.last(), rt, hash));
		
//		rt.setupRenderState();
//		
//		VertexBuffer cached = vertexCaches.get(hash);
//		
//		Matrix4f mat = RenderSystem.getModelViewMatrix();
//		
//		mat = mat.mul(poseStack.last().pose(), new Matrix4f());
//		
//		cached.bind();
//		cached.drawWithShader(mat, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
//		
//		rt.clearRenderState();
		
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
				
				RenderSystem.enableBlend();
				
			}

			VertexBuffer cached = vertexCaches.get(request.modelHash);
			
			Matrix4f mat = new Matrix4f(RenderSystem.getModelViewMatrix()).mul(request.pose.pose());

			setupShaderDynamicState(RenderSystem.getShader(), mat);
			
			cached.bind();
//			cached.drawWithShader(mat, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
			cached.draw();
			
		}
		
		if (activeType != null)
			activeType.clearRenderState();

		RenderSystem.disableBlend();
		
//		System.out.println("TOTAL STEATE SETUPS: " + stateSetups);
		
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

	       if (pShader.FOG_START != null) {
	          pShader.FOG_START.set(RenderSystem.getShaderFogStart());
	       }

	       if (pShader.FOG_END != null) {
	          pShader.FOG_END.set(RenderSystem.getShaderFogEnd());
	       }

	       if (pShader.FOG_COLOR != null) {
	          pShader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
	       }

	       if (pShader.FOG_SHAPE != null) {
	          pShader.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
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
