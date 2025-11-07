package de.m_marvin.industria.core.client.compound.renderers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.core.client.util.FlywheelModels;
import de.m_marvin.industria.core.compound.engine.VirtualBlock;
import de.m_marvin.industria.core.compound.types.blockentities.CompoundBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.task.Plan;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.LightUpdatedVisual;
import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.task.RunnablePlan;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CompoundBlockEntityVisual extends AbstractBlockEntityVisual<CompoundBlockEntity> implements DynamicVisual {
	
	protected static class CompoundVisual {
		
		private final VirtualBlock block;
		private BlockState lastState;
		private Optional<OrientedInstance> staticBlockInstance;
		private Optional<BlockEntityVisual<?>> dynamicBlockEntityVisual;
		
		public CompoundVisual(VirtualBlock block, Optional<OrientedInstance> staticBlockInstance, Optional<BlockEntityVisual<?>> dynamicBlockEntityVisual) {
			this.block = block;
			this.lastState = block.getState();
			this.staticBlockInstance = staticBlockInstance;
			this.dynamicBlockEntityVisual = dynamicBlockEntityVisual;
		}
		
		public void delete() {
			if (this.dynamicBlockEntityVisual.isPresent())
				this.dynamicBlockEntityVisual.get().delete();
			if (this.staticBlockInstance.isPresent())
				this.staticBlockInstance.get().delete();
		}
		
	}
	
	public static final Supplier<BlockEntityRenderDispatcher> BLOCK_ENTITY_RENDER_DISPATCHER = () -> Minecraft.getInstance().getBlockEntityRenderDispatcher();
	
	protected Map<VirtualBlock, CompoundVisual> parts = new HashMap<>();

	public CompoundBlockEntityVisual(VisualizationContext ctx, CompoundBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
		
		for (var part : blockEntity.getParts().values())
			createCompoundVisual(part, partialTick);
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends BlockEntity> void createCompoundVisual(VirtualBlock virtualBlock, float partialTick) {
		
		Optional<OrientedInstance> staticBlockInstance = Optional.empty();
		Optional<BlockEntityVisual<?>> dynamicBlockEntityVisual = Optional.empty();
		if (virtualBlock.getState().getRenderShape() == RenderShape.MODEL) {
			Model blockModel = FlywheelModels.block(virtualBlock.getState());
			staticBlockInstance = Optional.of(instancerProvider().instancer(InstanceTypes.ORIENTED, blockModel).createInstance());
			staticBlockInstance.get().position(getVisualPosition()).setChanged();
			relight(staticBlockInstance.get());
		} else if (virtualBlock.getState().getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED) {
			T virtualBlockEntity = (T) virtualBlock.getBlockEntity();
			BlockEntityType<T> blockEntityType = (BlockEntityType<T>) virtualBlockEntity.getType();
			@Nullable BlockEntityVisualizer<? super T> blockEntityVisualizer = VisualizerRegistry.getVisualizer(blockEntityType);
			if (blockEntityVisualizer != null) {
				BlockEntityVisual<?> blockEntityVisual = blockEntityVisualizer.createVisual(this.visualizationContext, virtualBlockEntity, partialTick);
				dynamicBlockEntityVisual = Optional.of(blockEntityVisual);
				if (dynamicBlockEntityVisual.get() instanceof LightUpdatedVisual lightedVisual)
					lightedVisual.updateLight(partialTick);
			}
		} else {
			return;
		}
		
		CompoundVisual visualPart = new CompoundVisual(virtualBlock, staticBlockInstance, dynamicBlockEntityVisual);
		this.parts.put(virtualBlock, visualPart);
		
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
		for (CompoundVisual compoundVisual : this.parts.values()) {
			if (compoundVisual.staticBlockInstance.isPresent())
				consumer.accept(compoundVisual.staticBlockInstance.get());
			if (compoundVisual.dynamicBlockEntityVisual.isPresent());
				compoundVisual.dynamicBlockEntityVisual.get().collectCrumblingInstances(consumer);
		}
	}

	@Override
	public void updateLight(float partialTick) {
		for (CompoundVisual compoundVisual : this.parts.values()) {
			if (compoundVisual.staticBlockInstance.isPresent())
				relight(compoundVisual.staticBlockInstance.get());
			if (compoundVisual.dynamicBlockEntityVisual.isPresent() && compoundVisual.dynamicBlockEntityVisual.get() instanceof LightUpdatedVisual dynamicLightVisual)
				dynamicLightVisual.updateLight(partialTick);
		}
	}

	@Override
	public Plan<Context> planFrame() {
		// TODO fix frame plan code
		Plan<Context> plan = RunnablePlan.of((ctx) -> {
			Iterator<CompoundVisual> iter = this.parts.values().iterator();
			while (iter.hasNext()) {
				CompoundVisual compoundVisual = iter.next();
				boolean wasRemoved = !this.blockEntity.getParts().containsValue(compoundVisual.block);
				boolean hasChanged = compoundVisual.staticBlockInstance.isPresent() && !compoundVisual.lastState.equals(compoundVisual.block.getState());
				if (wasRemoved || hasChanged) {
					compoundVisual.delete();
					iter.remove();
				}
			}
			for (VirtualBlock virtualBlock : this.blockEntity.getParts().values())
				if (!this.parts.containsKey(virtualBlock))
					createCompoundVisual(virtualBlock, ctx.partialTick());
		});
		for (CompoundVisual compoundVisual : this.parts.values())
			if (compoundVisual.dynamicBlockEntityVisual.isPresent() && compoundVisual.dynamicBlockEntityVisual.get() instanceof DynamicVisual dynamicVisual)
				plan = plan.and(dynamicVisual.planFrame());
		return plan;
	}
	
	@Override
	public void update(float partialTick) {
		for (CompoundVisual compoundVisual : this.parts.values())
			if (compoundVisual.dynamicBlockEntityVisual.isPresent())
				compoundVisual.dynamicBlockEntityVisual.get().update(partialTick);
	}
	
	@Override
	protected void _delete() {
		for (CompoundVisual compoundVisual : this.parts.values())
			compoundVisual.delete();
		this.parts.clear();
	}
	
}
