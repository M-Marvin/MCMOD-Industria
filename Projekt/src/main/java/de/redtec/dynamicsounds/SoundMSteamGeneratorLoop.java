package de.redtec.dynamicsounds;

import de.redtec.registys.ModSoundEvents;
import de.redtec.tileentity.TileEntityMSteamGenerator;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;

public class SoundMSteamGeneratorLoop extends TickableSound {

	protected TileEntityMSteamGenerator tileEntity;
	
	public SoundMSteamGeneratorLoop(TileEntityMSteamGenerator tileEntity) {
		super(ModSoundEvents.TURBIN_LOOP, SoundCategory.BLOCKS);
		this.tileEntity = tileEntity;
		this.x = tileEntity.getPos().getX();
		this.y = tileEntity.getPos().getY();
		this.z = tileEntity.getPos().getZ();
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.1F;
		this.pitch = 0.1F;
	}

	@Override
	public void tick() {
		
		if (tileEntity.accerlation > 0 && !tileEntity.isRemoved()) {
			
			this.pitch = this.tileEntity.accerlation / 20F * 2F;
			this.volume = this.tileEntity.accerlation / 20F;
			
			this.x = tileEntity.getPos().getX();
			this.y = tileEntity.getPos().getY();
			this.z = tileEntity.getPos().getZ();
			
		} else {
			
			this.func_239509_o_();
			
		}
		
	}

}
