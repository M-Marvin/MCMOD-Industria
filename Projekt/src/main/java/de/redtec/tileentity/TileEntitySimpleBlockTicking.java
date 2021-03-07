package de.redtec.tileentity;

import de.redtec.typeregistys.ModTileEntityType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;

public class TileEntitySimpleBlockTicking extends TileEntity implements ITickableTileEntity {

	public TileEntitySimpleBlockTicking() {
		super(ModTileEntityType.SIMPLE_BLOCK_TICKING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		if (!this.world.isRemote) {
			this.getBlockState().getBlock().tick(this.getBlockState(), (ServerWorld) this.world, this.pos, this.world.rand);
		}
	}
	
}
