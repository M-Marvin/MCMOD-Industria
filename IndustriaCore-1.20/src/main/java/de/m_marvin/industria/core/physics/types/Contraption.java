package de.m_marvin.industria.core.physics.types;

import java.util.HashSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.unimat.api.IQuaternionMath.EulerOrder;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Wrapper class for contraption (VS2 ships)
 * @author marvi
 *
 */
public class Contraption {
	
	public static final String CONTRAPTION_NAME_PATTERN = "[a-zA-Z0-9_-]*";
	
	private final Level level;
	private final long contraptionId;
	
	public Contraption(Level level, long id) {
		this.level = level;
		this.contraptionId = id;
	}
	
	public Contraption(Level level, Ship ship) {
		this.level = level;
		this.contraptionId = ship.getId();
	}
	
	public static List<Contraption> fromShipList(Level level, List<Ship> ships) {
		return ships.stream().map(s -> new Contraption(level, s)).toList();
	}

	public static List<Contraption> fromShipListLevelFiltered(Level level, List<Ship> ships) {
		return ships.stream().filter(s -> {
			String dim1 = s.getChunkClaimDimension();
			String dim2 = level.dimension().registry().toString() + ":" + level.dimension().location().toString();
			return dim1.equals(dim2);
		}).map(s -> new Contraption(level, s)).toList();
	}
	
	public Level getLevel() {
		return level;
	}
	
	public long getContraptionId() {
		return contraptionId;
	}
	
	public Ship getContraption() {
		return PhysicUtility.getContraptionById(level, contraptionId);
	}
	
	public ContraptionPosition getPosition() {
		return new ContraptionPosition(getContraption());
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
		return this.getContraption().getVelocity().length();
	}
	
	public Vec3d getVelocityVec() {
		return Vec3d.fromVec(this.getContraption().getVelocity());
	}
	
	public double getOmega() {
		return this.getContraption().getOmega().length();
	}
	
	public Vec3d getOmegaVec() {
		return Vec3d.fromVec(this.getContraption().getOmega());
	}

	public AABB getBounds() {
		AABBic aabb = this.getContraption().getShipAABB();
		return new AABB(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ());
	}

	public AABB getWorldBounds() {
		AABBdc aabb = this.getContraption().getWorldAABB();
		return new AABB(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ());
	}
	
	public double getMass() {
		return ((ServerShip) getContraption()).getInertiaData().getMass();
	}
	
	public double getSize() {
		AABB aabb = getBounds();
		return aabb.getSize();
	}
	
	public boolean isStatic() {
		return ((ServerShip) getContraption()).isStatic();
	}
	
	public Set<String> getTags() {
		Set<String> tags = PhysicUtility.getContraptionTags(this.level, getContraption());
		if (tags == null) tags = new HashSet<>();
		return tags;
	}

	public Component getName() {
		return Component.literal(this.getContraption().getSlug());
	}

	public Component getDisplayString() {
		if (this.getName().getString().isEmpty()) {
			return Component.literal(this.getIdString());
		} else {
			return this.getName();
		}
	}
	
	public String getIdString() {
		return "{" + this.contraptionId + "}";
	}
	
	public static OptionalLong parseIdString(String idString) {
		try {
			return OptionalLong.of(Long.parseLong(idString.substring(1, idString.length())));
		} catch (Exception e) {
			return OptionalLong.empty();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Contraption other) {
			return other.contraptionId == this.contraptionId;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(this.contraptionId);
	}

}
