package de.m_marvin.industria.conduits;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Conduit implements IForgeRegistryEntry<Conduit> {
	
	private ResourceLocation registryName;
	private ConduitType conduitType;
	
	public Conduit(ConduitType type) {
		this.conduitType = type;
	}
	
	public ConduitType getConduitType() {
		return conduitType;
	}
	
	@Override
	public Conduit setRegistryName(ResourceLocation name) {
		this.registryName = name;
		return this;
	}
	
	@Override
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}
	
	@Override
	public Class<Conduit> getRegistryType() {
		return Conduit.class;
	}
	
	public static class ConduitType implements IForgeRegistryEntry<ConduitType>{
		protected ResourceLocation registryName;
		protected float stiffness;
		protected int clampingLength;
		protected int thickness;

		public ConduitType(float stiffness, int clampingLength, int thickness) {
			this.stiffness = stiffness;
			this.clampingLength = clampingLength;
			this.thickness = thickness;
		}
		
		/*
		 * The stiffness of the wire, defines how much the wire can bend 
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
		
		@Override
		public ConduitType setRegistryName(ResourceLocation name) {
			this.registryName = name;
			return this;
		}
		
		@Override
		public ResourceLocation getRegistryName() {
			return this.registryName;
		}
		
		@Override
		public Class<ConduitType> getRegistryType() {
			return ConduitType.class;
		}
	}
	
}
