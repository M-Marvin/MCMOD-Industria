package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.function.Supplier;

import de.m_marvin.industria.core.parametrics.BlockParametrics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IElectricInfoProvider {

	public double getVoltage(BlockState state, Level level, BlockPos pos);
	public double getPower(BlockState state, Level level, BlockPos pos);
	
	public BlockParametrics getParametrics(BlockState state, Level level, BlockPos pos);
	
	public default ElectricInfo getInfo(BlockState state, Level level, BlockPos pos) {
		return new ElectricInfo(
					() -> getVoltage(state, level, pos),
					() -> getPower(state, level, pos),
					() -> getParametrics(state, level, pos)
				);
	}
	
	public static record ElectricInfo(Supplier<Double> voltage, Supplier<Double> power, Supplier<BlockParametrics> parametrics) {}
	
}
