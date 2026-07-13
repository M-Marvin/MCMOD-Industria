package de.m_marvin.industria.core.magnetism.engine;

import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.world.PhysLevel;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import de.m_marvin.industria.core.contraptions.engine.types.attachment.ForcesInducer;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.ServerContraption;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public final class MagneticForceInducer extends ForcesInducer {
	
	protected LongOpenHashSet fields = new LongOpenHashSet();

	public void addField(long fieldId) {
		this.fields.add(fieldId);
	}
	
	public void removeField(long fieldId) {
		this.fields.remove(fieldId);
	}

	public LongSet getFields() {
		return this.fields;
	}
	
	@Override
	public void physTick(PhysShip contraptionPhysics, PhysLevel contraptionLevel) {
		
		if (getLevel() == null) return;
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(this.getLevel(), Capabilities.MAGNETISM_HANDLER_CAPABILITY);

		ServerContraption contraption = getContraption();
		if (contraption == null) return;

		for (Long field1id : this.fields) {

			MagneticField field1 = handler.getField(field1id);
			if (field1 == null) continue;
			
			synchronized (handler.getMagneticFields()) {
				for (MagneticField field2 : handler.getMagneticFields()) {
					if (field2 != field1 && field1.isInEffectiveLinearRange(getLevel(), field2))
						field1.accumulateForces(getLevel(), (PhysShipImpl) contraptionPhysics, contraption, field2);
				}
			}
			
		}
	}
	
}
