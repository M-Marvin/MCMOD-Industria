package de.redtec.typeregistys;

import de.redtec.RedTec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModSoundEvents {
	
	public static final SoundEvent BLOCK_HOVER_EXTENSION_ACTIVATED = register("block.hover_extension.activated");
	public static final SoundEvent TURBIN_LOOP = register("block.turbin.loop");
	public static final SoundEvent PUMP_LOOP = register("block.pump.loop");
	public static final SoundEvent COMPRESSOR_LOOP = register("block.compressor.loop");
	public static final SoundEvent MACERATOR_LOOP = register("block.macerator.loop");
	public static final SoundEvent GENERATOR_LOOP = register("block.generator.loop");
	public static final SoundEvent MINER_LOOP = register("block.miner.loop");
	public static final SoundEvent TRANSFORMATOR_LOOP = register("block.transformator.loop");
	public static final SoundEvent SPARKING_CABLE = register("block.sparking_cable");
	public static final SoundEvent SCHREDDER_LOOP = register("block.schredder.loop");
	public static final SoundEvent BLENDER_LOOP = register("block.blender.loop");
	public static final SoundEvent RAFFINERY_LOOP = register("block.raffinery.loop");
	public static final SoundEvent THERMAL_ZENTRIFUGE_LOOP = register("block.thermal_zentrifuge.loop");
	public static final SoundEvent TREE_TAP_HARVEST = register("block.tree_tap.harvest");
	
	private static SoundEvent register(String key) {
		SoundEvent event = new SoundEvent(new ResourceLocation(RedTec.MODID, key));
		event.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.SOUND_EVENTS.register(event);
		return event;
	}
	
}
