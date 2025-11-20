package de.m_marvin.industria.core.client.conduits;

import java.util.stream.Stream;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.util.FlywheelUtility;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.EffectVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.visual.AbstractVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Direction.Axis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Bus.MOD, value=Dist.CLIENT)
public class ConduitVisual<T extends ConduitEntity> extends AbstractVisual implements EffectVisual<ConduitEffect<T>>, SimpleDynamicVisual {
	
	private final T conduit;
	private final TransformedInstance[] segmentInstances;
	
	@SuppressWarnings("unchecked")
	public ConduitVisual(VisualizationContext ctx, T conduit, float partialTick) {
		super(ctx, conduit.getLevel(), partialTick);
		this.conduit = conduit;
		
		ConduitShape shape = conduit.getShape();
		if (shape == null) {
			this.segmentInstances = new TransformedInstance[0];
			return;
		}
		
		Model[] models = FlywheelUtility.modelsOfConduit(conduit);
		Instancer<TransformedInstance>[] instancers = Stream.of(models)
				.map(model -> ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, model))
				.toArray(Instancer[]::new);
		
		int segments = shape.nodes.length - 1;
		this.segmentInstances = new TransformedInstance[segments];
		for (int segment = 0; segment < segments; segment++)
			this.segmentInstances[segment] = instancers[segment % instancers.length].createInstance();
		
		updateShape(partialTick);
	}

	@Override
	public void beginFrame(Context ctx) {
		updateShape(ctx.partialTick());
	}
	
	protected void updateShape(float partialTick) {
		
		ConduitShape shape = conduit.getShape();
		if (shape == null) return;
		
		int segments = shape.nodes.length - 1;
		Vec3i renderOrigin = Vec3i.fromVec(renderOrigin());
		Vec3f nodeOrigin = new Vec3f(shape.shapeNodeA.min(shape.shapeNodeB));
		Vec3f nodeRenderOrigin = nodeOrigin.sub(renderOrigin);
		for (int segment = 0; segment < segments; segment++) {
			
			Vec3d node1 = shape.lastPos[segment + 0].lerp(shape.nodes[segment + 0], (double) partialTick);
			Vec3d node2 = shape.lastPos[segment + 1].lerp(shape.nodes[segment + 1], (double) partialTick);
			
			Vec3f position = new Vec3f(node1);
			Vec3f direction = new Vec3f(node2.sub(node1));
			position.addI(nodeRenderOrigin).subI(0.5F, 0.5F, 0.5F);
			
			float angleHorizontal = -(float) new Vec2f(direction.x, direction.z).angle(new Vec2f(0F, -1F));
			Vec2f directionProjection = new Vec2f((float) Math.sqrt(direction.x * direction.x + direction.z * direction.z), direction.y).normalize();
			float angleVertical = (float) directionProjection.angle(new Vec2f(1F, 0F));
			float scale = (float) (direction.length() / shape.segmentLength);
			
			this.segmentInstances[segment]
					.setIdentityTransform()
					.translate(direction.x / 2, direction.y / 2, direction.z / 2)
					.translate(position.x, position.y, position.z)
					.rotate(angleHorizontal, Axis.Y)
					.rotate(angleVertical, Axis.X)
					.scaleZ(scale)
					.setChanged();
			
			int light1 = LevelRenderer.getLightColor(conduit.getLevel(), MathUtility.toBlockPos(node1.add(nodeOrigin).sub(0.5, 0.5, 0.5)));
			int light2 = LevelRenderer.getLightColor(conduit.getLevel(), MathUtility.toBlockPos(node2.add(nodeOrigin).sub(0.5, 0.5, 0.5)));
			FlatLit.relight((light1 + light2) / 2, this.segmentInstances[segment]);
			
		}
		
	}
	
	@Override
	public void update(float partialTick) {}

	@Override
	protected void _delete() {
		for (TransformedInstance instance : this.segmentInstances)
			if (instance != null) instance.delete();
	}
	
}
