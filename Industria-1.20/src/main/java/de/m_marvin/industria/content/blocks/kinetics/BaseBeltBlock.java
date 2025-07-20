package de.m_marvin.industria.content.blocks.kinetics;

import de.m_marvin.industria.content.blockentities.kinetics.BaseBeltBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blocks.BeltBlock;
import de.m_marvin.industria.core.util.types.DiagonalPlanarDirection;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BaseBeltBlock extends BeltBlock {

	public BaseBeltBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new BaseBeltBlockEntity(pPos, pState);
	}
	
	@Override
	public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
		
		// FIXME diagonal belt hitbox problem
		
		if (pLevel.getBlockEntity(pPos) instanceof BeltBlockEntity belt) {

			Axis axis = pState.getValue(AXIS);
			DiagonalPlanarDirection orientation = pState.getValue(ORIENTATION);
			double rpm = belt.getRPM(0);
			
			Vec2i vdir = new Vec2i(orientation.getNormal());
			vdir.x = Math.abs(vdir.x);
			
			Vec3i pushDirection;
			switch (axis) {
			case Z: pushDirection = new Vec3i(-vdir.x, -vdir.y, 0); break;
			case X: pushDirection = new Vec3i(0, -vdir.y, vdir.x); break;
			default: return;
			}

			Vec3d force = new Vec3d(pushDirection).mul(rpm * 0.0012F);
			
			Vec3 motion = pEntity.getDeltaMovement();
			pEntity.setDeltaMovement(
					force.x == 0 ? motion.x : force.x, 
					force.y == 0 ? motion.y : force.y, 
					force.z == 0 ? motion.z : force.z
			);
			
		}
		
	}
	
}
