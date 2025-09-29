package de.m_marvin.industria.content.client;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.unimat.impl.Quaternionf;
import de.m_marvin.univec.impl.Vec3f;

public abstract class AnimatedTransform {
	
	public static class LinearTransform extends AnimatedTransform {
		
		protected final Vec3f linear;
		
		protected LinearTransform(Vec3f linear) {
			super();
			this.linear = linear;
		}
		
		@Override
		protected void applyTransform(PoseStack pose, float delta) {
			pose.translate(this.linear.x * delta, this.linear.y * delta, this.linear.z * delta);
		}
		
	}
	
	public static class AngularTransform extends AnimatedTransform {
		
		protected final Vec3f origin;
		protected final Vec3f rotation;
		
		protected AngularTransform(Vec3f origin, Vec3f rotation) {
			super();
			this.origin = origin;
			this.rotation = rotation;
		}
		
		@Override
		protected void applyTransform(PoseStack pose, float delta) {
			Quaternionf rot = new Quaternionf(rotation.normalize(), rotation.length() * delta);
			pose.rotateAround(new org.joml.Quaternionf(rot.i, rot.j, rot.k, rot.r), origin.x, origin.y, origin.z);
		}
		
	}
	
	public static class ScaleTransform extends AnimatedTransform {
		
		protected final Vec3f scale;
		
		protected ScaleTransform(Vec3f scale) {
			super();
			this.scale = scale;
		}
		
		@Override
		protected void applyTransform(PoseStack pose, float delta) {
			pose.scale(this.scale.x * delta, this.scale.y * delta, this.scale.z * delta);
		}
		
	}
	
	protected AnimatedTransform nextTransform = null;
	protected AnimatedTransform firstTransform = null;
	
	protected AnimatedTransform() {
		this.firstTransform = this;
	}
	
	protected abstract void applyTransform(PoseStack pose, float delta); 
	
	protected int countTransforms() {
		return this.nextTransform != null ? this.nextTransform.countTransforms() + 1 : 1;
	}
	
	public AnimatedTransform fromStart() {
		return this.firstTransform;
	}
	
	public void transform(PoseStack pose, float animate) {
		int transforms = countTransforms();
		float localDelta = animate * transforms;
		if (localDelta < 1.0F) {
			applyTransform(pose, localDelta);
		} else {
			applyTransform(pose, 1.0F);
			if (nextTransform != null)
				nextTransform.transform(pose, (localDelta - 1) / (transforms - 1));
		}
	}

	public static AnimatedTransform firstLinear(Vec3f linear) {
		return new LinearTransform(linear);
	}

	public AnimatedTransform thenLinear(Vec3f linear) {
		this.nextTransform = firstLinear(linear);
		this.nextTransform.firstTransform = this.firstTransform;
		return this.nextTransform;
	}

	public static AnimatedTransform firstAngular(Vec3f origin, Vec3f rotation) {
		return new AngularTransform(origin, rotation);
	}

	public AnimatedTransform thenAngular(Vec3f origin, Vec3f rotation) {
		this.nextTransform = firstAngular(origin, rotation);
		this.nextTransform.firstTransform = this.firstTransform;
		return this.nextTransform;
	}

	public static AnimatedTransform firstScale(Vec3f scale) {
		return new ScaleTransform(scale);
	}

	public AnimatedTransform thenScale(Vec3f scale) {
		this.nextTransform = firstScale(scale);
		this.nextTransform.firstTransform = this.firstTransform;
		return this.nextTransform;
	}

}
