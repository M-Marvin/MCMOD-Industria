package de.m_marvin.industria.core.contraptions.engine.types;

import java.util.Optional;

import javax.annotation.Nullable;

import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;

import de.m_marvin.industria.core.contraptions.ContraptionUtility;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.ServerContraption;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public class ContraptionPosition {
	
	public static final String DIMENSION_REGISTRY = Registries.DIMENSION.location().toString();
	
	public Quaterniond orientation;
	public Vec3d position;
	public ResourceLocation dimension;
	
	public Optional<Vec3d> velocity = Optional.empty();
	public Optional<Vec3d> omega = Optional.empty();
	public Optional<Double> scale = Optional.empty();
	
	public ContraptionPosition(Quaterniond orientation, Vec3d position, ResourceLocation dimension, @Nullable Vec3d velocity, @Nullable Vec3d omega, @Nullable Double scale) {
		this.orientation = orientation;
		this.position = position;
		this.dimension = dimension;
		
		this.velocity = Optional.ofNullable(velocity);
		this.omega = Optional.ofNullable(omega);
		this.scale = Optional.ofNullable(scale);
	}
	
	public ContraptionPosition(Quaterniond orientation, Vec3d position, ResourceLocation dimension) {
		this.orientation = orientation;
		this.position = position;
		this.dimension = dimension;
	}
	
	public ContraptionPosition(Quaterniondc orientation, Vector3dc position, String dimension) {
		this(new Quaterniond(orientation.x(), orientation.y(), orientation.z(), orientation.w()), new Vec3d(position.x(), position.y(), position.z()), new ResourceLocation(dimension.substring(DIMENSION_REGISTRY.length() + 1)));
	}
	
	public ContraptionPosition(ShipTransform transform, String dimension) {
		this(transform.getShipToWorldRotation(), transform.getPositionInWorld(), dimension);
	}

	public ContraptionPosition(Ship contraption) {
		this(contraption.getTransform(), contraption.getChunkClaimDimension());
		this.velocity = Optional.of(Vec3d.fromVec(contraption.getVelocity()));
		this.omega = Optional.of(Vec3d.fromVec(contraption.getOmega()));
	}

	public ContraptionPosition(Ship contraption, boolean useGeometricCenter) {
		this(contraption);
		
		if (useGeometricCenter) {
			AABBic shipBounds = contraption.getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new Vec3d(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new Vec3d(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(contraption.getTransform().getPositionInShip());
			Vec3d centerOfMassOffset = ContraptionUtility.toWorldPos(contraption.getTransform(), shipCoordMassCenter).sub(ContraptionUtility.toWorldPos(contraption.getTransform(), shipCoordCenter));
			
			position.subI(centerOfMassOffset);	
		}
	}
	
	public ContraptionPosition(ContraptionPosition position) {
		this(
				position.getOrientation(), 
				position.getPosition(), 
				position.dimension,
				position.velocity.isPresent() ? position.velocity.get() : null,
				position.omega.isPresent() ? position.omega.get() : null,
				position.scale.isPresent() ? position.scale.get() : null
			);
	}

	public ShipTeleportData toTeleport(ServerContraption contraption, boolean useGeometricCenter) {
		if (useGeometricCenter) {
			AABBic shipBounds = contraption.getShip().getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new Vec3d(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new Vec3d(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(contraption.getShip().getInertiaData().getCenterOfMassInShip()).add(new Vec3d(0.5, 0.5, 0.5));
			Vec3d centerOfMassOffset = ContraptionUtility.toWorldPos(contraption.getShip().getTransform(), shipCoordMassCenter).sub(ContraptionUtility.toWorldPos(contraption.getShip().getTransform(), shipCoordCenter));
			
			ContraptionPosition temp = new ContraptionPosition(this);
			temp.getPosition().addI(centerOfMassOffset);
			
			return temp.toTeleport();
		}
		return toTeleport();
	}
	
	public ShipTeleportData toTeleport() {
		return new ShipTeleportDataImpl(
				this.position.writeTo(new Vector3d()), 
				new org.joml.Quaterniond(this.orientation.i, this.orientation.j, this.orientation.k, this.orientation.r), 
				this.velocity.isPresent() ? this.velocity.get().writeTo(new Vector3d()) : new Vector3d(), 
				this.omega.isPresent() ? this.omega.get().writeTo(new Vector3d()) : new Vector3d(), 
				DIMENSION_REGISTRY + ":" + this.dimension.toString(), 
				this.scale.isPresent() ? this.scale.get() : null
		);
	}
	
	public void toWorldPosition(ShipTransform transform) {
		Quaterniondc quat = transform.getShipToWorldRotation();
		this.orientation = new Quaterniond((float) quat.x(), (float) quat.y(), (float) quat.z(), (float) quat.w()).mul(this.orientation);
		this.position = ContraptionUtility.toWorldPos(transform, this.position);
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
	
	public void setDimension(ResourceLocation dimension) {
		this.dimension = dimension;
	}
	
	public ResourceLocation getDimension() {
		return dimension;
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
	
}
