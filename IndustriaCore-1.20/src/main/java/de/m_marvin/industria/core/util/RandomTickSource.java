package de.m_marvin.industria.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RandomTickSource {
	
	private RandomTickSource() {}
	
	@FunctionalInterface
	public static interface RandomTickConsumer {
		public void onRandomTick(ServerLevel level, BlockPos pos, BlockState state);
	}
	
	private static Map<Predicate<BlockState>, Supplier<RandomTickConsumer>> tickConsumers = new HashMap<>();
	
	public static void registerRandomTickTarget(Predicate<BlockState> blockPredicate, Supplier<RandomTickConsumer> consumer) {
		tickConsumers.put(blockPredicate, consumer);
	}
	
	@SubscribeEvent
	public static void onRandomTick(TickEvent.LevelTickEvent event) {
		
		if (event.side == LogicalSide.SERVER && event.phase == Phase.END) {
			
			ServerLevel level = (ServerLevel) event.level;
			int randomTickSpeed = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
			
			if (randomTickSpeed > 0) {
			 	  
				ServerChunkCache chunkCache = level.getChunkSource();
				for(ChunkHolder chunkholder : chunkCache.chunkMap.getChunks()) {
					LevelChunk levelchunk = chunkholder.getTickingChunk();
					if (levelchunk != null && level.shouldTickBlocksAt(levelchunk.getPos().toLong())) {
					
						int i = levelchunk.getPos().getMinBlockX();
						int j = levelchunk.getPos().getMinBlockZ();
						LevelChunkSection[] alevelchunksection = levelchunk.getSections();

						for(int l = 0; l < alevelchunksection.length; ++l) {
							LevelChunkSection levelchunksection = alevelchunksection[l];
							if (levelchunksection.isRandomlyTicking()) {
								int j1 = levelchunk.getSectionYFromSectionIndex(l);
								int k1 = SectionPos.sectionToBlockCoord(j1);

								for(int l1 = 0; l1 < randomTickSpeed; ++l1) {
									BlockPos blockpos = level.getBlockRandomPos(i, k1, j, 15);
									BlockState blockstate = levelchunksection.getBlockState(blockpos.getX() - i, blockpos.getY() - k1, blockpos.getZ() - j);
									
									for (Predicate<BlockState> predicate : tickConsumers.keySet()) {
										if (predicate.test(blockstate)) tickConsumers.get(predicate).get().onRandomTick(level, blockpos, blockstate);
									}
								}
							}
						}
						
					}
				}
				
			}
			
		}
		
	}
	
}
