package de.m_marvin.industria.core.client.util;

import java.util.function.Supplier;

import dev.engine_room.flywheel.api.backend.BackendManager;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import dev.engine_room.flywheel.lib.util.RendererReloadCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FlywheelUtility {
	
	private static final Supplier<BlockRenderDispatcher> BLOCK_RENDER_DISPATCHER = () -> Minecraft.getInstance().getBlockRenderer();
	
	private static final RendererReloadCache<BlockState, Model> BLOCK_STATE = new RendererReloadCache<>(state -> {
		BakedModel bakedModel = BLOCK_RENDER_DISPATCHER.get().getBlockModel(state);
		return BakedModelBuilder.create(bakedModel).build();
	});
	
	public static Model modelOfBlockForceBaked(BlockState state) {
		return BLOCK_STATE.get(state);
	}
	
	public static Model modelOfBlock(BlockState state) {
		return Models.block(state);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends BlockEntity> boolean hasFlywheelVisual(BlockEntity blockEntity) {
		BlockEntityType<T> blockEntityType = (BlockEntityType<T>) blockEntity.getType();
		BlockEntityVisualizer<? super T> visualizer = VisualizerRegistry.getVisualizer(blockEntityType);
		return visualizer != null && visualizer.skipVanillaRender((T) blockEntity);
	}
	
	public static boolean isFlywheelEnabled() {
		return BackendManager.currentBackend() != BackendManager.offBackend();
	}
	
}
