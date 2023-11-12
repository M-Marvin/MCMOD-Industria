package de.m_marvin.industria.core.physics.types;

import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec3d;

public class ContraptionPosition {
	
	public Quaterniond orientation;
	public Vec3d position;
	
	public ContraptionPosition(ShipTransform transform) {
		this(transform.getShipToWorldRotation(), transform.getPositionInWorld());
	}
	
	public ContraptionPosition(Quaterniondc orientation, Vector3dc position) {
		this(new Quaterniond((float) orientation.x(), (float) orientation.y(), (float) orientation.z(), (float) orientation.w()), new Vec3d(position.x(), position.y(), position.z()));
	}
	
	public ContraptionPosition(Quaterniond orientation, Vec3d position) {
		this.orientation = orientation;
		this.position = position;
	}
	
	public ContraptionPosition(ContraptionPosition position) {
		this.orientation = new Quaterniond(position.getOrientation().i, position.getOrientation().j, position.getOrientation().k, position.getOrientation().r);
		this.position = new Vec3d(position.position);
	}

	public ShipTeleportData toTeleport() {
		return new ShipTeleportDataImpl(
				new Vector3d(this.position.x, this.position.y, this.position.z),
				new org.joml.Quaterniond(this.orientation.i, this.orientation.j, this.orientation.k, this.orientation.r),
				new Vector3d(0, 0, 0),
				new Vector3d(0, 0, 0)
		);
	}
	
	public void toWorldPosition(ShipTransform transform) {
		Quaterniondc quat = transform.getShipToWorldRotation();
		this.orientation = new Quaterniond((float) quat.x(), (float) quat.y(), (float) quat.z(), (float) quat.w()).mul(this.orientation);
		this.position = PhysicUtility.toWorldPos(transform, this.position);
	}
	
	public Quaterniond getOrientation() {
		return orientation;
	}
	
	public void setOrientation(Quaterniond orientation) {
		this.orientation = orientation;
	}
	
	public Vec3d getPosition() {
		return position;
	}
	
	public Vector3d getPositionJOML() {
		return new Vector3d(this.position.x, this.position.y, this.position.z);
	}
	
	public Vector3i getPositionJOMLi() {
		return new Vector3i((int) Math.floor(this.position.x), (int) Math.floor(this.position.y), (int) Math.floor(this.position.z));
	}
	
	public void setPosition(Vec3d position) {
		this.position = position;
	}
	
}
