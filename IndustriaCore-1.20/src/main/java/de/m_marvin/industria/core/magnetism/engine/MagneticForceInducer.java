package de.m_marvin.industria.core.magnetism.engine;

import java.util.HashMap;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.engine.ForcesInducer;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.univec.impl.Vec3d;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public class MagneticForceInducer extends ForcesInducer {
	
	protected class ForceSet {
		
		ForceSet(Vec3d position, Vec3d linearForce, Vec3d angularForce) {
			this.position = new Vector3d(position.x, position.y, position.z);
			this.angularForce = new Vector3d(angularForce.x, angularForce.y, angularForce.z);
			this.linearForce = new Vector3d(linearForce.x, linearForce.y, linearForce.z);
		}
		
		public Vector3d position;
		public Vector3d angularForce;
		public Vector3d linearForce;
		
	}
	
	protected LongOpenHashSet fields = new LongOpenHashSet();
	protected HashMap<Long, ForceSet> forces = new HashMap<Long, ForceSet>();
	
	public void addField(long fieldId) {
		this.fields.add(fieldId);
	}
	
	public void removeField(long fieldId) {
		this.fields.remove(fieldId);
		this.forces.remove(fieldId);
	}

	public LongSet getFields() {
		return this.fields;
	}
	
	@Override
	public void applyForces(PhysShip contraptionPhysics) {
		
		if (getLevel() == null) return;
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(this.getLevel(), Capabilities.MAGNETISM_HANDLER_CAPABILITY);

		ServerShip contraption = (ServerShip) PhysicUtility.getContraptionById(level, contraptionPhysics.getId());
		if (contraption == null) return;
		
		for (Long field1id : this.fields) {
			
			MagneticField field1 = handler.getField(field1id);
			if (field1 == null) continue;
			
			for (MagneticField field2 : handler.getMagneticFields()) {
				
				Vec3d otherFieldVector = new Vec3d();
				
				if (field2 != field1 && field1.isInEffectiveLinearRange(this.level, field2)) {
					Vec3d interactingFieldVector = field2.getWorldFieldVectorInteracting(level, field1);
					otherFieldVector.addI(interactingFieldVector);
					//System.out.println("-.------------- " + interactingFieldVector);
					//field1.accumulate(this.level, field2, linearVelocity, angularVelocity, mass, linearForces, angularForces);
				}
				
				if (otherFieldVector.length() > 0) {
					field1.applyFieldForces(getLevel(), (PhysShipImpl) contraptionPhysics, contraption, otherFieldVector);
				} else {
					//System.out.println("ONE OUT OF RANGE"); FIXME
				}
				
			}
			
		}
		
	}

}
