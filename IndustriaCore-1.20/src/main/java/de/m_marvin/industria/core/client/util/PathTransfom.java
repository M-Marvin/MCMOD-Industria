package de.m_marvin.industria.core.client.util;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.unimat.impl.Quaternionf;
import de.m_marvin.univec.impl.Vec3f;

public class PathTransfom {
	
	float interpolationStart = 0F;
	float interpolationEnd = 0F;
	
	private float transformStart = 0F;
	private float transformEnd = 0F;
	private final List<TransformSpan> transforms = new ArrayList<PathTransfom.TransformSpan>();
	
	private static record TransformSpan(float start, float end, Transform transform) {
		
		public void apply(PoseStack poseStack, float interpolationPosition) {
			if (interpolationPosition < this.start) return;
			if (interpolationPosition < this.end) {
				this.transform.transform(poseStack, (interpolationPosition - this.start) / (this.end - this.start));
			} else {
				this.transform.transform(poseStack, 1F);
			}
		}
		
	}
	
	@FunctionalInterface
	private static interface Transform {
		public void transform(PoseStack poseStack, float interpolation);
	}
	
	public void transform(PoseStack poseStack, float interpolation) {
		for (var transformSpan : this.transforms)
			transformSpan.apply(poseStack, interpolation);
	}
	
	public PathTransfom between(float start, float end) {
		if (!Float.isFinite(start) || !Float.isFinite(end))
			throw new IllegalArgumentException("start and end position not finite");
		if (start > end)
			throw new IllegalArgumentException("start !<= end");
		this.interpolationStart = start;
		this.interpolationEnd = end;
		if (start < this.transformStart) this.transformStart = start;
		if (end > this.transformEnd) this.transformEnd = end;
		return this;
	}
	
	public PathTransfom at(float pos) {
		if (!Float.isFinite(pos))
			throw new IllegalArgumentException("position not finite");
		this.interpolationStart = pos;
		this.interpolationEnd = pos;
		if (pos < this.transformStart) this.transformStart = pos;
		if (pos > this.transformEnd) this.transformEnd = pos;
		return this;
	}
	
	public PathTransfom translate(Vec3f translation) {
		Vec3f translationImmutable = translation.copy();
		if (translationImmutable.length() == 0) return this;
		this.transforms.add(new TransformSpan(this.interpolationStart, this.interpolationEnd, (poseStack, interpolation) -> {
			Vec3f v = translationImmutable.mul(interpolation);
			poseStack.translate(v.x, v.y, v.z);
		}));
		return this;
	}
	
	public PathTransfom translate(float x, float y, float z) {
		return translate(new Vec3f(x, y, z));
	}
	
	public PathTransfom rotate(Vec3f origin, Vec3f rotation) {
		Vec3f originImmutable = origin.copy();
		Vec3f rotationImmutable = rotation.copy();
		if (rotationImmutable.length() == 0) return this;
		this.transforms.add(new TransformSpan(this.interpolationStart, this.interpolationEnd, (poseStack, interpolation) -> {
			Quaternionf rot = new Quaternionf(rotationImmutable.tryNormalize(), rotationImmutable.length() * interpolation);
			poseStack.rotateAround(new org.joml.Quaternionf(rot.i, rot.j, rot.k, rot.r), originImmutable.x, originImmutable.y, originImmutable.z);
		}));
		return this;
	}
	
	public PathTransfom rotate(float ox, float oy, float oz, float rx, float ry, float rz) {
		return rotate(new Vec3f(ox, oy, oz), new Vec3f(rx, ry, rz));
	}
	
	public PathTransfom scale(Vec3f scale) {
		Vec3f scaleImmutable = scale.copy();
		this.transforms.add(new TransformSpan(this.interpolationStart, this.interpolationEnd, (poseStack, interpolation) -> {
			Vec3f v = scaleImmutable.mul(interpolation);
			poseStack.scale(v.x, v.y, v.z);
		}));
		return this;
	}
	
	public PathTransfom scale(float x, float y, float z) {
		return scale(new Vec3f(x, y, z));
	}
	
}
