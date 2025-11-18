package de.m_marvin.industria.core.client.conduits;

import de.m_marvin.industria.core.client.registries.ParticleRenderTypes;
import de.m_marvin.industria.core.conduits.engine.particles.ConduitParticleOption;
import de.m_marvin.industria.core.conduits.types.ConduitState;
import de.m_marvin.univec.impl.Vec2f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ConduitBreakParticle extends SingleQuadParticle {
	
	private final TextureAtlasSprite sprite;
	private final Vec2f uv0;
	private final Vec2f uv1;
	
	@SuppressWarnings("deprecation")
	public ConduitBreakParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, ConduitState conduit) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.gravity = 1.0F;
		
		this.sprite = ConduitModelManager.getModel(conduit).getParticleIcon();
		// TODO
		float thikness = conduit.getConduit().getThickness() * 16F;
		float particleSize = random.nextFloat() * thikness;
		float particleWidth = particleSize / 16F;
		float particleHeight = particleSize / 16F;
		float particleU0 = random.nextFloat() * (1 - particleWidth);
		float particleV0 = random.nextFloat() * ((thikness * 4F / 16F) - particleHeight);
		
		this.quadSize = particleSize / 16F;
		this.uv0 = new Vec2f(
				sprite.getU0() + (particleU0) * (sprite.getU1() - sprite.getU0()),
				sprite.getV0() + (particleV0) * (sprite.getV1() - sprite.getV0())
		);
		this.uv1 = new Vec2f(
				sprite.getU0() + (particleU0 + particleWidth) * (sprite.getU1() - sprite.getU0()),
				sprite.getV0() + (particleV0 + particleHeight) * (sprite.getV1() - sprite.getV0())
		);
	}
	
	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderTypes.CONDUIT_SHEET_OPAQUE;
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
