package de.m_marvin.industria.core.physics.types;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;

import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3d;

public class ContraptionPosition {
	
	public Quaternion orientation;
	public Vec3d position;
	
	public ContraptionPosition(ShipTransform transform) {
		this(transform.getShipToWorldRotation(), transform.getPositionInWorld());
	}
	
	public ContraptionPosition(Quaterniondc orientation, Vector3dc position) {
		this(new Quaternion((float) orientation.x(), (float) orientation.y(), (float) orientation.z(), (float) orientation.w()), new Vec3d(position.x(), position.y(), position.z()));
	}
	
	public ContraptionPosition(Quaternion orientation, Vec3d position) {
		this.orientation = orientation;
		this.position = position;
	}
	
	public ContraptionPosition(ContraptionPosition position) {
		this.orientation = new Quaternion(position.getOrientation().i, position.getOrientation().j, position.getOrientation().k, position.getOrientation().r);
		this.position = new Vec3d(position.position);
	}

	public ShipTeleportData toTeleport() {
		return new ShipTeleportDataImpl(
				new Vector3d(this.position.x, this.position.y, this.position.z),
				new Quaterniond(this.orientation.i, this.orientation.j, this.orientation.k, this.orientation.r),
				new Vector3d(0, 0, 0),
				new Vector3d(0, 0, 0)
		);
	}
	
	public Quaternion getOrientation() {
		return orientation;
	}
	
	public void setOrientation(Quaternion orientation) {
		this.orientation = orientation;
	}
	
	public Vec3d getPosition() {
		return position;
	}
	
	public void setPosition(Vec3d position) {
		this.position = position;
	}
	
}
