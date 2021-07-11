package de.industria.tileentity;

import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class TileEntityMElectricHeater extends TileEntityMHeaterBase {
	
	public TileEntityMElectricHeater() {
		super(ModTileEntityType.ELECTRIC_HEATER, 0);
	}
	
	@Override
	public void updateWorkState() {
		
		ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
		ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
		this.isWorking = network.canMachinesRun() == Voltage.HightVoltage;
		
	}
	
	@Override
	public boolean canWork() {
		return this.powered;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return false;
	}
	
}
