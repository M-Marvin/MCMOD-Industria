package de.redtec.util.handler;

import java.util.HashMap;

import de.redtec.dynamicsounds.SoundMSteamGeneratorLoop;
import de.redtec.dynamicsounds.SoundMachine;
import de.redtec.tileentity.TileEntityMSteamGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MachineSoundHelper {
	
	protected static HashMap<TileEntity, SoundMachine> soundMap = new HashMap<TileEntity, SoundMachine>();
	
	public static void startSoundIfNotRunning(TileEntity tileEntity, SoundEvent soundEvent) {
		
		SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
		
		SoundMachine sound = soundMap.get(tileEntity);
				
		if (sound == null ? true : !soundHandler.isPlaying(sound)) {
			
			sound = new SoundMachine(tileEntity, soundEvent);
			soundHandler.play(sound);
			soundMap.put(tileEntity, sound);
			
		}
		
	}
	
	public static void startSoundTurbinIfNotRunning(TileEntityMSteamGenerator tileEntity) {
		
		SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
		
		SoundMachine turbinSound = soundMap.get(tileEntity);
		
		if (turbinSound == null ? true : !soundHandler.isPlaying(turbinSound)) {
			
			turbinSound = new SoundMSteamGeneratorLoop(tileEntity);
			soundHandler.play(turbinSound);
			
		}
		
	}
	
}
