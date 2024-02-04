package de.m_marvin.industria.core.physics.engine;

import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.minecraft.server.level.ServerLevel;

@SuppressWarnings("deprecation")
public abstract class ForcesInducer implements ShipForcesInducer {

	@JsonIgnore
	protected ServerLevel level;
	
	public void initLevel(ServerLevel level) {
		if (this.level == null) this.level = level;
	}
	
	public ServerLevel getLevel() {
		return level;
	}
	
}
