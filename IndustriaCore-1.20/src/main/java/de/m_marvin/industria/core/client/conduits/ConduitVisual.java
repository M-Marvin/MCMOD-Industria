package de.m_marvin.industria.core.client.conduits;

import de.m_marvin.industria.core.client.util.FlywheelUtility;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Blocks;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.EffectVisual;
import dev.engine_room.flywheel.api.visual.LightUpdatedVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.visual.AbstractVisual;
import net.minecraft.core.BlockPos;

public class ConduitVisual<T extends ConduitEntity> extends AbstractVisual implements EffectVisual<ConduitEffect<T>>, LightUpdatedVisual {
	
	private final T conduit;
	private final BlockPos pos;
	private final BlockPos visualPos;
	
	private final OrientedInstance testInstance;
	
	public ConduitVisual(VisualizationContext ctx, T conduit, float partialTick) {
		super(ctx, conduit.getLevel(), partialTick);
		this.conduit = conduit;
		this.pos = conduit.getPosition().getNodeApos();
		this.visualPos = this.pos.subtract(ctx.renderOrigin());
		
		Model model = FlywheelUtility.modelOfBlock(Blocks.ERROR_BLOCK.get().defaultBlockState());
		this.testInstance = ctx.instancerProvider().instancer(InstanceTypes.ORIENTED, model).createInstance();
		this.testInstance.position(getVisualPos()).setChanged();
	}
	
	public BlockPos getVisualPos() {
		return visualPos;
	}
	
	@Override
	public void update(float partialTick) {}

	@Override
	protected void _delete() {
		this.testInstance.delete();
	}

	@Override
	public void setSectionCollector(SectionCollector collector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLight(float partialTick) {
		// TODO Auto-generated method stub
		
	}
	
}
