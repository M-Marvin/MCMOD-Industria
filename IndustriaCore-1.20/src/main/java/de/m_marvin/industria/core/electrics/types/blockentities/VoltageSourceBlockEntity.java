package de.m_marvin.industria.core.electrics.types.blockentities;

import de.m_marvin.industria.core.electrics.types.containers.VoltageSourceMenu;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.engine.BlockParametricsManager;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.container.AbstractBlockContainerMenu.DummyContainer;
import de.m_marvin.industria.core.util.container.FriendlyContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class VoltageSourceBlockEntity extends AbstractSourceBlockEntity {

	protected double voltage;
	
	public VoltageSourceBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(pPos, pBlockState);
		BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(Blocks.VOLTAGE_SOURCE.get());
		this.voltage = parametrics.getNominalVoltage();
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.putDouble("Voltage", this.voltage);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.voltage = pTag.getDouble("Voltage");
	}
	
	@Override
	public FriendlyContainerData getContainerData() {
		return FriendlyContainerData.empty()
				.nextFloatItem(() -> (float) getVoltage(), this::setVoltage)
				.nextFloatItem(() -> (float) getPower(), this::setPower)
				.nextFloatItem(() -> (float) getDeviceVoltage(), null)
				.nextFloatItem(() -> (float) getDeviceCurrent(), null);
	}
	
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return GameUtility.openJunctionScreenOr(this, pContainerId, pPlayer, pPlayerInventory, () -> new VoltageSourceMenu(pContainerId, pPlayerInventory, getBlockPos(), new DummyContainer(this.getBlockPos()), getContainerData()));
	}
	
	public void setVoltage(double voltage) {
		BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(Blocks.VOLTAGE_SOURCE.get());
		this.voltage = Math.max(parametrics.getVoltageMin(), Math.min(parametrics.getVoltageMax(), voltage));
		this.setChanged();
	}
	
	public double getVoltage() {
		return this.voltage;
	}
	
	@Override
	public double getDeviceCurrent() {
		return -getDeviceCurrent("source", "i_src");
	}
	
}
