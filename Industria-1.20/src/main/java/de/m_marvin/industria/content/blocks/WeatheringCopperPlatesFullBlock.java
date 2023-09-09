package de.m_marvin.industria.content.blocks;

import de.m_marvin.industria.content.blocks.weathering.WeatheringCopperPlates;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperPlatesFullBlock extends Block implements WeatheringCopperPlates {
	
	private final WeatheringCopper.WeatherState weatherState;

	public WeatheringCopperPlatesFullBlock(WeatheringCopper.WeatherState pWeatherState, BlockBehaviour.Properties pProperties) {
		super(pProperties);
		this.weatherState = pWeatherState;
	}

	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		this.onRandomTick(pState, pLevel, pPos, pRandom);
	}

	public boolean isRandomlyTicking(BlockState pState) {
		return WeatheringCopperPlates.getNext(pState.getBlock()).isPresent();
	}

	public WeatheringCopper.WeatherState getAge() {
		return this.weatherState;
	}
	
}
