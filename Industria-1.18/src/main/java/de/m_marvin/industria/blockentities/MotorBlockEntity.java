package de.m_marvin.industria.blockentities;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;

import de.m_marvin.industria.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MotorBlockEntity extends GeneratingKineticTileEntity {

	public MotorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MOTOR.get(), pos, state);
		this.setSpeed(16);
		
		com.simibubi.create.foundation.config.CStress
	}

	
	
	@Override
	public float calculateAddedStressCapacity() {
		// TODO Auto-generated method stub
		return super.calculateAddedStressCapacity();
	}
	
	@Override
	protected Block getStressConfigKey() {
		// TODO Auto-generated method stub
		return super.getStressConfigKey();
	}
	
	@Override
	public float calculateStressApplied() {
		// TODO Auto-generated method stub
		return super.calculateStressApplied();
	}
	
	@Override
	protected void notifyStressCapacityChange(float capacity) {
		// TODO Auto-generated method stub
		super.notifyStressCapacityChange(capacity);
	}
	
}
