package de.m_marvin.industria.content.blocks;

import java.util.HashMap;
import java.util.Map.Entry;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.physics.engine.ForcesInducer;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
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
		case CEILING: return Direction.UP;
		case FLOOR: return Direction.DOWN;
		default: return pState.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
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
			forceInducer.setThruster(pPos, 0);
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
			if (forceInducer.getThrusters().isEmpty()) PhysicUtility.removeAttachment(serverContraption, ThrusterInducer.class);
		}
	}
	
	public static class ThrusterInducer extends ForcesInducer {
		
		protected HashMap<Long, Double> thrusters;
		
		public ThrusterInducer() {
			this.thrusters = new HashMap<>();
		}
		
		public void setThruster(BlockPos pos, double thrust) {
			this.thrusters.put(pos.asLong(), thrust);
		}
		
		public void removeThruster(BlockPos pos) {
			this.thrusters.remove(pos.asLong());
		}
		
		public HashMap<Long, Double> getThrusters() {
			return thrusters;
		}
		
		@Override
		public void applyForces(PhysShip contraption) {
			
			if (getLevel() != null) {

				Vec3d massCenter = PhysicUtility.toContraptionPos(contraption.getTransform(), Vec3d.fromVec(contraption.getTransform().getPositionInWorld()));
				
				for (Entry<Long, Double> thruster : thrusters.entrySet()) {
					
					BlockPos thrusterPos = BlockPos.of(thruster.getKey());
					BlockState state = getLevel().getBlockState(thrusterPos);
					if (state.getBlock() instanceof AbstractThrusterBlock thrusterBlock) {
						double thrust = thruster.getValue();
						if (Math.abs(thrust) >= 1) {
							Direction direction = thrusterBlock.getThrustDirection(state);
							Vec3d forceVec = new Vec3d(MathUtility.getDirectionVec(direction)).mul(thrust);
							contraption.applyRotDependentForceToPos(new Vector3d(forceVec.x, forceVec.y, forceVec.z), Vec3d.fromVec(thrusterPos).sub(massCenter).writeTo(new Vector3d()));
						}
					}
					
				}
			}
			
		}
		
	}
	
}
