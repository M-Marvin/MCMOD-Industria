package de.m_marvin.industria.core.contraptions.engine.types.contraption;

import org.valkyrienskies.core.api.ships.ClientShip;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientContraption extends Contraption {
	
	protected ClientShip ship;

	public ClientContraption(ClientShip ship) {
		this.ship = ship;
	}
	
	@Override
	public Level getLevel() {
		return Minecraft.getInstance().level;
	}
	
	@Override
	public ClientShip getShip() {
		return this.ship;
	}

}
