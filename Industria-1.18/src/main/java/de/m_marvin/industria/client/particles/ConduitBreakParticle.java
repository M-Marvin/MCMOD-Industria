package de.m_marvin.industria.client.particles;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.particleoptions.ConduitParticleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;

public class ConduitBreakParticle extends SingleQuadParticle {
	private final Conduit conduit;
	private final BlockPos pos;
	private final TextureAtlasSprite sprite;
	
	public ConduitBreakParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Conduit conduit) {
		this(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, conduit, new BlockPos(pX, pY, pZ));
	}
	
	@SuppressWarnings("deprecation")
	public ConduitBreakParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, Conduit conduit, BlockPos pos) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		this.conduit = conduit;
		this.pos = pos;
		this.sprite = ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES)).getSprite(conduit.getTexture());
		
		
//		((ParticleEngine.MutableSpriteSet) sprite).rebind()
//		this.setSprite(sprite.get(random));
//		this.gravity = 1.0F;
//		this.rCol = 0.6F;
//		this.gCol = 0.6F;
//		this.bCol = 0.6F;
////		if (!pState.is(Blocks.GRASS_BLOCK)) {
////			int i = Minecraft.getInstance().getBlockColors().getColor(pState, pLevel, pPos, 0);
////			this.rCol *= (float)(i >> 16 & 255) / 255.0F;
////			this.gCol *= (float)(i >> 8 & 255) / 255.0F;
////			this.bCol *= (float)(i & 255) / 255.0F;
////		}
//		
//		
//		
//		this.quadSize /= 2.0F;
//		this.uo = this.random.nextFloat() * 3.0F;
//		this.vo = this.random.nextFloat() * 3.0F;
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
		return this.sprite.getU0();
	}

	@Override
	protected float getU1() {
		return this.sprite.getU1();
	}

	@Override
	protected float getV0() {
		return this.sprite.getV0();
	}

	@Override
	protected float getV1() {
		return this.sprite.getV1();
	}
	
}
