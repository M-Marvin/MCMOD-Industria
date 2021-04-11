package de.industria.util.blockfeatures;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IElectricWireBlock extends IElectricConnectiveBlock {
	
	@Override
	default Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NoLimit;
	}
	
	@Override
	default float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0;
	}
	
	@Override
	default boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return true;
	}
	
	@Override
	default DeviceType getDeviceType() {
		return DeviceType.WIRE;
	}
	
}
