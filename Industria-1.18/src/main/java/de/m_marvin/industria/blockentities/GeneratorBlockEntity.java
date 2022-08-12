package de.m_marvin.industria.blockentities;

import java.util.List;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import de.m_marvin.industria.registries.ModBlockEntities;
import de.m_marvin.industria.util.Formater;
import de.m_marvin.industria.util.UtilityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class GeneratorBlockEntity extends KineticTileEntity implements IHaveGoggleInformation {
	
	// TODO CONFIG
	public static float motorStress = 10;
	public static float stressToCurrentRate = 1;
	public static float voltagePerRPM = 1;
	
	public GeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GENERATOR.get(), pos, state);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public float calculateStressApplied() {
		this.lastStressApplied = motorStress;
		return motorStress;
	}
	
	@Override
	public void onSpeedChanged(float previousSpeed) {
		super.onSpeedChanged(previousSpeed);
		UtilityHelper.updateElectricNetwork(level, worldPosition);
	}
	
	public double getVoltage() {
		return Math.abs(this.getSpeed() * voltagePerRPM);
	}
	
	public double getCurrent() {
		return motorStress * stressToCurrentRate;
	}
	
	public double getPower() {
		return getVoltage() * getCurrent();
	}
	
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		Formater.build().space(4).text("Power:").space().text("" + getPower()).addTooltip(tooltip);
		Formater.build().space(4).text("Voltage:").space().text("" + getVoltage()).addTooltip(tooltip);
		Formater.build().space(4).text("Current:").space().text("" + getCurrent()).addTooltip(tooltip);
		return true;
	}
	
}
