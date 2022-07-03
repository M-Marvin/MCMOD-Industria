package de.m_marvin.industria.blockentities;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;

import de.m_marvin.industria.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MotorBlockEntity extends GeneratingKineticTileEntity {
	
	public float rpm;
	public float stress;
	
	public MotorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MOTOR.get(), pos, state);
		
	}
	
	public void setGenerator(float rpm, float stress) {
		this.rpm = rpm;
		this.stress = stress;
		//this.level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 3, 1);
		this.updateGeneratedRotation();
	}
	
	@Override
	public float getGeneratedSpeed() {
		return this.rpm;
	}
	
	@Override
	public float calculateAddedStressCapacity() { // FIXME
		
		float capacity = this.stress;
		this.lastCapacityProvided = capacity;
		return capacity;
	}
	
}
