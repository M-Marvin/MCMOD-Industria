package de.m_marvin.industria.core.physics.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalLong;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.QueryableShipData;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.types.ContraptionHitResult;
import de.m_marvin.industria.core.physics.types.ContraptionPosition;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec3d;
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
	
	
	public VSConstraint getConstraint(int constraintId) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		return getAllConstraints().get(constraintId);
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, VSConstraint> getAllConstraints() {
		
		// FIXME This is horrible!!! hopefully VS2 adds an API for that soon ...
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
		
		return constraints;
	}
	
	/* Adding and removing attachments */
	
	public static <T> void attachObject(ServerShip contraption, Class<T> attachmentClass, T attachment) {
		contraption.saveAttachment(attachmentClass, attachment);
	}
	
	public static <T> T getAttachedObject(ServerShip contraption, Class<T> attachmentClass) {
		return contraption.getAttachment(attachmentClass);
	}
	
	/* Translating/moving of contraptions */
	
	public static ContraptionPosition getPosition(ServerShip contraption, boolean massCenter) {
		ContraptionPosition position = new ContraptionPosition(contraption.getTransform());
		
		if (!massCenter) {
			AABBic shipBounds = contraption.getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new Vec3d(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new Vec3d(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(contraption.getInertiaData().getCenterOfMassInShip()).add(new Vec3d(0.5, 0.5, 0.5));
			Vec3d centerOfMassOffset = PhysicUtility.toWorldPos(contraption.getTransform(), shipCoordMassCenter).sub(PhysicUtility.toWorldPos(contraption.getTransform(), shipCoordCenter));
			
			position.getPosition().subI(centerOfMassOffset);	
		}
		
		return position;
	}
	
	public void setPosition(ServerShip contraption, ContraptionPosition position, boolean massCenter) {
		if (!massCenter) {
			AABBic shipBounds = contraption.getShipAABB();
			Vec3d shipCoordCenter = MathUtility.getMiddle(new Vec3d(shipBounds.minX(), shipBounds.minY(), shipBounds.minZ()), new Vec3d(shipBounds.maxX(), shipBounds.maxY(), shipBounds.maxZ()));
			Vec3d shipCoordMassCenter = Vec3d.fromVec(contraption.getInertiaData().getCenterOfMassInShip()).add(new Vec3d(0.5, 0.5, 0.5));
			Vec3d centerOfMassOffset = PhysicUtility.toWorldPos(contraption.getTransform(), shipCoordMassCenter).sub(PhysicUtility.toWorldPos(contraption.getTransform(), shipCoordCenter));
			
			position = new ContraptionPosition(position);
			position.getPosition().addI(centerOfMassOffset);
		}
		VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).teleportShip(contraption, position.toTeleport());
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
	
	public BlockPos createContraptionAt(ContraptionPosition contraptionPosition, float scale) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		
		// Get parent ship (if existing)
		Ship parentContraption = VSGameUtilsKt.getShipManagingPos(level, contraptionPosition.getPosition().writeTo(new Vector3d()));
		
		// Apply parent ship translation if available
		if (parentContraption != null) {
			contraptionPosition.toWorldPosition(parentContraption.getTransform());
		}
		
		// Create new contraption
		String dimensionId = getDimensionId();
		Ship newContraption = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewShipAtBlock(contraptionPosition.getPositionJOMLi(), false, scale, dimensionId);
		
		// Stone for safety reasons
		BlockPos pos2 = PhysicUtility.toContraptionBlockPos(newContraption.getTransform(), MathUtility.toBlockPos(contraptionPosition.getPosition()));
		level.setBlock(pos2, Blocks.STONE.defaultBlockState(), 3);
		
		// Teleport ship to final destination
		VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).teleportShip((ServerShip) newContraption, contraptionPosition.toTeleport());
		
		return pos2;
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
	
	public boolean convertToContraption(AABB areaBounds, boolean removeOriginal, float scale) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		
		BlockPos structureCornerMin = null;
		BlockPos structureCornerMax = null;
		
		// Floor bounds
		int areaMinBlockX = (int) Math.floor(areaBounds.minX);
		int areaMinBlockY = (int) Math.floor(areaBounds.minY);
		int areaMinBlockZ = (int) Math.floor(areaBounds.minZ);
		int areaMaxBlockX = (int) Math.floor(areaBounds.maxX);
		int areaMaxBlockY = (int) Math.floor(areaBounds.maxY);
		int areaMaxBlockZ = (int) Math.floor(areaBounds.maxZ);
		
		// Check for solid blocks and invalid blocks, shrink bounds to actual size
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
		
		if (!hasSolids) return false;
		
		// Safety check, if (for what ever reason) no corners could be calculated, set center block as bounds
		if (structureCornerMax == null) structureCornerMax = structureCornerMin = MathUtility.toBlockPos(areaBounds.getCenter().x(), areaBounds.getCenter().y(), areaBounds.getCenter().z());
		
		// Create new contraption at center of bounds
		Vec3d contraptionWorldPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
		ContraptionPosition contraptionPosition = new ContraptionPosition(new Quaterniond(new Vec3d(0, 1, 1), 0), contraptionWorldPos, null);
		BlockPos contraptionBlockPos = createContraptionAt(contraptionPosition, scale);
		Ship contraption = PhysicUtility.getContraptionOfBlock(level, contraptionBlockPos);
		
		// Copy blocks to the new contraption
		for (int x = areaMinBlockX; x <= areaMaxBlockX; x++) {
			for (int z = areaMinBlockZ; z <= areaMaxBlockZ; z++) {
				for (int y = areaMinBlockY; y <= areaMaxBlockY; y++) {
					BlockPos itPos = new BlockPos(x, y, z);
					
					BlockPos relative = itPos.subtract(MathUtility.toBlockPos(contraptionWorldPos));
					BlockPos shipPos = contraptionBlockPos.offset(relative);
					
					GameUtility.copyBlock(level, itPos, shipPos);
					
				}
			}
		}
		
		// Remove original blocks
		if (removeOriginal) {
			for (int x = structureCornerMin.getX(); x <= structureCornerMax.getX(); x++) {
				for (int z = structureCornerMin.getZ(); z <= structureCornerMax.getZ(); z++) {
					for (int y = structureCornerMin.getY(); y <= structureCornerMax.getY(); y++) {
						GameUtility.removeBlock(level, new BlockPos(x, y, z));
					}
				}
			}
		}

		// Trigger updates both contraptions
		for (int x = structureCornerMin.getX(); x <= structureCornerMax.getX(); x++) {
			for (int z = structureCornerMin.getZ(); z <= structureCornerMax.getZ(); z++) {
				for (int y = structureCornerMin.getY(); y <= structureCornerMax.getY(); y++) {
					BlockPos itPos = new BlockPos(x, y, z);

					BlockPos relative = itPos.subtract(MathUtility.toBlockPos(contraptionWorldPos));
					BlockPos shipPos = contraptionBlockPos.offset(relative);
					
					GameUtility.triggerUpdate(level, itPos);
					GameUtility.triggerUpdate(level, shipPos);
				}
			}
		}

		// Set the final position gain, since the contraption moves slightly if blocks are added
		if (contraption != null) {
			PhysicUtility.setPosition((ServerLevel) level, (ServerShip) contraption, contraptionPosition, false);
		}
		
		return true;
		
	}
	
	public boolean assembleToContraption(List<BlockPos> blocks, boolean removeOriginal, float scale) {
		assert level instanceof ServerLevel : "Can't manage contraptions on client side!";
		
		if (blocks.isEmpty()) {
			return false;
		}
		
		BlockPos structureCornerMin = blocks.get(0);
		BlockPos structureCornerMax = blocks.get(0);
		boolean hasSolids = false;
		
		// Calculate bounds of the area containing all blocks adn check for solids and invalid blocks
		for (BlockPos itPos : blocks) {
			if (PhysicUtility.isSolidContraptionBlock(level.getBlockState(itPos))) {
				structureCornerMin = MathUtility.getMinCorner(structureCornerMin, itPos);
				structureCornerMax = MathUtility.getMaxCorner(structureCornerMax, itPos);
				hasSolids = true;
			}
		}
		
		if (!hasSolids) return false;

		// Create new contraption at center of bounds
		Vec3d contraptionWorldPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
		ContraptionPosition contraptionPosition = new ContraptionPosition(new Quaterniond(new Vec3d(0, 1, 1), 0), contraptionWorldPos, null);
		BlockPos contraptionBlockPos = createContraptionAt(contraptionPosition, scale);
		Ship contraption = PhysicUtility.getContraptionOfBlock(level, contraptionBlockPos);
		
		// Copy blocks and check if the center block got replaced (is default a stone block)
		boolean centerBlockReplaced = false;
		for (BlockPos itPos : blocks) {
			BlockPos relative = itPos.subtract(MathUtility.toBlockPos(contraptionWorldPos));
			BlockPos shipPos = contraptionBlockPos.offset(relative);
			
			GameUtility.copyBlock(level, itPos, shipPos);
			
			if (relative.equals(BlockPos.ZERO)) centerBlockReplaced = true;
		}
		
		// If center block got not replaced, remove the stone block
		if (!centerBlockReplaced) {
			level.setBlock(contraptionBlockPos, Blocks.AIR.defaultBlockState(), 3);
		}
		
		// Remove original blocks
		if (removeOriginal) {
			for (BlockPos itPos : blocks) {
				GameUtility.removeBlock(level, itPos);
			}
		}
		
		// Trigger updates on both contraptions
		for (BlockPos itPos : blocks) {
			BlockPos relative = itPos.subtract(MathUtility.toBlockPos(contraptionWorldPos));
			BlockPos shipPos = contraptionBlockPos.offset(relative);
			
			GameUtility.triggerUpdate(level, itPos);
			GameUtility.triggerUpdate(level, shipPos);
		}

		// Set the final position gain, since the contraption moves slightly if blocks are added
		if (contraption != null) {
			PhysicUtility.setPosition((ServerLevel) level, (ServerShip) contraption, contraptionPosition, false);
		}
		
		return true;
		
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