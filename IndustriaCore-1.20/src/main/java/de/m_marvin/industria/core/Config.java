package de.m_marvin.industria.core;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.util.SingleBlockBatchedRenderer;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	
	public static ForgeConfigSpec CONFIG_COMMON;
	public static ForgeConfigSpec CONFIG_SERVER;
	public static ForgeConfigSpec CONFIG_CLIENT;
	
	public static final String CATEGORY_UTIL = "util";
	public static ForgeConfigSpec.BooleanValue EF_DEBUG_LOGGING;
	public static ForgeConfigSpec.IntValue MAX_SELECTION_BLOCKS;
	public static ForgeConfigSpec.BooleanValue USE_ALTERNATE_F3SCREEN;
	
	public static final String CATEGORY_MAGNETISM = "magnetics";
	public static ForgeConfigSpec.DoubleValue MAGNETIC_FORCE_MULTIPLIER_LINEAR;
	public static ForgeConfigSpec.DoubleValue MAGNETIC_FORCE_MULTIPLIER_ANGULAR;
	public static ForgeConfigSpec.DoubleValue MAGNETIC_FIELD_RANGE;
	public static ForgeConfigSpec.DoubleValue MAGNETIC_FIELD_CHANGE_NOTIFY_LIMIT;
	
	public static final String CATEGORY_ELECTIRCS = "electrics";
	public static ForgeConfigSpec.IntValue ELECTIRC_SIMULATION_THREADS;
	public static ForgeConfigSpec.ConfigValue<String> ELECTRIC_SIMULATION_COMMANDS;
	public static ForgeConfigSpec.IntValue ELECTRIC_NETWORK_TRACE_DEPTH;

	public static final String CATEGORY_KINETICS = "kinetics";
	public static ForgeConfigSpec.IntValue KINETIC_NETWORK_TRACE_DEPTH;
	
	public static final String CATEGORY_GRAPHICS = "graphics";
	public static ForgeConfigSpec.BooleanValue USE_SINGLE_BATCHED_BLOCK_RENDERER;
	
	static {
		
		/** client configuration - only on client **/
		
		ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
		BUILDER.comment("Industria Core Graphics Settings").push(CATEGORY_GRAPHICS);
		USE_SINGLE_BATCHED_BLOCK_RENDERER = BUILDER
				.comment("If true, the optimized renderer for animated block entities is used, this rendere can significantly increase FPS with many block entities such as gears.")
				.define("use_single_batched_renderer", true);
		BUILDER.pop();
		
		BUILDER.comment("Industria Core Misc Settings").push(CATEGORY_UTIL);
		USE_ALTERNATE_F3SCREEN = BUILDER
				.comment("If true, uses an alternative F3 debug screen with a more readable list of the debug entries, filtering out most of the useless information and adding some additonal ones.")
				.define("use_alternate_f3screen", true);
		CONFIG_CLIENT = BUILDER.build();
		
		/** common configuration - server and client **/
		
		BUILDER = new ForgeConfigSpec.Builder();
		BUILDER.comment("Industria Core Electrics Settings").push(CATEGORY_ELECTIRCS);
		ELECTRIC_NETWORK_TRACE_DEPTH = BUILDER
				.comment("When recomputing networks (spliting and combining), how deep the tracing algorithm should search, higher values might solve problems with large networks, but can cause significant lag.")
				.defineInRange("electric_network_trace_depth", 2024, 1000, 10000000);
		BUILDER.pop();
		
		BUILDER.comment("Industria Core Kinetics settings").push(CATEGORY_KINETICS);
		KINETIC_NETWORK_TRACE_DEPTH = BUILDER
				.comment("When recomputing networks (spliting and combining), how deep the tracing algorithm should search, higher values might solve problems with large networks, but can cause significant lag.")
				.defineInRange("kinetic_network_trace_depth", 2024, 1000, 10000000);
		CONFIG_COMMON = BUILDER.build();
		
		/** server configuration - only on server **/
		
		BUILDER = new ForgeConfigSpec.Builder();
		BUILDER.comment("Industria Core utility settings").push(CATEGORY_UTIL);
		MAX_SELECTION_BLOCKS = BUILDER
				.comment("Maximum number of blocks selectable (/template /contraption assemble)")
				.defineInRange("max_assemble_blocks", 16 * 16 * 16, 1, Integer.MAX_VALUE);
		BUILDER.pop();
		
		BUILDER.comment("Industria Core Electrics Settings").push(CATEGORY_ELECTIRCS);
		EF_DEBUG_LOGGING = BUILDER
				.comment("If true, the native lib will print simmulation data (and some other things) from the electric networks into the logs.")
				.define("simulation_debug_logging", false);
		ELECTIRC_SIMULATION_THREADS = BUILDER
				.comment("Number of paralell running EPT's")
				.defineInRange("simulation_threads", 1, 1, 16);
		ELECTRIC_SIMULATION_COMMANDS = BUILDER
				.comment("Simulation engine execution commands '|' seperated.")
				.define("simulation_exec_command", "options reltol=2|op");
		BUILDER.pop();
		
		BUILDER.comment("Industria Core Magnetism Settings").push(CATEGORY_MAGNETISM);
		MAGNETIC_FORCE_MULTIPLIER_LINEAR = BUILDER
				.comment("The field strength of the magnets gets multiplied with this value for the linear force applied to the magnets (double this, double the strength of all magnets)")
				.defineInRange("magnetic_force_multiplier_linear", MagneticField.DEFAULT_LINEAR_FORCE_MULTIPLIER, 0.0, Double.MAX_VALUE);
		MAGNETIC_FORCE_MULTIPLIER_ANGULAR = BUILDER
				.comment("The field strength of the magnets gets multiplied with this value for the angular force applied to the magnets (double this, double the strength of all magnets)")
				.defineInRange("magnetic_force_multiplier_angular", MagneticField.DEFAULT_ANGULAR_FORCE_MULTIPLIER, 0.0, Double.MAX_VALUE);
		MAGNETIC_FIELD_RANGE = BUILDER
				.comment("The range of magnetic fields in blocks per field strength")
				.defineInRange("magnetic_field_range", MagneticField.DEFAULT_MAGNETIC_FIELD_RANGE_PER_STRENGTH, 0.0, Double.MAX_VALUE);
		MAGNETIC_FIELD_CHANGE_NOTIFY_LIMIT = BUILDER
				.comment("The minumum ammount a filed has to change before an update is triggered")
				.defineInRange("magnetic_field_change_notify_limit", MagneticField.DEFAULT_FIELD_CHANGE_NOTIFY_LIMIT, 0.01, Double.MAX_VALUE);
		CONFIG_SERVER = BUILDER.build();
		
	}
	
	public static void register(FMLJavaModLoadingContext modctx) {
		modctx.registerConfig(Type.COMMON, CONFIG_COMMON);
		modctx.registerConfig(Type.SERVER, CONFIG_SERVER);
		modctx.registerConfig(Type.CLIENT, CONFIG_CLIENT);
	}
	
	@SubscribeEvent
	public static void onReload(ModConfigEvent.Reloading event) {
		if (CONFIG_SERVER.isLoaded()) MagneticField.reloadConfig();
		if (CONFIG_CLIENT.isLoaded()) SingleBlockBatchedRenderer.reloadConfig();
	}
	
}
