package de.industria.tileentity;

import de.industria.blocks.BlockMBattery;
import de.industria.blocks.BlockMBattery.BatteryMode;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMBattery extends TileEntity implements ITickableTileEntity {
	
	public static final long MAX_STORAGE = 3000000000L;
	
	public float maxCurrent;
	public long storage;
	public Voltage voltage;
	
	public TileEntityMBattery() {
		super(ModTileEntityType.BATTERY);
		this.voltage = Voltage.LowVoltage;
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityMBattery) tileEntity).voltage = Voltage.valueOf((String) data[0]);
			if (data[1] != null) ((TileEntityMBattery) tileEntity).storage = (long) data[1];
			if (data[2] != null) ((TileEntityMBattery) tileEntity).maxCurrent = (float) data[2];
		}, () -> this.voltage.name(), () -> this.storage, () -> this.maxCurrent);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.putLong("Storage", this.storage);
		nbt.putString("Voltage", this.voltage.getSerializedName());
		return super.save(nbt);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.storage = nbt.getLong("Storage");
		this.voltage = Voltage.byName(nbt.getString("Voltage"));
	}

	@Override
	public void tick() {
		if (!this.level.isClientSide()) {
			
			ElectricityNetworkHandler.getHandlerForWorld(this.level).updateNetwork(this.level, this.worldPosition);
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(this.level).getNetwork(worldPosition);
			
			BlockState state = getBlockState();
			
			if (state.getBlock() == ModItems.battery) {
				
				if (this.storage == 0 && state.getValue(BlockMBattery.MODE) != BatteryMode.CHARGING) {
					this.voltage = Voltage.NoLimit;
					state = state.setValue(BlockMBattery.MODE, BatteryMode.IDLE);
				} else {
					state = state.setValue(BlockMBattery.MODE, BatteryMode.DISCHARGING);
				}
				
				boolean signal = this.level.getBestNeighborSignal(worldPosition) > 0;
				
				if (signal) {
					state = state.setValue(BlockMBattery.MODE, BatteryMode.CHARGING);
				}

				if (state != getBlockState()) level.setBlockAndUpdate(worldPosition, state);
				
				if (state.getValue(BlockMBattery.MODE) == BatteryMode.CHARGING) {
					if (network.getVoltage() == this.voltage && this.voltage != Voltage.NoLimit) {
						this.maxCurrent = (int) network.getCapacity();
						float chargeCurrent = getChargeCurrent();
						this.storage += Math.min(this.voltage.getVoltage() * chargeCurrent, MAX_STORAGE - storage);
					}
					if (network.getVoltage().getVoltage() > this.voltage.getVoltage()) this.voltage = network.getVoltage();
				} else if (state.getValue(BlockMBattery.MODE) == BatteryMode.DISCHARGING) {
					this.maxCurrent = Math.max(network.getNeedCurrent(), this.storage / this.voltage.getVoltage());
					float load = network.getGeneratorProductivity();
					float dischargeCurrent = load * (network.getNeedCurrent());
					this.storage -= Math.min(this.voltage.getVoltage() * dischargeCurrent, storage);
				}
				
			}
			
		}
	}
	
	public long getStorage() {
		return storage;
	}
	
	public Voltage getVoltage() {
		return voltage;
	}
	
	public boolean isEmpty() {
		return this.storage <= 0;
	}
	
	public float getChargeCurrent() {
		float capacity = (MAX_STORAGE - storage) / (float) voltage.getVoltage();
		return Math.min(Math.min(maxCurrent, capacity), 1000);
	}
	
	public float getDischargeCurrent() {
		return Math.min(Math.min(maxCurrent, storage / (float) voltage.getVoltage()), 1000);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getTag());
	}
	
}
