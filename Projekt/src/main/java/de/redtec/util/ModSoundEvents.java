package de.redtec.util;

import de.redtec.RedTec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModSoundEvents {
	
	public static final SoundEvent BLOCK_HOVER_EXTENSION_ACTIVATED = register("block.hover_extension.activated");
	
	@SuppressWarnings("deprecation")
	private static SoundEvent register(String key) {
		return Registry.register(Registry.SOUND_EVENT, new ResourceLocation(RedTec.MODID, key), new SoundEvent(new ResourceLocation(RedTec.MODID, key)));
	}
	
}
