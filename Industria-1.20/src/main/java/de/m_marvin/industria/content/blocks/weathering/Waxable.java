package de.m_marvin.industria.content.blocks.weathering;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public interface Waxable {

	public static final Supplier<List<Waxable>> WAXABLES = () -> ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof Waxable).map(block -> (Waxable) block).toList();
	
	public Optional<BlockState> getWaxedState(BlockState state);
	public Optional<BlockState> getUnwaxedState(BlockState state);
	
}
