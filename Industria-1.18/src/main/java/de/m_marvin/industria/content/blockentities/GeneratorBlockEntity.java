package de.m_marvin.industria.content.blockentities;

import de.m_marvin.industria.content.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GeneratorBlockEntity extends BlockEntity implements IIngameTooltip {
	
	// TODO CONFIG
	public static float generatorStress = 10;
	public static float stressToCurrentRate = 1;
	public static float voltagePerRPM = 1;
	public static float lossCompensationCurrent = 0.2F;
	
	public GeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GENERATOR.get(), pos, state);
	}
	
//	@Override
//	public float getStressApplied() {
//		return generatorStress;
//	}
//	
//	@Override
//	public void onSpeedChanged(float previousSpeed) {
//		super.onSpeedChanged(previousSpeed);
//		this.level.scheduleTick(worldPosition, this.getBlockState().getBlock(), 1);
//	}
	
	public double getVoltage() {
		return Math.abs(16 * voltagePerRPM); // this.getSpeed()
	}
	
	public double getCurrent() {
		return generatorStress * stressToCurrentRate;
	}
	public double getCompensatedCurrent() {
		return getCurrent() + lossCompensationCurrent;
	}
	
	public double getPower() {
		return getVoltage() * getCurrent();
	}

//	@Override
//	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
//		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
//		Formater.build().space(4).text("Power:").space().text("" + getPower()).addTooltip(tooltip);
//		Formater.build().space(4).text("Voltage:").space().text("" + getVoltage()).addTooltip(tooltip);
//		Formater.build().space(4).text("Current:").space().text("" + getCurrent()).addTooltip(tooltip);
//		return true;
//	}
	
}
