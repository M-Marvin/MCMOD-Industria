package de.m_marvin.industria.util;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CreateHelper {
	
	public static void updateKinetics(Level level, BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof KineticTileEntity kineticBlockEntity && kineticBlockEntity.hasNetwork()) {
			kineticBlockEntity.getOrCreateNetwork().updateNetwork();
		}
	}
	
}
