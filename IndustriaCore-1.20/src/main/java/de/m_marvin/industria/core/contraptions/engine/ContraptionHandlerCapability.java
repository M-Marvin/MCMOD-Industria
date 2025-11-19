package de.m_marvin.industria.core.contraptions.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.apigame.world.ShipWorldCore;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.contraptions.ContraptionUtility;
import de.m_marvin.industria.core.contraptions.engine.types.ContraptionHitResult;
import de.m_marvin.industria.core.contraptions.engine.types.ContraptionPosition;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.ClientContraption;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.Contraption;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.ServerContraption;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.ssdplugins.engine.IStructureTemplateExtended;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec3d;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Bus.FORGE)
public class ContraptionHandlerCapability implements ICapabilitySerializable<CompoundTag> {
	
	/* Capability handling */
	
	private LazyOptional<ContraptionHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == Capabilities.CONTRAPTION_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private Long2ObjectMap<Set<String>> contraptionTags = new Long2ObjectArrayMap<>();
	private Level level;
	
	private static MinecraftServer staticServer;
	
	@Override
	public CompoundTag serializeNBT() {
		long[] tagsToDelete = this.contraptionTags.keySet().longStream().filter(id -> getContraptionById(id) == null).toArray();
		for (long id : tagsToDelete) this.contraptionTags.remove((long) id);
		
		CompoundTag tag = new CompoundTag();
		ListTag contraptionTagsList = new ListTag();
		for (Entry<Long, Set<String>> entry : this.contraptionTags.long2ObjectEntrySet()) {
			CompoundTag contraptionTags = new CompoundTag();
			contraptionTags.putLong("Contraption", entry.getKey());
			ListTag tagList = new ListTag();
			for (String tags : entry.getValue()) {
				tagList.add(StringTag.valueOf(tags));
			}
			contraptionTags.put("Tags", tagList);
			contraptionTagsList.add(contraptionTags);
		}
		tag.put("ContraptionTags", contraptionTagsList);
		IndustriaCore.LOGGER.info("Saved " + contraptionTagsList.size() + "/" + this.contraptionTags.size() + " constraption tags");
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		ListTag contraptionTagList = tag.getList("ContraptionTags", CompoundTag.TAG_COMPOUND);
		this.contraptionTags.clear();
		for (int i = 0; i < contraptionTagList.size(); i++) {
			CompoundTag contraptionTags = contraptionTagList.getCompound(i);
			long contraptionId = contraptionTags.getLong("Contraption");
			ListTag tagList = contraptionTags.getList("Tags", StringTag.TAG_STRING);
			Set<String> tags = new HashSet<>();
			for (int i2 = 0; i2 < tagList.size(); i2++) tags.add(tagList.getString(i2));
			this.contraptionTags.put(contraptionId, tags);
		}
		IndustriaCore.LOGGER.info("Loaded " + this.contraptionTags.size() + "/" + contraptionTagList.size() + " contraption tags");
	}
	
	/* Event handling */
	
	@SubscribeEvent
	public static void onServerStartup(ServerStartedEvent event) {
		staticServer = event.getServer();
	}
	
	/* End of events */
	
	public static MinecraftServer getStaticServer() {
		return staticServer;
	}
	
	public ContraptionHandlerCapability(Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return this.level;
	}
	
	public ResourceLocation getDimension() {
		return this.level.dimension().location();
	}
	
	public long getGroundBodyId() {
		if (this.level.isClientSide()) return -1;
		ServerShipWorldCore shipWorld = getShipWorld();
		return shipWorld.getDimensionToGroundBodyIdImmutable().get(Registries.DIMENSION.location().toString() + ":" + getDimension().toString());
	}
	
	
	
	/* Accessing contraptions */

	@SuppressWarnings("unchecked")
	public <T extends ShipWorldCore> T getShipWorld() {
		return (T) VSGameUtilsKt.getShipObjectWorld(level);
	}

	public <T extends Contraption> List<T> getAllContraptions(boolean onlyThisDimension) {
		return getShipWorld().getAllShips().stream()
			.map(s -> { T c = getContraptionOfShip(s); return c; })
			.filter(c -> !onlyThisDimension || c.getPosition().getDimension().equals(this.getDimension()))
			.toList();
	}

