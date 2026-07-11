package de.m_marvin.industria.core.contraptions.engine.types.contraption;

import java.util.HashSet;
import java.util.OptionalLong;
import java.util.Set;

import org.joml.Vector3i;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.world.LevelYRange;

import de.m_marvin.industria.core.contraptions.engine.ContraptionHandlerCapability;
import de.m_marvin.industria.core.contraptions.engine.types.ContraptionPosition;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.unimat.api.IQuaternionMath.EulerOrder;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class Contraption {
	
	public abstract Ship getShip();
	public abstract Level getLevel();

	public long getId() {
		return getShip().getId();
	}

	public String getIdString() {
		return "{" + getId() + "}";
	}

	public ContraptionHandlerCapability getHandler() {
		return GameUtility.getLevelCapability(getLevel(), Capabilities.CONTRAPTION_HANDLER_CAPABILITY);
	}
	
	public static OptionalLong parseIdString(String idString) {
		try {
			return OptionalLong.of(Long.parseLong(idString.substring(1, idString.length())));
		} catch (Exception e) {
			return OptionalLong.empty();
		}
	}
	
	public ContraptionPosition getPosition() {
		return new ContraptionPosition(getShip());
	}

	public ContraptionPosition getGeometricCenterPosition() {
		return new ContraptionPosition(getShip(), true);
	}
	
	public BlockPos getCenterPos() {
		Vector3i pos = new Vector3i();
		getShip().getChunkClaim().getCenterBlockCoordinates(new LevelYRange(0, 15), pos);
		int levelYmin = getLevel().getMinBuildHeight();
		int levelYmax = getLevel().getMaxBuildHeight();
		return new BlockPos(pos.x, (levelYmax - levelYmin) / 2 + levelYmin, pos.z);
	}
	
	public String getNameStr() {
		return getShip().getSlug();
	}
	
	public AABBic getContraptionSpaceBounds() {
		return getShip().getShipAABB(); // FIXME [VS2dep] This does ignore non collision blocks, which is a big problem!
	}
	
	public AABBic getContraptionHitboxBounds() {
		return getShip().getShipAABB();
	}
	
	public AABBdc getContraptionHitboxInWorldBounds() {
		return getShip().getWorldAABB();
	}

	public AABB getContraptionHitboxInWorldBoundsV() {
		AABBdc aabb = getContraptionHitboxInWorldBounds();
		return new AABB(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ());
	}
	
	public double distanceToSqr(Vec3 position) {
		Vec3 shipPos = getPosition().getPosition().writeTo(new Vec3(0, 0, 0));
		return position.distanceTo(shipPos);
	}

	public double getXRot() {
		return this.getPosition().getOrientation().euler(EulerOrder.XYZ, false).x();
	}
	
	public double getYRot() {
		return this.getPosition().getOrientation().euler(EulerOrder.XYZ, false).y();	
	}
		
	public double getZRot() {
		return this.getPosition().getOrientation().euler(EulerOrder.XYZ, false).z();
	}

	public double getVelocity() {
		return this.getShip().getVelocity().length();
	}
	
	public Vec3d getVelocityVec() {
		return Vec3d.fromVec(this.getShip().getVelocity());
	}
	
	public double getOmega() {
		return this.getShip().getAngularVelocity().length();
	}
	
	public Vec3d getOmegaVec() {
		return Vec3d.fromVec(this.getShip().getAngularVelocity());
	}

	public double getSize() {
		AABBic aabb = getContraptionHitboxBounds();
		double x = aabb.maxX() - aabb.minX() + 1;
		double y = aabb.maxY() - aabb.minY() + 1;
		double z = aabb.maxZ() - aabb.minZ() + 1;
		return (x + y + z) / 3.0D;
	}
	
	public Set<String> getTags() {
		Set<String> tags = getHandler().getContraptionTags().get(getId());
		if (tags == null) tags = new HashSet<>();
		return tags;
	}

	public Component getName() {
		return Component.literal(this.getShip().getSlug());
	}

	public Component getDisplayString() {
		if (this.getName().getString().isEmpty()) {
			return Component.literal(this.getIdString());
		} else {
			return this.getName();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Contraption other) {
			return other.getId() == this.getId();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
	}

}
