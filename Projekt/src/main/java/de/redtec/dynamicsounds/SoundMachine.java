package de.redtec.dynamicsounds;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundMachine extends TickableSound {
	
	protected TileEntity machine;
public SoundMachine(TileEntity machine, SoundEvent soundIn) {
		super(soundIn, SoundCategory.BLOCKS);
		this.machine = machine;
		this.x = machine.getPos().getX();
		this.y = machine.getPos().getY();
		this.z = machine.getPos().getZ();
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 1;
		this.pitch = 1;
	}

	@Override
	public void tick() {
		
		if ((this.machine instanceof ISimpleMachineSound ? ((ISimpleMachineSound) this.machine).isSoundRunning() : false) && !this.machine.isRemoved()) {
			
			this.x = machine.getPos().getX();
			this.y = machine.getPos().getY();
			this.z = machine.getPos().getZ();
			
		} else {
			
			this.func_239509_o_();
			
		}
		
	}

}
