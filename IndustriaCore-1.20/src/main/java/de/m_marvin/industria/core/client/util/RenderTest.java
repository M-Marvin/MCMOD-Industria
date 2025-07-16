package de.m_marvin.industria.core.client.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.valkyrienskies.core.impl.shadow.po;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
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
public class RenderTest {

	// FIXME ANIMATED BLOCK RENDERING OPTIMIZATION
	
	@SubscribeEvent
	public static void onRenderTick(RenderLevelStageEvent event) {
		
		if (event.getStage() == Stage.AFTER_SOLID_BLOCKS) {

			processDraws();
			
			drawRequests.clear();
			
			vertexCaches.values().forEach(v -> v.close());
			vertexCaches.clear();
			
		}
		
	}

	private static Supplier<BlockRenderDispatcher> dispatcher = () -> Minecraft.getInstance().getBlockRenderer();
	private static Supplier<RandomSource> random = () -> Minecraft.getInstance().level.random;
	
	private static Int2ObjectMap<VertexBuffer> vertexCaches = new Int2ObjectOpenHashMap<VertexBuffer>();

	private record DrawRequest(Matrix4f pose, Vec3f translation, RenderType type, int modelHash) implements Comparable<DrawRequest> {

		@Override
		public int compareTo(DrawRequest o) {
			return Integer.compare(this.type.hashCode(), o.type.hashCode()); 
		}
		
	}
	
	private static Set<DrawRequest> drawRequests = new HashSet<>();
	
	public static void renderBlock(BlockState state, PoseStack poseStack, int packedLight, int packedOverlay) {
		
		BakedModel model = dispatcher.get().getBlockModel(state);	
		for (net.minecraft.client.renderer.RenderType rt : model.getRenderTypes(state, random.get(), ModelData.EMPTY))
			renderToCache(state, model, poseStack, rt, packedLight, packedOverlay);
		
	}
	
	private static final BufferBuilder cachingBufferBuilder = new BufferBuilder(2048);
	
	private static void renderToCache(BlockState state, BakedModel model, PoseStack poseStack, RenderType rt, int packedLight, int packedOverlay) {

		RenderType flushingRenderType = RenderTypeHelper.getEntityRenderType(rt, false);
		int hash = Objects.hash(state, rt, packedLight, packedOverlay);
		
		Matrix4f pose = poseStack.last().pose();
//		Vec3f translation = new Vec3f(pose.m30(), pose.m31(), pose.m32());
		Matrix4f p2 = new Matrix4f(pose);
		
		if (!vertexCaches.containsKey(hash)) {
			
			VertexBuffer vbuff = new VertexBuffer(Usage.STATIC);;
			
			BufferBuilder buff = cachingBufferBuilder;

			buff.begin(flushingRenderType.mode(), flushingRenderType.format());
			
//			poseStack.pushPose();
//			poseStack.translate(-translation.x, -translation.y, -translation.z);
			
			dispatcher.get().getModelRenderer().renderModel(new PoseStack().last(), buff, state, model, 1F, 1F, 1F, packedLight, packedOverlay, ModelData.EMPTY, rt);
			
//			poseStack.popPose();
			
			vbuff.bind();
			vbuff.upload(buff.end());
			buff.clear();
			
			vertexCaches.put(hash, vbuff);
			
		}
		
		drawRequests.add(new DrawRequest(p2, new Vec3f(0, 0, 0), flushingRenderType, hash));
		
//		processDraws();
		
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
			
			
			
			Matrix4f translation = request.pose; //new Matrix4f().translateLocal(request.translation.x, request.translation.y, request.translation.z);
			
			Matrix4f mat = new Matrix4f(translation).mul(RenderSystem.getModelViewMatrix());

			setupShaderDynamicState(RenderSystem.getShader(), mat);
			
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
