package de.m_marvin.industria.content.blockentities;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseGeneratingKineticBlockEntity extends GeneratingKineticTileEntity {

	public BaseGeneratingKineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		// TODO Auto-generated constructor stub
	}

	public float getStressApplied() {
		return 0;
	}
	
	public abstract float getAddedStressCapacity();
	
	@Override
	public float getGeneratedSpeed() {
		return 0;
	}
		
	@Override
	public float calculateStressApplied() {
		this.lastStressApplied = getStressApplied();
		return this.lastStressApplied;
	}
	
	@Override
	public float calculateAddedStressCapacity() {
		return getAddedStressCapacity();
	}
	
	@Override
	public void onSpeedChanged(float previousSpeed) {
		super.onSpeedChanged(previousSpeed);
	}
	
	@Override
	public float getSpeed() {
		return super.getSpeed();
	}

	public float getStress() {
		return super.stress;
	}

	public float getStressCapacity() {
		return super.capacity;
	}
	
	@Override
	public void updateGeneratedRotation() {
		super.updateGeneratedRotation();
	}
	
}
