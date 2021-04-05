package de.redtec.util.handler;

import java.util.ArrayList;
import java.util.List;

import de.redtec.dynamicsounds.SoundMSteamGeneratorLoop;
import de.redtec.dynamicsounds.SoundMachine;
import de.redtec.tileentity.TileEntityMSteamGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MachineSoundHelper {
	
	protected static List<SoundMachine> soundMap = new ArrayList<SoundMachine>();
	
	public static void startSoundIfNotRunning(TileEntity tileEntity, SoundEvent soundEvent) {
		
		SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
		
		SoundMachine sound = getSoundForTileEntity(tileEntity);
				
		if (sound == null || sound.isDonePlaying()) {
			
			sound = new SoundMachine(tileEntity, soundEvent);
			soundHandler.play(sound);
			soundMap.add(sound);
			
		}
		
	}
	
	public static void startSoundTurbinIfNotRunning(TileEntityMSteamGenerator tileEntity) {
		
		SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
		
		SoundMachine turbinSound = getSoundForTileEntity(tileEntity);
		
		if (turbinSound == null || turbinSound.isDonePlaying()) {
			
			turbinSound = new SoundMSteamGeneratorLoop(tileEntity);
			soundHandler.play(turbinSound);
			soundMap.add(turbinSound);
			
		}
		
	}
	
	public static SoundMachine getSoundForTileEntity(TileEntity tileEntity) {
		BlockPos tilePos = tileEntity.getPos();
		SoundMachine tileSound = null;
		for (SoundMachine sound : soundMap) {
			if (new BlockPos(sound.getX(), sound.getY(), sound.getZ()).equals(tilePos)) {
				tileSound = sound;
			}
		}
		return tileSound;
	}
	
}
