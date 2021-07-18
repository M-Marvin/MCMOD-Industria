package de.industria.tileentity;

import de.industria.typeregistys.ModTileEntityType;
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
		if (!this.level.isClientSide) {
			this.getBlockState().getBlock().tick(this.getBlockState(), (ServerWorld) this.level, this.worldPosition, this.level.random);
		}
	}
	
}
