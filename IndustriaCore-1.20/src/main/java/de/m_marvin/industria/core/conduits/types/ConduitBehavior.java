package de.m_marvin.industria.core.conduits.types;

import de.m_marvin.industria.core.conduits.types.ConduitNode.NodeType;
import net.minecraft.world.level.block.SoundType;

public class ConduitBehavior {

	protected final float nodeMass;					// the weight of an conduit per node (points between segments)
	protected final float stiffness;				// how stiff the segments are against stretching forces (inverse elasticity)
	protected final float tension;					// base tension of the segments
	protected final int clampingLength;				// how far the two nodes the conduit is placed in between can be apart from each other (max length)
	protected final float thickness;				// how wide the collision box if the conduit is
	protected final float segmentLength;			// length of an individual segment of the conduit shape (base value, might be adjusted to achieve uneven total lengths)
	protected final double constraintCompensation;	// compensation factor for VS2 constraint
	protected final double constraintForce;			// force factor for VS2 constraint
	protected final SoundType soundType;			// the break/place sound type
	protected final NodeType[] validNodeTypes;		// which types of nodes the conduit can be attached to
	
	public ConduitBehavior(Properties properties) {
		this.nodeMass = properties.nodeMass;
		this.stiffness = properties.stiffness;
		this.tension = properties.tension;
		this.clampingLength = properties.clampingLength;
		this.thickness = properties.thickness;
		this.segmentLength = properties.segmentLength;
		this.constraintCompensation = properties.constraintCompensation;
		this.constraintForce = properties.constraintForce;
		this.soundType = properties.soundType;
		this.validNodeTypes = properties.validNodeTypes;
	}
	
	public float getNodeMass() {
		return nodeMass;
	}
	
	public float getStiffness() {
		return stiffness;
	}
	
	public float getTension() {
		return tension;
	}
	
	public int getClampingLength() {
		return clampingLength;
	}
	
	public float getThickness() {
		return thickness;
	}
	
	public float getSegmentLength() {
		return segmentLength;
	}
	
	public double getConstraintCompensation() {
		return constraintCompensation;
	}
	
	public double getConstraintForce() {
		return constraintForce;
	}
	
	public SoundType getSoundType() {
		return soundType;
	}
	
	public NodeType[] getValidNodeTypes() {
		return validNodeTypes;
	}
	
	public static class Properties {
		
		protected float nodeMass = 0.003F;
		protected float stiffness = 1F;
		protected float tension = 2F;
		protected int clampingLength = 128;
		protected float thickness = 0.0625F;
		protected float segmentLength = 0.5F;
		protected double constraintCompensation = 1e-10;
		protected double constraintForce = 1e10;
		protected SoundType soundType;
		protected NodeType[] validNodeTypes;
		
		private Properties() {}
		
		public static Properties of() {
			return new Properties();
		}
		
		public static Properties of(ConduitBehavior behavior) {
			Properties properties = of();
			properties.nodeMass = behavior.nodeMass;
			properties.stiffness = behavior.stiffness;
			properties.tension = behavior.tension;
			properties.clampingLength = behavior.clampingLength;
			properties.thickness = behavior.thickness;
			properties.segmentLength = behavior.segmentLength;
			properties.constraintCompensation = behavior.constraintCompensation;
			properties.constraintForce = behavior.constraintForce;
			properties.soundType = behavior.soundType;
			properties.validNodeTypes = behavior.validNodeTypes;
			return properties;
		}
		
		public Properties nodeMass(float nodeMass) {
			this.nodeMass = nodeMass;
			if (this.nodeMass < 0)
				this.nodeMass = 0;
			return this;
		}
		
		public Properties stiffness(float stiffness) {
			this.stiffness = stiffness;
			if (this.stiffness > 1)
				this.stiffness = 1;
			else if (this.stiffness < 0)
				this.stiffness = 0;
			return this;
		}

		public Properties tension(float tension) {
			this.tension = tension;
			if (this.tension < 0.001F)
				this.tension = 0.001F;
			return this;
		}
		
		public Properties clampingLength(int clampingLength) {
			this.clampingLength = clampingLength;
			if (this.clampingLength < 1)
				this.clampingLength = 1;
			return this;
		}
		
		public Properties thickness(float thickness) {
			this.thickness = thickness;
			if (this.thickness < 1)
				this.thickness = 1;
			return this;
		}
		
		public Properties segmentLength(float segmentLength) {
			this.segmentLength = segmentLength;
			if (this.segmentLength < 0.001F)
				this.segmentLength = 0.001F;
			return this;
		}
		
		public Properties constraintCompensation(float constraintCompensation) {
			this.constraintCompensation = constraintCompensation;
			return this;
		}
		
		public Properties constraintForce(float constraintForce) {
			this.constraintForce = constraintForce;
			return this;
		}
		
		public Properties soundType(SoundType soundType) {
			this.soundType = soundType;
			return this;
		}
		
		public Properties validNodes(NodeType... nodeTypes) {
			this.validNodeTypes = nodeTypes;
			return this;
		}
		
	}
	
}
