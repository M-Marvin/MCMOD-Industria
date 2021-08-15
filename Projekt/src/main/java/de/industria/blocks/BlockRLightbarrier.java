package de.industria.blocks;

import java.util.List;
import java.util.Random;

import de.industria.tileentity.TileEntitySimpleBlockTicking;
import de.industria.util.handler.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockRLightbarrier extends BlockContainerBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	
	public BlockRLightbarrier() {
		super("lightbarrier", Material.WOOD, 1.5F, SoundType.WOOD, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		return new TileEntitySimpleBlockTicking();
	}
	
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		
		int powerIn = rayTraceTargets(state, world, pos, rand);
		int power = state.getValue(POWER);
		
		if (powerIn != power) {
			
			world.setBlockAndUpdate(pos, state.setValue(POWER, powerIn));
			world.updateNeighborsAtExceptFromFacing(pos.relative(state.getValue(FACING).getOpposite()), this, state.getValue(FACING));
			
		}
		
	}
	
	public int rayTraceTargets(BlockState state, World world, BlockPos pos, Random rand) {
		
		int maxReach = 32;
		Direction facing = state.getValue(FACING);
		
		int i;
		for (i = 0; i <= maxReach; i++) {
			BlockPos pos1 = pos.relative(facing, i);
			BlockState state1 = world.getBlockState(pos1);
			if (state1.getBlock() == this && state1.getValue(FACING) == facing.getOpposite()) break;
		}
		
		if (i > maxReach) return 0;
		
		Vector3i facingVec = facing.getNormal();
		Vector3d startPoint = new Vector3d(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F).add(facingVec.getX() * 0.5F, facingVec.getY() * 0.5F, facingVec.getZ() * 0.5F);
		Vector3d endPoint = startPoint.add(facingVec.getX() * (i - 1), facingVec.getY() * (i - 1), facingVec.getZ() * (i - 1));
		
		world.addParticle(ParticleTypes.END_ROD,startPoint.x , startPoint.y, startPoint.z, 0, 0, 0);
		world.addParticle(ParticleTypes.END_ROD,endPoint.x , endPoint.y, endPoint.z, 0, 0, 0);
		
		RayTraceContext rayTrace = new RayTraceContext(startPoint, endPoint, BlockMode.VISUAL, FluidMode.ANY, new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ()));
		BlockRayTraceResult result = world.clip(rayTrace);
		
		float power = 0;
		
		if (result.getType() == Type.MISS) {
			
			BlockPos endPos = pos.relative(facing, i);
			AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() + 0.8F, pos.getY() + 0.8F, pos.getZ() + 0.8F, endPos.getX() + 0.2F, endPos.getY() + 0.2F, endPos.getZ() + 0.2F);
			List<Entity> entitysBetween = world.getEntities(null, aabb);
			
			if (entitysBetween.size() > 0) {
				
				double dist = i;
				for (Entity entity : entitysBetween) {
					Vector3d entityPos = entity.getPosition(0).multiply(facingVec.getX(), facingVec.getY(), facingVec.getZ());
					Vector3d blockPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ()).multiply(facingVec.getX(), facingVec.getY(), facingVec.getZ());
					double distance = MathHelper.getDistance(entityPos.x + entityPos.y + entityPos.z, blockPos.x + blockPos.y + blockPos.z);
					
					if (distance < dist) dist = distance;
				}
				
				power = (float) (dist / (float) i);
				
			}
			
		} else {
			
			Vector3d collidePos = new Vector3d(result.getBlockPos().getX(), result.getBlockPos().getY(), result.getBlockPos().getZ()).multiply(facingVec.getX(), facingVec.getY(), facingVec.getZ());
			Vector3d blockPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ()).multiply(facingVec.getX(), facingVec.getY(), facingVec.getZ());
			double distance = MathHelper.getDistance(collidePos.x + collidePos.y + collidePos.z, blockPos.x + blockPos.y + blockPos.z);
			
			power = (float) (distance / (float) i);
			
		}
		
		int power1 = (int) Math.max(0, Math.min(15, power * 15));
		
		if (power1 > 0 && world.isClientSide) {
			
			for (float i1 = 0; i1 < i; i1 += 0.1F) {
				
				if (rand.nextInt(6) == 0) {
					IParticleData particle = ParticleTypes.END_ROD;
					Vector3d particlePos = startPoint.add(new Vector3d(i1, i1, i1).multiply(facingVec.getX(), facingVec.getY(), facingVec.getZ()));
					world.addParticle(particle, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
				}
				
			}
			
		}
		
		return power1;
		
	}
	
	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		this.rayTraceTargets(state, world, pos, rand);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == world.getBlockState(pos).getValue(FACING);
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
