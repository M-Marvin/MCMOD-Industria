package de.m_marvin.industria.core.conduits.engine;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ConduitEvent extends Event {
	
    private final LevelAccessor level;
	private ConduitPos position;
	private ConduitEntity conduitState;
	
	public ConduitEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitState) {
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
	
	public ConduitEntity getConduitState() {
		return conduitState;
	}
	
	@Cancelable
	public static class ConduitBreakEvent extends ConduitEvent {
		
		public ConduitBreakEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitState) {
			super(level, position, conduitState);
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
		
	}

	@Cancelable
	public static class ConduitPlaceEvent extends ConduitEvent {
		
		public ConduitPlaceEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitState) {
			super(level, position, conduitState);
		}
		
		@Override
		public boolean isCancelable() {
			return true;
		}
		
	}
	
	public static class ConduitLoadEvent extends ConduitEvent {
		
		public ConduitLoadEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitState) {
			super(level, position, conduitState);
		}
		
	}

	public static class ConduitUnloadEvent extends ConduitEvent {

		public ConduitUnloadEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitState) {
			super(level, position, conduitState);
		}
		
	}
	
}
