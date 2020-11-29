package de.redtec.util;

import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IElectricConnective {
	
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side);
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side);
	public default void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {}
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state);
	public DeviceType getDeviceType();
	public default boolean isSwitchClosed(World worldIn, BlockPos pos, BlockState state) {
		return true;
	}
	public default boolean beforNetworkChanges(World world, BlockPos pos, BlockState state, ElectricityNetwork network) {
		return false;
	}
	
	public default ElectricityNetwork getNetwork(World worldIn, BlockPos pos) {
		
		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
		ElectricityNetwork network = handler.getNetwork(pos);
		
		return network;
		
	}
	
	public default void updateNetwork(World worldIn, BlockPos pos) {
		ElectricityNetworkHandler.getHandlerForWorld(worldIn).updateNetwork(worldIn, pos);
	}
	
	public static enum Voltage implements IStringSerializable {
		
		NoLimit(-1, "no_limit"),LowVoltage(24, "low"),NormalVoltage(230, "normal"),HightVoltage(1000, "hight"),ExtremVoltage(20000, "extrem");
		
		protected int voltage;
		protected String name;
		
		Voltage(int voltage, String name) {
			this.voltage = voltage;
			this.name = name;
		}
		
		public int getVoltage() {
			return voltage;
		}
		
		public static Voltage byVoltageInt(int voltage) {
			if (voltage >= LowVoltage.getVoltage()) return LowVoltage;
			if (voltage >= NormalVoltage.getVoltage()) return NormalVoltage;
			if (voltage >= HightVoltage.getVoltage()) return HightVoltage;
			if (voltage >= ExtremVoltage.getVoltage()) return ExtremVoltage;
			return voltage <= 0 ? NoLimit : LowVoltage;
		}
		
		public static Voltage byName(String name) {
			if (name == LowVoltage.name()) return LowVoltage;
			if (name == NormalVoltage.name()) return NormalVoltage;
			if (name == HightVoltage.name()) return HightVoltage;
			if (name == ExtremVoltage.name()) return ExtremVoltage;
			return NoLimit;
		}

		@Override
		public String func_176610_l() {
			return this.name;
		}

		public Voltage next() {
			
			if (this == LowVoltage) {
				return NormalVoltage;
			} else if (this == NormalVoltage) {
				return HightVoltage;
			} else if (this == HightVoltage) {
				return ExtremVoltage;
			}
			return LowVoltage;
			
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
