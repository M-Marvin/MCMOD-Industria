package de.m_marvin.industria.core.electrics.circuits;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.resources.ResourceLocation;

public class Circuits {
	
	public static final double SHUNT_RESISTANCE = 0.001;
	
	public static final ResourceLocation CONSTANT_CURRENT_LOAD = new ResourceLocation(IndustriaCore.MODID, "constant_current_load");
	public static final ResourceLocation CURRENT_LIMITED_VOLTAGE_SOURCE = new ResourceLocation(IndustriaCore.MODID, "current_limited_voltage_source");
	public static final ResourceLocation JUNCTION_RESISTOR = new ResourceLocation(IndustriaCore.MODID, "junction_resistor");
	public static final ResourceLocation RESISTOR = new ResourceLocation(IndustriaCore.MODID, "resistor");
	
}
