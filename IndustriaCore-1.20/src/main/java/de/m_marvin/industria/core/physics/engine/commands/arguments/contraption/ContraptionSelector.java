package de.m_marvin.industria.core.physics.engine.commands.arguments.contraption;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.valkyrienskies.core.api.ships.Ship;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.types.Contraption;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ContraptionSelector {
	
	public static final BiConsumer<Vec3, List<? extends Contraption>> ORDER_ARBITRARY = (p_261404_, p_261405_) -> {
	};
		
	private final int maxResults;
	private final boolean worldLimited;
	private final Predicate<Contraption> predicate;
	private final MinMaxBounds.Doubles range;
	private final MinMaxBounds.Doubles mass;
	private final MinMaxBounds.Doubles size;
	private final Optional<Boolean> isStatic;
	private final Function<Vec3, Vec3> position;
	@Nullable
	private final AABB aabb;
	private final BiConsumer<Vec3, List<? extends Contraption>> order;
	private final boolean currentContraption;
	@Nullable
	private final String contraptionName;
	@Nullable
	private final OptionalLong contraptionId;
	private final boolean usesSelector;
	 
	public ContraptionSelector(int pMaxResults, boolean pWorldLimited, Predicate<Contraption> predicate, MinMaxBounds.Doubles pRange, MinMaxBounds.Doubles pMass, MinMaxBounds.Doubles pSize, Optional<Boolean> pStatic, Function<Vec3, Vec3> pPositions, @Nullable AABB pAabb, BiConsumer<Vec3, List<? extends Contraption>> order, boolean pCurrentContraption, @Nullable String pContraptionName, @Nullable OptionalLong contraptionId, boolean pUsesSelector) {
		this.maxResults = pMaxResults;
		this.worldLimited = pWorldLimited;
		this.predicate = predicate;
		this.range = pRange;
		this.mass = pMass;
		this.size = pSize;
		this.isStatic = pStatic;
		this.position = pPositions;
		this.aabb = pAabb;
		this.order = order;
		this.currentContraption = pCurrentContraption;
		this.contraptionName = pContraptionName;
		this.contraptionId = contraptionId;
		this.usesSelector = pUsesSelector;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public boolean isWorldLimited() {
		return worldLimited;
	}

	public Predicate<Contraption> getPredicate() {
		return predicate;
	}

	public MinMaxBounds.Doubles getRange() {
		return range;
	}

	public MinMaxBounds.Doubles getMass() {
		return mass;
	}

	public MinMaxBounds.Doubles getSize() {
		return size;
	}

	public Optional<Boolean> getIsStatic() {
		return isStatic;
	}

	public Function<Vec3, Vec3> getPosition() {
		return position;
	}

	public AABB getAabb() {
		return aabb;
	}

	public BiConsumer<Vec3, List<? extends Contraption>> getOrder() {
		return order;
	}

	public boolean isCurrentContraption() {
		return currentContraption;
	}

	public String getContraptionName() {
		return contraptionName;
	}

	public OptionalLong getContraptionId() {
		return contraptionId;
	}

	public boolean isUsesSelector() {
		return usesSelector;
	}
	
	private Predicate<Contraption> getPredicateWithAdditonal(Vec3 position) {
		Predicate<Contraption> predicate = this.predicate;
		if (this.aabb != null) {
			AABB aabb = this.aabb.move(position);
			predicate = predicate.and((contraption) -> {
				return aabb.intersects(contraption.getWorldBounds());
			});
		}
		
		if (!this.range.isAny()) {
			predicate = predicate.and((contraption) -> {
				return this.range.matchesSqr(position.distanceToSqr(contraption.getPosition().getPosition().writeTo(new Vec3(0, 0, 0))));
			});
		}
		
		if (!this.mass.isAny()) {
			predicate = predicate.and((contraption) -> {
				return this.mass.matches(contraption.getMass());
			});
		}

		if (!this.size.isAny()) {
			predicate = predicate.and((contraption) -> {
				return this.size.matches(contraption.getSize());
			});
		}

		if (this.isStatic.isPresent()) {
			predicate = predicate.and((contraption) -> {
				return this.isStatic.get() == contraption.isStatic();
			});
		}
		
		return predicate;
	}
	
	private void checkPermissions(CommandSourceStack pSource) throws CommandSyntaxException {
		if (this.usesSelector && !net.minecraftforge.common.ForgeHooks.canUseEntitySelectors(pSource)) {
			throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
		}
	}

	public Contraption findSingleContraption(CommandSourceStack source) throws CommandSyntaxException {
		this.checkPermissions(source);
		List<Contraption> list = findContraptions(source);
		if (list.isEmpty()) {
			throw ContraptionArgument.NO_CONTRAPTIONS_FOUND.create();
		} else if (list.size() > 1) {
			throw ContraptionArgument.ERROR_NOT_SINGLE_CONTRAPTION.create();
		} else {
			return list.get(0);
		}
	}
	
	private void addContraptions(List<Contraption> list, ServerLevel level, Vec3 position, Predicate<Contraption> predicate) {
		int i = this.order == ORDER_ARBITRARY ? this.maxResults : Integer.MAX_VALUE;
		if (list.size() < i) {
			list.addAll(Contraption.fromShipListLevelFiltered(level, PhysicUtility.getLoadedContraptions(level)).stream().filter(predicate).toList());
		}
	}
	
	public List<Contraption> findContraptions(CommandSourceStack source) throws CommandSyntaxException {
		this.checkPermissions(source);
		
		if (this.contraptionName != null) {
			return Lists.newArrayList(Contraption.fromShipList(source.getLevel(), PhysicUtility.getContraptionsWithName(source.getLevel(), this.contraptionName)));
		} else if (this.contraptionId.isPresent()) {
			Ship s = PhysicUtility.getContraptionById(source.getLevel(), this.contraptionId.getAsLong());
			Contraption contraption = s != null ? new Contraption(source.getLevel(), s) : null;
			return contraption == null ? Collections.emptyList() : Lists.newArrayList(contraption);
		} else {
			
			Vec3 position = this.position.apply(source.getPosition());
			Predicate<Contraption> predicate = this.getPredicateWithAdditonal(position);
			
			if (this.currentContraption) {
				Ship s = PhysicUtility.getContraptionOfBlock(source.getLevel(), BlockPos.containing(source.getPosition().x, source.getPosition().y, source.getPosition().z));
				Contraption contraption = s != null ? new Contraption(source.getLevel(), s) : null;
				return contraption == null ? Collections.emptyList() : Lists.newArrayList(contraption);
			} else {
				
				List<Contraption> list = Lists.newArrayList();
				if (this.isWorldLimited()) {
					this.addContraptions(list, source.getLevel(), position, predicate);
				} else {
					for (ServerLevel serverLevel : source.getServer().getAllLevels()) {
						this.addContraptions(list, serverLevel, position, predicate);
					}
				}
				return this.sortAndLimit(position, list);
			}
		}
		
	}
	
	private List<Contraption> sortAndLimit(Vec3 position, List<Contraption> contraptions) {
		if (contraptions.size() > 1) {
			this.order.accept(position, contraptions);
		}
		return contraptions.subList(0, Math.min(this.maxResults, contraptions.size()));
	}
	
}
