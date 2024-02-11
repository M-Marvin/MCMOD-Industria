package de.m_marvin.industria.core.physics.types;

import java.util.Optional;

import javax.annotation.Nullable;

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
	public Optional<String> dimension;
	
	public Optional<Vec3d> velocity = Optional.empty();
	public Optional<Vec3d> omega = Optional.empty();
	public Optional<Double> scale = Optional.empty();

	public ContraptionPosition(Quaterniond orientation, Vec3d position, @Nullable String dimension, @Nullable Vec3d velocity, @Nullable Vec3d omega, @Nullable Double scale) {
		this.orientation = orientation;
		this.position = position;
		this.dimension = Optional.ofNullable(dimension);
		
		this.velocity = Optional.ofNullable(velocity);
		this.omega = Optional.ofNullable(omega);
		this.scale = Optional.ofNullable(scale);
	}
	
	public ContraptionPosition(Quaterniond orientation, Vec3d position, @Nullable String dimension) {
		this.orientation = orientation;
		this.position = position;
		this.dimension = Optional.ofNullable(dimension);
	}
	
	public ContraptionPosition(Quaterniondc orientation, Vector3dc position, @Nullable String dimension) {
		this(new Quaterniond(orientation.x(), orientation.y(), orientation.z(), orientation.w()), new Vec3d(position.x(), position.y(), position.z()), dimension);
	}
	
	public ContraptionPosition(ShipTransform transform) {
		this(transform.getShipToWorldRotation(), transform.getPositionInWorld(), null);
	}
	
	public ContraptionPosition(ContraptionPosition position) {
		this(
				position.getOrientation(), 
				position.getPosition(), 
				position.dimension.isPresent() ? position.dimension.get() : null,
				position.velocity.isPresent() ? position.velocity.get() : null,
				position.omega.isPresent() ? position.omega.get() : null,
				position.scale.isPresent() ? position.scale.get() : null
			);
	}

	public ShipTeleportData toTeleport() {
		return new ShipTeleportDataImpl(
				this.getPositionJOML(),
				this.getOrientationJOML(),
				this.velocity.isPresent() ? this.velocity.get().writeTo(new Vector3d()) : new Vector3d(),
				this.omega.isPresent() ? this.omega.get().writeTo(new Vector3d()) : new Vector3d(),
				this.dimension.isPresent() ? this.dimension.get() : null, 
				this.scale.isPresent() ? this.scale.get() : null
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

	public void setPosition(Vec3d position) {
		this.position = position;
	}
	
	public void setDimension(@Nullable String dimension) {
		this.dimension = Optional.ofNullable(dimension);
	}
	
	public Optional<String> getDimension() {
		return this.dimension;
	}
	
	public void setVelocity(@Nullable Vec3d velocity) {
		this.velocity = Optional.ofNullable(velocity);
	}
	
	public Optional<Vec3d> getVelocity() {
		return velocity;
	}
	
	public void setOmega(@Nullable Vec3d omega) {
		this.omega = Optional.ofNullable(omega);
	}
	
	public Optional<Vec3d> getOmega() {
		return omega;
	}
	
	public void setScale(@Nullable Double scale) {
		this.scale = Optional.ofNullable(scale);
	}
	
	public Optional<Double> getScale() {
		return scale;
	}
	
	public Vector3d getPositionJOML() {
		return new Vector3d(this.position.x, this.position.y, this.position.z);
	}
	
	public Vector3i getPositionJOMLi() {
		return new Vector3i((int) Math.floor(this.position.x), (int) Math.floor(this.position.y), (int) Math.floor(this.position.z));
	}
	
	public Quaterniondc getOrientationJOML() {
		return new org.joml.Quaterniond(this.orientation.i, this.orientation.j, this.orientation.k, this.orientation.r);
	}
	
}
