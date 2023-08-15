package de.m_marvin.industria.core.client.registries;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import de.m_marvin.industria.core.client.conduits.ConduitTextureManager;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class ParticleRenderTypes {
	
	public static final ResourceLocation LOCATION_CONDUITS = new ResourceLocation("textures/atlas/conduits.png");
	
	public static final ParticleRenderType CONDUIT_SHEET_OPAQUE = new ParticleRenderType() {
		public void begin(BufferBuilder p_107448_, TextureManager p_107449_) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			RenderSystem.setShader(GameRenderer::getParticleShader);
			RenderSystem.setShaderTexture(0, ConduitTextureManager.LOCATION_CONDUITS);
			p_107448_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}
		public void end(Tesselator p_107451_) {
			p_107451_.end();
		}
		@Override
		public String toString() {
			return "CONDUIT_SHEET_OPAQUE";
		}
	};
	
}
