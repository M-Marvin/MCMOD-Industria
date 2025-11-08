package de.m_marvin.industria.core.client.flyinstances;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

import de.m_marvin.industria.core.client.util.ShiftedTextureAnimation.ShiftedTextureAnimationSprite;
import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.ColoredLitOverlayInstance;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class ShiftedTextureInstance extends ColoredLitOverlayInstance {

	public static final int MAX_ANIMATED_QUADS = 90;
	
	// TODO [FLWdep] uv data should be part of the model vertecies
	
	public float posX;
	public float posY;
	public float posZ;
	public Vector2fc[] shifts;
	
	public ShiftedTextureInstance(InstanceType<? extends ColoredLitOverlayInstance> type, InstanceHandle handle) {
		super(type, handle);
		this.posX = 0.0F;
		this.posY = 0.0F;
		this.posZ = 0.0F;
		this.shifts = new Vector2fc[MAX_ANIMATED_QUADS];
		for (int i = 0; i < MAX_ANIMATED_QUADS; i++)
			this.shifts[i] = new Vector2f(0F, 0F);
	}
	
	public ShiftedTextureInstance position(float x, float y, float z) {
		posX = x;
		posY = y;
		posZ = z;
		return this;
	}

	public ShiftedTextureInstance position(Vector3fc pos) {
		return position(pos.x(), pos.y(), pos.z());
	}

	public ShiftedTextureInstance position(Vec3i pos) {
		return position(pos.getX(), pos.getY(), pos.getZ());
	}

	public ShiftedTextureInstance position(Vec3 pos) {
		return position((float) pos.x(), (float) pos.y(), (float) pos.z());
	}

	public ShiftedTextureInstance zeroPosition() {
		return position(0, 0, 0);
	}

	public ShiftedTextureInstance shift(BakedModel model, float shiftU, float shiftV, String... animationNames) {
		List<String> animations = Arrays.asList(animationNames);
		return shift(model, shiftU, shiftV, animations::contains);
	}
	
	@SuppressWarnings("deprecation")
	public ShiftedTextureInstance shift(BakedModel model, float shiftU, float shiftV, Predicate<String> animations) {
		try {
			int quadId = 0;
			for (Direction d : Direction.values()) {
				for (BakedQuad quad : model.getQuads(null, d, null)) {
					shift(quadId++, quad, shiftU, shiftV, animations);
				}
			}
			for (BakedQuad quad : model.getQuads(null, null, null)) {
				shift(quadId++, quad, shiftU, shiftV, animations);
			}
		} catch (IndexOutOfBoundsException e) {
			throw new RuntimeException("model reached hard limit of quads for texture uv animation: " + model.toString(), e);
		}
		return this;
	}
	
	public ShiftedTextureInstance shift(int quadId, BakedQuad quad, float shiftU, float shiftV, Predicate<String> animations) {
		if (quadId < 0 || quadId >= MAX_ANIMATED_QUADS)
			throw new IndexOutOfBoundsException("model reached hard limit of quads for texture uv animation: #faces > " + MAX_ANIMATED_QUADS);
		if (quad.getSprite() instanceof ShiftedTextureAnimationSprite sprite && animations.test(sprite.getAnimationName())) {
			this.shifts[quadId] = new Vector2f(
					(sprite.getU1() - sprite.getU0()) * shiftU * sprite.getFactorU(),
					(sprite.getV1() - sprite.getV0()) * shiftV * sprite.getFactorV()
			);
		}
		return this;
	}
	
	public ShiftedTextureInstance zeroShift() {
		for (int i = 0; i < this.shifts.length; i++)
			this.shifts[i] = new Vector2f(0F, 0F);
		return this;
	}
	
}
