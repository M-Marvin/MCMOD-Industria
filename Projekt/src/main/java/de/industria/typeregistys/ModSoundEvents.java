package de.industria.typeregistys;

import de.industria.Industria;
import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
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
	
	public static final SoundEvent CARDBOARD_HIT = register("material.cardboard.hit");
	public static final SoundEvent CARDBOARD_PLACE = register("material.cardboard.place");
	public static final SoundEvent CARDBOARD_BREAK = register("material.cardboard.break");
	public static final SoundEvent CARDBOARD_STEP = register("material.cardboard.step");
	public static final SoundType CARDBOARD = new ForgeSoundType(3, 0.8F, () -> CARDBOARD_BREAK, () -> CARDBOARD_STEP, () -> CARDBOARD_PLACE, () -> CARDBOARD_HIT, () -> CARDBOARD_STEP);
	
	private static SoundEvent register(String key) {
		SoundEvent event = new SoundEvent(new ResourceLocation(Industria.MODID, key));
		event.setRegistryName(new ResourceLocation(Industria.MODID, key));
		ForgeRegistries.SOUND_EVENTS.register(event);
		return event;
	}
	
}
