package de.m_marvin.industria.core;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	
	public static ForgeConfigSpec CONFIG_COMMON;
	public static ForgeConfigSpec CONFIG_SERVER;
	
	public static final String CATEGORY_UTIL = "util";
	public static ForgeConfigSpec.BooleanValue SPICE_DEBUG_LOGGING;
	
	public static final String CATEGORY_PHYSICS = "physics";
	public static ForgeConfigSpec.DoubleValue MAGNETIC_FORCE_MULTIPLIER_LINEAR;
	public static ForgeConfigSpec.DoubleValue MAGNETIC_FORCE_MULTIPLIER_ANGULAR;
	public static ForgeConfigSpec.DoubleValue MAGNETIC_FIELD_RANGE;
	public static ForgeConfigSpec.DoubleValue MAGNETIC_FIELD_CHANGE_NOTIFY_LIMIT;
	
	public static final String CATEGORY_ELECTIRCS = "electrics";
	public static ForgeConfigSpec.IntValue ELECTIRC_SIMULATION_THREADS;
	public static ForgeConfigSpec.ConfigValue<String> ELECTRIC_SIMULATION_COMMANDS;
	
	static {
		ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
		BUILDER.comment("Industria Core utility settings").push(CATEGORY_UTIL);
		SPICE_DEBUG_LOGGING = BUILDER.comment("If true, the native lib will print simmulation data (and some other things) from the electric networks into the logs.").define("simulation_debug_logging", false);
		BUILDER.pop();
		BUILDER.comment("Industria Core electrics settings").push(CATEGORY_ELECTIRCS);
		ELECTIRC_SIMULATION_THREADS = BUILDER.comment("Number of paralell running EPT's").defineInRange("simulation_threads", 2, 1, 16);
		ELECTRIC_SIMULATION_COMMANDS = BUILDER.comment("Simulation engine execution commands.").define("simulation_exec_commands", "op");
		CONFIG_COMMON = BUILDER.build();
		
		BUILDER = new ForgeConfigSpec.Builder();
		BUILDER.comment("Physics related settings").push(CATEGORY_PHYSICS);
		MAGNETIC_FORCE_MULTIPLIER_LINEAR = BUILDER.comment("The field strength of the magnets gets multiplied with this value for the linear force applied to the magnets (double this, double the strength of all magnets)").defineInRange("magnetic_force_multiplier_linear", MagneticField.DEFAULT_LINEAR_FORCE_MULTIPLIER, 0.0, Double.MAX_VALUE);
		MAGNETIC_FORCE_MULTIPLIER_ANGULAR = BUILDER.comment("The field strength of the magnets gets multiplied with this value for the angular force applied to the magnets (double this, double the strength of all magnets)").defineInRange("magnetic_force_multiplier_angular", MagneticField.DEFAULT_ANGULAR_FORCE_MULTIPLIER, 0.0, Double.MAX_VALUE);
		MAGNETIC_FIELD_RANGE = BUILDER.comment("The range of magnetic fields in blocks per field strength").defineInRange("magnetic_field_range", MagneticField.DEFAULT_MAGNETIC_FIELD_RANGE_PER_STRENGTH, 0.0, Double.MAX_VALUE);
		MAGNETIC_FIELD_CHANGE_NOTIFY_LIMIT = BUILDER.comment("The minumum ammount a filed has to change before an update is triggered").defineInRange("magnetic_field_change_notify_limit", MagneticField.DEFAULT_FIELD_CHANGE_NOTIFY_LIMIT, 0.01, Double.MAX_VALUE);
		CONFIG_SERVER = BUILDER.build();
	}
	
	public static void register() {
		ModLoadingContext.get().registerConfig(Type.COMMON, CONFIG_COMMON);
		ModLoadingContext.get().registerConfig(Type.SERVER, CONFIG_SERVER);
	}
	
	@SubscribeEvent
	public static void onReload(ModConfigEvent.Reloading event) {
		if (CONFIG_SERVER.isLoaded()) MagneticField.reloadConfig();
	}
	
}
