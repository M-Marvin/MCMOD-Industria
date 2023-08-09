package de.m_marvin.industria.content.blockentities;

import de.m_marvin.industria.content.registries.ModBlockEntities;
import de.m_marvin.industria.core.electrics.types.blockentities.IEditableJunction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class JunctionBoxBlockEntity extends BlockEntity implements IEditableJunction<JunctionBoxBlockEntity> {
	
	public JunctionBoxBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntities.JUNCTION_BOX.get(), pPos, pBlockState);
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
	}

	@Override
	public Component getDisplayName() {
		return new TextComponent("test"); // TODO
	}

	@Override
	public JunctionBoxBlockEntity getContainerBlockEntity() {
		return this;
	}
	
}
