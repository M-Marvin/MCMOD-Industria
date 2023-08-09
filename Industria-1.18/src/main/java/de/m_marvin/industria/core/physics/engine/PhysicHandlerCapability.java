package de.m_marvin.industria.core.physics.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jetbrains.annotations.NotNull;
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
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.impl.game.ships.ShipData;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;
import org.valkyrienskies.core.impl.pipelines.VSGameFrame;
import org.valkyrienskies.core.impl.pipelines.VSPhysicsPipelineStage;
import org.valkyrienskies.core.impl.pipelines.VSPipelineImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import com.electronwill.nightconfig.core.conversion.ReflectionException;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.types.ContraptionHitResult;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.registries.Capabilities;
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
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class PhysicHandlerCapability implements ICapabilitySerializable<CompoundTag> {
	
	/* Capability handling */
	
	private LazyOptional<PhysicHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == Capabilities.PHYSIC_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}

	private Map<String, Long> contraptionNames = new HashMap<>();
	private Level level;
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		CompoundTag contraptionNamesTag = new CompoundTag();
		for (Entry<String, Long> entry : this.contraptionNames.entrySet()) {
			contraptionNamesTag.putLong(entry.getKey(), entry.getValue());
		}
		tag.put("ContraptionNames", contraptionNamesTag);
		IndustriaCore.LOGGER.log(org.apache.logging.log4j.Level.DEBUG ,"Saved " + contraptionNamesTag.size() + " constraption names");
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		CompoundTag contraptionNamesTag = tag.getCompound("ContraptionNames");
		this.contraptionNames.clear();
		for (String name : contraptionNamesTag.getAllKeys()) {
			this.contraptionNames.put(name, contraptionNamesTag.getLong(name));
		}
		IndustriaCore.LOGGER.log(org.apache.logging.log4j.Level.DEBUG ,"Loaded " + this.contraptionNames.size() + " contraption names");
	}
	
	public PhysicHandlerCapability(Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return level;
	}
	
	
	
	
	
	/* Naming and finding of contraptions */
	
	public Map<String, Long> getContraptionNames() {
		return contraptionNames;
	}
	
	public void setContraptionName(long contraptionId, String name) {
		this.contraptionNames.put(name, contraptionId);
	}
	
	public OptionalLong getContraption(String name) {
		if (this.contraptionNames.containsKey(name)) {
			return OptionalLong.of(this.contraptionNames.get(name));
		} else {
			return OptionalLong.empty();
		}
	}
	
	public String getContraptionName(long id) {
		for (Entry<String, Long> entry : this.contraptionNames.entrySet()) {
			if (entry.getValue() == id) return entry.getKey();
		}
		return null;
	}
	
	public void removeContraptionName(String name) {
		this.contraptionNames.remove(name);
	}
	
	public void removeContraptionId(long id) {
		String name = null;
		for (Entry<String, Long> entry : this.contraptionNames.entrySet()) {
			if (entry.getValue() == id) name = entry.getKey();
		}
		if (name != null) this.contraptionNames.remove(name);
	}
	
	public Iterable<Ship> getContraptionIntersecting(BlockPos position)  {
		return VSGameUtilsKt.getShipsIntersecting(level, new AABB(position, position));
	}
	
	public Ship getContraptionOfBlock(BlockPos shipBlockPos) {
		Ship contraption = VSGameUtilsKt.getShipObjectManagingPos(level, shipBlockPos);
		if (contraption == null) contraption = VSGameUtilsKt.getShipManagingPos(level, shipBlockPos);
		return contraption;
	}

	public Ship getContraptionById(long id) {
		for (Ship contraption : getLoadedContraptions()) {
			if (contraption.getId() == id) {
				return contraption;
			}
		}
		return null;
	}
	
	/* Listing and creation of constraints */
	
	public int addConstraint(VSConstraint constraint) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		
		return VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewConstraint(constraint);
	}
	
	public boolean removeConstaint(int constraintId) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		
		return VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).removeConstraint(constraintId);
	}
	
	
	@SuppressWarnings("unchecked")
	public VSConstraint getConstraint(int constraintId) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		
		// FIXME This is horrible!!! hopefully VS2 adds a API for that soon ...
		Map<Integer, VSConstraint> constraints = null;
		try {
			@NotNull Field constraintField = ObfuscationReflectionHelper.findField(ShipObjectServerWorld.class, "constraints");
			constraintField.setAccessible(true);
			constraints = (Map<Integer, VSConstraint>) constraintField.get(VSGameUtilsKt.getShipObjectWorld((ServerLevel) level));
		} catch (Exception e) {
			IndustriaCore.LOGGER.error("Something went wrong, but the code on that point is janky anyway ...");
			e.printStackTrace();
			constraints = new HashMap<>();
		}
		
		return constraints.get(constraintId);
	}
	
	public List<VSConstraint> getAllConstraints() {
		throw new UnsupportedOperationException("Not implemented yet!"); // TODO
	}
	
	/* Translating/moving of contraptions */
	
	public static ContraptionPosition getPosition(ServerShip contraption, boolean massCenter) {
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
	
	public static void setPosition(ServerShip contraption, ContraptionPosition position, boolean massCenter) {
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
			position = PhysicUtility.toWorldPos(parentContraption.getTransform(), position);
		}
		String dimensionId = getDimensionId();
		Ship newContraption = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewShipAtBlock(position.writeTo(new Vector3i()), false, scale, dimensionId);
		
		// Stone for safety reasons
		BlockPos pos2 = PhysicUtility.toContraptionBlockPos(newContraption.getTransform(), position);
		level.setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
		
		return (ServerShip) newContraption;
	}
	
	public boolean removeContraption(Ship contraption) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		LazyOptional<PhysicHandlerCapability> dataHolder = level.getCapability(Capabilities.PHYSIC_HANDLER_CAPABILITY);
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
		
		int areaMinBlockX = (int) Math.floor(areaBounds.minX);
		int areaMinBlockY = (int) Math.floor(areaBounds.minY);
		int areaMinBlockZ = (int) Math.floor(areaBounds.minZ);
		int areaMaxBlockX = (int) Math.floor(areaBounds.maxX);
		int areaMaxBlockY = (int) Math.floor(areaBounds.maxY);
		int areaMaxBlockZ = (int) Math.floor(areaBounds.maxZ);
		boolean hasSolids = false;
		
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
					
					if (PhysicUtility.isSolidContraptionBlock(itState)) hasSolids = true;
					
				}
			}
		}
		
		if (!hasSolids) return null;
		
		if (structureCornerMax == null) structureCornerMax = structureCornerMin = new BlockPos(areaBounds.getCenter().x(), areaBounds.getCenter().y(), areaBounds.getCenter().z());
		
		Vec3d contraptionPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
		ServerShip contraption = createContraptionAt(contraptionPos, scale);
		
		Vec3d contraptionOrigin = PhysicUtility.toContraptionPos(contraption.getTransform(), contraptionPos);
		
		for (int x = areaMinBlockX; x <= areaMaxBlockX; x++) {
			for (int z = areaMinBlockZ; z <= areaMaxBlockZ; z++) {
				for (int y = areaMinBlockY; y <= areaMaxBlockY; y++) {
					BlockPos itPos = new BlockPos(x, y, z);
					Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
					Vec3d shipPos = contraptionOrigin.add(relativePosition);
					
					GameUtility.copyBlock(level, itPos, new BlockPos(shipPos.x, shipPos.y, shipPos.z));
					
				}
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

		for (int x = structureCornerMin.getX(); x <= structureCornerMax.getX(); x++) {
			for (int z = structureCornerMin.getZ(); z <= structureCornerMax.getZ(); z++) {
				for (int y = structureCornerMin.getY(); y <= structureCornerMax.getY(); y++) {
					BlockPos itPos = new BlockPos(x, y, z);
					Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
					Vec3d shipPos = contraptionOrigin.add(relativePosition);
					
					GameUtility.triggerUpdate(level, itPos);
					GameUtility.triggerUpdate(level, new BlockPos(shipPos.x, shipPos.y, shipPos.z));
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
		boolean hasSolids = false;
		
		for (BlockPos itPos : blocks) {
			structureCornerMin = MathUtility.getMinCorner(structureCornerMin, itPos);
			structureCornerMax = MathUtility.getMaxCorner(structureCornerMax, itPos);
			
			if (PhysicUtility.isSolidContraptionBlock(level.getBlockState(itPos))) hasSolids = true;
		}
		
		if (!hasSolids) return null;
		
		Vec3d contraptionPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
		ServerShip contraption = createContraptionAt(contraptionPos, scale);
		
		Vec3d contraptionOrigin = PhysicUtility.toContraptionPos(contraption.getTransform(), contraptionPos);
		BlockPos centerBlockPos = new BlockPos(contraptionPos.x, contraptionPos.y, contraptionPos.z);
		
		for (BlockPos itPos : blocks) {
			Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
			Vec3d shipPos = contraptionOrigin.add(relativePosition);
			
			GameUtility.copyBlock(level, itPos, new BlockPos(shipPos.x, shipPos.y, shipPos.z));

		}
		
		if (!blocks.contains(centerBlockPos)) {
			BlockPos centerShipPos = PhysicUtility.toContraptionBlockPos(contraption.getTransform(), centerBlockPos);
			level.setBlock(centerShipPos, Blocks.AIR.defaultBlockState(), 3);
		}
		
		if (removeOriginal) {
			for (BlockPos itPos : blocks) {
				GameUtility.removeBlock(level, itPos);
			}
		}
		
		for (BlockPos itPos : blocks) {
			Vec3d relativePosition = Vec3d.fromVec(itPos).sub(contraptionPos);
			Vec3d shipPos = contraptionOrigin.add(relativePosition);
			
			GameUtility.triggerUpdate(level, itPos);
			GameUtility.triggerUpdate(level, new BlockPos(shipPos.x, shipPos.y, shipPos.z));
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
	
	@SuppressWarnings("unchecked")
	public boolean resetFrameQueue() {
		try {
			VSPipelineImpl pipeline = (VSPipelineImpl) VSGameUtilsKt.getVsPipeline(this.level.getServer());
			
			Field physicsStageField = ObfuscationReflectionHelper.findField(VSPipelineImpl.class, "physicsStage");
			Field gameFramesQueueField = ObfuscationReflectionHelper.findField(VSPhysicsPipelineStage.class, "gameFramesQueue");
			
			physicsStageField.setAccessible(true);
			gameFramesQueueField.setAccessible(true);
			
			VSPhysicsPipelineStage pipelineStage = (VSPhysicsPipelineStage) physicsStageField.get(pipeline);
			ConcurrentLinkedQueue<VSGameFrame> frameQueue = (ConcurrentLinkedQueue<VSGameFrame>) gameFramesQueueField.get(pipelineStage);
			
			frameQueue.clear();
			return true;
		} catch (ReflectionException | IllegalAccessException | NullPointerException e) {
			return false;
		}
	}
	
}