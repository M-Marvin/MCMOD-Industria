package de.m_marvin.industria.core.physics.engine;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.api.ShipForcesInducer;

import de.m_marvin.industria.Industria;
import de.m_marvin.univec.api.IVector3;

@SuppressWarnings("deprecation")
public class ContraptionForceInducer implements ShipForcesInducer {
	
	private List<Force> forcesToApply = new ArrayList<>();
	
	public static ContraptionForceInducer getOrCreate(ServerShip ship) {
		ContraptionForceInducer inducer = ship.getAttachment(ContraptionForceInducer.class);
		if (inducer == null)  {
			inducer = new ContraptionForceInducer();
			ship.saveAttachment(ContraptionForceInducer.class, inducer);
		}
		return inducer;
	}
	
	@Override
	public void applyForces(PhysShip ship) {
		
		//System.out.println(forcesToApply.size());
		
		if (true) {
			forcesToApply.forEach(force -> {
				switch (force.type) {
				case INVARIANT_LINEAR:
					ship.applyInvariantForce(new Vector3d((Double) force.force.x(), (Double) force.force.y(), (Double) force.force.z()));
					break;
				case INVARIANT_LINEAR_AT:
					ship.applyInvariantForceToPos(new Vector3d((Double) force.force.x(), (Double) force.force.y(), (Double) force.force.z()), new Vector3d((Double) force.origin.x(), (Double) force.origin.y(), (Double) force.origin.z()));
					break;
				case ROTDEP_LINEAR:
					ship.applyRotDependentForce(new Vector3d((Double) force.force.x(), (Double) force.force.y(), (Double) force.force.z()));
					break;
				case ROTDEP_LINEAR_AT:
					ship.applyRotDependentForceToPos(new Vector3d((Double) force.force.x(), (Double) force.force.y(), (Double) force.force.z()), new Vector3d((Double) force.origin.x(), (Double) force.origin.y(), (Double) force.origin.z()));
					break;
				case INVARIANT_ANGULAR:
					ship.applyInvariantTorque(new Vector3d((Double) force.force.x(), (Double) force.force.y(), (Double) force.force.z()));
					break;
				case ROTDEP_ANGULAR:
					ship.applyRotDependentTorque(new Vector3d((Double) force.force.x(), (Double) force.force.y(), (Double) force.force.z()));
					break;
				}
			});
			forcesToApply.clear();
		}
				
	}
	
	public void apply(Force force) {
		if (forcesToApply.size() < 200) {
			forcesToApply.add(force);
		} else {
			Industria.LOGGER.warn("Tick force list to large, is the server overloaded ?");
		}
	}
	
	public void applyInvariantLinear(IVector3<? extends Number> force) {
		apply(new Force(ForceType.INVARIANT_LINEAR, force, null));
	}

	public void applyRotDependentLinear(IVector3<? extends Number> force) {
		apply(new Force(ForceType.ROTDEP_LINEAR, force, null));
	}

	public void applyInvariantLinearAt(IVector3<? extends Number> force, IVector3<? extends Number> origin) {
		apply(new Force(ForceType.INVARIANT_LINEAR_AT, force, origin));
	}

	public void applyRotDependentLinearAt(IVector3<? extends Number> force, IVector3<? extends Number> origin) {
		apply(new Force(ForceType.ROTDEP_LINEAR_AT, force, origin));
	}

	public void applyInvariantAngular(IVector3<? extends Number> force) {
		apply(new Force(ForceType.INVARIANT_ANGULAR, force, null));
	}

	public void applyRotDependentAngular(IVector3<? extends Number> force) {
		apply(new Force(ForceType.ROTDEP_ANGULAR, force, null));
	}
	
	public static enum ForceType {
		INVARIANT_LINEAR,INVARIANT_LINEAR_AT,ROTDEP_LINEAR,ROTDEP_LINEAR_AT,INVARIANT_ANGULAR,ROTDEP_ANGULAR;
	}
	
	public static record Force(ForceType type, IVector3<? extends Number> force, @Nullable IVector3<? extends Number> origin) {}
	
}
