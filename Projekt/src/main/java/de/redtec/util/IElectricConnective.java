package de.redtec.util;

import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IElectricConnective {
	
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side);
	public int getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side);
	public default void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {}
	public boolean canConnect(Direction side, BlockState state);
	public DeviceType getDeviceType();
	
	public default ElectricityNetwork getNetwork(World worldIn, BlockPos pos) {
		
		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
		ElectricityNetwork network = handler.getNetwork(pos);
		
		return network;
		
	}
	
	public default void updateNetwork(World worldIn, BlockPos pos) {
		ElectricityNetworkHandler.getHandlerForWorld(worldIn).updateNetwork(worldIn, pos);
	}
	
	public static enum Voltage {
		
		NoLimit(-1),LowVoltage(24),NormalVoltage(230),HightVoltage(1000),ExtremVoltage(20000);
		
		protected int voltage;
		
		Voltage(int voltage) {
			this.voltage = voltage;
		}
		
		public int getVoltage() {
			return voltage;
		}
		
	}
	
	public static enum DeviceType {
		
		MASCHINE(),WIRE(),SWITCH();
		
		boolean canConnectWith(DeviceType type) {
			if (this == MASCHINE) return type == WIRE;
			if (this == SWITCH) return type == WIRE;
			return true;
		}
		
	}
	
}
