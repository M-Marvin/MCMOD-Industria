package de.m_marvin.industria.core.physics;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4dc;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PhysicUtility {
	
	public static Iterable<Ship> getContraptionIntersecting(Level level, BlockPos position)  {
		return VSGameUtilsKt.getShipsIntersecting(level, new AABB(position, position));
	}
	
	public static Ship getContraptionOfBlock(Level level, BlockPos shipBlockPos) {
		return VSGameUtilsKt.getShipManagingPos(level, shipBlockPos);
	}
	
	public static String getDimensionId(Level level) {
		return VSGameUtilsKt.getDimensionId(level);
	}

	public static Vec3d toWorldPos(Ship contraption, Vec3d pos) {
		Matrix4dc shipToWorld = contraption.getShipToWorld();
		if (shipToWorld != null) {
			Vector3d transformedPosition = shipToWorld.transformPosition(pos.writeTo(new Vector3d()));
			return Vec3d.fromVec(transformedPosition);
		}
		return new Vec3d(0, 0, 0);
	}
	
	public static Vec3d toWorldPos(Ship contaption, BlockPos pos) {
		return toWorldPos(contaption, Vec3d.fromVec(pos));
	}

	public static BlockPos toWorldBlockPos(Ship contraption, BlockPos pos) {
		Vec3d position = toWorldPos(contraption, pos);
		return new BlockPos(position.x, position.y, position.z);
	}
	
	public static ContraptionPosition getPosition(Ship contraption, boolean massCenter) {
		if (massCenter) {
			Vec3d position = Vec3d.fromVec(contraption.getTransform().getPositionInWorld());
			Quaterniondc jomlQuat = contraption.getTransform().getShipToWorldRotation();
			Quaternion orientation = new Quaternion((float) jomlQuat.x(), (float) jomlQuat.y(), (float) jomlQuat.z(), (float) jomlQuat.w());
			return new ContraptionPosition(orientation, position);		
		} else {
			AABBic shipBounds = contraption.getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new BlockPos(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new BlockPos(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(((ShipData) contraption).getInertiaData().getCenterOfMassInShip());
			Vec3d centerOfMassOffset = shipCoordMassCenter.sub(shipCoordCenter).add(1.0, 1.0, 1.0);
			Vec3d position = Vec3d.fromVec(contraption.getTransform().getPositionInWorld()).sub(centerOfMassOffset);
			Quaterniondc jomlQuat = contraption.getTransform().getShipToWorldRotation();
			Quaternion orientation = new Quaternion((float) jomlQuat.x(), (float) jomlQuat.y(), (float) jomlQuat.z(), (float) jomlQuat.w());
			return new ContraptionPosition(orientation, position);		
		}
		
	}
	
	public static void setPosition(Ship contraption, ContraptionPosition position, boolean massCenter) {
		if (massCenter) {
			ShipTransform transform = contraption.getTransform();
			((Vector3d) transform.getPositionInWorld()).set(position.getPosition().writeTo(new Vector3d()));
			((Quaterniond) transform.getShipToWorldRotation()).set(position.getOrientation().i(), position.getOrientation().j(), position.getOrientation().k(), position.getOrientation().r());
			((ShipData) contraption).setTransform(transform);	
		} else {
			AABBic shipBounds = contraption.getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new BlockPos(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new BlockPos(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(((ShipData) contraption).getInertiaData().getCenterOfMassInShip());
			Vec3d centerOfMassOffset = shipCoordMassCenter.sub(shipCoordCenter).add(1.0, 1.0, 1.0);
			ShipTransform transform = contraption.getTransform();
			((Vector3d) transform.getPositionInWorld()).set(position.getPosition().add(centerOfMassOffset).writeTo(new Vector3d()));
			((Quaterniond) transform.getShipToWorldRotation()).set(position.getOrientation().i(), position.getOrientation().j(), position.getOrientation().k(), position.getOrientation().r());
			((ShipData) contraption).setTransform(transform);	
		}
	}
	
	public static Vec3d toContraptionPos(Ship contraption, Vec3d pos) {
		Matrix4dc worldToShip = contraption.getWorldToShip();
		if (worldToShip != null) {
			Vector3d transformPosition = worldToShip.transformPosition(pos.writeTo(new Vector3d()));
			return Vec3d.fromVec(transformPosition);
		}
		return new Vec3d(0, 0, 0);
	}
	
	public static BlockPos toContraptionBlockPos(Ship contraption, Vec3d pos) {
		Vec3d position = toContraptionPos(contraption, pos);
		return new BlockPos(position.x, position.y, position.z);
	}
	
	public static BlockPos toContraptionBlockPos(Ship contraption, BlockPos pos) {
		return toContraptionBlockPos(contraption, Vec3d.fromVec(pos));
	}
	
	public static List<Ship> getLoadedContraptions(Level level) {
		QueryableShipData<LoadedShip> shipData = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips(); // ERROR
		List<Ship> ships = new ArrayList<>();
		ships.addAll(shipData);
		return ships;
	}
	
	public static Ship createNewContraptionAt(ServerLevel level, BlockPos position) {
		return createContraptionAt(level, Vec3d.fromVec(position));
	}
	
	public static Ship createContraptionAt(ServerLevel level, Vec3d position) {
		Ship parentContraption = VSGameUtilsKt.getShipManagingPos(level, position.writeTo(new Vector3d()));
		if (parentContraption != null) {
			position = toWorldPos(parentContraption, position);
		}
		String dimensionId = getDimensionId(level);
		Ship newContraption = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(position.writeTo(new Vector3i()), false, 1, dimensionId);
		
		// Stone for safety reasons
		BlockPos pos2 = toContraptionBlockPos(newContraption, position);
		level.setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
		
		return newContraption;
	}
	
	public static void removeContraption(ServerLevel level, Ship contraption) {
		AABBic bounds = contraption.getShipAABB();
		if (bounds != null) {
			for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
				for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
					for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
						level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}
		}
	}
	
	public static Ship convertToContraption(ServerLevel level, AABB structureBounds, boolean removeOriginal) {
		
		BlockPos max = null;
		BlockPos min = null;
		boolean isEmpty = true;
		
		for (int x = (int) structureBounds.minX; x <= structureBounds.maxX; x++) {
			for (int y = (int) structureBounds.minY; y <= structureBounds.maxY; y++) {
				for (int z = (int) structureBounds.minZ; z <= structureBounds.maxZ; z++) {
					
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = level.getBlockState(pos);
					
					if (!state.getCollisionShape(level, pos).isEmpty()) {
						isEmpty = false;
						
						if (min == null) {
							min = pos;
						} else {
							min = MathUtility.getMaxCorner(min, pos);
						}
						
						if (max == null) {
							max = pos;
						} else {
							max = MathUtility.getMinCorner(max, pos);
						}
						
					}
					
				}
			}
		}
		
		if (isEmpty) {
			return null;
		}
		
		Vec3d structureCenter = MathUtility.getMiddle(min, max);
		
		Ship contraption = createContraptionAt(level, structureCenter);
		
		for (int x = (int) structureBounds.minX; x <= structureBounds.maxX; x++) {
			for (int y = (int) structureBounds.minY; y <= structureBounds.maxY; y++) {
				for (int z = (int) structureBounds.minZ; z <= structureBounds.maxZ; z++) {
					
					BlockPos pos = new BlockPos(x, y, z);
					BlockPos target = toContraptionBlockPos(contraption, pos);
					RelocationUtilKt.relocateBlock((Level) level, pos, target, false, (ServerShip) contraption, Rotation.NONE);
					
				}
			}
		}

		for (int x = (int) structureBounds.minX; x <= structureBounds.maxX; x++) {
			for (int y = (int) structureBounds.minY; y <= structureBounds.maxY; y++) {
				for (int z = (int) structureBounds.minX; z <= structureBounds.maxZ; z++) {
					
					BlockPos pos = new BlockPos(x, y, z);
					BlockPos target = toContraptionBlockPos(contraption, pos);
					RelocationUtilKt.updateBlock(level, pos, target, level.getBlockState(target));
					
				}
			}
		}
		
		setPosition(contraption, new ContraptionPosition(new Quaternion(new Vec3i(1, 0, 0), 0), structureCenter), false);
		
		return contraption;
		
	}
	
	public static Ship getContraptionById(Level level, long id) {
		for (Ship contraption : getLoadedContraptions(level)) {
			if (contraption.getId() == id) {
				return contraption;
			}
		}
		return null;
	}
	
}
