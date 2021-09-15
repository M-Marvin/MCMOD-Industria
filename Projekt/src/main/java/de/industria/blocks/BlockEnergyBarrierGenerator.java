package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class BlockEnergyBarrierGenerator extends BlockEnergyBarrierBorder implements IBElectricConnectiveBlock, IBAdvancedBlockInfo {
	
	public BlockEnergyBarrierGenerator() {
		super("energy_barrier_generator");
	}
	
	public void setEnergyField(BlockState state, World world, BlockPos pos, boolean active) {
		world.setBlockAndUpdate(pos, state.setValue(ACTIVE, active));
		world.getBlockTicks().scheduleTick(pos, this, 10);
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.ExtremVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0.1F;
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MACHINE;
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		boolean active = network.canMachinesRun() == Voltage.ExtremVoltage;
		if (state.getValue(ACTIVE) != active) setEnergyField(state, worldIn, pos, active);
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.needEnergy", (0.1F * Voltage.ExtremVoltage.getVoltage() / 1000F) + "k"));
			info.add(new TranslationTextComponent("industria.block.info.needVoltage", Voltage.ExtremVoltage.getVoltage()));
			info.add(new TranslationTextComponent("industria.block.info.needCurrent", 0.1F));
			info.add(new TranslationTextComponent("industria.block.info.energyBarrierGenerator"));
		};
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
}
