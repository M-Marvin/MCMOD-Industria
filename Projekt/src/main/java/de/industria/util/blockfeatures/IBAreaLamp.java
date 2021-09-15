package de.industria.util.blockfeatures;

import java.util.HashMap;

import de.industria.typeregistys.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public interface IBAreaLamp {
	
	public boolean isLit(BlockState state, IWorldReader worldReader, BlockPos pos);
	
	@SuppressWarnings("deprecation")
	public default void updateLight(BlockState state, World world, BlockPos pos, IAreaLightSupplier areaLightSupplier) {
		if (this.isLit(state, world, pos)) {
			HashMap<BlockPos, Direction> newLights = areaLightSupplier.supply(state);
			newLights.entrySet().forEach((light) -> {
				BlockState areaLightState = ModItems.luminous_air.defaultBlockState().setValue(BlockStateProperties.FACING, light.getValue().getOpposite());
				BlockPos areaLightPos = light.getKey().offset(pos);
				BlockState replaceState = world.getBlockState(areaLightPos);
				if (replaceState.isAir() ) world.setBlock(areaLightPos, areaLightState, 2);
			});
			newLights.entrySet().forEach((light) -> {
				BlockPos areaLightPos = light.getKey().offset(pos);
				BlockState lightState = world.getBlockState(areaLightPos);
				if (!lightState.canSurvive(world, areaLightPos)) world.setBlock(areaLightPos, Blocks.AIR.defaultBlockState(), 2);
			});
		} else {
			if (world.getBlockState(pos.north()).getBlock() == ModItems.luminous_air) world.neighborChanged(pos.north(), state.getBlock(), pos);
			if (world.getBlockState(pos.south()).getBlock() == ModItems.luminous_air) world.neighborChanged(pos.south(), state.getBlock(), pos);
			if (world.getBlockState(pos.east()).getBlock() == ModItems.luminous_air) world.neighborChanged(pos.east(), state.getBlock(), pos);
			if (world.getBlockState(pos.west()).getBlock() == ModItems.luminous_air) world.neighborChanged(pos.west(), state.getBlock(), pos);
			if (world.getBlockState(pos.above()).getBlock() == ModItems.luminous_air) world.neighborChanged(pos.above(), state.getBlock(), pos);
			if (world.getBlockState(pos.below()).getBlock() == ModItems.luminous_air) world.neighborChanged(pos.below(), state.getBlock(), pos);
		}
	}
	
	@FunctionalInterface
	public static interface IAreaLightSupplier {
		public HashMap<BlockPos, Direction> supply(BlockState state);
	}
	
}
