package de.industria.dynamicsounds;

import de.industria.tileentity.TileEntityMSteamGenerator;
import de.industria.typeregistys.ModSoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundMSteamGeneratorLoop extends SoundMachine {

	protected World world;
	protected BlockPos pos;
	
	public SoundMSteamGeneratorLoop(TileEntityMSteamGenerator tileEntity) {
		super(tileEntity, ModSoundEvents.TURBIN_LOOP);
		this.pos = tileEntity.getBlockPos();
		this.world = tileEntity.getLevel();
		this.x = tileEntity.getBlockPos().getX();
		this.y = tileEntity.getBlockPos().getY();
		this.z = tileEntity.getBlockPos().getZ();
		this.looping = true;
		this.delay = 0;
		this.volume = 0.1F;
		this.pitch = 0.1F;
	}

	@Override
	public void tick() {
		
		TileEntity te = this.world.getBlockEntity(pos);
		
		if (te instanceof TileEntityMSteamGenerator) {
			TileEntityMSteamGenerator tileEntity = (TileEntityMSteamGenerator) te;
			
			if (tileEntity.accerlation > 0) {
				
				this.pitch = tileEntity.accerlation / 20F * 2F;
				this.volume = tileEntity.accerlation / 20F;
				
				this.x = tileEntity.getBlockPos().getX();
				this.y = tileEntity.getBlockPos().getY();
				this.z = tileEntity.getBlockPos().getZ();
				
			} else {
				
				this.stop();
				
			}
			
		} else {
			
			this.stop();
			
		}
 		
	}

}
