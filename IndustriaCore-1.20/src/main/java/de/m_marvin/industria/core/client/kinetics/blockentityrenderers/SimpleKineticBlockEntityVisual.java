package de.m_marvin.industria.core.client.kinetics.blockentityrenderers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import de.m_marvin.industria.core.client.util.ClientTimer;
import de.m_marvin.industria.core.client.util.FlywheelModels;
import de.m_marvin.industria.core.kinetics.types.blockentities.IKineticBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blockentities.SimpleKineticBlockEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.InstancerProvider;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction.Axis;

public class SimpleKineticBlockEntityVisual extends AbstractBlockEntityVisual<SimpleKineticBlockEntity> implements SimpleDynamicVisual {

	protected static class VisualInstance {
		private TransformedInstance instance;
		private float lastAngle;
		public VisualInstance(TransformedInstance instance) {
			this.instance = instance;
			this.lastAngle = 0;
		}
	}
	
	private final Map<IKineticBlockEntity.CompoundPart, VisualInstance> instances = new HashMap<>();
	private final SimpleKineticBlockEntity blockEntity;
	private IKineticBlockEntity.CompoundPart[] lastVisuals;

	protected final Matrix4f baseTransform = new Matrix4f();

	public SimpleKineticBlockEntityVisual(VisualizationContext ctx, SimpleKineticBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
		this.blockEntity = blockEntity;
		this.lastVisuals = this.blockEntity.getVisualParts();
		
		float blockSpeed = (float) blockEntity.getRPM(0);
		for (IKineticBlockEntity.CompoundPart visualPart : blockEntity.getVisualParts())
			createInstance(ctx.instancerProvider(), visualPart, blockSpeed);
	}
	
	protected void createInstance(InstancerProvider instanceProvider, IKineticBlockEntity.CompoundPart visualPart, float blockSpeed) {
		Model model = FlywheelModels.blockForceBaked(visualPart.state());
		TransformedInstance instance = instanceProvider.instancer(InstanceTypes.TRANSFORMED, model).createInstance();
		instance.translate(getVisualPosition()).center();
		this.baseTransform.set(instance.pose);
		instance.uncenter().setChanged();
		VisualInstance visualInstance = new VisualInstance(instance);
		this.instances.put(visualPart, visualInstance);
		animate(visualPart, visualInstance, blockSpeed);
	}
		
	@Override
	protected void _delete() {
		for (VisualInstance instance : this.instances.values())
			instance.instance.delete();
		this.instances.clear();
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
		for (VisualInstance instance : this.instances.values())
			consumer.accept(instance.instance);
	}

	@Override
	public void updateLight(float partialTick) {
		for (VisualInstance instance : this.instances.values())
			relight(instance.instance);
	}
	
	public void animate(IKineticBlockEntity.CompoundPart visualPart, VisualInstance instance, float blockSpeed) {
		Axis axis = visualPart.rotationAxis();
		float speed = (float) visualPart.rotationRatio() * blockSpeed;
		float angle = (float) (ClientTimer.getRenderTicks() / 3000 * speed * 2 * Math.PI - visualPart.axialOffset());
		if (Math.abs(instance.lastAngle - angle) < 0.01) return;
		instance.instance.setTransform(baseTransform).rotate(angle, axis).uncenter().setChanged();
		instance.lastAngle = angle;
	}

	@Override
	public void beginFrame(Context ctx) {
		float blockSpeed = (float) this.blockEntity.getRPM(0);
		boolean partsChanged = !Arrays.equals(this.lastVisuals, this.blockEntity.getVisualParts());
		if (partsChanged) {
			_delete();
			for (IKineticBlockEntity.CompoundPart visualPart : blockEntity.getVisualParts())
				createInstance(instancerProvider(), visualPart, blockSpeed);
			this.lastVisuals = this.blockEntity.getVisualParts();
			updateLight(ctx.partialTick());
		}
		for (var visualPair : this.instances.entrySet())
			animate(visualPair.getKey(), visualPair.getValue(), blockSpeed);
	}

}