package de.m_marvin.industria.util;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
	
	
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec CONFIG;
	
	public static final  String CATAGORY_TEST = "test";
	public static ForgeConfigSpec.IntValue ELECTRIC_MOTOR_STRESS;
	
	static {
		BUILDER.comment("Test settings").push(CATAGORY_TEST);
		ELECTRIC_MOTOR_STRESS = BUILDER.comment("Motor stress capacity").defineInRange("motor_stress", 8000, 1, Integer.MAX_VALUE);
		BUILDER.pop();
		
		CONFIG = BUILDER.build();
	}
	
	public static void loadConfig(Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path)
			.sync().autosave().writingMode(WritingMode.REPLACE).build();
		configData.load();
		CONFIG.setConfig(configData);
	}
	
}
