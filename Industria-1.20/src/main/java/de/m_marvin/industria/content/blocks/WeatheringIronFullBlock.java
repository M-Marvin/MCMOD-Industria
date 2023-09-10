package de.m_marvin.industria.content.blocks;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.blocks.weathering.GenericWeatheringEventListener;
import de.m_marvin.industria.content.blocks.weathering.WeatheringIron;
import de.m_marvin.industria.content.registries.ModBlocks;
import de.m_marvin.industria.core.util.RandomTickSource;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
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
	
	public static void randomTickIronBlock(ServerLevel level, BlockPos pos, BlockState state) {
		((WeatheringIronFullBlock) ModBlocks.IRON_PLATES.get()).onRandomTick(state, level, pos, level.getRandom());
	}
	
	@SubscribeEvent
	public static void onServerSetup(FMLCommonSetupEvent event) {
		RandomTickSource.registerRandomTickTarget(state -> state.getBlock() == Blocks.IRON_BLOCK, () -> WeatheringIronFullBlock::randomTickIronBlock);
		GenericWeatheringEventListener.registerNonInterfaceWaxable(state -> state.getBlock() == Blocks.IRON_BLOCK, WeatheringIron::getWaxedStateStatic);
	}
	
}
