package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.engine.BlockParametricsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IElectricInfoProvider {

	public double getVoltage(BlockState state, Level level, BlockPos pos);
	public default double getPower(BlockState state, Level level, BlockPos pos) {
		if (this instanceof IElectricBlock block) return Math.abs(block.getCurrentPower(level, ElectricReference.block(pos), state));
		return 0.0;
	}
	
	public default BlockPos getComponentPos(BlockState state, Level level, BlockPos pos) {
		return this instanceof IElectricBlock electric ? electric.getConnectorMasterPos(level, pos, state) : pos;
	}
	
	public default ElectricInfo getInfo(BlockState state, Level level, BlockPos pos) {
		BlockPos masterPos = getComponentPos(state, level, pos);
		BlockState masterState = masterPos.equals(pos) ? state : level.getBlockState(masterPos);
		return new ElectricInfo(
					() -> getVoltage(masterState, level, masterPos),
					() -> getPower(masterState, level, masterPos),
					() -> BlockParametricsManager.getInstance().getParametrics(state.getBlock()),
					masterPos
				);
	}
	
	public static record ElectricInfo(Supplier<Double> voltage, Supplier<Double> power, Supplier<BlockParametrics> parametrics, BlockPos componentPos) {}
	
}
