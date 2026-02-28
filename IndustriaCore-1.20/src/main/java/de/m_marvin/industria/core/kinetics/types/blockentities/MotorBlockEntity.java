package de.m_marvin.industria.core.kinetics.types.blockentities;

import de.m_marvin.industria.core.kinetics.types.blocks.ShortShaftBlock;
import de.m_marvin.industria.core.kinetics.types.containers.MotorMenu;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.util.container.AbstractBlockContainerMenu.DummyContainer;
import de.m_marvin.industria.core.util.container.FriendlyContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MotorBlockEntity extends SimpleKineticBlockEntity implements MenuProvider {
	
	protected double sourceRPM = 16;
	protected double sourceTorque = 100;
	
	public MotorBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(BlockEntityTypes.MOTOR.get(), pPos, pBlockState);
	}
	
	public FriendlyContainerData getContainerData() {
		return FriendlyContainerData.empty()
				.nextDoubleItem(this::getSourceTorque, this::setSourceTorque)
				.nextDoubleItem(this::getSourceRPM, this::setSourceRPM);
	}
	
	public double getSourceRPM() {
		return sourceRPM;
	}
	
	public double getSourceTorque() {
		return Math.max(0, sourceTorque);
	}
	
	public void setSourceRPM(double sourceRPM) {
		this.sourceRPM = sourceRPM;
		setChanged();
	}
	
	public void setSourceTorque(double sourceTorque) {
		this.sourceTorque = sourceTorque;
		setChanged();
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		pTag.putDouble("SourceRPM", this.sourceRPM);
		pTag.putDouble("SourceTorque", this.sourceTorque);
		super.saveAdditional(pTag);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		this.sourceRPM = pTag.getDouble("SourceRPM");
		this.sourceTorque = pTag.getDouble("SourceTorque");
		super.load(pTag);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag pTag = super.getUpdateTag();
		pTag.putDouble("SourceRPM", this.sourceRPM);
		pTag.putDouble("SourceTorque", this.sourceTorque);
		return pTag;
	}
	
	@Override
	public CompoundPart[] getVisualParts() {
		Direction facing = this.getBlockState().getValue(BlockStateProperties.FACING);
		return new CompoundPart[] {
				new CompoundPart(
						Blocks.SHORT_SHAFT_2.get().defaultBlockState().setValue(ShortShaftBlock.FACING, facing), 
						facing.getAxis(), 
						DEFAULT_ROTATIONAL_OFFSET, 
						1.0),
				new CompoundPart(
						getBlockState(), 
						facing.getAxis(), 
						0.0, 
						0.0)
		};
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return new MotorMenu(pContainerId, pPlayerInventory, getBlockPos(), new DummyContainer(getBlockPos()), getContainerData());
	}

	@Override
	public Component getDisplayName() {
		return this.getBlockState().getBlock().getName();
	}
	
}
