package de.m_marvin.industria.core.conduits.engine;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ConduitEvent extends Event {
	
    private final LevelAccessor level;
	private ConduitPos position;
	private PlacedConduit conduitState;
	
	public ConduitEvent(LevelAccessor level, ConduitPos position, PlacedConduit conduitState) {
		this.level = level;
		this.position = position;
		this.conduitState = conduitState;
	}
	
	public LevelAccessor getLevel() {
		return level;
	}
	
	public ConduitPos getPosition() {
		return position;
	}
	
	public PlacedConduit getConduitState() {
		return conduitState;
	}
	
	@Cancelable
	public static class ConduitBreakEvent extends ConduitEvent {
		
		public ConduitBreakEvent(LevelAccessor level, ConduitPos position, PlacedConduit conduitState) {
			super(level, position, conduitState);
		}
		
	}

	@Cancelable
	public static class ConduitPlaceEvent extends ConduitEvent {
		
		public ConduitPlaceEvent(LevelAccessor level, ConduitPos position, PlacedConduit conduitState) {
			super(level, position, conduitState);
		}
		
	}
	
}
