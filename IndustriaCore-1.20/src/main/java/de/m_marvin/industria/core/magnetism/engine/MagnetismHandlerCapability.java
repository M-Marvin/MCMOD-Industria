package de.m_marvin.industria.core.magnetism.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.magnetism.engine.network.SMagneticInfluencePackage;
import de.m_marvin.industria.core.magnetism.engine.network.SSyncMagneticPackage;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import de.m_marvin.industria.core.magnetism.types.MagneticFieldInfluence;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.registries.IndustriaTags;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.StructureFinder;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MagnetismHandlerCapability implements ICapabilitySerializable<ListTag> {

	/* Capability handling */
	
	private LazyOptional<MagnetismHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == Capabilities.MAGNETISM_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	protected final Level level;
	protected final Map<BlockPos, MagneticFieldInfluence> pos2influenceMap = new HashMap<>();
	protected final Map<BlockPos, MagneticField> pos2fieldMap = new HashMap<>();
	protected final Map<Long, MagneticField> id2fieldMap = new HashMap<>();
	protected final Set<MagneticField> fieldSet = Collections.synchronizedSet(new HashSet<>());
	protected long idCounter = 0;
	
	@Override
	public ListTag serializeNBT() {
		ListTag tag = new ListTag();
		for (MagneticField field : this.fieldSet) {
			if (this.pos2fieldMap.containsValue(field)) {
				
				if (field.clearInvalidInfluences(this.level)) {
					IndustriaCore.LOGGER.info("Removed invalid magnetic field!");
					continue;
				}
				
				tag.add(field.serialize());
			}
		}
		IndustriaCore.LOGGER.info("Saved " + tag.size() + "/" + this.fieldSet.size() + " magnetic fields");
		return tag;
	}

	@Override
	public void deserializeNBT(ListTag nbt) {
		this.pos2influenceMap.clear();
		this.pos2fieldMap.clear();
		this.id2fieldMap.clear();
		this.fieldSet.clear();
		
		for (int i = 0; i < nbt.size(); i++) {
			MagneticField field = MagneticField.deserialize(nbt.getCompound(i));
			addField(field);
		}
		IndustriaCore.LOGGER.info("Loaded " + this.fieldSet.size() + "/" + nbt.size() + " magnetic fields");
	}
	
	public MagnetismHandlerCapability(Level level) {
		this.level = level;
	}
 
	/* Event handling */

	@SubscribeEvent
	public static void onLevelTick(LevelTickEvent event) {
		if (event.phase == Phase.START) {
			Level level = event.level;
			MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
			
			handler.updateFieldInduction();
		}
	}
	
	@SubscribeEvent
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		ServerLevel level = event.getLevel();
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		
		List<MagneticField> fields = handler.getFieldsInChunk(event.getPos());
		if (fields.size() > 0) {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(() -> event.getPlayer()), new SSyncMagneticPackage(fields, event.getPos(), SyncRequestType.ADDED));
		}
	}
	
	@SubscribeEvent
	public static void onClientUnloadChunk(ChunkWatchEvent.UnWatch event) {
		ServerLevel level = event.getLevel();
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		
		List<MagneticField> fields = handler.getFieldsInChunk(event.getPos());
		if (fields.size() > 0) {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(() -> event.getPlayer()), new SSyncMagneticPackage(fields, event.getPos(), SyncRequestType.REMOVED));
		}
	}
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getLevel();
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		
		if (event.getState().is(IndustriaTags.Blocks.MAGNETIC)) {
			
			handler.setFieldInfluence(new MagneticFieldInfluence(event.getPos()));
			
		} else {
			
			handler.removeFieldInfluence(event.getPos());
			
		}
		
	}
	
	/* MagneticField handling */

	private long getNextId() {
		long l = this.idCounter - 1;
		while (this.id2fieldMap.keySet().contains(this.idCounter) && this.idCounter != l) this.idCounter++;
		if (this.idCounter == l) throw new IllegalStateException("Unable to generate new field id's!");
		return this.idCounter;
	}
	
	public void addField(MagneticField field) {
		for (MagneticFieldInfluence influence : field.getInfluences()) {
			this.pos2influenceMap.put(influence.getPos(), influence);
			MagneticField removedField = this.pos2fieldMap.put(influence.getPos(), field);
			if (removedField != null) this.fieldSet.remove(removedField);
		}
		this.fieldSet.add(field);
		this.id2fieldMap.put(field.getId(), field);
	}

	public void removeField(MagneticField field) {
		for (MagneticFieldInfluence influence : field.getInfluences()) {
			this.pos2influenceMap.remove(influence.getPos());
			this.pos2fieldMap.remove(influence.getPos());
		}
		this.fieldSet.remove(field);
		this.id2fieldMap.remove(field.getId());
	}
	
	public void setFieldInfluence(MagneticFieldInfluence influence) {
		MagneticFieldInfluence overridenInfluence = this.pos2influenceMap.put(influence.getPos(), influence);
		MagneticField field = this.pos2fieldMap.get(influence.getPos());
		
		if (field == null) {
			
			makeField(influence);
			
		} else {
			
			if (overridenInfluence != null) {
				field.removeInfluence(overridenInfluence);
			}
			field.addInfluence(influence);
			field.updateField(this.level);
			
		}
		
		if (!this.level.isClientSide()) {
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(influence.getPos())), new SMagneticInfluencePackage.SAddInfluencePackage(influence));
		}
		
	}
	
	public void removeFieldInfluence(BlockPos pos) {
		MagneticFieldInfluence overridenInfluence = this.pos2influenceMap.remove(pos);
		if (overridenInfluence != null) {
			MagneticField field = this.pos2fieldMap.remove(pos);
			
			if (field != null) {
				
				if (!this.level.isClientSide()) field.removeInducer((ServerLevel) this.level);
				this.fieldSet.remove(field);
				
				field.removeInfluence(overridenInfluence);
				addToFields(field.getInfluences());
				
			}
			
		}
		
		if (!this.level.isClientSide()) {
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(pos)), new SMagneticInfluencePackage.SRemoveInfluencePackage(pos));
		}
		
	}
	
	public void updateField(BlockPos influencePos) {
		MagneticField field = this.pos2fieldMap.get(influencePos);
		if (field != null) {
			field.updateField(level);
		}
	}
	
	public void addToFields(Set<MagneticFieldInfluence> influences) {
		
		while (influences.size() > 0) {
			
			influences.removeAll(makeField(influences.stream().findAny().get()));
			
		}
		
	}
	
	public Set<MagneticFieldInfluence> makeField(MagneticFieldInfluence influence) {
		
		BlockPos startPos = influence.getPos();
		Optional<List<BlockPos>> fieldBlocks = StructureFinder.findStructureInRange(this.level, startPos, 120000, 48, state -> state.is(IndustriaTags.Blocks.MAGNETIC));
		
		if (fieldBlocks.isPresent() && fieldBlocks.get().size() > 0) {
			
			MagneticField field = new MagneticField(getNextId());
			
			for (BlockPos influencePos : fieldBlocks.get()) {
				
				MagneticFieldInfluence influenceAtPos = this.pos2influenceMap.get(influencePos);
				if (influenceAtPos == null) continue;
				MagneticField overridenField = this.pos2fieldMap.put(influencePos, field);
				field.addInfluence(influenceAtPos);
				
				if (overridenField != null) {
					if (!this.level.isClientSide()) overridenField.removeInducer((ServerLevel) this.level);
					this.fieldSet.remove(overridenField);
				}
				
			}
			
			this.fieldSet.add(field);
			this.id2fieldMap.put(field.getId(), field);
			field.updateField(this.level);
			
			return field.getInfluences();
			
		}
		
		Set<MagneticFieldInfluence> set = new HashSet<>();
		set.add(influence);
		return set;
		
	}
	
	public List<MagneticField> getFieldsInChunk(ChunkPos chunk) {
		List<MagneticField> fields = new ArrayList<MagneticField>();
 		for (MagneticField field : this.fieldSet) {
 			BlockPos pos = MathUtility.getMiddleBlock(field.getMinPos(), field.getMaxPos());
			if (MathUtility.isInChunk(chunk, pos) || MathUtility.isInChunk(chunk, pos)) {
				fields.add(field);
			}
		}
 		return fields;
	}
	
	public Collection<MagneticFieldInfluence> getMagneticInfluences() {
		return this.pos2influenceMap.values();
	}
	
	public Collection<MagneticField> getMagneticFields() {
		return this.fieldSet;
	}
	
	public MagneticField getFieldAt(BlockPos pos) {
		return this.pos2fieldMap.get(pos);
	}
	
	public MagneticField getField(long id) {
		return this.id2fieldMap.get(id);
	}
	
	public MagneticFieldInfluence getInfluenceOf(BlockPos pos) {
		return this.pos2influenceMap.get(pos);
	}
	
	public void updateFieldInduction() {
		
		for (MagneticField field : this.fieldSet) {
			field.updateInduction(this.level, this.fieldSet);
		}
		
	}
	
}
