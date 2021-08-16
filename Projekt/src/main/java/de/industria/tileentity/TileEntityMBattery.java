package de.industria.tileentity;

import de.industria.blocks.BlockMBattery;
import de.industria.blocks.BlockMBattery.BatteryMode;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTileEntityType;
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
	
	public static final int MAX_STORAGE = 16000000;
	
	public float maxCurrent;
	public int storage;
	public Voltage voltage;
	
	public TileEntityMBattery() {
		super(ModTileEntityType.BATTERY);
		this.voltage = Voltage.LowVoltage;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.putInt("Storage", this.storage);
		nbt.putString("Voltage", this.voltage.getSerializedName());
		return super.save(nbt);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.storage = nbt.getInt("Storage");
		this.voltage = Voltage.byName(nbt.getString("Voltage"));
	}

	@Override
	public void tick() {
		if (!this.level.isClientSide()) {
			
			ElectricityNetworkHandler.getHandlerForWorld(this.level).updateNetwork(this.level, this.worldPosition);
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(this.level).getNetwork(worldPosition);
			this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			
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
	
	public int getStorage() {
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
		return Math.min(maxCurrent, capacity);
	}
	
	public float getDischargeCurrent() {
		return Math.min(maxCurrent, storage / (float) voltage.getVoltage());
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
