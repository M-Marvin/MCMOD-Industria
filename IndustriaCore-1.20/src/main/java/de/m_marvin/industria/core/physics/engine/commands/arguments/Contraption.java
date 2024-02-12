package de.m_marvin.industria.core.physics.engine.commands.arguments;

import java.util.HashSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

import org.valkyrienskies.core.api.ships.Ship;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.unimat.api.IQuaternionMath.EulerOrder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
	
	public Level getLevel() {
		return level;
	}
	
	public long getContraptionId() {
		return contraptionId;
	}
	
	public ContraptionPosition getPosition() {
		Ship contraption = PhysicUtility.getContraptionById(this.level, this.contraptionId);
		return new ContraptionPosition(contraption.getTransform());
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

	public Set<String> getTags() {
		// TODO Auto-generated method stub
		return new HashSet<>();
	}

	public Component getName() {
		// TODO Auto-generated method stub
		return Component.literal("n/a");
	}

	public String getIdString() {
		return "{" + this.contraptionId + "}";
	}
	
	public static OptionalLong parseIdString(String idString) {
		try {
			return OptionalLong.of(Long.parseLong(idString.substring(1, idString.length() - 1)));
		} catch (Exception e) {
			return OptionalLong.empty();
		}
	}
	
}
