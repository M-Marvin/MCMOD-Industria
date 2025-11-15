package de.m_marvin.industria.core.client.conduits;

import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import dev.engine_room.flywheel.api.visual.Effect;
import dev.engine_room.flywheel.api.visual.EffectVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.minecraft.world.level.LevelAccessor;

public class ConduitEffect<T extends ConduitEntity> implements Effect {
	
	private final T conduit;
	
	public ConduitEffect(T conduit) {
		this.conduit = conduit;
	}
	
	@Override
	public LevelAccessor level() {
		return this.conduit.getLevel();
	}

	@Override
	public EffectVisual<?> visualize(VisualizationContext ctx, float partialTick) {
		return new ConduitVisual<T>(ctx, this.conduit, partialTick);
	}
	
}
