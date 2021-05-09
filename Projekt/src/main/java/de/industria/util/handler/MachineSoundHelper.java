package de.industria.util.handler;

import java.util.ArrayList;
import java.util.List;

import de.industria.dynamicsounds.SoundMSteamGeneratorLoop;
import de.industria.dynamicsounds.SoundMachine;
import de.industria.tileentity.TileEntityMSteamGenerator;
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
	
	public static boolean isPlayingMachineSound(SoundEvent sound, int maxDistance, BlockPos origin) {
		
		for (SoundMachine machineSound : soundMap) {
			if (machineSound.getSoundLocation() == sound.getName()) {
				double distance = origin.distanceSq(machineSound.getX(), machineSound.getY(), machineSound.getZ(), true);
				if (distance <= maxDistance && !machineSound.isDonePlaying()) {
					return true;
				}
			}
		}
		return false;
		
	}
	
	public static void startSoundIfNotRunning(TileEntity tileEntity, SoundEvent soundEvent) {
		
		SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
		
		SoundMachine sound = getSoundForTileEntity(tileEntity);
		
		if (sound == null || sound.isDonePlaying() || !soundHandler.isPlaying(sound)) {
			
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
		List<SoundMachine> soundsToRemove = new ArrayList<SoundMachine>();
		for (SoundMachine sound : soundMap) {
			if (new BlockPos(sound.getX(), sound.getY(), sound.getZ()).equals(tilePos)) {
				if (sound.isDonePlaying()) {
					soundsToRemove.add(sound);
				}
				tileSound = sound;
			}
		}
		soundMap.removeAll(soundsToRemove);
		return tileSound;
	}
	
}
