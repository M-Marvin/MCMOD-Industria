package de.m_marvin.industria.core.contraptions.engine.types.attachment;

import java.util.Optional;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.internal.world.VsiShipWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.contraptions.engine.ContraptionHandlerCapability;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.ServerContraption;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.server.level.ServerLevel;

public class ContraptionAttachment {
	
	@JsonProperty
	private Long contraptionId = -1L;
	
	@JsonIgnore
	private ServerContraption contraption;

	public void setContraption(ServerContraption contraption) {
		this.contraptionId = contraption.getId();
		this.contraption = contraption;
	}

	@JsonIgnore
	public ServerLevel getLevel() {
		if (getContraption() != null) {
			return this.contraption.getLevel();
		}
		return null;
	}
	
	public ServerContraption getContraption() {
		if (this.contraption == null) {
			
			if (ContraptionHandlerCapability.getStaticServer() == null) return null;
			VsiShipWorld shipWorld = VSGameUtilsKt.getShipObjectWorld(ContraptionHandlerCapability.getStaticServer());
			Optional<Ship> ship = shipWorld.getAllShips().stream().filter(s -> s.getId() == contraptionId).findAny();
			if (ship.isPresent() && ship.get() instanceof ServerShip sship) {
				ServerLevel level = VSGameUtilsKt.getLevelFromDimensionId(ContraptionHandlerCapability.getStaticServer(), ship.get().getChunkClaimDimension());
				ContraptionHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
				this.contraption = handler.getContraptionOfShip(sship);
			}
			
			IndustriaCore.LOGGER.warn("unable to initialize contraption attachment, ship not found: " + this.contraptionId);
		}
		return contraption;
	}
	
}
