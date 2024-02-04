package de.m_marvin.industria.core.magnetism.engine;

import java.util.HashMap;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

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
	
//	public void updateForces(long id, Vec3d position, Vec3d linearForceAccumulated, Vec3d angularForceAccumulated) {
//		if (linearForceAccumulated.length() == 0 && angularForceAccumulated.length() == 0) {
//			this.forces.remove(id);
//		} else {
//			this.forces.put(id, new ForceSet(position, linearForceAccumulated, angularForceAccumulated));
//		}
//	}
	
	@Override
	public void applyForces(PhysShip contraptionPhysics) {
		
		if (getLevel() == null) return;
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(this.getLevel(), Capabilities.MAGNETISM_HANDLER_CAPABILITY);

		ServerShip contraption = (ServerShip) PhysicUtility.getContraptionById(level, contraptionPhysics.getId());
		
		for (Long field1id : this.fields) {
			
			MagneticField field1 = handler.getField(field1id);
			if (field1 == null) continue;
			
			for (MagneticField field2 : handler.getMagneticFields()) {
				
				Vec3d otherFieldVector = new Vec3d();
				
				if (field2 != field1 && field1.isInEffectiveLinearRange(this.level, field2)) {
					Vec3d interactingFieldVector = field2.getWorldFieldVectorInteracting(level, field1);
					otherFieldVector.addI(interactingFieldVector);
					System.out.println("-.------------- " + interactingFieldVector);
					//field1.accumulate(this.level, field2, linearVelocity, angularVelocity, mass, linearForces, angularForces);
				}
				
				field1.applyFieldForces(getLevel(), contraptionPhysics, contraption, otherFieldVector);
				
			}
			
//			if (linearForces.isFinite() && angularForces.isFinite() && relativeMagneticCenter.isFinite()) {
//
//				System.out.println("Apply " + angularForces + " - " + this);
//				
//				contraption.applyRotDependentForceToPos(new Vector3d(linearForces.x, linearForces.y, linearForces.z), new Vector3d(relativeMagneticCenter.x, relativeMagneticCenter.y, relativeMagneticCenter.z));
//				contraption.applyRotDependentTorque(new Vector3d(angularForces.x, angularForces.y, angularForces.z));
//				
//			} else {
//				IndustriaCore.LOGGER.warn("Non finite value in MFI detected!");
//			}
			
		}
		
//		for (MagneticField field : handler.getMagneticFields()) {
//			this.angularForceAccumulated.addI(field.ac)
//			
//			field.applyAccumulated(contraption);
//		}
		
//		for (ForceSet forces : this.forces.values()) {
//			
//			if (forces.linearForce.isFinite() && forces.angularForce.isFinite() && forces.position.isFinite()) {
//				contraption.applyRotDependentForceToPos(forces.linearForce, forces.position);
//				contraption.applyRotDependentTorque(forces.angularForce);
//			} else {
//				IndustriaCore.LOGGER.warn("Non finite value in MFI detected!");
//			}
//			
//		}
//		
//		this.forces.clear();
		
	}

}