	public <T extends Contraption> List<T> getLoadedContraptions(boolean onlyThisDimension) {
		return getShipWorld().getLoadedShips().stream()
			.map(s -> { T c = getContraptionOfShip(s); return c; })
			.filter(c -> !onlyThisDimension || c.getPosition().getDimension().equals(this.getDimension()))
			.toList();
	}
	
	public <T extends Contraption> T getContraptionOfShip(Ship ship) {
		return ship == null ? null : getContraptionById(ship.getId());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Contraption> T getContraptionById(long id) {
		Optional<Ship> ship = getShipWorld().getAllShips().stream().filter(s -> s.getId() == id).findAny();
		if (ship.isPresent()) {
			if (!this.level.isClientSide() && ship.get() instanceof ServerShip serverShip) {
				return (T) new ServerContraption(this.level.getServer(), serverShip);
			} else if (ship.get() instanceof ClientShip clientShip) {
				return (T) new ClientContraption(clientShip);
			}
		}
		return null;
	}

	public <T extends Contraption> List<T> getContraptionsWithTag(String tag) {
		return this.contraptionTags.long2ObjectEntrySet().stream()
			.filter(e -> e.getValue().contains(tag))
			.map(e -> { T c = getContraptionById(e.getLongKey()); return c; })
			.toList();
	}
	
	public <T extends Contraption> List<T> getContraptionsWithName(String name, boolean onlyThisDimension) {
		List<T> list = getAllContraptions(onlyThisDimension);
		return list.stream()
			.filter(c -> c.getNameStr().equals(name))
			.toList();
	}
	
	public Long2ObjectMap<Set<String>> getContraptionTags() {
		return this.contraptionTags;
	}
	
	/* Searching for contraptions */
	
	public <T extends Contraption> List<T> getContraptionIntersecting(BlockPos position)  {
		return StreamSupport.stream(VSGameUtilsKt.getShipsIntersecting(level, new AABB(position, position)).spliterator(), false)
			.map(s -> { T c = getContraptionOfShip(s); return c; })
			.toList();
	}
	
	public <T extends Contraption> T getContraptionOfBlock(BlockPos shipBlockPos) {
		return getContraptionOfShip(VSGameUtilsKt.getShipManagingPos(level, shipBlockPos));
	}

	/* Raycasting for contraptions */
	
	public ContraptionHitResult clipForContraption(Vec3d from, Vec3d to) {
		ClipContext clipContext = new ClipContext(from.writeTo(new Vec3(0, 0, 0)), to.writeTo(new Vec3(0, 0, 0)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null);
		HitResult clipResult = level.clip(clipContext);
		
		if (clipResult.getType() == Type.BLOCK) {
			BlockPos hitBlockPos = ((BlockHitResult) clipResult).getBlockPos();
			Contraption contraption = getContraptionOfBlock(hitBlockPos);
			if (contraption != null) {
				Vec3 hitPosition = clipResult.getLocation();
				return ContraptionHitResult.hit(hitPosition, hitBlockPos, contraption);
			}
			
		}
		return ContraptionHitResult.miss(clipResult.getLocation());
	}
	
	/* Translating/moving of contraptions */
	
	public boolean teleportContraption(ServerContraption contraption, ContraptionPosition position, boolean useGeometricCenter) {
		if (this.level.isClientSide()) return false;
		if (!position.getDimension().equals(this.getDimension())) return false;
		
		if (!position.getDimension().equals(contraption.getPosition().getDimension())) {
			
			AABBic bounds = contraption.getContraptionSpaceBounds();
			if (bounds != null) {
				
				// Get access to other dimension
				ResourceLocation dimension = contraption.getPosition().getDimension();
				ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, dimension);
				Level otherLevel = this.level.getServer().getLevel(dimensionKey);
				
				// Calculate structure parameters
				BlockPos posMin = new BlockPos(bounds.minX(), bounds.minY(), bounds.minZ());
				BlockPos posMax = new BlockPos(bounds.maxX() - 1, bounds.maxY() - 1, bounds.maxZ() - 1);
				BlockPos size = posMax.subtract(posMin).offset(1, 1, 1);
				BlockPos middle = MathUtility.getMiddleBlock(posMin, posMax);
				
				// Copy structure to template
				StructureTemplate template = new StructureTemplate();
				template.fillFromWorld(otherLevel, posMin, size, false, null);
				
				// Remove blocks
				GameUtility.removeBlocksAndConduits(otherLevel, posMin, posMax);
				otherLevel.setBlock(middle, Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
				
				// Teleport ship
				ServerShipWorldCore shipWorld = getShipWorld();
				shipWorld.teleportShip(contraption.getShip(), position.toTeleport(contraption, useGeometricCenter));
				
				// Place structure in new dimension at center of ship
				Vec3i templateSize = template.getSize();
				BlockPos origin = contraption.getCenterPos().subtract(new BlockPos(templateSize.getX() / 2, templateSize.getY() / 2, templateSize.getZ() / 2));
				StructurePlaceSettings settings = new StructurePlaceSettings();
				template.placeInWorld((ServerLevel) this.level, origin, origin, settings, this.level.getRandom(), 3);
				
				// Remove safety block if left over
				if (this.level.getBlockState(origin).getBlock() == Blocks.ERROR_BLOCK.get()) {
					this.level.removeBlock(origin, false);
				}
				
				return true;
			}
			
			// Unable to teleport, ship is broken, remove
			removeContraption(contraption);
			return false;
		} else {

			// Just teleport ship, no reallocation required
			ServerShipWorldCore shipWorld = getShipWorld();
			shipWorld.teleportShip(contraption.getShip(), position.toTeleport(contraption, useGeometricCenter));
			
			return true;
		}
		
	}
	
	/* Listing and creation contraptions in the world */
	
	public ServerContraption createContraptionAt(ContraptionPosition contraptionPosition, float scale) {
		if (this.level.isClientSide()) return null;
		if (!contraptionPosition.getDimension().equals(this.getDimension())) return null;
		
		// Get parent ship (if existing)
		Ship parentContraption = VSGameUtilsKt.getShipManagingPos(level, contraptionPosition.getPosition().writeTo(new Vector3d()));
		
		// Apply parent ship translation if available
		if (parentContraption != null) {
			contraptionPosition.toWorldPosition(parentContraption.getTransform());
		}
		
		// Create new contraption
		String dimensionId = Registries.DIMENSION.location().toString() + ":" + getDimension().toString();
		ServerShip newShip = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewShipAtBlock(new Vector3i(0, 0, 0), false, scale, dimensionId);
		ServerContraption contraption = getContraptionOfShip(newShip);
		
		// Block for safety reasons
		level.setBlock(contraption.getCenterPos(), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
		
		// Teleport ship to final destination
		VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).teleportShip((ServerShip) newShip, contraptionPosition.toTeleport());
		
		return contraption;
	}
	
	public boolean removeContraption(ServerContraption contraption) {
		if (this.level.isClientSide()) return false;
		if (!contraption.getPosition().getDimension().equals(this.getDimension())) return false;
		
		AABBic bounds = contraption.getContraptionSpaceBounds();
		if (bounds != null) {
			
			try {
				
				// Remove all blocks and block entities and conduits
				GameUtility.removeBlocksAndConduits(this.level, new BlockPos(bounds.minX(), bounds.minY(), bounds.minZ()), new BlockPos(bounds.maxX() - 1, bounds.maxY() - 1, bounds.maxZ() - 1));
				
				return true;
				
			} catch(ArrayIndexOutOfBoundsException e) {
			} finally {

				// The ship is empty, this should never happen, remove manually to prevent further problems
				ServerShipWorldCore shipWorld = getShipWorld();
				shipWorld.deleteShip(contraption.getShip());
				
			}

			return true;
			
		}

		// The ship is empty, this should never happen, remove manually to prevent further problems
		ServerShipWorldCore shipWorld = getShipWorld();
		shipWorld.deleteShip(contraption.getShip());
		return true;
		
	}
	
	public boolean convertToContraption(BlockPos pos1, BlockPos pos2, boolean removeOriginal, float scale) {
		if (this.level.isClientSide()) return false;
		
		// Check for solid blocks and invalid blocks, shrink bounds to actual size
		BlockPos structureCornerMin1 = MathUtility.getMinCorner(pos1, pos2);
		BlockPos structureCornerMax1 = MathUtility.getMaxCorner(pos1, pos2);
		BlockPos structureCornerMin = pos1;
		BlockPos structureCornerMax = pos2;
		boolean hasSolids = false;
		for (int x = structureCornerMin1.getX(); x <= structureCornerMax1.getX(); x++) {
			for (int z = structureCornerMin1.getZ(); z <= structureCornerMax1.getZ(); z++) {
				for (int y = structureCornerMin1.getY(); y <= structureCornerMax1.getY(); y++) {
					BlockPos itPos = new BlockPos(x, y, z);
					BlockState itState = level.getBlockState(itPos);
					if (ContraptionUtility.isValidContraptionBlock(itState)) {
						structureCornerMin = MathUtility.getMinCorner(structureCornerMin, itPos);
						structureCornerMax = MathUtility.getMaxCorner(structureCornerMax, itPos);
					}
					if (ContraptionUtility.isSolidContraptionBlock(itState)) hasSolids = true;
				}
			}
		}
		
		if (!hasSolids) return false;
		
		// Safety check, if (for what ever reason) no corners could be calculated, set center block as bounds
		if (structureCornerMax == null || structureCornerMin == null) structureCornerMax = structureCornerMin = MathUtility.getMiddleBlock(pos1, pos2);
		
		// Create new contraption at center of bounds
		Vec3d contraptionWorldPos = MathUtility.getMiddle(structureCornerMin, structureCornerMax);
		ContraptionPosition contraptionPosition = new ContraptionPosition(new Quaterniond(new Vec3d(0, 1, 1), 0), contraptionWorldPos, this.getDimension());
		ServerContraption contraption = createContraptionAt(contraptionPosition, scale);
		
		// Create template from world blocks
		StructureTemplate template = new StructureTemplate();
		BlockPos structSize = structureCornerMax.subtract(structureCornerMin).offset(1, 1, 1);
		template.fillFromWorld(this.level, structureCornerMin, structSize, false, null);
		
		// Place blocks on ship
		BlockPos contraptionCenter = contraption.getCenterPos();
		BlockPos shipPosOrigin = contraptionCenter.offset(-structSize.getX() / 2, -structSize.getY() / 2, -structSize.getZ() / 2);
		StructurePlaceSettings settings = new StructurePlaceSettings();
		settings.setIgnoreEntities(true);
		if (!template.placeInWorld((ServerLevel) this.level, shipPosOrigin, shipPosOrigin, settings, this.level.getRandom(), 3)) {
			
			// Placement failed, place error blocks to mark intended bounds
			this.level.setBlock(shipPosOrigin.offset(0, 0, 0), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(shipPosOrigin.offset(0, 0, structSize.getZ()), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(shipPosOrigin.offset(structSize.getX(), 0, 0), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(shipPosOrigin.offset(structSize.getX(), 0, structSize.getZ()), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(shipPosOrigin.offset(0, structSize.getY(), 0), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(shipPosOrigin.offset(0, structSize.getY(), structSize.getZ()), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(shipPosOrigin.offset(structSize.getX(), structSize.getY(), 0), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(shipPosOrigin.offset(structSize.getX(), structSize.getY(), structSize.getZ()), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			
		}
		
		// Remove all blocks, block entities and conduits in origin bounds
		if (removeOriginal) GameUtility.removeBlocksAndConduits(this.level, structureCornerMin, structureCornerMax);
		if (this.level.getBlockState(contraptionCenter).getBlock() == Blocks.ERROR_BLOCK.get()) {
			this.level.removeBlock(contraptionCenter, false);
		}
		
		// Set the final position gain, since the contraption moves slightly if blocks are added
		if (contraption != null) teleportContraption(contraption, contraptionPosition, true);
		
		return true;
		
	}
	
	public boolean assembleToContraption(List<BlockPos> blocks, boolean removeOriginal, float scale) {
		if (this.level.isClientSide()) return false;
		if (blocks.isEmpty()) return false;
		
		// Calculate bounds of the area containing all blocks adn check for solids and invalid blocks
		BlockPos structureCornerMin = blocks.get(0);
		BlockPos structureCornerMax = blocks.get(0);
		boolean hasSolids = false;
		for (BlockPos itPos : blocks) {
			if (ContraptionUtility.isSolidContraptionBlock(level.getBlockState(itPos))) {
				BlockState itState = level.getBlockState(itPos);
				if (ContraptionUtility.isValidContraptionBlock(itState)) {
					structureCornerMin = MathUtility.getMinCorner(structureCornerMin, itPos);
					structureCornerMax = MathUtility.getMaxCorner(structureCornerMax, itPos);
				}
				if (ContraptionUtility.isSolidContraptionBlock(itState)) hasSolids = true;
			}
		}
		
		if (!hasSolids) return false;
		
		// Copy original blocks and conduits
		StructureTemplate template = new StructureTemplate();
		BlockPos templateOrigin = ((IStructureTemplateExtended) template).fillFromLevelPosIterable(level, blocks, null);
		
		// Create new contraption at min corner
		Vec3d shipPos = Vec3d.fromVec(templateOrigin).addI(Vec3d.fromVec(template.getSize()).div(2.0));
		ContraptionPosition contraptionPosition = new ContraptionPosition(new Quaterniond(new Vec3d(0, 1, 1), 0), shipPos, this.getDimension());
		ServerContraption contraption = createContraptionAt(contraptionPosition, scale);
		
		// Place blocks on ship
		BlockPos contraptionCenter = contraption.getCenterPos();
		StructurePlaceSettings settings = new StructurePlaceSettings();
		settings.setIgnoreEntities(true);
		
		if (!template.placeInWorld((ServerLevel) this.level, contraptionCenter, contraptionCenter, settings, this.level.getRandom(), 3)) {
			
			// Placement failed, place error blocks to mark intended bounds
			Vec3i structSize = template.getSize();
			this.level.setBlock(contraptionCenter.offset(0, 0, 0), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(contraptionCenter.offset(0, 0, structSize.getZ()), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(contraptionCenter.offset(structSize.getX(), 0, 0), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(contraptionCenter.offset(structSize.getX(), 0, structSize.getZ()), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(contraptionCenter.offset(0, structSize.getY(), 0), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(contraptionCenter.offset(0, structSize.getY(), structSize.getZ()), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(contraptionCenter.offset(structSize.getX(), structSize.getY(), 0), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			this.level.setBlock(contraptionCenter.offset(structSize.getX(), structSize.getY(), structSize.getZ()), Blocks.ERROR_BLOCK.get().defaultBlockState(), 3);
			
		}

		// Remove all blocks, block entities and conduits in origin bounds
		if (removeOriginal) GameUtility.removeBlocksAndConduits(this.level, blocks);
		if (this.level.getBlockState(contraptionCenter).getBlock() == Blocks.ERROR_BLOCK.get()) {
			this.level.removeBlock(contraptionCenter, false);
		}

		// Set the final position gain, since the contraption moves slightly if blocks are added
		if (contraption != null) teleportContraption(contraption, contraptionPosition, true);
		
		return true;
		
	}
	
	public int addConstraint(VSConstraint constraint) {
		ServerShipWorldCore shipWorld = getShipWorld();
		return shipWorld.createNewConstraint(constraint);
	}
	
	public boolean removeConstaint(int constraintId) {
		ServerShipWorldCore shipWorld = getShipWorld();
		return shipWorld.removeConstraint(constraintId);
	}
	
	public VSConstraint getConstraint(int constraintId) {
		return getAllConstraints().get(constraintId);
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, VSConstraint> getAllConstraints() {
		// FIXME [VS2dep] This is horrible!!! hopefully VS2 adds an API for that soon ...
		Map<Integer, VSConstraint> constraints = null;
		try {
			ServerShipWorldCore shipWorld = getShipWorld();
			Field constraintField = ObfuscationReflectionHelper.findField(ServerShipWorldCore.class, "constraints");
			constraintField.setAccessible(true);
			constraints = (Map<Integer, VSConstraint>) constraintField.get(shipWorld);
		} catch (Exception e) {
			IndustriaCore.LOGGER.error("Something went wrong, but the code on that point is janky anyway ...");
			e.printStackTrace();
			constraints = new HashMap<>();
		}
		return constraints;
	}
	
	
}