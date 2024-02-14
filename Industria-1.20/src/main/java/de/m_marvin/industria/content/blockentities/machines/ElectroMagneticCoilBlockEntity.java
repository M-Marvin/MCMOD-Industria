package de.m_marvin.industria.content.blockentities.machines;

import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.content.blocks.machines.ElectroMagneticCoilBlock;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.items.IConduitItem;
import de.m_marvin.industria.core.electrics.types.conduits.IElectricConduit;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ElectroMagneticCoilBlockEntity extends BlockEntity {
	
	protected Optional<BlockPos> masterPos = Optional.empty();
	protected Optional<BlockPos> maxPos = Optional.empty();
	protected Optional<BlockPos> minPos = Optional.empty();
	protected boolean isMaster = false;
	protected ItemStack wires = ItemStack.EMPTY;
	protected double currentFieldStrength = 0.0;
	
	public ElectroMagneticCoilBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.ELECTRO_MAGNETIC_COIL.get(), pPos, pBlockState);
	}
	
	protected ElectroMagneticCoilBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}
	
	public ItemStack getWires() {
		return wires;
	}
	
	public double getCurrentFieldStrength() {
		return currentFieldStrength;
	}
	
	public void updateCurrentField() {
		
		// TODO calculate field strength emitted
		
	}
	
	public void setWires(ItemStack wires) {
		this.wires = wires;
		this.setChanged();
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
			ElectroMagneticCoilBlockEntity master = this.getMaster();
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
			ElectroMagneticCoilBlockEntity master = this.getMaster();
			if (master.maxPos.isEmpty()) master.findPositions();
			if (master.maxPos.isPresent()) return master.maxPos.get();
			return this.worldPosition;
		}
	}
	
	public ElectroMagneticCoilBlockEntity getMaster() {
		BlockEntity masterBlockEntity = level.getBlockEntity(getMasterPos());
		if (masterBlockEntity instanceof ElectroMagneticCoilBlockEntity transformerBlockEntity) return transformerBlockEntity;
		return this;
	}
	
	public void findPositions() {
		BlockState state = getBlockState();
		if (state.getBlock() instanceof ElectroMagneticCoilBlock block) {
			List<BlockPos> transformerBlocks = block.findTransformerBlocks(this.level, this.worldPosition, state);
			if (transformerBlocks.isEmpty()) return;
			for (BlockPos pos : transformerBlocks) {
				if (level.getBlockEntity(pos) instanceof ElectroMagneticCoilBlockEntity transformer && transformer.isMaster) this.masterPos = Optional.of(pos);
			}
			BlockPos minPos = transformerBlocks.stream().reduce(MathUtility::getMinCorner).get();
			BlockPos maxPos = transformerBlocks.stream().reduce(MathUtility::getMaxCorner).get();
			this.minPos = Optional.of(minPos);
			this.maxPos = Optional.of(maxPos);
		}
	}
	
	public Axis getAxis() {
		BlockState state = getBlockState();
		if (state.getBlock() instanceof ElectroMagneticCoilBlock) {
			return state.getValue(BlockStateProperties.AXIS);
		}
		return Axis.Y;
	}
	
	public Conduit getWireConduit() {
		if (this.wires.isEmpty()) return Conduits.NONE.get();
		if (this.wires.getItem() instanceof IConduitItem conduitItem) return conduitItem.getConduit();
		return Conduits.NONE.get();
	}
	
	public boolean isValidWireItem(ItemStack stack) {
		if (!wires.isEmpty() && stack.getItem() != stack.getItem()) return false;
		if (stack.getItem() instanceof IConduitItem conduitItem && conduitItem.getConduit() instanceof IElectricConduit) return true;
		return false;
	}

	public int getWiresPerWinding() {
		int windingLength = 4;
		switch (getAxis()) {
		case Y: windingLength = (this.getMaxPos().getX() - this.getMinPos().getX() + 1) * 2 + (this.getMaxPos().getZ() - this.getMinPos().getZ() + 1) * 2; break;
		case X: windingLength = (this.getMaxPos().getY() - this.getMinPos().getY() + 1) * 2 + (this.getMaxPos().getZ() - this.getMinPos().getZ() + 1) * 2; break;
		case Z: windingLength = (this.getMaxPos().getX() - this.getMinPos().getX() + 1) * 2 + (this.getMaxPos().getY() - this.getMinPos().getY() + 1) * 2; break;
		}
		return windingLength / Conduit.BLOCKS_PER_WIRE_ITEM;
	}
	
	public int getMaxWindings() {
		switch (getAxis()) {
		default:
		case Y: return (this.getMaxPos().getY() - this.getMinPos().getY() + 1) * 6;
		case X: return (this.getMaxPos().getX() - this.getMinPos().getX() + 1) * 6;
		case Z: return (this.getMaxPos().getZ() - this.getMinPos().getZ() + 1) * 6;
		}
	}
	
	public int getWindings() {
		return this.wires.getCount() / getWiresPerWinding();
	}
	
	public void dropWires() {
		if (!this.wires.isEmpty()) {
			GameUtility.dropItem(level, wires, Vec3f.fromVec(this.worldPosition).add(0.5F, 0.5F, 0.5F), 0.5F, 1F);
			this.wires = ItemStack.EMPTY;
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		if (!this.isMaster()) return;
		pTag.put("Wires", wires.serializeNBT());
		pTag.putBoolean("IsMaster", this.isMaster);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.currentFieldStrength = pTag.getDouble("currentFieldStrength");
		this.wires = ItemStack.of(pTag.getCompound("Wires"));
		this.isMaster = pTag.getBoolean("IsMaster");
		this.minPos = pTag.contains("minPos") ? Optional.of(BlockPos.of(pTag.getLong("minPos"))) : Optional.empty();
		this.maxPos = pTag.contains("maxPos") ? Optional.of(BlockPos.of(pTag.getLong("maxPos"))) : Optional.empty();
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putDouble("currentFieldStrength", this.currentFieldStrength);
		tag.put("Wires", this.wires.serializeNBT());
		tag.putBoolean("IsMaster", this.isMaster);
		if (this.minPos.isPresent()) tag.putLong("minPos", this.minPos.get().asLong());
		if (this.maxPos.isPresent()) tag.putLong("maxPos", this.maxPos.get().asLong());
		return tag;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
}
