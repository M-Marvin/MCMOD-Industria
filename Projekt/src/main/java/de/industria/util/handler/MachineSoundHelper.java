package de.industria.util.handler;

import java.util.ArrayList;
import java.util.List;

import de.industria.dynamicsounds.SoundMSteamGeneratorLoop;
import de.industria.dynamicsounds.SoundMachine;
import de.industria.tileentity.TileEntityMSteamGenerator;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
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
	
	public static void resetSoundCache() {
		SoundHandler soundHandler = Minecraft.getInstance().getSoundManager();
		soundMap.forEach((sound) -> soundHandler.stop(sound));
		soundMap.clear();
	}
	
	public static void startSoundIfNotRunning(TileEntity tileEntity, SoundEvent soundEvent) {
		
		if (tileEntity instanceof ITESimpleMachineSound) {
			
			if (soundMap.size() > 2000) {
				System.err.println("SoundMap overloading! " + soundMap.size() + " sounds cached, reset cache!");
				resetSoundCache();
			}
			
			SoundMachine sound = getSoundForTileEntity(tileEntity);
			
			if (((ITESimpleMachineSound) tileEntity).isSoundRunning()) {
				
				SoundHandler soundHandler = Minecraft.getInstance().getSoundManager();
				
				if (sound == null || sound.isStopped() || !soundHandler.isActive(sound)) {
					
					if (sound == null) {
						sound = new SoundMachine(tileEntity, soundEvent);
						soundMap.add(sound);
					}
					soundHandler.play(sound);
					
				}
				
			} else {
				
				soundMap.remove(sound);
				
			}
			
		}
		
	}
	
	public static void startSoundTurbinIfNotRunning(TileEntityMSteamGenerator tileEntity) {
		
		if (soundMap.size() > 2000) {
			System.err.println("SoundMap overloading! " + soundMap.size() + " sounds cached, reset cache!");
			resetSoundCache();
		}
		
		SoundMachine sound = getSoundForTileEntity(tileEntity);
		
		if (tileEntity.accerlation > 0) {
			
			SoundHandler soundHandler = Minecraft.getInstance().getSoundManager();
			
			if (sound == null || sound.isStopped() || !soundHandler.isActive(sound)) {
				
				if (sound == null) {
					sound = new SoundMSteamGeneratorLoop(tileEntity);
					soundMap.add(sound);
				}
				soundHandler.play(sound);
				
			}
			
		} else {
			
			soundMap.remove(sound);
			
		}
		
	}
	
	public static SoundMachine getSoundForTileEntity(TileEntity tileEntity) {
		BlockPos tilePos = tileEntity.getBlockPos();
		for (SoundMachine sound : soundMap) {
			if (new BlockPos(sound.getX(), sound.getY(), sound.getZ()).equals(tilePos)) {
				return sound;
			}
		}
		return null;
	}
	
}
