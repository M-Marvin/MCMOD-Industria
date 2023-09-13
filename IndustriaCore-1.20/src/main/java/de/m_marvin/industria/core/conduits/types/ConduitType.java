package de.m_marvin.industria.core.conduits.types;

import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.resources.ResourceLocation;

public class ConduitType {
	
	protected ResourceLocation registryName;
	protected float nodeMass;
	protected float stiffness;
	protected int clampingLength;
	protected int thickness;

	public ConduitType(float nodeMass, float stiffness, int clampingLength, int thickness) {
		this.nodeMass = nodeMass;
		this.stiffness = MathUtility.clamp(stiffness, 0, 1);
		this.clampingLength = clampingLength;
		this.thickness = thickness;
	}
	
	/*
	 * The mass of the individual nodes
	 */
	public float getNodeMass() {
		return nodeMass;
	}
	
	/*
	 * The stiffness of the wire beams
	 */
	public float getStiffness() {
		return stiffness;
	}
	
	/*
	 * The maximum clamping length in blocks
	 */
	public int getClampingLength() {
		return clampingLength;
	}
	
	/*
	 * The thickness of the wire in pixels (1/16 block)
	 */
	public int getThickness() {
		return thickness;
	}
	
}
