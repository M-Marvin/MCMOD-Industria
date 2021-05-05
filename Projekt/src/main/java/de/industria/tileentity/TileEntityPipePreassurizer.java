package de.industria.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.industria.blocks.BlockPipePreassurizer;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IFluidConnective;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityPipePreassurizer extends TileEntityPreassurePipe implements IFluidConnective {
	
	public final int maxStorage;
	public FluidStack compressedAir;
	public int airTimer;
	
	public TileEntityPipePreassurizer() {
		super(ModTileEntityType.ITEM_PIPE_PREASSURIZER);
		this.compressedAir = FluidStack.EMPTY;
		this.maxStorage = 1000;
	}
	
	@Override
	public Direction getOtherOutlet(Direction inputDirection) {
		if (inputDirection.getAxis() == this.getBlockState().get(BlockStateProperties.FACING).getAxis()) return inputDirection;
		return null;
	}
	
	@Override
	public boolean isPreassurized() {
		return this.preassure > 0;
	}

	public Direction getInputSide() {
		return this.getBlockState().get(BlockStateProperties.FACING);
	}
	
	public Direction getOutputSide() {
		return this.getInputSide().getOpposite();
	}
	
	@Override
	public VoxelShape getItemDetectBounds() {
		Direction facing = this.getBlockState().get(BlockPipePreassurizer.FACING);
		int extraHeight = (int) (this.preassure * 16);
		if (facing.getAxis().isVertical()) {
			return Block.makeCuboidShape(2, -extraHeight, 2, 14, 16 + extraHeight, 14);
		} else {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(2, 2, -extraHeight, 14, 14, 16 + extraHeight), facing);
		}
	}
	
	@Override
	public void tick() {
		
		int power = this.world.getRedstonePowerFromNeighbors(pos);
		float preassure = power / 15F * 10;
		int neededAir = (int) (preassure * 10);
		if (this.compressedAir.getAmount() >= neededAir) {
			this.preassure = power / 15F * 10;
			if (this.compressedAir.getAmount() >= neededAir && !this.compressedAir.isEmpty() && this.isPreassurized()) this.compressedAir.shrink(neededAir);
		} else {
			this.preassure = 0;
		}
		
		if (!this.world.isRemote) {
			
			if (this.isPreassurized() && this.world.getGameTime() % 10 == 0) {
				
				// Push
				Direction outletDirection = getOutputSide();
				BlockPos outletPos = this.pos.offset(outletDirection);
				TileEntity nextPipe = this.world.getTileEntity(outletPos);
				
				if (nextPipe instanceof TileEntityPreassurePipe && this.world.getGameTime() % 20 == 0) {
					
					List<BlockPos> pipeStream = new ArrayList<BlockPos>();
					((TileEntityPreassurePipe) nextPipe).preassurizePipe(outletDirection, preassure, pipeStream);
					
				}
				
				// Pull
				Direction inputDirection = getInputSide();
				BlockPos inputPos = this.pos.offset(inputDirection);
				nextPipe = this.world.getTileEntity(inputPos);
				
				if (nextPipe instanceof TileEntityPreassurePipe) {
					
					List<BlockPos> pipeStream = new ArrayList<BlockPos>();
					((TileEntityPreassurePipe) nextPipe).preassurizePipe(inputDirection, -preassure, pipeStream);
					
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
		compound.put("CompressedAir", this.compressedAir.writeToNBT(new CompoundNBT()));
		compound.putInt("AirTimer", this.airTimer);
		compound.putFloat("Preassure", this.preassure);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		this.compressedAir = FluidStack.loadFluidStackFromNBT(nbt.getCompound("CompressedAir"));
		this.airTimer = nbt.getInt("AirTimer");
		this.preassure = nbt.getFloat("Preassure");
		super.read(state, nbt);
	}

	@Override
	public FluidStack getFluid(int amount) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		if (fluid.getFluid() == ModFluids.COMPRESSED_AIR) {
			int capcaity = this.maxStorage - compressedAir.getAmount();
			int transfer = Math.min(capcaity, fluid.getAmount());
			if (transfer > 0) {
				FluidStack fluidRest = fluid.copy();
				fluidRest.shrink(transfer);
				if (compressedAir.isEmpty()) {
					compressedAir = new FluidStack(fluid.getFluid(), transfer);
				} else {
					compressedAir.grow(transfer);
				}
				return fluidRest;
			}
		}
		return fluid;
	}

	@Override
	public Fluid getFluidType() {
		return ModFluids.COMPRESSED_AIR;
	}

	@Override
	public FluidStack getStorage() {
		return this.compressedAir;
	}

	@Override
	public boolean canConnect(Direction side) {
		return this.getBlockState().get(BlockStateProperties.FACING).getAxis() != side.getAxis();
	}
	
}
