package de.m_marvin.industria.content.magnetism.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.magnetism.engine.network.SMagneticInfluencePackage;
import de.m_marvin.industria.content.magnetism.types.MagneticField;
import de.m_marvin.industria.content.magnetism.types.MagneticFieldInfluence;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.registries.IndustriaTags;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.StructureFinder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MagnetismHandlerCapability implements ICapabilitySerializable<ListTag> {

	/* Capability handling */
	
	private LazyOptional<MagnetismHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == de.m_marvin.industria.content.magnetism.Capabilities.MAGNETISM_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	protected final Level level;
	protected final HashMap<BlockPos, MagneticFieldInfluence> pos2influenceMap = new HashMap<>();
	protected final HashMap<BlockPos, MagneticField> pos2fieldMap = new HashMap<>();
	protected final HashSet<MagneticField> fieldSet = new HashSet<>();
	
	@Override
	public ListTag serializeNBT() {
		ListTag tag = new ListTag();
		for (MagneticField field : this.fieldSet) {
			if (this.pos2fieldMap.containsValue(field)) {
				tag.add(field.serialize());
			}
		}
		Industria.LOGGER.info("Saved " + tag.size() + "/" + this.fieldSet.size() + " magnetic fields");
		return tag;
	}

	@Override
	public void deserializeNBT(ListTag nbt) {
		this.pos2influenceMap.clear();
		this.pos2fieldMap.clear();
		this.fieldSet.clear();
		
		for (int i = 0; i < nbt.size(); i++) {
			MagneticField field = MagneticField.deserialize(nbt.getCompound(i));
			
			for (MagneticFieldInfluence influence : field.getInfluences()) {
				this.pos2influenceMap.put(influence.getPos(), influence);
				this.pos2fieldMap.put(influence.getPos(), field);
			}
			this.fieldSet.add(field);
		}
		Industria.LOGGER.info("Loaded " + this.fieldSet.size() + "/" + nbt.size() + " magnetic fields");
	}
	
	public MagnetismHandlerCapability(Level level) {
		this.level = level;
	}
 
	/* Event handling */
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getLevel();
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, de.m_marvin.industria.content.magnetism.Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		
		if (event.getState().is(IndustriaTags.Blocks.MAGNETIC)) {
			
			System.out.println("Magnetic block detected !");
			
			handler.setFieldInfluence(new MagneticFieldInfluence(event.getPos()));
			
		} else {
			
			System.out.println("Block removed !");
			
			handler.removeFieldInfluence(event.getPos());
			
		}

		System.out.println(handler.pos2fieldMap.size() + " / " + handler.pos2influenceMap.size() + " / " + handler.fieldSet.size());
		
	}
	
	/* MagneticField handling */
	
	public void setFieldInfluence(MagneticFieldInfluence influence) {
		MagneticFieldInfluence overridenInfluence = this.pos2influenceMap.put(influence.getPos(), influence);
		MagneticField field = this.pos2fieldMap.get(influence.getPos());
		
		if (field == null) {
			
			makeField(influence);
			
		} else {
			
			System.out.println("Replace 1 influence");
			
			if (overridenInfluence != null) {
				field.removeInfluence(overridenInfluence);
			}
			field.addInfluence(influence);
			
		}
		
		if (!this.level.isClientSide()) {
			Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(influence.getPos())), new SMagneticInfluencePackage.SCAddInfluencePackage(influence));
		}
		
	}
	
	public void removeFieldInfluence(BlockPos pos) {
		MagneticFieldInfluence overridenInfluence = this.pos2influenceMap.remove(pos);
		if (overridenInfluence != null) {
			MagneticField field = this.pos2fieldMap.remove(pos);
			
			if (field != null) {
				
				this.fieldSet.remove(field);
				
				field.removeInfluence(overridenInfluence);
				addToFields(field.getInfluences());
				
			}
			
		}
		
		if (!this.level.isClientSide()) {
			Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(pos)), new SMagneticInfluencePackage.SCRemoveInfluencePackage(pos));
		}
		
	}
	
	public void addToFields(Set<MagneticFieldInfluence> influences) {
		
		System.out.println("Integrate " + influences.size() + " influences");
		
		while (influences.size() > 0) {
			
			influences.removeAll(makeField(influences.stream().findAny().get()));
			
		}
		
	}
	
	public Set<MagneticFieldInfluence> makeField(MagneticFieldInfluence influence) {
		
		BlockPos startPos = influence.getPos();
		Optional<List<BlockPos>> fieldBlocks = StructureFinder.findStructure(this.level, startPos, 256, state -> state.is(IndustriaTags.Blocks.MAGNETIC));
		
		if (fieldBlocks.isPresent()) {
			
			MagneticField field = new MagneticField();
			
			for (BlockPos influencePos : fieldBlocks.get()) {
				
				MagneticFieldInfluence influenceAtPos = this.pos2influenceMap.get(influencePos);
				MagneticField overridenField = this.pos2fieldMap.put(influencePos, field);
				field.addInfluence(influenceAtPos);
				
				if (overridenField != null) {
					this.fieldSet.remove(overridenField);
				}
				
			}
			
			this.fieldSet.add(field);
			
			return field.getInfluences();
			
		}
		
		// FIXME !!! Make structure finder return values even if max block count exceeded / Range instead of block limit
		return null;
		
	}
	
	public Collection<MagneticFieldInfluence> getMagneticInfluences() {
		return this.pos2influenceMap.values();
	}
	
	public Collection<MagneticField> getMagneticFields() {
		return this.fieldSet;
	}
	
}
