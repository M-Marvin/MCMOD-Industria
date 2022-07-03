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
	
}
