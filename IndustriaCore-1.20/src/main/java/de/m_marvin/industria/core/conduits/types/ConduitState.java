package de.m_marvin.industria.core.conduits.types;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitNode.NodeType;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class ConduitState extends StateHolder<Conduit, ConduitState> {
	
	public static final Codec<ConduitState> CODEC = codec(Conduits.CONDUITS_REGISTRY.get().getCodec(), Conduit::defaultConduitState).stable();
	
	public ConduitState(Conduit pOwner, ImmutableMap<Property<?>, Comparable<?>> pValues, MapCodec<ConduitState> pPropertiesCodec) {
		super(pOwner, pValues, pPropertiesCodec);
	}
	
	public CompoundTag writeNbt() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Name", Conduits.CONDUITS_REGISTRY.get().getKey(this.owner).toString());
		ImmutableMap<Property<?>, Comparable<?>> immutablemap = getValues();
		if (!immutablemap.isEmpty()) {
			CompoundTag poropertiesTag = new CompoundTag();
			for(Map.Entry<Property<?>, Comparable<?>> entry : immutablemap.entrySet()) {
				Property<?> property = entry.getKey();
				
				poropertiesTag.putString(property.getName(), getName(property, entry.getValue()));
			}
			tag.put("Properties", poropertiesTag);
		}	
		return tag;
	}
	
	public static ConduitState loadNbt(CompoundTag tag) {
		if (!tag.contains("Name", 8)) {
			return Conduits.NONE.get().defaultConduitState();
		} else {
			ResourceLocation resourcelocation = new ResourceLocation(tag.getString("Name"));
			@Nullable Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(resourcelocation);
			if (conduit == null) {
				return Conduits.NONE.get().defaultConduitState();
			} else {
				ConduitState blockstate = conduit.defaultConduitState();
				if (tag.contains("Properties", 10)) {
					CompoundTag compoundtag = tag.getCompound("Properties");
					StateDefinition<Conduit, ConduitState> statedefinition = conduit.getStateDefinition();
					for(String s : compoundtag.getAllKeys()) {
						Property<?> property = statedefinition.getProperty(s);
						if (property != null) {
							blockstate = setValueHelper(blockstate, property, s, compoundtag, tag);
						}
					}
				}
				return blockstate;
			}
		}
	}

	public void writeBuff(FriendlyByteBuf buff) {
		buff.writeNbt(writeNbt());
	}
	
	public static ConduitState readBuff(FriendlyByteBuf buff) {
		return loadNbt(buff.readNbt());
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Comparable<T>> String getName(Property<T> pProperty, Comparable<?> pValue) {
		return pProperty.getName((T)pValue);
	}

	private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S pStateHolder, Property<T> pProperty, String pPropertyName, CompoundTag pPropertiesTag, CompoundTag pBlockStateTag) {
		Optional<T> optional = pProperty.getValue(pPropertiesTag.getString(pPropertyName));
		if (optional.isPresent()) {
			return pStateHolder.setValue(pProperty, optional.get());
		} else {
			IndustriaCore.LOGGER.warn("Unable to read property: {} with value: {} for conduitstate: {}", pPropertyName, pPropertiesTag.getString(pPropertyName), pBlockStateTag.toString());
			return pStateHolder;
		}
	}
	
	public Conduit getConduit() {
		return this.owner;
	}

	public float getNodeMass(ConduitEntity entity) {
		return getConduit().getNodeMass(this, entity);
	}
	
	public float getStiffness(ConduitEntity entity) {
		return getConduit().getStiffness(this, entity);
	}
	
	public float getTension(ConduitEntity entitity) {
		return getConduit().getTension(this, entitity);
	}
	
	public int getClampingLength(ConduitEntity entity) {
		return getConduit().getClampingLength(this, entity);
	}
	
	public float getThickness(ConduitEntity entity) {
		return getConduit().getThickness(this, entity);
	}
	
	public float getSegmentLength(ConduitEntity entity) {
		return getConduit().getSegmentLength(this, entity);
	}
	
	public double getConstraintCompensation(ConduitEntity entity) {
		return getConduit().getConstraintCompensation(this, entity);
	}

	public double getConstraintForce(ConduitEntity entity) {
		return getConduit().getConstraintForce(this, entity);
	}
	
	public SoundType getSoundType(ConduitEntity entity) {
		return getConduit().getSoundType(this, entity);
	}
	
	public NodeType[] getValidNodeTypes(ConduitEntity entity) {
		return getConduit().getValidNodeTypes(this, entity);
	}
	
	public List<ItemStack> getDrops(ConduitEntity entity) {
		return getConduit().getDrops(this, entity);
	}
	
	public boolean collidesWithBlock(BlockPos pos, ConduitEntity entity) {
		return getConduit().collidesWithBlock(pos, this, entity);
	}
	
	public ConduitShape buildShape(ConduitEntity entity) {
		return getConduit().buildShape(this, entity);
	}
	
	public void onBuild(ConduitEntity entity) {
		getConduit().onBuild(this, entity);
	}
	
	public void dismantleShape(ConduitEntity entity) {
		getConduit().dismantleShape(this, entity);
	}
	
	public void onDismantle(ConduitEntity entity) {
		getConduit().onDismantle(this, entity);
	}
	
	public void updateShape(ConduitEntity entity) {
		getConduit().updateShape(this, entity);
	}
	
	public void onPlace(ConduitEntity conduitEntity) {
		getConduit().onPlace(this, conduitEntity);
	}
	
	public void onBreak(ConduitEntity conduitEntity, boolean dropItems) {
		getConduit().onBreak(this, conduitEntity, dropItems);
	}
	
	public void onNodeStateChange(BlockPos nodePos, BlockState nodeState, ConduitEntity conduitEntity) {
		getConduit().onNodeStateChange(nodePos, nodeState, this, conduitEntity);
	}

	public boolean isNone() {
		return this.owner == Conduits.NONE.get();
	}
	
}
