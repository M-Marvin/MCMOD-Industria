package de.industria.tileentity;

import java.util.List;

import de.industria.blocks.BlockPreassurePipe;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.handler.MachineSoundHelper;
import de.industria.util.handler.MathHelper;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class TileEntityPreassurePipe extends TileEntity implements ITickableTileEntity, ITESimpleMachineSound {
	
	public float preassure;
	public Direction inputSide;
	public Direction outputSide;
	protected long lastPressurizing;
	
	public TileEntityPreassurePipe() {
		this(ModTileEntityType.PREASSURE_PIPE);
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			//if (data[0] != null) ((TileEntityPreassurePipe) tileEntity).preassure = (Float) data[0];
			//if (data[1] != null) ((TileEntityPreassurePipe) tileEntity).inputSide = ((int) data[1]) >= 0 ? Direction.from3DDataValue((int) data[1]) : null;
			//if (data[2] != null) ((TileEntityPreassurePipe) tileEntity).outputSide = ((int) data[2]) >= 0 ? Direction.from3DDataValue((int) data[2]) : null;
		}, () -> preassure, () -> inputSide != null ? inputSide.get3DDataValue() : -1, () -> outputSide != null ? outputSide.get3DDataValue() : -1);
	}
	
	public TileEntityPreassurePipe(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	public boolean isPreassurized() {
		return inputSide != null && outputSide != null && this.preassure != 0;
	}
	
	public Direction getInputSide() {
		return inputSide;
	}
	
	public Direction getOutputSide() {
		return outputSide;
	}
	
	public VoxelShape getItemDetectBounds() {
		Direction outlet = this.getOutputSide();
		int extraHeight = (int) ((this.preassure < 0 ? -this.preassure : this.preassure) * 16);
		if (!((BlockPreassurePipe) ModItems.preassure_pipe).canConnect(getBlockState(), level, worldPosition, outlet)) {
			if (outlet.getAxis().isVertical()) {
				return outlet == Direction.UP ? Block.box(2, 2, 2, 14, 16 + extraHeight, 14) : Block.box(2, -extraHeight, 2, 14, 14, 14);
			} else {
				return VoxelHelper.rotateShape(Block.box(2, 2, -extraHeight, 14, 14, 14), outlet);
			}
		}
		return Block.box(2, 2, 2, 14, 14, 14);
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide) {
			
			if (this.level.getGameTime() - this.lastPressurizing > 10) {
				this.inputSide = null;
				this.outputSide = null;
			}
			
			if (this.level.getGameTime() % 10 == 0) this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			
		} else {

			if (!MachineSoundHelper.isPlayingMachineSound(ModSoundEvents.ITEM_PIPE_STRAM, 7, this.worldPosition)) {
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.ITEM_PIPE_STRAM);
			}
			
			if (isPreassurized()) {
				
				IParticleData particle = ParticleTypes.CLOUD;
				float speed = 0.05F * (this.preassure < 0 ? -1 : 1);
				
				float posX = this.worldPosition.getX() + 0.5F;
				float posY = this.worldPosition.getY() + 0.5F;
				float posZ = this.worldPosition.getZ() + 0.5F;
				
				float xIn = getInputSide().getStepX() * speed;
				float yIn = getInputSide().getStepY() * speed;
				float zIn = getInputSide().getStepZ() * speed;
				float xOut = getOutputSide().getStepX() * speed;			
				float yOut = getOutputSide().getStepY() * speed;			
				float zOut = getOutputSide().getStepZ() * speed;			
				
				if (this.level.random.nextInt(14) == 0) {
					this.level.addParticle(particle, posX, posY, posZ, xOut, yOut, zOut);
					this.level.addParticle(particle, posX + xIn, posY + yIn, posZ + zIn, -xIn, -yIn, -zIn);
					
					if (!((BlockPreassurePipe) ModItems.preassure_pipe).canConnect(getBlockState(), level, worldPosition, this.getOutputSide()) && this.level.random.nextInt((int) (Math.max(4 / this.preassure, 1))) == 0) {
						if (this.preassure < 0) {
//							TODO: Particles for negative preassure
//							float xOut2 = (this.preassure < 0 ? getInputSide() : getOutputSide()).getXOffset() * speed * -this.preassure * 4;
//							float yOut2 = (this.preassure < 0 ? getInputSide() : getOutputSide()).getYOffset() * speed * -this.preassure * 4;			
//							float zOut2 = (this.preassure < 0 ? getInputSide() : getOutputSide()).getZOffset() * speed * -this.preassure * 4;			
//							float rX = (this.world.rand.nextFloat() - 0.5F) * 0.7F;	
//							float rY = (this.world.rand.nextFloat() - 0.5F) * 0.7F;	
//							float rZ = (this.world.rand.nextFloat() - 0.5F) * 0.7F;	
//							
//							this.world.addParticle(particle, posX + xOut2 * 10 + rX, posY + yOut2 * 10 + rY, posZ + zOut2 * 10 + rZ, xOut * this.preassure * -4, yOut * this.preassure * -4, zOut * this.preassure * -4);
						} else {
							this.level.addParticle(particle, posX + xOut * 10, posY + yOut * 10, posZ + zOut * 10, xOut * this.preassure * 4, yOut * this.preassure * 4, zOut * this.preassure * 4);
						}
					}
				}
								
			}
			
		}

		if (isPreassurized()) {
			
			List<AxisAlignedBB> itemBounds = getItemDetectBounds().toAabbs();
			
			itemBounds.forEach((boundBox) -> {
				boundBox = boundBox.move(this.worldPosition);
				this.level.getEntities(null, boundBox).forEach((entity) -> {
					
					if (entity instanceof ItemEntity) {
						((ItemEntity) entity).setDefaultPickUpDelay();
						((ItemEntity) entity).tickCount = 0;
					}
					
					boolean flag = true;
					if (entity instanceof PlayerEntity) {
						if (entity.isSpectator()) flag = false;
					}
					
					if (flag) {

						entity.fallDistance = 0;
						
						Vector3i motionVec1 = this.getInputSide().getOpposite().getNormal();
						Vector3i motionVec2 = this.getOutputSide().getNormal();
						
						float acceleration = 0.1F;
						Vector3d force = new Vector3d(motionVec1.getX() == 0 ? motionVec2.getX() : motionVec1.getX(), motionVec1.getY() == 0 ? motionVec2.getY() : motionVec1.getY(), motionVec1.getZ() == 0 ? motionVec2.getZ() : motionVec1.getZ());
						force = force.multiply(preassure * acceleration, preassure * acceleration, preassure * acceleration);
						
						Vector3d motion = entity.getDeltaMovement();
						motion = motion.add(force);
						motion = new Vector3d(MathHelper.castBounds(preassure, motion.x), MathHelper.castBounds(preassure, motion.y), MathHelper.castBounds(preassure, motion.z));
						entity.setDeltaMovement(motion);
						
					}
					
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
		if (((BlockPreassurePipe) ModItems.preassure_pipe).canConnect(getBlockState(), this.level, this.worldPosition, outletDirection)) {
			return true;
		} else {
			BlockState endState = this.level.getBlockState(pos);
			if (!endState.isSolidRender(this.level, pos) || endState.isAir()) return true;
		}
		return false;
	}
	
	public Direction getOtherOutlet(Direction inputDirection) {
		BlockState state = getBlockState();
		if (inputDirection.equals(state.getValue(BlockPreassurePipe.FACING).getOpposite())) {
			return state.getValue(BlockPreassurePipe.CONNECTION);
		} else if (inputDirection.equals(state.getValue(BlockPreassurePipe.CONNECTION).getOpposite())) {
			return state.getValue(BlockPreassurePipe.FACING);
		}
		return null;
	}
	
	public boolean preassurizePipe(Direction inputDirection, float preassure, List<BlockPos> pipeStreamList) {
		
		if (!pipeStreamList.contains(this.worldPosition)) {
			
			Direction outletDirection = getOtherOutlet(inputDirection);
			
			if (outletDirection != null) {
				
				if (isOpenPipe(this.worldPosition.relative(outletDirection), outletDirection)) {
					
					this.inputSide = inputDirection.getOpposite();
					this.outputSide = outletDirection;
					this.preassure = preassure;
					this.lastPressurizing = this.level.getGameTime();
					pipeStreamList.add(this.worldPosition);
					
					TileEntity nextPipe = this.level.getBlockEntity(worldPosition.relative(outletDirection));
					if (nextPipe instanceof TileEntityPreassurePipe) {
						if (!((TileEntityPreassurePipe) nextPipe).preassurizePipe(outletDirection, preassure, pipeStreamList)) {
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
	public CompoundNBT save(CompoundNBT compound) {
		if (this.inputSide != null && this.outputSide != null) {
			 compound.putString("InputSide", this.inputSide.getName());
			compound.putString("OutputSide", this.outputSide.getName());
			compound.putFloat("Preassure", this.preassure);
		}
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		this.inputSide = null;
		this.outputSide = null;
		this.preassure = 0;
		if (nbt.contains("InputSide") && nbt.contains("OutputSide")) {
			this.inputSide = Direction.byName(nbt.getString("InputSide"));
			this.outputSide = Direction.byName(nbt.getString("OutputSide"));
			this.preassure = nbt.getFloat("Preassure");
		}
		super.load(state, nbt);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getTag());
	}
	
}
