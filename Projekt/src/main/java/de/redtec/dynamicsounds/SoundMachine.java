package de.redtec.dynamicsounds;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundMachine extends TickableSound {

	protected World world;
	protected BlockPos pos;
	
	public SoundMachine(TileEntity machine, SoundEvent soundIn) {
		super(soundIn, SoundCategory.BLOCKS);
		this.pos = machine.getPos();
		this.world = machine.getWorld();
		this.x = machine.getPos().getX();
		this.y = machine.getPos().getY();
		this.z = machine.getPos().getZ();
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 1F;
		this.pitch = 1F;
	}

	@Override
	public void tick() {
		
		TileEntity machine = this.world.getTileEntity(pos);
		
		if ((machine instanceof ISimpleMachineSound ? ((ISimpleMachineSound) machine).isSoundRunning() : false) && !machine.isRemoved()) {
			
			this.x = machine.getPos().getX();
			this.y = machine.getPos().getY();
			this.z = machine.getPos().getZ();
			
		} else {
			
			this.finishPlaying();
			
		}
		
	}

}
