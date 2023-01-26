package de.m_marvin.industria.content.blockentities;

import com.simibubi.create.content.contraptions.components.actors.DrillTileEntity;

import de.m_marvin.industria.content.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DiamondDrillBlockEntity extends DrillTileEntity {

	public DiamondDrillBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DIAMOND_DRILL.get(), pos, state);
	}
	
	@Override
	protected float getBreakSpeed() {
		return Math.abs(this.getSpeed() / 1.0f);
	}
	
}
