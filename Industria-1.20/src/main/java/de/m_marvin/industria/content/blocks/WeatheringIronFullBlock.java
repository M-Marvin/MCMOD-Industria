package de.m_marvin.industria.content.blocks;

import de.m_marvin.industria.content.blocks.weathering.WeatheringIron;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WeatheringIronFullBlock extends Block implements WeatheringIron {
	
	private final WeatheringCopper.WeatherState weatherState;

	public WeatheringIronFullBlock(WeatheringCopper.WeatherState pWeatherState, BlockBehaviour.Properties pProperties) {
		super(pProperties);
		this.weatherState = pWeatherState;
	}

	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		this.onRandomTick(pState, pLevel, pPos, pRandom);
	}

	public boolean isRandomlyTicking(BlockState pState) {
		return WeatheringIron.getNext(pState.getBlock()).isPresent();
	}

	public WeatheringCopper.WeatherState getAge() {
		return this.weatherState;
	}
	
	@SubscribeEvent
	public static void onRandomTick(TickEvent.LevelTickEvent event) {
		
		// TODO Random tick on iron block
		
	}
	
}
