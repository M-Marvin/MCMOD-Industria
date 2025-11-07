package de.m_marvin.industria.core.client.util;

import java.util.function.Supplier;

import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import dev.engine_room.flywheel.lib.util.RendererReloadCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

public class FlywheelModels {
	
	private static final Supplier<BlockRenderDispatcher> BLOCK_RENDER_DISPATCHER = () -> Minecraft.getInstance().getBlockRenderer();
	
	private static final RendererReloadCache<BlockState, Model> BLOCK_STATE = new RendererReloadCache<>(state -> {
		BakedModel bakedModel = BLOCK_RENDER_DISPATCHER.get().getBlockModel(state);
		return BakedModelBuilder.create(bakedModel).build();
	});
	
	public static Model blockForceBaked(BlockState state) {
		return BLOCK_STATE.get(state);
	}
	
	public static Model block(BlockState state) {
		return Models.block(state);
	}
	
}
