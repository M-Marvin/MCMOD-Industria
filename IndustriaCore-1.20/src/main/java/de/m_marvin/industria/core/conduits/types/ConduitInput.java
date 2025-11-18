package de.m_marvin.industria.core.conduits.types;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.Property;

public class ConduitInput {
	private final ConduitState state;
	private final Set<Property<?>> properties;
	@Nullable
	private final CompoundTag tag;

	public ConduitInput(ConduitState pState, Set<Property<?>> pProperties, @Nullable CompoundTag pTag) {
		this.state = pState;
		this.properties = pProperties;
		this.tag = pTag;
	}

	public ConduitState getState() {
		return this.state;
	}

	public Set<Property<?>> getDefinedProperties() {
		return this.properties;
	}
	
	public CompoundTag getTag() {
		return tag;
	}

	public boolean place(ServerLevel pLevel, ConduitPos pPos, float length) {
		if (ConduitUtility.setConduit(pLevel, pPos, getState(), length)) {
			Optional<ConduitEntity> conduitEntity = ConduitUtility.getConduit(pLevel, pPos);
			if (conduitEntity.isPresent() && this.tag != null) {
				conduitEntity.get().loadAdditional(getTag());
				ConduitUtility.triggerClientSync(pLevel, pPos);
			}
			return true;
		}
		return false;
	}
	
}