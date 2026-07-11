package de.m_marvin.industria.core.client.compound.renderers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.core.client.util.FlywheelUtility;
import de.m_marvin.industria.core.compound.engine.VirtualBlock;
import de.m_marvin.industria.core.compound.types.blockentities.CompoundBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.task.Plan;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.LightUpdatedVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.task.NestedPlan;
import dev.engine_room.flywheel.lib.task.PlanMap;
import dev.engine_room.flywheel.lib.task.RunnablePlan;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CompoundBlockEntityVisual extends AbstractBlockEntityVisual<CompoundBlockEntity> implements DynamicVisual, TickableVisual {
	
	protected class subVisual {
		
		private final VirtualBlock block;
		private BlockState lastState;
		private Optional<OrientedInstance> staticBlockInstance;
		private Optional<BlockEntityVisual<?>> dynamicBlockEntityVisual;
		private Optional<Plan<DynamicVisual.Context>> dynamicFramePlan = null;
		private Optional<Plan<TickableVisual.Context>> dynamicTickPlan = null;
		
		public subVisual(VirtualBlock block, Optional<OrientedInstance> staticBlockInstance, Optional<BlockEntityVisual<?>> dynamicBlockEntityVisual) {
			this.block = block;
			this.lastState = block.getState();
			this.staticBlockInstance = staticBlockInstance;
			this.dynamicBlockEntityVisual = dynamicBlockEntityVisual;
			if (this.dynamicBlockEntityVisual.isPresent() && this.dynamicBlockEntityVisual.get() instanceof DynamicVisual dynamicVisual)
				this.dynamicFramePlan = Optional.ofNullable(dynamicVisual.planFrame());
			else
				this.dynamicFramePlan = Optional.empty();
			if (this.dynamicBlockEntityVisual.isPresent() && this.dynamicBlockEntityVisual.get() instanceof TickableVisual tickableVisual)
				this.dynamicTickPlan = Optional.ofNullable(tickableVisual.planTick());
			else
				this.dynamicTickPlan = Optional.empty();
		}
		
		public void setup(float partialTick) {
			if (this.dynamicFramePlan.isPresent())
				CompoundBlockEntityVisual.this.partFramePlans.add(this, this.dynamicFramePlan.get());
			if (this.dynamicTickPlan.isPresent())
				CompoundBlockEntityVisual.this.partTickPlans.add(this, this.dynamicTickPlan.get());
			if (this.staticBlockInstance.isPresent())
				staticBlockInstance.get().position(getVisualPosition()).setChanged();
			updateLight(partialTick);
		}
		
		public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
			if (this.staticBlockInstance.isPresent())
				consumer.accept(this.staticBlockInstance.get());
			if (this.dynamicBlockEntityVisual.isPresent())
				this.dynamicBlockEntityVisual.get().collectCrumblingInstances(consumer);
		}
		
		public void updateLight(float partialTick) {
			if (this.staticBlockInstance.isPresent())
				CompoundBlockEntityVisual.this.relight(this.staticBlockInstance.get());
			if (this.dynamicBlockEntityVisual.isPresent() && this.dynamicBlockEntityVisual.get() instanceof LightUpdatedVisual lightedVisual)
				lightedVisual.updateLight(partialTick);
		}
		
		public void update(float partialTick) {
			if (this.dynamicBlockEntityVisual.isPresent())
				this.dynamicBlockEntityVisual.get().update(partialTick);
		}
		
		public void delete() {
			if (this.dynamicFramePlan.isPresent())
				CompoundBlockEntityVisual.this.partFramePlans.remove(this);
			if (this.dynamicTickPlan.isPresent())
				CompoundBlockEntityVisual.this.partTickPlans.remove(this);
			if (this.dynamicBlockEntityVisual.isPresent())
				this.dynamicBlockEntityVisual.get().delete();
			if (this.staticBlockInstance.isPresent())
				this.staticBlockInstance.get().delete();
		}
		
	}
	
	public static final Supplier<BlockEntityRenderDispatcher> BLOCK_ENTITY_RENDER_DISPATCHER = () -> Minecraft.getInstance().getBlockEntityRenderDispatcher();
	
	protected OrientedInstance fallbackInstance;
	protected Map<VirtualBlock, subVisual> parts = new HashMap<>();
	protected PlanMap<subVisual, DynamicVisual.Context> partFramePlans = new PlanMap<>();
	protected PlanMap<subVisual, TickableVisual.Context> partTickPlans = new PlanMap<>();

	public CompoundBlockEntityVisual(VisualizationContext ctx, CompoundBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
		
		for (var part : blockEntity.getParts().values())
			createSubVisual(part, partialTick);
		
		if (this.parts.isEmpty())
			createFallbackInstance();
	}
	
	protected void createFallbackInstance() {
		if (this.fallbackInstance != null) return;
		Model fallbackModel = FlywheelUtility.modelOfBlockForceBaked(blockEntity.getBlockState());
		this.fallbackInstance = instancerProvider().instancer(InstanceTypes.ORIENTED, fallbackModel).createInstance();
		this.fallbackInstance.position(getVisualPosition()).setChanged();
	}
	
	protected void removeFallbackInstance() {
		if (this.fallbackInstance == null) return;
		this.fallbackInstance.delete();
		this.fallbackInstance = null;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends BlockEntity> void createSubVisual(VirtualBlock virtualBlock, float partialTick) {
		
		Optional<OrientedInstance> staticBlockInstance = Optional.empty();
		Optional<BlockEntityVisual<?>> dynamicBlockEntityVisual = Optional.empty();
		if (virtualBlock.getState().getRenderShape() == RenderShape.MODEL) {
			Model blockModel = FlywheelUtility.modelOfBlock(virtualBlock.getState());
			staticBlockInstance = Optional.of(instancerProvider().instancer(InstanceTypes.ORIENTED, blockModel).createInstance());
		} else if (virtualBlock.getState().getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED) {
			T virtualBlockEntity = (T) virtualBlock.getBlockEntity();
			BlockEntityType<T> blockEntityType = (BlockEntityType<T>) virtualBlockEntity.getType();
			@Nullable BlockEntityVisualizer<? super T> blockEntityVisualizer = VisualizerRegistry.getVisualizer(blockEntityType);
			if (blockEntityVisualizer != null) {
				BlockEntityVisual<?> blockEntityVisual = blockEntityVisualizer.createVisual(this.visualizationContext, virtualBlockEntity, partialTick);
				dynamicBlockEntityVisual = Optional.of(blockEntityVisual);
			}
		} else {
			return;
		}
		
		subVisual subVisual = new subVisual(virtualBlock, staticBlockInstance, dynamicBlockEntityVisual);
		this.parts.put(virtualBlock, subVisual);
		subVisual.setup(partialTick);
		
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
		for (subVisual subVisual : this.parts.values()) {
			if (subVisual.staticBlockInstance.isPresent())
				consumer.accept(subVisual.staticBlockInstance.get());
			if (subVisual.dynamicBlockEntityVisual.isPresent());
				subVisual.dynamicBlockEntityVisual.get().collectCrumblingInstances(consumer);
		}
	}
	
	@Override
	public void updateLight(float partialTick) {
		for (subVisual subVisual : this.parts.values()) {
			if (subVisual.staticBlockInstance.isPresent())
				relight(subVisual.staticBlockInstance.get());
			if (subVisual.dynamicBlockEntityVisual.isPresent() && subVisual.dynamicBlockEntityVisual.get() instanceof LightUpdatedVisual dynamicLightVisual)
				dynamicLightVisual.updateLight(partialTick);
		}
		relight(this.fallbackInstance);
	}

	@Override
	public Plan<DynamicVisual.Context> planFrame() {
		return this.partFramePlans;
	}

	@Override
	public Plan<TickableVisual.Context> planTick() {
		return NestedPlan.of(this.partTickPlans, RunnablePlan.of(ctx -> {
			
			// TODO move this into update() and call from block entity
			// NOTE: requires some changes to how the compound block entities sync with the client trough the compound
			Iterator<subVisual> iter = this.parts.values().iterator();
			while (iter.hasNext()) {
				subVisual subVisual = iter.next();
				boolean wasRemoved = !this.blockEntity.getParts().containsValue(subVisual.block);
				boolean hasChanged = subVisual.staticBlockInstance.isPresent() && !subVisual.lastState.equals(subVisual.block.getState());
				if (wasRemoved || hasChanged) {
					subVisual.delete();
					iter.remove();
				}
			}
			for (VirtualBlock virtualBlock : this.blockEntity.getParts().values())
				if (!this.parts.containsKey(virtualBlock))
					createSubVisual(virtualBlock, 0F);
			
			if (this.fallbackInstance == null && this.parts.isEmpty())
				createFallbackInstance();
			else if (this.fallbackInstance != null && !this.parts.isEmpty())
				removeFallbackInstance();
			
		}));
	}
	
	@Override
	public void update(float partialTick) {
		for (subVisual subVisual : this.parts.values())
			subVisual.update(partialTick);
	}
	
	@Override
	protected void _delete() {
		for (subVisual subVisual : this.parts.values())
			subVisual.delete();
		this.parts.clear();
		if (this.fallbackInstance != null)
			removeFallbackInstance();
	}
	
}
