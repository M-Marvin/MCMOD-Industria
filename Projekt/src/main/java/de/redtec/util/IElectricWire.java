package de.redtec.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IElectricWire extends IElectricConnective {
	
	@Override
	default Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NoLimit;
	}
	
	@Override
	default int getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0;
	}
	
	@Override
	default boolean canConnect(Direction side, BlockState state) {
		return true;
	}
	
	@Override
	default DeviceType getDeviceType() {
		return DeviceType.WIRE;
	}
	
}
