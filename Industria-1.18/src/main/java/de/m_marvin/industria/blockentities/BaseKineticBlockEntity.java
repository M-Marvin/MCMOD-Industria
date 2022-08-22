package de.m_marvin.industria.blockentities;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseKineticBlockEntity extends KineticTileEntity {
	
	public BaseKineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	public abstract float getStressApplied();

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
	
}
