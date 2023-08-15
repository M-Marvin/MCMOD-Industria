package de.m_marvin.industria.content.types;

import net.minecraft.util.StringRepresentable;

public enum MotorMode implements StringRepresentable {
	MOTOR("motor"),
	GENERATOR("generator");
	
	private String name;
	
	private MotorMode(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
	
}
