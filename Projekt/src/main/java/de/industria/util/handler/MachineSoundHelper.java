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
			if (machineSound.getLocation() == sound.getLocation()) {
				double distance = origin.distSqr(machineSound.getX(), machineSound.getY(), machineSound.getZ(), true);
				if (distance <= maxDistance && !machineSound.isStopped()) {
					return true;
				}
			}
		}
		return false;
		
	}
	
	public static void startSoundIfNotRunning(TileEntity tileEntity, SoundEvent soundEvent) {
		
		SoundHandler soundHandler = Minecraft.getInstance().getSoundManager();
		
		SoundMachine sound = getSoundForTileEntity(tileEntity);
		
		if (sound == null || sound.isStopped() || !soundHandler.isActive(sound)) {
			
			sound = new SoundMachine(tileEntity, soundEvent);
			soundHandler.play(sound);
			soundMap.add(sound);
			
		}
		
	}
	
	public static void startSoundTurbinIfNotRunning(TileEntityMSteamGenerator tileEntity) {
		
		SoundHandler soundHandler = Minecraft.getInstance().getSoundManager();
		
		SoundMachine turbinSound = getSoundForTileEntity(tileEntity);
		
		if (turbinSound == null || turbinSound.isStopped()) {
			
			turbinSound = new SoundMSteamGeneratorLoop(tileEntity);
			soundHandler.play(turbinSound);
			soundMap.add(turbinSound);
			
		}
		
	}
	
	public static SoundMachine getSoundForTileEntity(TileEntity tileEntity) {
		BlockPos tilePos = tileEntity.getBlockPos();
		SoundMachine tileSound = null;
		List<SoundMachine> soundsToRemove = new ArrayList<SoundMachine>();
		for (SoundMachine sound : soundMap) {
			if (new BlockPos(sound.getX(), sound.getY(), sound.getZ()).equals(tilePos)) {
				if (sound.isStopped()) {
					soundsToRemove.add(sound);
				}
				tileSound = sound;
			}
		}
		soundMap.removeAll(soundsToRemove);
		return tileSound;
	}
	
}
