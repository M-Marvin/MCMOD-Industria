package de.redtec.util;

import de.redtec.RedTec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModSoundEvents {
	
	public static final SoundEvent BLOCK_HOVER_EXTENSION_ACTIVATED = register("block.hover_extension.activated");
	public static final SoundEvent MACHINE_START = register("block.machine.start");
	public static final SoundEvent MACHINE_LOOP = register("block.machine.loop");
	public static final SoundEvent MACHINE_TOP = register("block.machine.stop");
	public static final SoundEvent MACHINE_INTERRUPT = register("block.machine.interrupt");
	public static final SoundEvent MACHINE_OVERLOAD = register("block.machine.overload");
	public static final SoundEvent PUMP_LOOP = register("block.pump.loop");
	public static final SoundEvent COMPRESSOR_LOOP = register("block.compressor.loop");
	public static final SoundEvent MACERATOR_LOOP = register("block.macerator.loop");
	public static final SoundEvent GENERATOR_LOOP = register("block.generator.loop");
	public static final SoundEvent MINER_LOOP = register("block.miner.loop");
	public static final SoundEvent TRANSFORMATOR_LOOP = register("block.transformator.loop");
	public static final SoundEvent SPARKING_CABLE = register("block.sparking_cable");
	
	@SuppressWarnings("deprecation")
	private static SoundEvent register(String key) {
		return Registry.register(Registry.SOUND_EVENT, new ResourceLocation(RedTec.MODID, key), new SoundEvent(new ResourceLocation(RedTec.MODID, key)));
	}
	
}
