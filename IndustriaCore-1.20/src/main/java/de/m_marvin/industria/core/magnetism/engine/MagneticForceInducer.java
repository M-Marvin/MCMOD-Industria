package de.m_marvin.industria.core.magnetism.engine;

import java.util.HashMap;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.physics.engine.ForcesInducer;
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

	public void updateForces(long id, Vec3d position, Vec3d linearForceAccumulated, Vec3d angularForceAccumulated) {
		if (linearForceAccumulated.length() == 0 && angularForceAccumulated.length() == 0) {
			this.forces.remove(id);
		} else {
			this.forces.put(id, new ForceSet(position, linearForceAccumulated, angularForceAccumulated));
		}
	}
	
	@Override
	public void applyForces(PhysShip contraption) {
		
		for (ForceSet forces : this.forces.values()) {
			
			if (forces.linearForce.isFinite() && forces.angularForce.isFinite() && forces.position.isFinite()) {
				//contraption.applyRotDependentForceToPos(forces.linearForce, forces.position);
				contraption.applyRotDependentTorque(forces.angularForce);
			} else {
				IndustriaCore.LOGGER.warn("Non finite value in MFI detected!");
			}
			
		}
		
	}

}
