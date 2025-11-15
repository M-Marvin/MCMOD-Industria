package de.m_marvin.industria.core.conduits.events;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ConduitEvent extends Event {
	
    private final LevelAccessor level;
	private final ConduitPos position;
	private final ConduitEntity conduitEntity;
	
	public ConduitEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity) {
		this.level = level;
		this.position = position;
		this.conduitEntity = conduitEntity;
	}
	
	public LevelAccessor getLevel() {
		return level;
	}
	
	public ConduitPos getPosition() {
		return position;
	}
	
	public ConduitEntity getConduitEntity() {
		return conduitEntity;
	}
	
	@Cancelable
	public static class ConduitBreakEvent extends ConduitEvent {
		
		public boolean dropItems;
		
		public ConduitBreakEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity, boolean dropItems) {
			super(level, position, conduitEntity);
			this.dropItems = dropItems;
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
		
	}

	@Cancelable
	public static class ConduitPlaceEvent extends ConduitEvent {
		
		public ConduitPlaceEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity) {
			super(level, position, conduitEntity);
		}
		
		@Override
		public boolean isCancelable() {
			return true;
		}
		
	}
	
	public static class ConduitLoadEvent extends ConduitEvent {
		
		public ConduitLoadEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity) {
			super(level, position, conduitEntity);
		}
		
	}

	public static class ConduitUnloadEvent extends ConduitEvent {

		public ConduitUnloadEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity) {
			super(level, position, conduitEntity);
		}
		
	}
	
}
