package de.industria.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.industria.blocks.BlockItemPipePreassurizer;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;

public class TileEntityItemPipePreassurizer extends TileEntityItemPipe {
	
	public TileEntityItemPipePreassurizer() {
		super(ModTileEntityType.ITEM_PIPE_PREASSURIZER);
	}
	
	@Override
	public Direction getOtherOutlet(Direction inputDirection) {
		if (inputDirection.getAxis() == this.getBlockState().get(BlockStateProperties.FACING).getAxis()) return inputDirection;
		return null;
	}
	
	@Override
	public boolean isPreassurized() {
		return true;
	}

	public Direction getInputSide() {
		return this.getBlockState().get(BlockStateProperties.FACING);
	}
	
	public Direction getOutputSide() {
		return this.getInputSide().getOpposite();
	}
	
	@Override
	public VoxelShape getItemDetectBounds() {
		Direction facing = this.getBlockState().get(BlockItemPipePreassurizer.FACING);
		if (facing.getAxis().isVertical()) {
			return Block.makeCuboidShape(0, -16, 0, 16, 32, 16);
		} else {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(0, 0, -16, 16, 16, 32), facing);
		}
	}
	
	@Override
	public void tick() {
		
		// TODO
		this.preassure = 1.0F;
		
		if (!this.world.isRemote) {
			
			if (this.isPreassurized() && this.world.getGameTime() % 10 == 0) {
				
				Direction outletDirection = getOutputSide();
				BlockPos outletPos = this.pos.offset(outletDirection);
				TileEntity nextPipe = this.world.getTileEntity(outletPos);
				
				if (nextPipe instanceof TileEntityItemPipe) {
					
					List<BlockPos> pipeStream = new ArrayList<BlockPos>();
					((TileEntityItemPipe) nextPipe).preassurizePipe(outletDirection, preassure, pipeStream);
					
				}
				
			}
			
		} else {

			float posX = this.pos.getX() + 0.5F;
			float posY = this.pos.getY() + 0.5F;
			float posZ = this.pos.getZ() + 0.5F;
			
			if (isPreassurized()) {
				
				IParticleData particle = ParticleTypes.CLOUD;
				float speed = 0.1F;
				
				float xIn = getInputSide().getXOffset() * speed;
				float yIn = getInputSide().getYOffset() * speed;
				float zIn = getInputSide().getZOffset() * speed;		
				
				if (this.world.rand.nextInt(14) == 0) {
					this.world.addParticle(particle, posX + xIn * 10, posY + yIn * 10, posZ + zIn * 10, -xIn, -yIn, -zIn);
				}
							
			}
			
		}

		super.tick();
		
	}	
	
	@Override
	public boolean preassurizePipe(Direction inputDirection, float preassure, List<BlockPos> pipeStreamList) {

		if (pipeStreamList.contains(this.pos)) return false;
			
		Direction outletDirection = getOtherOutlet(inputDirection);
		
		if (outletDirection == null) return false;
				
		if (isOpenPipe(this.pos.offset(outletDirection), outletDirection)) {
			
			pipeStreamList.add(this.pos);
			return true;
			
		}
		
		return false;
		
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		
		super.read(state, nbt);
	}
	
}
