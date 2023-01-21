package de.m_marvin.industria.blockentities;

import java.util.List;

import de.m_marvin.industria.registries.ModBlockEntities;
import de.m_marvin.industria.util.Formater;
import de.m_marvin.industria.util.blockentity.IIngameTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class MotorBlockEntity extends BaseGeneratingKineticBlockEntity implements IIngameTooltip {
	
	// TODO CONFIG
	public static float maxMotorStress = 10;
	public static float currentPerStress = 1;
	public static float RPMPerVoltage = 1;
	
	public float rpm;
	
	public MotorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MOTOR.get(), pos, state);
	}
		
	@Override
	public float getGeneratedSpeed() {
		return this.rpm;
	}
	
	@Override
	public float getAddedStressCapacity() {
		return 10;
	}
	
	public void setVoltage(double voltage) {
		this.rpm = (float) Math.round(RPMPerVoltage * voltage);
		this.updateGeneratedRotation();
	}
	
	public double getCurrent() {
		float stressPercent = getStressCapacity() == 0 ? 0 : getStress() / getStressCapacity();
		float requiredStress = maxMotorStress * stressPercent;
		return currentPerStress * requiredStress;
	}
	
	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		Formater.build().space(4).text("RPM:").space().text("" + this.getGeneratedSpeed()).addTooltip(tooltip);
		Formater.build().space(4).text("Current:").space().text("" + this.getCurrent()).addTooltip(tooltip);
		
		//Formater.build().space(4).text("Max Stress:").space().text("" + this.maxStess).addTooltip(tooltip);
//		Formater.build().space(4).text("Current:").space().text("" + getCurrent()).addTooltip(tooltip);
		return true;
	}
	
}
