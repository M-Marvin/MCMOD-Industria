package de.m_marvin.industria.core.compound.types.blockentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import de.m_marvin.industria.core.compound.engine.VirtualBlock;
import de.m_marvin.industria.core.compound.types.blocks.CompoundBlock;
import de.m_marvin.industria.core.kinetics.types.blockentities.IKineticBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.TransmissionNode;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.StateTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CompoundBlockEntity extends BlockEntity implements IKineticBlockEntity, WorldlyContainer {
	
	protected Map<Integer, VirtualBlock> parts = new HashMap<>();
	
	public CompoundBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(BlockEntityTypes.COMPOUND_BLOCK.get(), pPos, pBlockState);
	}
	
	public TransmissionNode[] getTransmissionNodes() {
		return this.parts.entrySet().stream()
				.filter(e -> e.getValue().getBlock() instanceof IKineticBlock)
				.flatMap(e -> 
					Stream.of(((IKineticBlock) e.getValue().getBlock())
					.getTransmissionNodes(e.getValue().getLevel(), e.getValue().getPos(), e.getValue().getState()))
					.map(t -> t.withReference(KineticReference.subPart(worldPosition, e.getKey())))
				)
				.toArray(TransmissionNode[]::new);
	}
	
	public BlockState getPartState(int partId) {
		if (!this.parts.containsKey(partId)) return getBlockState();
		return this.parts.get(partId).getState();
	}
	
	public Map<Integer, VirtualBlock> getParts() {
		return parts;
	}
	
	@SuppressWarnings("deprecation")
	public int countParts() {
		return (int) parts.values().stream()
			.filter(v -> !v.getState().isAir() && !v.getState().liquid())
			.count();
	}
	
	public boolean isEmpty() {
		return countParts() == 0;
	}
	
	public void checkCompound() {
		int i = countParts();
		if (i == 0) {
			if (getBlockState().getValue(BlockStateProperties.WATERLOGGED))
				level.setBlockAndUpdate(worldPosition, Blocks.WATER.defaultBlockState());
			else
				level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
		} else if (i == 1) {
			VirtualBlock part = parts.values().stream().filter(v -> !v.getState().isAir()).findAny().get();
			BlockState state = part.getState();
			BlockEntity partBlockEntity = part.getBlockEntity();
			part.setBlockEntity(null); // Prevent the block from dropping its items or interact with its BE in other ways because of its removal
			if (state.getBlock() instanceof SimpleWaterloggedBlock)
				state = state.setValue(BlockStateProperties.WATERLOGGED, getBlockState().getValue(BlockStateProperties.WATERLOGGED));
			level.setBlockAndUpdate(worldPosition, de.m_marvin.industria.core.registries.Blocks.ERROR_BLOCK.get().defaultBlockState());
			level.setBlockAndUpdate(worldPosition, state);
			if (partBlockEntity != null) {
				level.setBlockEntity(partBlockEntity);
				partBlockEntity.setLevel(level);
			} else {
				level.removeBlockEntity(worldPosition);
			}
		} else {
			setChanged();
			GameUtility.triggerClientSync(level, worldPosition);
		}
	}
	
	@Override
	public void setRPM(int partId, double rpm) {
		var virtualBlock = this.parts.get(partId);
		if (virtualBlock != null && virtualBlock.getBlock() instanceof IKineticBlock block) {
			block.setRPM(virtualBlock.getLevel(), virtualBlock.getPos(), 0, virtualBlock.getState(), rpm);
			this.setChanged();
		}
	}

	@Override
	public double getRPM(int partId) {
		var virtualBlock = this.parts.get(partId);
		if (virtualBlock != null && virtualBlock.getBlock() instanceof IKineticBlock block) {
			block.getRPM(virtualBlock.getLevel(), virtualBlock.getPos(), 0, virtualBlock.getState());
		}
		return 0;
	}
	
	public int addPart(BlockState state, BlockEntity blockEntity) {
		int id = 1;
		for (; this.parts.containsKey(id); id++)
			if (this.parts.get(id).getState().isAir()) break;
		var virtualBlock = this.parts.get(id);
		
		// Do not allow waterlogged blocks in compound, instead apply water logging to compound
		if (state.getBlock() instanceof SimpleWaterloggedBlock) {
			if (state.getValue(BlockStateProperties.WATERLOGGED)) {
				state = state.setValue(BlockStateProperties.WATERLOGGED, false);
				if (!getBlockState().getValue(BlockStateProperties.WATERLOGGED)) {
					getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BlockStateProperties.WATERLOGGED, true));
				}
			}
		}
		
		if (!this.parts.containsKey(id)) {
			virtualBlock = new VirtualBlock(this::getBlockPos);
			virtualBlock.setBlock(state);
			virtualBlock.setBlockEntity(blockEntity);
			virtualBlock.setStateChangeEvent(this::checkCompound);
			if (level != null) virtualBlock.setLevel(level);
			this.parts.put(id, virtualBlock);
		} else {
			virtualBlock.setBlock(state);
			virtualBlock.setBlockEntity(blockEntity);
		}
		this.setChanged();
		return id;
	}
	
	public int addPart(BlockState state) {
		return addPart(state, null);
	}
	
	public void applyTransform() {
		if (!hasLevel()) return;
		BlockState state = getLevel().getBlockState(getBlockPos());
		if (!(state.getBlock() instanceof CompoundBlock)) return;
		StateTransform transform = state.getValue(CompoundBlock.TRANSFORM);
		if (transform == StateTransform.NONE) return;
		this.parts.values().forEach(vb -> vb.transform(transform));
		getLevel().scheduleTick(getBlockPos(), getBlockState().getBlock(), 1);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		CompoundTag parts = new CompoundTag();
		for (var part : this.parts.entrySet()) {
			if (part.getValue().getState().isAir() && !part.getValue().getState().liquid()) continue;
			parts.put(Integer.toString(part.getKey()), part.getValue().serialize());
		}
		pTag.put("Parts", parts);
	}
	
	@Override
	public void setLevel(Level pLevel) {
		super.setLevel(pLevel);
		this.parts.values().forEach(v -> v.setLevel(pLevel));
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		CompoundTag parts = pTag.getCompound("Parts");
		this.parts.clear();
		for (var key : parts.getAllKeys()) {
			if (!key.matches("\\d")) continue;
			int id = Integer.parseInt(key);
			VirtualBlock virtualBlock = 
					VirtualBlock.deserialize(this::getBlockPos, parts.getCompound(key));
			virtualBlock.setStateChangeEvent(this::checkCompound);
			if (this.level != null) virtualBlock.setLevel(this.level);
			this.parts.put(id, virtualBlock);
		}
		applyTransform();
		this.setChanged();
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = new CompoundTag();
		saveAdditional(nbt);
		return nbt;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	@Override
	public CompoundPart[] getVisualParts() {
		return new CompoundPart[0];
	}
	
	public static void tickPartBlockEntities(Level pLevel, BlockPos pPos, BlockState pState, CompoundBlockEntity pBlockEntity) {
		for (var part : pBlockEntity.getParts().values()) {
			part.tickBlockEntity();
		}
	}

	@Override
	public void clearContent() {
		for (var part : getParts().values())
			if (part.getBlockEntity() instanceof Container container)
				container.clearContent();
	}

	@Override
	public int getContainerSize() {
		int size = 0;
		for (var part : getParts().values())
			if (part.getBlockEntity() instanceof Container container)
				size += container.getContainerSize();
		return size;
	}

	@Override
	public ItemStack getItem(int pSlot) {
		int size = 0;
		for (var part : getParts().values())
			if (part.getBlockEntity() instanceof Container container) {
				size += container.getContainerSize();
				if (size > pSlot) 
					return container.getItem(pSlot - size + container.getContainerSize());
			}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int pSlot, int pAmount) {
		int size = 0;
		for (var part : getParts().values())
			if (part.getBlockEntity() instanceof Container container) {
				size += container.getContainerSize();
				if (size > pSlot) 
					return container.removeItem(pSlot - size + container.getContainerSize(), pAmount);
			}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int pSlot) {
		int size = 0;
		for (var part : getParts().values())
			if (part.getBlockEntity() instanceof Container container) {
				size += container.getContainerSize();
				if (size > pSlot) 
					return container.removeItemNoUpdate(pSlot - size + container.getContainerSize());
			}
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int pSlot, ItemStack pStack) {
		int size = 0;
		for (var part : getParts().values())
			if (part.getBlockEntity() instanceof Container container) {
				size += container.getContainerSize();
				if (size > pSlot) {
					container.setItem(pSlot - size + container.getContainerSize(), pStack);
					return;
				}
			}
	}

	@Override
	public int[] getSlotsForFace(Direction pSide) {
		List<Integer> slots = new ArrayList<>();
		int offset = 0;
		for (var part : getParts().values()) {
			if (part.getBlockEntity() instanceof Container container) {
				if (container instanceof WorldlyContainer wcontainer) {
					for (int i : wcontainer.getSlotsForFace(pSide))
						slots.add(i + offset);
				} else {
					for (int i = 0; i < container.getContainerSize(); i++)
						slots.add(i + offset);
				}
				offset += container.getContainerSize();
			}
		}
		return slots.stream().mapToInt(Integer::intValue).toArray();
	}

	@Override
	public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, Direction pDirection) {
		int size = 0;
		for (var part : getParts().values())
			if (part.getBlockEntity() instanceof Container container) {
				size += container.getContainerSize();
				if (size > pIndex) {
					if (container instanceof WorldlyContainer wcontainer)
						return wcontainer.canPlaceItemThroughFace(pIndex - size + container.getContainerSize(), pItemStack, pDirection);
					else
						return true;
				}
			}
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
		int size = 0;
		for (var part : getParts().values())
			if (part.getBlockEntity() instanceof Container container) {
				size += container.getContainerSize();
				if (size > pIndex) {
					if (container instanceof WorldlyContainer wcontainer)
						return wcontainer.canTakeItemThroughFace(pIndex - size + container.getContainerSize(), pStack, pDirection);
					else
						return true;
				}
			}
		return false;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return false; // UIs not supported in compounds
	}

}
