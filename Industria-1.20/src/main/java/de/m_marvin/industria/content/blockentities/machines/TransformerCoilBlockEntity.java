package de.m_marvin.industria.content.blockentities.machines;

import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.content.blocks.machines.TransformerCoilBlock;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.items.IConduitItem;
import de.m_marvin.industria.core.electrics.types.conduits.IElectricConduit;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TransformerCoilBlockEntity extends BlockEntity {
	
	protected Optional<BlockPos> masterPos = Optional.empty();
	protected boolean isMaster = false;
	protected ItemStack wires = ItemStack.EMPTY;
	
	public TransformerCoilBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.TRANSFORMER_COIL.get(), pPos, pBlockState);
	}
	
	public ItemStack getWires() {
		return wires;
	}
	
	public void setWires(ItemStack wires) {
		this.wires = wires;
		this.setChanged();
	}
	
	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
		this.masterPos = Optional.empty();
		this.setChanged();
	}
	
	public boolean isMaster() {
		return isMaster;
	}
	
	public BlockPos getMasterPos() {
		if (this.isMaster) return this.worldPosition;
		if (this.masterPos.isEmpty()) this.masterPos = Optional.ofNullable(findMasterPos());
		if (this.masterPos.isPresent()) return this.masterPos.get();
		return this.worldPosition;
	}
	
	public TransformerCoilBlockEntity getMaster() {
		BlockEntity masterBlockEntity = level.getBlockEntity(getMasterPos());
		if (masterBlockEntity instanceof TransformerCoilBlockEntity transformerBlockEntity) return transformerBlockEntity;
		return this;
	}
	
	public BlockPos findMasterPos() {
		BlockState state = getBlockState();
		if (state.getBlock() instanceof TransformerCoilBlock block) {
			List<BlockPos> transformerBlocks = block.findTransformerBlocks(this.level, this.worldPosition, state);
			for (BlockPos pos : transformerBlocks) {
				if (level.getBlockEntity(pos) instanceof TransformerCoilBlockEntity transformer && transformer.isMaster) return pos;
			}
		}
		return this.worldPosition;
	}
	
	public Conduit getWireConduit() {
		if (this.wires.isEmpty()) return null;
		if (this.wires.getItem() instanceof IConduitItem conduitItem) return conduitItem.getConduit();
		return null;
	}
	
	public boolean isValidWireItem(ItemStack stack) {
		if (!wires.isEmpty() && stack.getItem() != stack.getItem()) return false;
		if (stack.getItem() instanceof IConduitItem conduitItem && conduitItem.getConduit() instanceof IElectricConduit) return true;
		return false;
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.put("Wires", wires.serializeNBT());
		pTag.putBoolean("IsMaster", this.isMaster);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.wires = ItemStack.of(pTag.getCompound("Wires"));
		this.isMaster = pTag.getBoolean("IsMaster");
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.put("Wires", this.wires.serializeNBT());
		tag.putBoolean("IsMaster", this.isMaster);
		return tag;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	public void dropWires() {
		if (!this.wires.isEmpty()) {
			GameUtility.dropItem(level, wires, Vec3f.fromVec(this.worldPosition).add(0.5F, 0.5F, 0.5F), 0.5F, 1F);
			this.wires = ItemStack.EMPTY;
		}
	}
	
}
