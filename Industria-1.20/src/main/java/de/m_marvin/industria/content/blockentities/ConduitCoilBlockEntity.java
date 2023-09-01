package de.m_marvin.industria.content.blockentities;

import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ConduitCoilBlockEntity extends BlockEntity {
	
	protected int wireLength;
	
	public ConduitCoilBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.WIRE_COIL.get(), pPos, pBlockState);
	}

	public void setWireLength(int wireLength) {
		this.wireLength = wireLength;
		this.setChanged();
	}
	
	public int getWireLength() {
		return wireLength;
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.putInt("WireLength", wireLength);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.wireLength = pTag.getInt("WireLength");
	}
	
}
