package de.m_marvin.industria.blocks;

import com.simibubi.create.content.contraptions.components.actors.DrillBlock;
import com.simibubi.create.content.contraptions.components.actors.DrillTileEntity;

import de.m_marvin.industria.registries.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DiamondDrillBlock extends DrillBlock {

	public DiamondDrillBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public BlockEntityType<? extends DrillTileEntity> getTileEntityType() {
		return ModBlockEntities.DIAMOND_DRILL.get();
	}

}
