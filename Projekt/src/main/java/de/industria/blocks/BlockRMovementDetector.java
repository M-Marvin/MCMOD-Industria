package de.industria.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import de.industria.tileentity.TileEntitySimpleBlockTicking;
import de.industria.util.handler.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class BlockRMovementDetector extends BlockContainerBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	
	public BlockRMovementDetector() {
		super("movement_detector", Material.WOOD, 1.5F, 1.5F, SoundType.WOOD);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER);
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		return new TileEntitySimpleBlockTicking();
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	/**
	 * Needed to get the motion of PlayerEntitys, getDeltaMovement() returns 0 0 0 by players.
	 */
	protected static HashMap<UUID, Vector3d> playerMovements = new HashMap<UUID, Vector3d>();
	
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		
		int range = 6;
		
		Direction facing = state.getValue(FACING);
		Vector3i facingVec = facing.getNormal();
		
		BlockPos pos1 = new BlockPos(facingVec.getX() > 0 ? 0 : -range, facingVec.getY() > 0 ? 0 : -range, facingVec.getZ() > 0 ? 0 : -range);
		BlockPos pos2 = new BlockPos(facingVec.getX() < 0 ? 0 : range + 1, facingVec.getY() < 0 ? 0 : range + 1, facingVec.getZ() < 0 ? 0 : range + 1);
		AxisAlignedBB aabb = new AxisAlignedBB(pos1.offset(pos).offset(0.5F, 0.5F, 0.5F), pos2.offset(pos).offset(0.5F, 0.5F, 0.5F));
		
		List<Entity> entitysInRange = world.getEntities(null, aabb);
		
		for (Entity entity : entitysInRange.toArray(new Entity[] {})) {
			Vector3d entityPos = entity.getEyePosition(0);
			Vector3d sensorPos = new Vector3d(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F).add(facingVec.getX() * 0.5F, facingVec.getY() * 0.5F, facingVec.getZ() * 0.5F);
			RayTraceContext rayTrace = new RayTraceContext(sensorPos, entityPos, BlockMode.VISUAL, FluidMode.ANY, null);
			BlockRayTraceResult result = world.clip(rayTrace);
			if (result.getType() == Type.BLOCK) {
				entitysInRange.remove(entity);
			}
		}

		int powerIn = 0;
		
		if (entitysInRange.size() > 0) {
			
			powerIn = 8;
			
			for (Entity entity : entitysInRange) {
				
				Vector3d motion = entity.getDeltaMovement();
				if (entity instanceof PlayerEntity) {
					Vector3d position = entity.position();
					Vector3d lastTickPosition = playerMovements.put(((PlayerEntity) entity).getUUID(), position);
					if (lastTickPosition != null) motion = lastTickPosition.subtract(position);
				}
				
				double speed = MathHelper.makePositive(motion.x) + MathHelper.makePositive(motion.y) + MathHelper.makePositive(motion.z);
				
				if (speed > 0.1F) {
					
					powerIn = 15;
					break;
					
				}
				
			}
			
		}
		
		int power = state.getValue(POWER);
		if (power != powerIn) {
			
			world.setBlockAndUpdate(pos, state.setValue(POWER, powerIn));
			world.updateNeighborsAtExceptFromFacing(pos.relative(state.getValue(FACING).getOpposite()), this, state.getValue(FACING));
			
		}
		
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == state.getValue(FACING).getOpposite();
	}
	
	@Override
	public int getDirectSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return this.getSignal(state, world, pos, side);
	}
	
	@Override
	public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == state.getValue(FACING) ? state.getValue(POWER) : 0;
	}

}
