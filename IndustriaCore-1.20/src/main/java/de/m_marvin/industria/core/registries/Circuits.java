package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.resources.ResourceLocation;

public class Circuits {
	
	public static final double SHUNT_RESISTANCE = 0.001;
	
	public static final ResourceLocation CONSTANT_POWER_LOAD = new ResourceLocation(IndustriaCore.MODID, "constant_power_load");
	public static final ResourceLocation VOLTAGE_SOURCE = new ResourceLocation(IndustriaCore.MODID, "voltage_source");
	public static final ResourceLocation JUNCTION_RESISTOR = new ResourceLocation(IndustriaCore.MODID, "junction_resistor");
	public static final ResourceLocation RESISTOR = new ResourceLocation(IndustriaCore.MODID, "resistor");
	public static final ResourceLocation TRANSFORMER = new ResourceLocation(IndustriaCore.MODID, "transformer");
	
}
