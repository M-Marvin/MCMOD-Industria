package de.industria.tileentity;

import java.util.List;

import de.industria.Industria;
import de.industria.blocks.BlockItemPipe;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.handler.MachineSoundHelper;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class TileEntityItemPipe extends TileEntity implements ITickableTileEntity, ISimpleMachineSound {
	
	public float preassure;
	public Direction inputSide;
	public Direction outputSide;
	protected long lastPressurizing;
	
	public TileEntityItemPipe() {
		this(ModTileEntityType.ITEM_PIPE);
	}
	
	public TileEntityItemPipe(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	public boolean isPreassurized() {
		return inputSide != null && outputSide != null;
	}
	
	public Direction getInputSide() {
		return inputSide;
	}
	
	public Direction getOutputSide() {
		return outputSide;
	}

	public VoxelShape getItemDetectBounds() {
		Direction outlet = this.getOutputSide();
		int extraHeight = (int) (this.preassure * 16);
		if (!((BlockItemPipe) Industria.item_pipe).canConnect(getBlockState(), world, pos, outlet)) {
			if (outlet.getAxis().isVertical()) {
				return outlet == Direction.UP ? Block.makeCuboidShape(3, 3, 3, 13, 16 + extraHeight, 13) : Block.makeCuboidShape(3, -extraHeight, 3, 13, 13, 13);
			} else {
				return VoxelHelper.rotateShape(Block.makeCuboidShape(3, 3, -extraHeight, 13, 13, 13), outlet);
			}
		}
		return Block.makeCuboidShape(3, 3, 3, 13, 13, 13);
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote) {
			
			if (this.world.getGameTime() - this.lastPressurizing > 60) {
				this.inputSide = null;
				this.outputSide = null;
			}
			
			if (this.world.getGameTime() % 10 == 0) this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
		} else {
			
			if (isPreassurized()) {
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.ITEM_PIPE_STRAM);
				
				IParticleData particle = ParticleTypes.CLOUD;
				float speed = 0.05F;

				float posX = this.pos.getX() + 0.5F;
				float posY = this.pos.getY() + 0.5F;
				float posZ = this.pos.getZ() + 0.5F;
				
				float xIn = getInputSide().getXOffset() * speed;
				float yIn = getInputSide().getYOffset() * speed;
				float zIn = getInputSide().getZOffset() * speed;
				float xOut = getOutputSide().getXOffset() * speed;			
				float yOut = getOutputSide().getYOffset() * speed;			
				float zOut = getOutputSide().getZOffset() * speed;			
				
				if (this.world.rand.nextInt(14) == 0) {
					this.world.addParticle(particle, posX, posY, posZ, xOut, yOut, zOut);
					this.world.addParticle(particle, posX + xIn, posY + yIn, posZ + zIn, -xIn, -yIn, -zIn);
				}
				
				if (!((BlockItemPipe) Industria.item_pipe).canConnect(getBlockState(), world, pos, this.getOutputSide()) && this.world.rand.nextInt((int) (Math.max(4 / this.preassure, 1))) == 0) {
					this.world.addParticle(particle, posX + xOut * 10, posY + yOut * 10, posZ + zOut * 10, xOut * this.preassure * 4, yOut * this.preassure * 4, zOut * this.preassure * 4);
				}
				
			}
			
		}

		if (isPreassurized()) {
			
			List<AxisAlignedBB> itemBounds = getItemDetectBounds().toBoundingBoxList();
			
			itemBounds.forEach((boundBox) -> {
				boundBox = boundBox.offset(this.pos);
				this.world.getEntitiesInAABBexcluding(null, boundBox, null).forEach((entity) -> {
					
					if (entity instanceof ItemEntity) {
						((ItemEntity) entity).setDefaultPickupDelay();
					}
					
					entity.fallDistance = 0;
					
					Vector3i motionVec1 = this.getInputSide().getOpposite().getDirectionVec();
					Vector3i motionVec2 = this.getOutputSide().getDirectionVec();
					
					Vector3d force = new Vector3d(motionVec1.getX() == 0 ? motionVec2.getX() : motionVec1.getX(), motionVec1.getY() == 0 ? motionVec2.getY() : motionVec1.getY(), motionVec1.getZ() == 0 ? motionVec2.getZ() : motionVec1.getZ());
					force = force.mul(preassure, preassure, preassure);
					
					Vector3d motion = entity.getMotion();
					motion = motion.add(force);
					motion = new Vector3d(Math.max(-preassure, Math.min(preassure, motion.x)), Math.max(-preassure, Math.min(preassure, motion.y)), Math.max(-preassure, Math.min(preassure, motion.z)));
					entity.setMotion(motion);
					
				});
			});
			
		}
		
	}

	@Override
	public boolean isSoundRunning() {
		return this.isPreassurized();
	}
	
	@SuppressWarnings("deprecation")
	public boolean isOpenPipe(BlockPos pos, Direction outletDirection) {
		if (((BlockItemPipe) Industria.item_pipe).canConnect(getBlockState(), this.world, this.pos, outletDirection)) {
			return true;
		} else {
			BlockState endState = this.world.getBlockState(pos);
			if (!endState.isSolid() || endState.isAir()) return true;
		}
		return false;
	}
	
	public Direction getOtherOutlet(Direction inputDirection) {
		BlockState state = getBlockState();
		if (inputDirection.equals(state.get(BlockItemPipe.FACING).getOpposite())) {
			return state.get(BlockItemPipe.CONNECTION);
		} else if (inputDirection.equals(state.get(BlockItemPipe.CONNECTION).getOpposite())) {
			return state.get(BlockItemPipe.FACING);
		}
		return null;
	}
	
	public boolean preassurizePipe(Direction inputDirection, float preassure, List<BlockPos> pipeStreamList) {
		
		if (!pipeStreamList.contains(this.pos)) {
			
			Direction outletDirection = getOtherOutlet(inputDirection);
			
			if (outletDirection != null) {
				
				if (isOpenPipe(this.pos.offset(outletDirection), outletDirection)) {
					
					this.inputSide = inputDirection.getOpposite();
					this.outputSide = outletDirection;
					this.preassure = preassure;
					this.lastPressurizing = this.world.getGameTime();
					pipeStreamList.add(this.pos);
					
					TileEntity nextPipe = this.world.getTileEntity(pos.offset(outletDirection));
					if (nextPipe instanceof TileEntityItemPipe) {
						if (!((TileEntityItemPipe) nextPipe).preassurizePipe(outletDirection, preassure, pipeStreamList)) {
							this.inputSide = null;
							this.outputSide = null;
							this.preassure = 0;
							return false;
						}
					}
							
					return true;
					
				}
				
			}
			
		}
		
		this.inputSide = null;
		this.outputSide = null;
		this.preassure = 0;
		return false;
		
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (this.inputSide != null && this.outputSide != null) {
			 compound.putString("InputSide", this.inputSide.getName2());
			compound.putString("OutputSide", this.outputSide.getName2());
			compound.putFloat("Preassure", this.preassure);
		}
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		this.inputSide = null;
		this.outputSide = null;
		this.preassure = 0;
		if (nbt.contains("InputSide") && nbt.contains("OutputSide")) {
			this.inputSide = Direction.byName(nbt.getString("InputSide"));
			this.outputSide = Direction.byName(nbt.getString("OutputSide"));
			this.preassure = nbt.getFloat("Preassure");
		}
		super.read(state, nbt);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
}
