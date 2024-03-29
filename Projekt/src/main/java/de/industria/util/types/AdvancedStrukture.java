package de.industria.util.types;

import java.util.ArrayList;
import java.util.List;

import de.industria.util.blockfeatures.ITEPostMoveHandled;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AdvancedStrukture {
	
	private List<BlockPos> blocks;
	private Direction moveDirection;
	
	/** Create a Advanced Structure by the a AdvancedPistonStructure.
	 * Move Direction and Facing are copied from the PistonStructure Push Direction.
	 * 
	 * @param pistonStrukture Piston Structure
	 */
	public AdvancedStrukture(AdvancedPistonBlockStructureHelper pistonStrukture) {
		
		this.blocks = pistonStrukture.getBlocksToMove();
		this.blocks.addAll(pistonStrukture.getBlocksToDestroy());
		this.moveDirection = pistonStrukture.getMoveDirection();
		
	}
	
	public List<BlockPos> getBlocks() {
		return blocks;
	}
	
	public Direction getMoveDirection() {
		return moveDirection;
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	public boolean doMove(World world, int moveDistance) {
		
		List<BlockPos> positions = new ArrayList<BlockPos>();
		List<BlockState> states = new ArrayList<BlockState>();
		List<CompoundNBT> nbt = new ArrayList<CompoundNBT>();
		int minY = this.blocks.get(0).getY();
		int maxY = this.blocks.get(0).getY();
		
		for (BlockPos block : this.blocks) {
			
			BlockState state = world.getBlockState(block);
			TileEntity tileEntity = world.getBlockEntity(block);
			
			if (!state.isAir()) {
				
				BlockState replaceState = world.getBlockState(block.relative(this.moveDirection, moveDistance));
				boolean flag = replaceState.isAir();
				if (!flag) {
					if (replaceState.getPistonPushReaction() == PushReaction.DESTROY) flag = true;
					if (state.getPistonPushReaction() == PushReaction.DESTROY) flag = true;
				}
				
				if (!flag && !this.blocks.contains(block.relative(this.moveDirection, moveDistance)) && world.getBlockState(block.relative(this.moveDirection, moveDistance)).getBlock() != Blocks.WATER) {
					return false;
				}
				
				if (world.isOutsideBuildHeight(block.relative(this.moveDirection, moveDistance))) return false;
				positions.add(block);
				states.add(state);
				
				if (tileEntity != null) {
					
					CompoundNBT compound = new CompoundNBT();
					tileEntity.save(compound);
					nbt.add(compound);
					
				} else {
					
					nbt.add(null);
					
				}
				
			}
			
		}
		
		for (BlockPos block : this.blocks) {
			
			world.removeBlockEntity(block);
			world.setBlock(block, Blocks.AIR.defaultBlockState(), 114);
			if (block.getY() > maxY) maxY = block.getY();
			if (block.getY() < minY) minY = block.getY();
			
		}
		
		for (int index = 0; index < positions.size(); index++) {
			
			BlockPos pos = positions.get(index).relative(this.moveDirection, moveDistance);
			BlockState state = states.get(index);
			
			world.setBlock(pos, state, 114);
			
			if (state.hasTileEntity()) {
				
				CompoundNBT compound = nbt.get(index);
				TileEntity tileEntity = world.getBlockEntity(pos);
				
				if (tileEntity != null) {
					
					tileEntity.setPosition(pos);
					if (!(tileEntity instanceof BannerTileEntity)) tileEntity.load(state, compound);
					tileEntity.setPosition(pos);
					
					if (tileEntity instanceof ITEPostMoveHandled) {
						
						((ITEPostMoveHandled) tileEntity).handlePostMove(tileEntity.getBlockPos(), this.moveDirection, moveDistance, false);
						
					}
					
				}
				
			}
			
		}
		
		for (int index = 0; index < positions.size(); index++) {
			
			BlockPos posOld = positions.get(index);
			BlockPos posNew = posOld.relative(this.moveDirection, moveDistance);
			
			int newStateIndex = positions.indexOf(posOld.relative(this.moveDirection.getOpposite(), moveDistance));
			
			if (newStateIndex == -1) {
				world.updateNeighborsAt(posOld, Blocks.AIR);
			} else {
				world.updateNeighborsAt(posOld, states.get(newStateIndex).getBlock());
			}
			
			world.updateNeighborsAt(posNew, states.get(index).getBlock());
			
		}

		List<Entity> handledEntitys = new ArrayList<Entity>();
		for (int i = minY; i <= maxY; i++) {
			
			AxisAlignedBB aabb = calculateBoundsAtY(i);
			List<Entity> entitysToMove = world.getEntities(null, aabb);
			
			for (Entity entity : entitysToMove) {
				
				if (!handledEntitys.contains(entity)) {
					this.moveEntity(entity, this.moveDirection, moveDistance);
					handledEntitys.add(entity);
				}
				
			}
			
		}
		
		return true;
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean doRotate(World world, boolean right) {

		List<BlockPos> positions = new ArrayList<BlockPos>();
		List<BlockState> states = new ArrayList<BlockState>();
		List<CompoundNBT> nbt = new ArrayList<CompoundNBT>();
		int minY = this.blocks.get(0).getY();
		int maxY = this.blocks.get(0).getY();
		BlockPos center = calculateCenterAtY(-1);
		
		for (BlockPos block : this.blocks) {
			
			BlockState state = world.getBlockState(block);
			TileEntity tileEntity = world.getBlockEntity(block);
			
			if (!state.isAir()) {
				
				if (!world.getBlockState(calculateRotatedPosition(block, center, right)).isAir() && !this.blocks.contains(calculateRotatedPosition(block, center, right))) {
					return false;
				}
				
				positions.add(block);
				states.add(state);
				
				if (tileEntity != null) {
					
					CompoundNBT compound = new CompoundNBT();
					tileEntity.save(compound);
					nbt.add(compound);
					
				} else {
					
					nbt.add(null);
					
				}
				
			}
			
		}
		
		for (BlockPos block : this.blocks) {
			
			world.removeBlockEntity(block);
			world.setBlock(block, Blocks.AIR.defaultBlockState(), 114);
			if (block.getY() > maxY) maxY = block.getY();
			if (block.getY() < minY) minY = block.getY();
			
		}
		
		for (int index = 0; index < positions.size(); index++) {
			
			BlockPos pos = calculateRotatedPosition(positions.get(index), center, right);
			Rotation rotation = right ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
			BlockState state = states.get(index).rotate(rotation);
						
			world.setBlock(pos, state, 114);
			
			if (state.hasTileEntity()) {
				
				CompoundNBT compound = nbt.get(index);
				TileEntity tileEntity = world.getBlockEntity(pos);
				tileEntity.load(state, compound);
				tileEntity.setPosition(pos);
				tileEntity.rotate(rotation);
				
				if (tileEntity instanceof ITEPostMoveHandled) {
					
					((ITEPostMoveHandled) tileEntity).handlePostMove(positions.get(index), pos, false);
					
				}
				
			}
			
		}
		
		for (int index = 0; index < positions.size(); index++) {
			
			BlockPos posOld = positions.get(index);
			BlockPos posNew = calculateRotatedPosition(posOld, center, right);
			
			int newStateIndex = positions.indexOf(calculateRotatedPosition(posOld, center, !right));
			
			if (newStateIndex == -1) {
				world.updateNeighborsAt(posOld, Blocks.AIR);
			} else {
				world.updateNeighborsAt(posOld, states.get(newStateIndex).getBlock());
			}
			
			world.updateNeighborsAt(posNew, states.get(index).getBlock());
			
		}

		List<Entity> handledEntitys = new ArrayList<Entity>();
		for (int i = minY; i <= maxY; i++) {
			
			AxisAlignedBB aabb = calculateBoundsAtY(i);
			List<Entity> entitysToMove = world.getEntities(null, aabb);
			
			for (Entity entity : entitysToMove) {
				
				if (!handledEntitys.contains(entity)) {
					this.rotateEntity(entity, center, right);
					handledEntitys.add(entity);
				}
				
			}
			
		}
		
		return true;
		
	}
	
	private void moveEntity(Entity entity, Direction direction, int moveDistance) {
		
		if (entity instanceof PlayerEntity) {
			
			Vector3d pos = entity.position();
			pos = pos.add(direction.getStepX() * moveDistance, direction.getStepY() * moveDistance, direction.getStepZ() * moveDistance);
			((PlayerEntity) entity).teleportToWithTicket(pos.x, pos.y, pos.z);
			
		} else {
			
			Vector3d pos = entity.position();
			pos = pos.add(direction.getStepX() * moveDistance, direction.getStepY() * moveDistance, direction.getStepZ() * moveDistance);
			entity.setPos(pos.x, pos.y, pos.z);
			
		}
		
	}
	
	private void rotateEntity(Entity entity, BlockPos center, boolean rotateRight) {
		
		Vector3d pos = entity.position();

		Vector3d offset = pos.subtract(center.getX() + 0.5F, center.getY(), center.getZ() + 0.5F);
		
		if (!rotateRight) {
			
			double i1 = offset.x();
			double i2 = offset.z();
			
			offset = new Vector3d(i2, offset.y(), -i1);
			
		} else {

			double i1 = offset.z();
			double i2 = offset.x();
			
			offset = new Vector3d(-i1, offset.y(), i2);
			
		}
		
		pos = offset.add(center.getX() + 0.5F, center.getY(), center.getZ() + 0.5F);

		if (entity instanceof PlayerEntity) {

			((ServerPlayerEntity) entity).teleportTo((ServerWorld) entity.level, pos.x, pos.y, pos.z, entity.yRot + (rotateRight ? 90 : -90), entity.xRot);
			
		} else {
			
			entity.setPos(pos.x, pos.y, pos.z);
			entity.yRot += rotateRight ? 90 : -90;
			
		}
		
	}
	
	public BlockPos calculateRotatedPosition(BlockPos position, BlockPos center, boolean rotateRight) {
		
		BlockPos offset = position.subtract(center);
		
		if (!rotateRight) {
			
			int i1 = offset.getX();
			int i2 = offset.getZ();
			
			offset = new BlockPos(i2, offset.getY(), i1 * -1);
			
		} else {

			int i1 = offset.getZ();
			int i2 = offset.getX();
			
			offset = new BlockPos(i1 * -1, offset.getY(), i2);
			
		}
		
		return center.offset(offset);
		
	}
	
	public BlockPos calculateCenterAtY(int y) {
		
		int minX = -1;
		int minZ = -1;
		int maxX = -1;
		int maxZ = -1;
		
		for (BlockPos pos : this.blocks) {
			
			if (pos.getY() == y || y == -1) {
				
				if (pos.getX() > maxX || maxX == -1) {
					maxX = pos.getX();
				}
				if (pos.getZ() > maxZ || maxZ == -1) {
					maxZ = pos.getZ();
				}
				if (pos.getX() < minX || minX == -1) {
					minX = pos.getX();
				}
				if (pos.getZ() < minZ || minZ == -1) {
					minZ = pos.getZ();
				}
				
			}
			
		}
		
		int centerX = minX + (maxX - minX) / 2;
		int centerZ = minZ + (maxZ - minZ) / 2;
		
		return new BlockPos(centerX, y, centerZ);
		
	}
	
	public AxisAlignedBB calculateBoundsAtY(int y) {
		
		int minX = -1;
		int minZ = -1;
		int maxX = -1;
		int maxZ = -1;
		
		for (BlockPos pos : this.blocks) {
			
			if (pos.getY() == y) {
				
				if (pos.getX() > maxX || maxX == -1) maxX = pos.getX();
				if (pos.getZ() > maxZ || maxZ == -1) maxZ = pos.getZ();
				if (pos.getX() < minX || minX == -1) minX = pos.getX();
				if (pos.getZ() < minZ || minZ == -1) minZ = pos.getZ();
				
			}
			
		}
		
		return new AxisAlignedBB(minX - 2, y - 2, minZ - 2, maxX + 2, y + 2, maxZ + 2);
		
	}
	
}
