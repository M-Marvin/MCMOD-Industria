package de.m_marvin.industria.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
	
	
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec CONFIG;
	
	public static final String CATEGORY_UTIL = "util";
	public static ForgeConfigSpec.BooleanValue SPICE_DEBUG_LOGGING;
	
	static {
		BUILDER.comment("Industria Core utility settings").push(CATEGORY_UTIL);
		SPICE_DEBUG_LOGGING = BUILDER.comment("If true, the nglink native lib will print simmulation data (and some other things) from the electric networks into the logs.").define("spice_debug_logging", false);
		BUILDER.pop();
		CONFIG = BUILDER.build();
	}
	
	public static void register() {
		ModLoadingContext.get().registerConfig(Type.COMMON, CONFIG);
	}
	
}
