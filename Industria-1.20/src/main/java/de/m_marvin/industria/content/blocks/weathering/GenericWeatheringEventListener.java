package de.m_marvin.industria.content.blocks.weathering;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import de.m_marvin.industria.content.Industria;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent.BlockToolModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class GenericWeatheringEventListener {

	public static Map<Predicate<BlockState>, Function<BlockState, Optional<BlockState>>> nonInterfaceWaxables = new HashMap<>();

	public static void registerNonInterfaceWaxable(Predicate<BlockState> predicate, Function<BlockState, Optional<BlockState>> waxableFunction) {
		nonInterfaceWaxables.put(predicate, waxableFunction);
	}
	
	@SubscribeEvent
	public static void onItemUsedOnBlock(PlayerInteractEvent.RightClickBlock event) {
		
		Player player = event.getEntity();
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);
		ItemStack item = event.getItemStack();
		
		if (item.getItem() == Items.HONEYCOMB) {
			
			
			if (state.getBlock() instanceof Waxable waxable) {

				InteractionResult result = waxable.getWaxedState(state).map(waxedState -> {
			         if (player instanceof ServerPlayer) {
			            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, item);
			         }

			         item.shrink(1);
			         level.setBlock(pos, waxedState, 11);
			         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxedState));
			         level.levelEvent(player, 3003, pos, 0);
			         return InteractionResult.sidedSuccess(level.isClientSide);
				}).orElse(InteractionResult.PASS);
				
				if (result == InteractionResult.SUCCESS) {
					event.setCancellationResult(result);
				}
				
			} else {

				for (Predicate<BlockState> predicate : nonInterfaceWaxables.keySet()) {
					if (predicate.test(state)) {
						
						InteractionResult result = nonInterfaceWaxables.get(predicate).apply(state).map(waxedState -> {
					         if (player instanceof ServerPlayer) {
					            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, item);
					         }

					         item.shrink(1);
					         level.setBlock(pos, waxedState, 11);
					         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxedState));
					         level.levelEvent(player, 3003, pos, 0);
					         return InteractionResult.sidedSuccess(level.isClientSide);
						}).orElse(InteractionResult.PASS);

						if (result == InteractionResult.SUCCESS) {
							event.setCancellationResult(result);
						}
						
					}
				}
				
			}
			
		}
		
	}
	
	@SubscribeEvent
	public static void onToolUseOnBlock(BlockToolModificationEvent event) {
		
		BlockState state = event.getState();
		
		if (event.getToolAction() == ToolActions.AXE_WAX_OFF) {
			
			for (Waxable waxable : Waxable.WAXABLES.get()) {
				Optional<BlockState> unwaxedState = waxable.getUnwaxedState(state);
				if (unwaxedState.isPresent()) event.setFinalState(unwaxedState.get());
			}
			
		} else if (event.getToolAction() == ToolActions.AXE_SCRAPE) {
			
			if (state.getBlock() instanceof WeatheringNonVanilla weathering) {
				
				Optional<BlockState> scarpedState = weathering.getPreviousNonStatic(state);
				if (scarpedState.isPresent()) event.setFinalState(scarpedState.get());
				
			}
			
		}
		
	}
	
}
