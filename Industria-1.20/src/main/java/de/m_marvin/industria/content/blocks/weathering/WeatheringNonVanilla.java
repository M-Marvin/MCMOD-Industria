package de.m_marvin.industria.content.blocks.weathering;

import java.util.Optional;

import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public interface WeatheringNonVanilla extends ChangeOverTimeBlock<WeatheringCopper.WeatherState>, Waxable {
	
	Optional<BlockState> getPreviousNonStatic(BlockState pState);
	
}
