package de.m_marvin.industria.blockentities;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;

import de.m_marvin.industria.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GeneratorBlockEntity extends KineticTileEntity {
	
	public GeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GENERATOR.get(), pos, state);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public float calculateStressApplied() {
		float impact = 10;
		this.lastStressApplied = impact;
		return impact;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		System.out.println(this.getSpeed());
	}
	
	@Override
	public void onSpeedChanged(float previousSpeed) {
		super.onSpeedChanged(previousSpeed);
		
		System.out.println("Speed: " + this.getSpeed());
		
		double voltage = Math.abs(this.getSpeed() * 2);
		double energy = Math.abs(this.getSpeed() * 10);
		double current = energy / voltage;
		
		System.out.println("Energy: " + energy + "W");
		System.out.println("Voltage: " + voltage + "V");
		System.out.println("Current: " + current + "I");
		
	}
	
	public double getVoltage() {
		return Math.abs(this.getSpeed() * 2);
	}
	
	public double getCurrent() {
		return getPower() / getVoltage();
	}
	
	public double getPower() {
		return Math.abs(this.getSpeed() * 10);
	}
	
}
