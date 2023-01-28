package de.m_marvin.industria.core.physics;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import de.m_marvin.univec.impl.Vec3d;
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
		
		Vector3d transform = (Vector3d) newContraption.getTransform().getPositionInWorld();
		
		ShipAssemblyKt
		
		transform.add(new Vector3d(position.x - transform.x(), position.y - transform.y(), position.z - transform.z()), transform);
			
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
		
		Vec3d center = Vec3d.fromVec(structureBounds.getCenter());
		boolean isEmpty = true;
		
		for (int x = (int) structureBounds.minX; x <= structureBounds.maxX; x++) {
			for (int y = (int) structureBounds.minY; y <= structureBounds.maxY; y++) {
				for (int z = (int) structureBounds.minZ; z <= structureBounds.maxZ; z++) {
					
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = level.getBlockState(pos);
					
					if (!state.getCollisionShape(level, pos).isEmpty()) {
						isEmpty = false;
						break;
					}
					
				}
			}
		}
		
		if (isEmpty) {
			return null;
		}
		
		Ship contraption = createContraptionAt(level, center.add(0.0, 0.0, 0.0));
		
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
