package de.m_marvin.industria.core.registries;

import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.engine.NetworkElementManager;
import net.minecraft.resources.ResourceLocation;
import tvnlnna.nodal.NodalElement;

public class ElectricElements {
	
	public static final Supplier<NodalElement> RESISTOR = () -> NetworkElementManager.getInstance().getElement(ResourceLocation.tryBuild(IndustriaCore.MODID, "resistor"));
	public static final Supplier<NodalElement> INDUCTOR = () -> NetworkElementManager.getInstance().getElement(ResourceLocation.tryBuild(IndustriaCore.MODID, "inductor"));
	public static final Supplier<NodalElement> CAPACITOR = () -> NetworkElementManager.getInstance().getElement(ResourceLocation.tryBuild(IndustriaCore.MODID, "capacitor"));
	public static final Supplier<NodalElement> VOLTAGE_FIXED = () -> NetworkElementManager.getInstance().getElement(ResourceLocation.tryBuild(IndustriaCore.MODID, "voltage_independent"));
	public static final Supplier<NodalElement> CURRENT_FIXED = () -> NetworkElementManager.getInstance().getElement(ResourceLocation.tryBuild(IndustriaCore.MODID, "current_independent"));
	
}
