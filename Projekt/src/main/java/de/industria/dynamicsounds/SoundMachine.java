package de.industria.dynamicsounds;

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
		this.pos = machine.getBlockPos();
		this.world = machine.getLevel();
		this.x = machine.getBlockPos().getX();
		this.y = machine.getBlockPos().getY();
		this.z = machine.getBlockPos().getZ();
		this.looping = true;
		this.delay = 0;
		this.volume = 1F;
		this.pitch = 1F;
	}

	@Override
	public void tick() {
		
		TileEntity machine = this.world.getBlockEntity(pos);
		
		if ((machine instanceof ISimpleMachineSound ? ((ISimpleMachineSound) machine).isSoundRunning() : false) && !machine.isRemoved()) {
			
			this.x = machine.getBlockPos().getX();
			this.y = machine.getBlockPos().getY();
			this.z = machine.getBlockPos().getZ();
			
		} else {
			
			this.stop();
			
		}
		
	}

}
