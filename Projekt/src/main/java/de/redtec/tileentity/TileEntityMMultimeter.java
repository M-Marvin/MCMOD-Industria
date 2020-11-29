package de.redtec.tileentity;

import de.redtec.registys.ModTileEntityType;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;

public class TileEntityMMultimeter extends TileEntityGauge {
	
	public ElectricityNetwork network;
	
	public TileEntityMMultimeter() {
		super(ModTileEntityType.MULTIMETER);
		network = new ElectricityNetwork();
	}

	@Override
	public void tick() {
		if (!world.isRemote()) {
			
			this.network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			this.name = "V";
			this.value = 0.8F;
			
		}
	}
	
}
