package de.m_marvin.industria.core.util.blocks;

import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class DynamicMultiBlockEntity<T extends DynamicMultiBlockEntity<T>> extends BlockEntity {

	protected Optional<BlockPos> masterPos = Optional.empty();
	protected Optional<BlockPos> maxPos = Optional.empty();
	protected Optional<BlockPos> minPos = Optional.empty();
	protected boolean isMaster = false;

	public DynamicMultiBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}
	
	public abstract Class<T> getMultiBlockTypeClass();
	
	protected void findPositions() {
		BlockState state = getBlockState();
		if (state.getBlock() instanceof IBaseEntityDynamicMultiBlock block) {
			List<BlockPos> blockentityBlocks = block.findMultiBlockEntityBlocks(this.level, this.worldPosition, state);
			if (blockentityBlocks.isEmpty()) return;
			for (BlockPos pos : blockentityBlocks) {
				if (level.getBlockEntity(pos) instanceof DynamicMultiBlockEntity multiblockentity && multiblockentity.isMaster) this.masterPos = Optional.of(pos);
			}
			BlockPos minPos = blockentityBlocks.stream().reduce(MathUtility::getMinCorner).get();
			BlockPos maxPos = blockentityBlocks.stream().reduce(MathUtility::getMaxCorner).get();
			this.minPos = Optional.of(minPos);
			this.maxPos = Optional.of(maxPos);
		}
	}
	
	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
		this.masterPos = Optional.empty();
		this.maxPos = Optional.empty();
		this.minPos = Optional.empty();
		this.setChanged();
	}
	
	public boolean isMaster() {
		return isMaster;
	}
	
	public BlockPos getMasterPos() {
		if (this.isMaster) return this.worldPosition;
		if (this.masterPos.isEmpty()) findPositions();
		if (this.masterPos.isPresent()) return this.masterPos.get();
		return this.worldPosition;
	}
	
	public BlockPos getMinPos() {
		if (isMaster) {
			if (this.minPos.isEmpty()) findPositions();
			if (this.minPos.isPresent()) return this.minPos.get();
			return this.worldPosition;
		} else {
			T master = this.getMaster();
			if (master.minPos.isEmpty()) master.findPositions();
			if (master.minPos.isPresent()) return master.minPos.get();
			return this.worldPosition;
		}
	}
	
	public BlockPos getMaxPos() {
		if (isMaster) {
			if (this.maxPos.isEmpty()) findPositions();
			if (this.maxPos.isPresent()) return this.maxPos.get();
			return this.worldPosition;
		} else {
			T master = this.getMaster();
			if (master.maxPos.isEmpty()) master.findPositions();
			if (master.maxPos.isPresent()) return master.maxPos.get();
			return this.worldPosition;
		}
	}
	
	@SuppressWarnings("unchecked")
	public T getMaster() {
		BlockEntity masterBlockEntity = level.getBlockEntity(getMasterPos());
		if (getMultiBlockTypeClass().isInstance(masterBlockEntity)) return (T) masterBlockEntity;
		return (T) this;
	}
	
	@Override
	public void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		if (!this.isMaster()) return;
		pTag.putBoolean("IsMaster", this.isMaster);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.isMaster = pTag.getBoolean("IsMaster");
		this.minPos = pTag.contains("minPos") ? Optional.of(BlockPos.of(pTag.getLong("minPos"))) : Optional.empty();
		this.maxPos = pTag.contains("maxPos") ? Optional.of(BlockPos.of(pTag.getLong("maxPos"))) : Optional.empty();
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putBoolean("IsMaster", this.isMaster);
		if (this.minPos.isPresent()) tag.putLong("minPos", this.minPos.get().asLong());
		if (this.maxPos.isPresent()) tag.putLong("maxPos", this.maxPos.get().asLong());
		return tag;
	}
}
