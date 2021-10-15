package de.industria.tileentity;

import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.types.MultipartBuild.MultipartBuildLocation;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class TileEntityMElectricHeater extends TileEntityMHeaterBase {
	
	public TileEntityMElectricHeater() {
		super(ModTileEntityType.ELECTRIC_HEATER, 0);
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityMHeaterBase) tileEntity).isWorking = (boolean) data[0];
			if (data[1] != null) ((TileEntityMHeaterBase) tileEntity).powered = (boolean) data[1];
		}, () -> isWorking, () -> powered);
	}
	
	@Override
	public void updateWorkState() {
		
		ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
		ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
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
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return false;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("BuildData", this.buildData.writeNBT(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.buildData = MultipartBuildLocation.loadNBT(compound.getCompound("BuildData"));
		super.load(state, compound);
	}

	public MultipartBuildLocation buildData = MultipartBuildLocation.EMPTY;
	public void storeBuildData(MultipartBuildLocation buildData) {
		this.buildData = buildData;
	}

	public MultipartBuildLocation getBuildData() {
		return this.buildData;
	}
	
}
