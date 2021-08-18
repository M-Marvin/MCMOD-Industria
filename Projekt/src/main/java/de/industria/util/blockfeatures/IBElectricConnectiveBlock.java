package de.industria.util.blockfeatures;

import java.awt.Color;
import java.util.List;

import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBElectricConnectiveBlock {
	
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side);
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side);
	public default void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {}
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state);
	public DeviceType getDeviceType();
	public default boolean isSwitchClosed(World worldIn, BlockPos pos, BlockState state) {
		return true;
	}
	public default NetworkChangeResult beforNetworkChanges(World world, BlockPos pos, BlockState state, ElectricityNetwork network, int lap) {
		return NetworkChangeResult.CONTINUE;
	}
	public default List<BlockPos> getMultiBlockParts(World world, BlockPos pos, BlockState state) {
		return null;
	}
	
	public default ElectricityNetwork getNetwork(World worldIn, BlockPos pos) {
		
		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(worldIn);
		ElectricityNetwork network = handler.getNetwork(pos);
		
		return network;
		
	}
	
	public default void updateNetwork(World worldIn, BlockPos pos) {
		ElectricityNetworkHandler.getHandlerForWorld(worldIn).updateNetwork(worldIn, pos);
	}
	
	public static enum NetworkChangeResult {
		CONTINUE,RETRY,SKIPTICK;
	}
	
	public static enum Voltage implements IStringSerializable {
		
		NoLimit(-1, "no_limit"),LowVoltage(24, "low"),NormalVoltage(230, "normal"),HightVoltage(1000, "hight"),ExtremVoltage(20000, "extreme");
		
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
			if (name.equals(LowVoltage.getSerializedName())) return LowVoltage;
			if (name.equals(NormalVoltage.getSerializedName())) return NormalVoltage;
			if (name.equals(HightVoltage.getSerializedName())) return HightVoltage;
			if (name.equals(ExtremVoltage.getSerializedName())) return ExtremVoltage;
			return NoLimit;
		}

		@Override
		public String getSerializedName() {
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

		public Color getRenderColor() {
			switch (this) {
			case LowVoltage: return new Color(0, 255, 255);
			case NormalVoltage: return new Color(0, 255, 0);
			case HightVoltage: return new Color(255, 255, 0);
			case ExtremVoltage: return new Color(255, 0, 0);
			default: return new Color(255, 255, 255);
			}
		}
		
	}
	
	public static enum DeviceType {
		
		MACHINE(),WIRE(),SWITCH();
		
		public boolean canConnectWith(DeviceType type) {
			if (this == MACHINE) return type == WIRE;
			if (this == SWITCH) return type == WIRE;
			return true;
		}
		
	}
	
}
