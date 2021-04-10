package de.redtec.tileentity;

import java.util.Random;

import de.redtec.typeregistys.ModTileEntityType;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEnderCore extends TileEntity {
	
	public float rotationProgress;
	
	public TileEntityEnderCore() {
		super(ModTileEntityType.ENDER_CORE);
		this.rotationProgress = new Random().nextFloat();
	}

}
