package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IElectricInfoProvider {

	public double getVoltage(BlockState state, Level level, BlockPos pos);
	public double getPower(BlockState state, Level level, BlockPos pos);
	
	public int getNominalVoltage(BlockState state, Level level, BlockPos pos);
	public int getNominalPower(BlockState state, Level level, BlockPos pos);
	
	public float getVoltageTolerance(BlockState state, Level level, BlockPos pos);
	public float getPowerTolerance(BlockState state, Level level, BlockPos pos);
	
	public default ElectricInfo getInfo(BlockState state, Level level, BlockPos pos) {
		return new ElectricInfo(
					() -> getVoltage(state, level, pos),
					() -> getPower(state, level, pos),
					() -> getNominalVoltage(state, level, pos),
					() -> getNominalPower(state, level, pos),
					() -> getVoltageTolerance(state, level, pos),
					() -> getPowerTolerance(state, level, pos)
				);
	}
	
	public static record ElectricInfo(Supplier<Double> voltage, Supplier<Double> power, Supplier<Integer> targetVoltage, Supplier<Integer> targetPower, Supplier<Float> voltageTolerance, Supplier<Float> powerTolerance) {}
	
}
