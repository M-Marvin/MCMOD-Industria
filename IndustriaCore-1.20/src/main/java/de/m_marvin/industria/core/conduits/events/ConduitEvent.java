package de.m_marvin.industria.core.conduits.events;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.util.types.EventStage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ConduitEvent extends Event {
	
    private final LevelAccessor level;
	private final ConduitPos position;
	private final ConduitEntity conduitEntity;
	private final EventStage stage;
	
	public ConduitEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity, EventStage stage) {
		this.level = level;
		this.position = position;
		this.conduitEntity = conduitEntity;
		this.stage = stage;
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
	
	public EventStage getStage() {
		return stage;
	}
	
	/**
	 * This event is fired on both client and server whenever an conduit is destroyed, usually either trough an player or an command.
	 * The removal will not happen if the event is canceled.
	 */
	@Cancelable
	public static class ConduitBreakEvent extends ConduitEvent {
		
		public boolean dropItems;
		
		public ConduitBreakEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity, EventStage stage, boolean dropItems) {
			super(level, position, conduitEntity, stage);
			this.dropItems = dropItems;
		}

		@Override
		public boolean isCancelable() {
			return this.getStage() == EventStage.PRE;
		}
		
	}

	/**
	 * This event is fired on both client and server whenever an conduit is placed, usually either trough an player or an command.
	 * The placement will not happen if the event is canceled.
	 */
	@Cancelable
	public static class ConduitPlaceEvent extends ConduitEvent {
		
		public ConduitPlaceEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity, EventStage stage) {
			super(level, position, conduitEntity, stage);
		}
		
		@Override
		public boolean isCancelable() {
			return this.getStage() == EventStage.PRE;
		}
		
	}
	
	/**
	 * This event is fired on client side only side when an conduit is loaded together with the chunk it is in from the an client (aka the chunk gets watched).
	 * Since conduits are always loaded on the server and can span multiple chunks, an compromise is made here:
	 * An conduit is assumed to belong to the chunk where the center between its two nodes lies, if this chunk loads on the client, the conduits is loaded as well.
	 * NOTE: For this event only the POST stage will ever fire.
	 */
	public static class ConduitWatchEvent extends ConduitEvent {
		
		private final ServerPlayer player;
		
		public ConduitWatchEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity, EventStage stage, ServerPlayer player) {
			super(level, position, conduitEntity, stage);
			this.player = player;
		}
		
		public ServerPlayer getPlayer() {
			return player;
		}
		
	}

	/**
	 * This event is fired on the client side only when an conduit is unloaded together with the chunk it is in from the an client (aka the chunk gets unwatched.
	 * Since conduits are always loaded on the server and can span multiple chunks, an compromise is made here:
	 * An conduit is assumed to belong to the chunk where the center between its two nodes lies, if this chunk unloads on the client, the conduits is unloaded as well.
	 * NOTE: For this event only the POST stage will ever fire.
	 */
	public static class ConduitUnwatchEvent extends ConduitEvent {

		private final ServerPlayer player;
		
		public ConduitUnwatchEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity, EventStage stage, ServerPlayer player) {
			super(level, position, conduitEntity, stage);
			this.player = player;
		}

		public ServerPlayer getPlayer() {
			return player;
		}
		
	}

	/**
	 * This event is fired on both client and server when an conduit is added to the list of loaded conduits.
	 * An conduit can be added either trough loading of new chunks on the client side, or trough placement by the player or commands.
	 * This event is guaranteed to fire for every conduit exactly once upon creation.
	 */
	public static class ConduitAddEvent extends ConduitEvent {
		
		public ConduitAddEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity, EventStage stage) {
			super(level, position, conduitEntity, stage);
		}
		
	}

	/**
	 * This event is fired on both client and server when an conduit is removed from the list of loaded conduits.
	 * An conduit can be removed either trough unloading of chunks on the client side, or trough removal by the player or commands.
	 * This event is guaranteed to fire for every conduit exactly once when it is removed or unloaded.
	 */
	public static class ConduitRemoveEvent extends ConduitEvent {

		public ConduitRemoveEvent(LevelAccessor level, ConduitPos position, ConduitEntity conduitEntity, EventStage stage) {
			super(level, position, conduitEntity, stage);
		}
		
	}
	
}
