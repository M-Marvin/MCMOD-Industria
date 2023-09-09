package de.m_marvin.industria.content.blocks.weathering;

import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import de.m_marvin.industria.content.registries.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public interface WeatheringBrass extends WeatheringNonVanilla {
	
	Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
		return ImmutableBiMap.<Block, Block>builder()
				.put(ModBlocks.BRASS_BLOCK.get(), ModBlocks.EXPOSED_BRASS_BLOCK.get())
				.put(ModBlocks.EXPOSED_BRASS_BLOCK.get(), ModBlocks.WEATHERED_BRASS_BLOCK.get())
				.put(ModBlocks.WEATHERED_BRASS_BLOCK.get(), ModBlocks.OXIDIZED_BRASS_BLOCK.get())
				.put(ModBlocks.BRASS_PLATES.get(), ModBlocks.EXPOSED_BRASS_PLATES.get())
				.put(ModBlocks.EXPOSED_BRASS_PLATES.get(), ModBlocks.WEATHERED_BRASS_PLATES.get())
				.put(ModBlocks.WEATHERED_BRASS_PLATES.get(), ModBlocks.OXIDIZED_BRASS_PLATES.get())
				.build();
	});
	Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> {
		return NEXT_BY_BLOCK.get().inverse();
	});
	Supplier<BiMap<Block, Block>> WAXED_BY_BLOCK = Suppliers.memoize(() -> {
		return ImmutableBiMap.<Block, Block>builder()
				.put(ModBlocks.BRASS_BLOCK.get(), ModBlocks.WAXED_BRASS_BLOCK.get())
				.put(ModBlocks.EXPOSED_BRASS_BLOCK.get(), ModBlocks.WAXED_EXPOSED_BRASS_BLOCK.get())
				.put(ModBlocks.WEATHERED_BRASS_BLOCK.get(), ModBlocks.WAXED_WEATHERED_BRASS_BLOCK.get())
				.put(ModBlocks.OXIDIZED_BRASS_BLOCK.get(), ModBlocks.WAXED_OXIDIZED_BRASS_BLOCK.get())
				.put(ModBlocks.BRASS_PLATES.get(), ModBlocks.WAXED_BRASS_PLATES.get())
				.put(ModBlocks.EXPOSED_BRASS_PLATES.get(), ModBlocks.WAXED_EXPOSED_BRASS_PLATES.get())
				.put(ModBlocks.WEATHERED_BRASS_PLATES.get(), ModBlocks.WAXED_WEATHERED_BRASS_PLATES.get())
				.put(ModBlocks.OXIDIZED_BRASS_PLATES.get(), ModBlocks.WAXED_OXIDIZED_BRASS_PLATES.get())
				.build();
	});
	Supplier<BiMap<Block, Block>> UNWAXED_BY_BLOCK = Suppliers.memoize(() -> {
		return WAXED_BY_BLOCK.get().inverse();
	});
	
	@Override
	default Optional<BlockState> getWaxedState(BlockState state) {
		return Optional.ofNullable(WAXED_BY_BLOCK.get().get(state.getBlock())).map(block -> block.withPropertiesOf(state));
	}
	
	@Override
	default Optional<BlockState> getUnwaxedState(BlockState state) {
		return Optional.ofNullable(UNWAXED_BY_BLOCK.get().get(state.getBlock())).map(block -> block.withPropertiesOf(state));
	}
	
	static Optional<Block> getPrevious(Block pBlock) {
		return Optional.ofNullable(PREVIOUS_BY_BLOCK.get().get(pBlock));
	}
	
	static Block getFirst(Block pBlock) {
		Block block = pBlock;
	
		for(Block block1 = PREVIOUS_BY_BLOCK.get().get(pBlock); block1 != null; block1 = PREVIOUS_BY_BLOCK.get().get(block1)) {
			block = block1;
		}
	
		return block;
	}
	
	static Optional<BlockState> getPrevious(BlockState pState) {
		return getPrevious(pState.getBlock()).map((p_154903_) -> {
			return p_154903_.withPropertiesOf(pState);
		});
	}
	
	static Optional<Block> getNext(Block pBlock) {
		return Optional.ofNullable(NEXT_BY_BLOCK.get().get(pBlock));
	}
	
	static BlockState getFirst(BlockState pState) {
		return getFirst(pState.getBlock()).withPropertiesOf(pState);
	}
	
	default Optional<BlockState> getPreviousNonStatic(BlockState pState) {
		return getPrevious(pState.getBlock()).map((p_154903_) -> {
			return p_154903_.withPropertiesOf(pState);
		});
	}
	
	default Optional<BlockState> getNext(BlockState pState) {
		return getNext(pState.getBlock()).map((p_154896_) -> {
			return p_154896_.withPropertiesOf(pState);
		});
	}
	
	default float getChanceModifier() {
		return this.getAge() == WeatheringCopper.WeatherState.UNAFFECTED ? 0.75F : 1.0F;
	}
	
}
