package de.m_marvin.industria.client.particles;

import de.m_marvin.industria.client.rendering.ConduitWorldRenderer;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.particleoptions.ConduitParticleOption;
import de.m_marvin.industria.util.unifiedvectors.Vec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ConduitBreakParticle extends SingleQuadParticle {
	
	private final TextureAtlasSprite sprite;
	private final Vec2f uv0;
	private final Vec2f uv1;
	
	@SuppressWarnings("deprecation")
	public ConduitBreakParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Conduit conduit) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.sprite = ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES)).getSprite(conduit.getTexture());
		
		Vec2f spriteUV0 = new Vec2f(sprite.getU0(), sprite.getV0());
		Vec2f spriteUV1 = new Vec2f(sprite.getU1(), sprite.getV1());
		float segHight = conduit.getConduitType().getThickness() / (float) ConduitWorldRenderer.TEXTURE_MAP_SIZE;
		Vec2f particleSeg = new Vec2f(random.nextFloat(), random.nextFloat() * segHight * 3);
		this.uv0 = particleSeg.copy().mul(spriteUV1.copy().sub(spriteUV0));
		this.uv1 = this.uv0.copy().add(new Vec2f(segHight, segHight).mul(spriteUV1.copy().sub(spriteUV0)));
	}
	
	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	
	public static class Provider implements ParticleProvider<ConduitParticleOption> {
		@Override
		public Particle createParticle(ConduitParticleOption pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new ConduitBreakParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType.getConduit());
		}
	}

	@Override
	protected float getU0() {
		return this.uv0.x;
	}

	@Override
	protected float getU1() {
		return this.uv1.x;
	}

	@Override
	protected float getV0() {
		return this.uv0.y;
	}

	@Override
	protected float getV1() {
		return this.uv1.y;
	}
	
}
