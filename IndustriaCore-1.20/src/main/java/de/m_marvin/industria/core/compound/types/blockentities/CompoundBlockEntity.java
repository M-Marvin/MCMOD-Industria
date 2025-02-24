package de.m_marvin.industria.core.compound.types.blockentities;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import de.m_marvin.industria.core.compound.types.blocks.CompoundBlock;
import de.m_marvin.industria.core.kinetics.types.blockentities.IKineticBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.TransmissionNode;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.StateTransform;
import de.m_marvin.industria.core.util.virtualblock.VirtualBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CompoundBlockEntity extends BlockEntity implements IKineticBlockEntity {

	protected static record KineticPart() {}
	
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
	
	public int countParts() {
		return (int) parts.values().stream()
			.filter(v -> !v.getState().isAir())
			.count();
	}
	
	public boolean isEmpty() {
		return countParts() == 0;
	}
	
	public void checkCompound() {
		int i = countParts();
		if (i == 0) {
			level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
		} else if (i == 1) {
			VirtualBlock part = parts.values().stream().filter(v -> !v.getState().isAir()).findAny().get();
			level.setBlockAndUpdate(worldPosition, part.getState());
			if (part.getBlockEntity() != null) {
				level.setBlockEntity(part.getBlockEntity());
				part.getBlockEntity().setLevel(level);
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
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		CompoundTag parts = new CompoundTag();
		for (var part : this.parts.entrySet()) {
			if (part.getValue().getState().isAir()) continue;
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
	
}
