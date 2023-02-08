package de.m_marvin.industria.core.physics.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.types.ContraptionHitResult;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.registries.ModCapabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.unimat.impl.Quaternion;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PhysicHandlerCapability implements ICapabilitySerializable<CompoundTag> {
	
	/* Capability handling */
	
	private LazyOptional<PhysicHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}

	private Map<String, Long> contraptionNames = new HashMap<>();
	private Map<String, Integer> constraintNames = new HashMap<>();
	private Level level;
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		CompoundTag contraptionNamesTag = new CompoundTag();
		List<Long> contraptionIds = PhysicUtility.getAllContraptions(level).stream().map(Ship::getId).toList();
		for (Entry<String, Long> entry : this.contraptionNames.entrySet()) {
			if (contraptionIds.contains(entry.getValue())) contraptionNamesTag.putLong(entry.getKey(), entry.getValue());
		}
		tag.put("ContraptionNames", contraptionNamesTag);
		CompoundTag constraintMapTag = new CompoundTag();
		//List<Integer> constraintIds = PhysicUtility.getAllConstraints(level); TODO Requires implementation of PhysicUtility#getAllConstraints()
		for (Entry<String, Integer> entry : this.constraintNames.entrySet()) {
			//if (constraintIds.contains(entry.getValue())) 
				constraintMapTag.putLong(entry.getKey(), entry.getValue());
		}
		tag.put("ConstraintNames", constraintMapTag);
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG ,"Saved " + contraptionNamesTag.size() + " constraption names and " + constraintMapTag.size() + " constraint names");
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		CompoundTag contraptionNamesTag = tag.getCompound("ContraptionNames");
		this.contraptionNames.clear();
		for (String name : contraptionNamesTag.getAllKeys()) {
			this.contraptionNames.put(name, contraptionNamesTag.getLong(name));
		}
		CompoundTag constraintMapTag = tag.getCompound("ConstraintNames");
		this.constraintNames.clear();
		for (String name : constraintMapTag.getAllKeys()) {
			this.constraintNames.put(name, constraintMapTag.getInt(name));
		}
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG ,"Loaded " + this.contraptionNames.size() + " contraptions and  " + this.constraintNames.size() + " constraints");
	}
	
	public PhysicHandlerCapability(Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return level;
	}
	
	
	
	
	/* Naming and finding of contraptions */
	
	public Map<String, Integer> getConstraintNames() {
		return constraintNames;
	}
	
	public Map<String, Long> getContraptionNames() {
		return contraptionNames;
	}
	
	public void setContraptionName(long contraptionId, String name) {
		this.contraptionNames.put(name, contraptionId);
	}
	
	public void setConstraintName(int constraintId, String name) {
		this.constraintNames.put(name, constraintId);
	}
	
	public long getContraption(String name) {
		return this.contraptionNames.get(name);
	}
	
	public int getConstraint(String name) {
		return this.constraintNames.get(name);
	}
	
	public String getContraptionName(long id) {
		for (Entry<String, Long> entry : this.contraptionNames.entrySet()) {
			if (entry.getValue() == id) return entry.getKey();
		}
		return null;
	}

	public String getConstraintName(int id) {
		for (Entry<String, Integer> entry : this.constraintNames.entrySet()) {
			if (entry.getValue() == id) return entry.getKey();
		}
		return null;
	}
	
	public void removeContraptionName(String name) {
		this.contraptionNames.remove(name);
	}
	
	public void removeConstraintName(String name) {
		this.constraintNames.remove(name);
	}

	public void removeContraptionId(long id) {
		String name = null;
		for (Entry<String, Long> entry : this.contraptionNames.entrySet()) {
			if (entry.getValue() == id) name = entry.getKey();
		}
		if (name != null) this.contraptionNames.remove(name);
	}
	
	public void removeConstraintId(int id) {
		String name = null;
		for (Entry<String, Integer> entry : this.constraintNames.entrySet()) {
			if (entry.getValue() == id) name = entry.getKey();
		}
		if (name != null) this.constraintNames.remove(name);
	}

	public Iterable<Ship> getContraptionIntersecting(BlockPos position)  {
		return VSGameUtilsKt.getShipsIntersecting(level, new AABB(position, position));
	}
	
	public Ship getContraptionOfBlock(BlockPos shipBlockPos) {
		return VSGameUtilsKt.getShipManagingPos(level, shipBlockPos);
	}

	public Ship getContraptionById(long id) {
		for (Ship contraption : getLoadedContraptions()) {
			if (contraption.getId() == id) {
				return contraption;
			}
		}
		return null;
	}
	
	/* Translating/moving of contraptions */
	
	public ContraptionPosition getPosition(ServerShip contraption, boolean massCenter) {
		if (massCenter) {
			Vec3d position = Vec3d.fromVec(contraption.getTransform().getPositionInWorld());
			Quaterniondc jomlQuat = contraption.getTransform().getShipToWorldRotation();
			Quaternion orientation = new Quaternion((float) jomlQuat.x(), (float) jomlQuat.y(), (float) jomlQuat.z(), (float) jomlQuat.w());
			return new ContraptionPosition(orientation, position);		
		} else {
			AABBic shipBounds = contraption.getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new BlockPos(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new BlockPos(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(contraption.getInertiaData().getCenterOfMassInShip());
			Vec3d centerOfMassOffset = shipCoordMassCenter.sub(shipCoordCenter).add(1.0, 1.0, 1.0);
			Vec3d position = Vec3d.fromVec(contraption.getTransform().getPositionInWorld()).sub(centerOfMassOffset);
			Quaterniondc jomlQuat = contraption.getTransform().getShipToWorldRotation();
			Quaternion orientation = new Quaternion((float) jomlQuat.x(), (float) jomlQuat.y(), (float) jomlQuat.z(), (float) jomlQuat.w());
			return new ContraptionPosition(orientation, position);		
		}
		
	}
	
	public void setPosition(ServerShip contraption, ContraptionPosition position, boolean massCenter) {
		if (massCenter) {
			ShipTransform transform = contraption.getTransform();
			((Vector3d) transform.getPositionInWorld()).set(position.getPosition().writeTo(new Vector3d()));
			((Quaterniond) transform.getShipToWorldRotation()).set(position.getOrientation().i(), position.getOrientation().j(), position.getOrientation().k(), position.getOrientation().r());
			((ShipData) contraption).setTransform(transform);	// FIXME does not work with LoadedShip
		} else {
			AABBic shipBounds = contraption.getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new BlockPos(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new BlockPos(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(contraption.getInertiaData().getCenterOfMassInShip());
			Vec3d centerOfMassOffset = shipCoordMassCenter.sub(shipCoordCenter).add(1.0, 1.0, 1.0);
			ShipTransform transform = contraption.getTransform();
			((Vector3d) transform.getPositionInWorld()).set(position.getPosition().add(centerOfMassOffset).writeTo(new Vector3d()));
			((Quaterniond) transform.getShipToWorldRotation()).set(position.getOrientation().i(), position.getOrientation().j(), position.getOrientation().k(), position.getOrientation().r());
			((ShipData) contraption).setTransform(transform);	// FIXME does not work with LoadedShip
		}
	}
	
	/* Listing and creation contraptions in the world */
	
	public List<Ship> getLoadedContraptions() {
		QueryableShipData<LoadedShip> shipData = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips();
		List<Ship> ships = new ArrayList<>();
		ships.addAll(shipData);
		return ships;
	}

	public List<Ship> getAllContraptions() {
		QueryableShipData<Ship> shipData = VSGameUtilsKt.getShipObjectWorld(level).getAllShips();
		List<Ship> ships = new ArrayList<>();
		ships.addAll(shipData);
		return ships;
	}
	
	public ServerShip createContraptionAt(Vec3d position, float scale) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		Ship parentContraption = VSGameUtilsKt.getShipManagingPos(level, position.writeTo(new Vector3d()));
		if (parentContraption != null) {
			position = PhysicUtility.toWorldPos(parentContraption, position);
		}
		String dimensionId = getDimensionId();
		Ship newContraption = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewShipAtBlock(position.writeTo(new Vector3i()), false, scale, dimensionId);
		
		// Stone for safety reasons
		BlockPos pos2 = PhysicUtility.toContraptionBlockPos(newContraption, position);
		level.setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
		
		return (ServerShip) newContraption;
	}
	
	public boolean removeContraption(Ship contraption) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(ModCapabilities.PHYSIC_DATA_HOLDER_CAPABILITY);
		if (dataHolder.isPresent()) {
			AABBic bounds = contraption.getShipAABB();
			if (bounds != null) {
				for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
					for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
						for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
							GameUtility.removeBlock(level, new BlockPos(x, y, z));
						}
					}
				}
			}
			dataHolder.resolve().get().removeContraptionId(contraption.getId());
			return true;
		}
		return false;
	}
	
	public Ship convertToContraption(AABB areaBounds, boolean removeOriginal, float scale) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		
		BlockPos structureCornerMin = null;
		BlockPos structureCornerMax = null;
		boolean noSolids = true;
		
		int areaMinBlockX = (int) Math.floor(areaBounds.minX);
		int areaMinBlockY = (int) Math.floor(areaBounds.minY);
		int areaMinBlockZ = (int) Math.floor(areaBounds.minZ);
		int areaMaxBlockX = (int) Math.floor(areaBounds.maxX);
		int areaMaxBlockY = (int) Math.floor(areaBounds.maxY);
		int areaMaxBlockZ = (int) Math.floor(areaBounds.maxZ);

		for (int x = areaMinBlockX; x <= areaMaxBlockX; x++) {
			for (int z = areaMinBlockZ; z <= areaMaxBlockZ; z++) {
				for (int y = areaMinBlockY; y <= areaMaxBlockY; y++) {
					
					BlockPos itPos = new BlockPos(x, y, z);
					BlockState itState = level.getBlockState(itPos);
					
					if (PhysicUtility.isValidContraptionBlock(itState)) {
						
						if (structureCornerMin == null) {
							structureCornerMin = itPos;
						} else {
							structureCornerMin = MathUtility.getMinCorner(itPos, structureCornerMin);
						}
						
						if (structureCornerMax == null) {
							structureCornerMax = itPos;
						} else {
							structureCornerMax = MathUtility.getMaxCorner(itPos, structureCornerMax);
						}
						
					}
					
				}
			}
		}
		
		if (structureCornerMax == null) structureCornerMax = structureCornerMin = new BlockPos(areaBounds.getCenter().x(), areaBounds.getCenter().y(), areaBounds.getCenter().z());
		
		Vec3d contraptionPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
		ServerShip contraption = createContraptionAt(contraptionPos, scale);
		
		Vec3d contraptionOrigin = PhysicUtility.toContraptionPos(contraption, contraptionPos);
				
		for (int x = areaMinBlockX; x <= areaMaxBlockX; x++) {
			for (int z = areaMinBlockZ; z <= areaMaxBlockZ; z++) {
				for (int y = areaMinBlockY; y <= areaMaxBlockY; y++) {
					
					BlockPos itPos = new BlockPos(x, y, z);
					BlockState itState = level.getBlockState(itPos);
					
					if (PhysicUtility.isValidContraptionBlock(itState)) {
						
						Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
						Vec3d shipPos = contraptionOrigin.add(relativePosition);
						
						GameUtility.copyBlock(level, itPos, new BlockPos(shipPos.x, shipPos.y, shipPos.z));
						
						if (PhysicUtility.isSolidContraptionBlock(itState)) {
							
							noSolids = false;
							
						}
						
					}
					
				}
			}
		}
		
		if (noSolids) {
			level.setBlock(new BlockPos(contraptionOrigin.x, contraptionOrigin.y, contraptionOrigin.z), Blocks.STONE.defaultBlockState(), 34);
		} else {
			BlockState centerStructureBlock = level.getBlockState(new BlockPos(contraptionPos.x, contraptionPos.y, contraptionPos.z));
			if (!PhysicUtility.isValidContraptionBlock(centerStructureBlock)) {
				level.setBlock(new BlockPos(contraptionOrigin.x, contraptionOrigin.y, contraptionOrigin.z), Blocks.AIR.defaultBlockState(), 34);
			}
		}
		
		if (removeOriginal) {
			for (int x = structureCornerMin.getX(); x <= structureCornerMax.getX(); x++) {
				for (int z = structureCornerMin.getZ(); z <= structureCornerMax.getZ(); z++) {
					for (int y = structureCornerMin.getY(); y <= structureCornerMax.getY(); y++) {
						
						GameUtility.removeBlock(level, new BlockPos(x, y, z));
						
					}
				}
			}
		}
		
		setPosition((ServerShip) contraption, new ContraptionPosition(new Quaternion(new Vec3i(0, 1, 1), 0), contraptionPos), false);
		
		return contraption;
		
	}
	
	public ServerShip assembleToContraption(List<BlockPos> blocks, boolean removeOriginal, float scale) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		
		if (blocks.isEmpty()) {
			return null;
		}

		BlockPos structureCornerMin = blocks.get(0);
		BlockPos structureCornerMax = blocks.get(0);
		
		for (BlockPos itPos : blocks) {
			structureCornerMin = MathUtility.getMinCorner(structureCornerMin, itPos);
			structureCornerMax = MathUtility.getMaxCorner(structureCornerMax, itPos);
		}
		
		Vec3d contraptionPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
		ServerShip contraption = createContraptionAt(contraptionPos, scale);
		
		Vec3d contraptionOrigin = PhysicUtility.toContraptionPos(contraption, contraptionPos);
		boolean noSolids = true;

		for (BlockPos itPos : blocks) {
			
			BlockState itState = level.getBlockState(itPos);
			
			Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
			Vec3d shipPos = contraptionOrigin.add(relativePosition);
			
			GameUtility.copyBlock(level, itPos, new BlockPos(shipPos.x, shipPos.y, shipPos.z));
			
			if (PhysicUtility.isSolidContraptionBlock(itState)) {
				
				noSolids = false;
				
			}
			
		}
		
		if (noSolids) {
			level.setBlock(new BlockPos(contraptionOrigin.x, contraptionOrigin.y, contraptionOrigin.z), Blocks.STONE.defaultBlockState(), 34);
		} else {
			BlockState centerStructureBlock = level.getBlockState(new BlockPos(contraptionPos.x, contraptionPos.y, contraptionPos.z));
			if (!PhysicUtility.isValidContraptionBlock(centerStructureBlock)) {
				level.setBlock(new BlockPos(contraptionOrigin.x, contraptionOrigin.y, contraptionOrigin.z), Blocks.AIR.defaultBlockState(), 34);
			}
		}
		
		if (removeOriginal) {
			for (BlockPos itPos : blocks) {
				GameUtility.removeBlock(level, itPos);
			}
		}
		
		setPosition((ServerShip) contraption, new ContraptionPosition(new Quaternion(new Vec3i(0, 1, 1), 0), contraptionPos), false);
		
		return contraption;
		
	}
	
	/* Raycasting for contraptions */
	
	public ContraptionHitResult clipForContraption(Vec3d from, Vec3d to) {
		ClipContext clipContext = new ClipContext(from.writeTo(new Vec3(0, 0, 0)), to.writeTo(new Vec3(0, 0, 0)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null);
		HitResult clipResult = level.clip(clipContext);
		
		if (clipResult.getType() == Type.BLOCK) {
			BlockPos hitBlockPos = ((BlockHitResult) clipResult).getBlockPos();
			Ship contraption = getContraptionOfBlock(hitBlockPos);
			if (contraption != null) {
				Vec3 hitPosition = clipResult.getLocation();
				return ContraptionHitResult.hit(hitPosition, hitBlockPos, contraption);
			}
			
		}
		return ContraptionHitResult.miss(clipResult.getLocation());
	}
		
	/* Util stuff */

	public String getDimensionId() {
		return VSGameUtilsKt.getDimensionId(level);
	}
	
}