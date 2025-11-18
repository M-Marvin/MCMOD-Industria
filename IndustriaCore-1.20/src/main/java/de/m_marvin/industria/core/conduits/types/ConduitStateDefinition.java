package de.m_marvin.industria.core.conduits.types;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;

import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class ConduitState extends StateHolder<Conduit, ConduitState> {

	protected ConduitState(Conduit pOwner, ImmutableMap<Property<?>, Comparable<?>> pValues,
			MapCodec<ConduitState> pPropertiesCodec) {
		super(pOwner, pValues, pPropertiesCodec);
		// TODO Auto-generated constructor stub
	}
	
	
	
}
