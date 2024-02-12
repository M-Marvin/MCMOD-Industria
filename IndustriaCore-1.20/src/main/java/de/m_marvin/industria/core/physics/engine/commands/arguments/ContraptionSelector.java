package de.m_marvin.industria.core.physics.engine.commands.arguments;

import java.util.Collection;
import java.util.List;
import java.util.OptionalLong;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ContraptionSelector {
	
	public static final BiConsumer<Vec3, List<? extends Contraption>> ORDER_ARBITRARY = (p_261404_, p_261405_) -> {
	};
		
	private final int maxResults;
	private final boolean worldLimited;
	private final Predicate<Contraption> predicate;
	private final MinMaxBounds.Doubles range;
	private final Function<Vec3, Vec3> position;
	@Nullable
	private final AABB aabb;
	private final BiConsumer<Vec3, List<? extends Contraption>> order;
	private final boolean currentEntity;
	@Nullable
	private final String playerName;
	@Nullable
	private final OptionalLong contraptionId;
	private final boolean usesSelector;
	
	public ContraptionSelector(int pMaxResults, boolean pWorldLimited, Predicate<Contraption> predicate, MinMaxBounds.Doubles pRange, Function<Vec3, Vec3> pPositions, @Nullable AABB pAabb, BiConsumer<Vec3, List<? extends Contraption>> order, boolean pCurrentEntity, @Nullable String pPlayerName, @Nullable OptionalLong contraptionId, boolean pUsesSelector) {
		this.maxResults = pMaxResults;
		this.worldLimited = pWorldLimited;
		this.predicate = predicate;
		this.range = pRange;
		this.position = pPositions;
		this.aabb = pAabb;
		this.order = order;
		this.currentEntity = pCurrentEntity;
		this.playerName = pPlayerName;
		this.contraptionId = contraptionId;
		this.usesSelector = pUsesSelector;
	}

	   public int getMaxResults() {
	      return this.maxResults;
	   }

	   public boolean isSelfSelector() {
	      return this.currentEntity;
	   }

	   public boolean isWorldLimited() {
	      return this.worldLimited;
	   }

	   public boolean usesSelector() {
	      return this.usesSelector;
	   }

	public Long findSingleContraption() {
		return null;
	}
	
	public List<Contraption> findContraptions() {
		return null;
	}

	public Contraption findSingleEntity(CommandSourceStack source) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<? extends Contraption> findEntities(CommandSourceStack source) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
