package de.m_marvin.industria.content.blocks;

import java.util.HashSet;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.engine.ForcesInducer;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public abstract class AbstractThrusterBlock extends BaseEntityBlock {

	public AbstractThrusterBlock(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.HORIZONTAL_FACING);
		pBuilder.add(BlockStateProperties.ATTACH_FACE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction direction = pContext.getNearestLookingDirection();
		if (direction.getAxis() == Direction.Axis.Y) {
			return this.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.getHorizontalDirection());
		} else {
			return this.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL).setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getOpposite());
		}
	}
	
	public Direction getThrustDirection(BlockState pState) {
		switch (pState.getValue(BlockStateProperties.ATTACH_FACE)) {
		case CEILING: return Direction.DOWN;
		case FLOOR: return Direction.UP;
		default: return pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		}
	}
	
	public abstract int getThrust(Level level, BlockPos pos, BlockState state);
	
	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
		super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
		Ship contraption = PhysicUtility.getContraptionOfBlock(pLevel, pPos);
		if (contraption instanceof ServerShip serverContraption) {
			ThrusterInducer forceInducer = PhysicUtility.getOrCreateForceInducer((ServerLevel) pLevel, serverContraption, ThrusterInducer.class);
			forceInducer.addThruster(pPos);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
		Ship contraption = PhysicUtility.getContraptionOfBlock(pLevel, pPos);
		if (contraption instanceof ServerShip serverContraption) {
			ThrusterInducer forceInducer = PhysicUtility.getOrCreateForceInducer((ServerLevel) pLevel, serverContraption, ThrusterInducer.class);
			forceInducer.removeThruster(pPos);
		}
	}
	
	public static class ThrusterInducer extends ForcesInducer {
		
		protected HashSet<Long> thrusters;
		
		public ThrusterInducer() {
			this.thrusters = new HashSet<>();
		}
		
		public void addThruster(BlockPos pos) {
			this.thrusters.add(pos.asLong());
		}
		
		public void removeThruster(BlockPos pos) {
			this.thrusters.remove(pos.asLong());
		}
		
		@Override
		public void applyForces(PhysShip contraption) {
			
			if (getLevel() != null) {
				for (long thruster : thrusters) {
					
					BlockPos thrusterPos = BlockPos.of(thruster);
					BlockState state = getLevel().getBlockState(thrusterPos);
					if (state.getBlock() instanceof AbstractThrusterBlock thrusterBlock) {
						int thrust = thrusterBlock.getThrust(level, thrusterPos, state);
						if (thrust != 0) {
							Direction direction = thrusterBlock.getThrustDirection(state);
							Vec3i forceVec = MathUtility.getDirectionVec(direction).mul(thrust);
							contraption.applyRotDependentForce(new Vector3d(forceVec.x, forceVec.y, forceVec.z));
						}
					}
					
				}
			}
			
		}
		
	}
	
}
