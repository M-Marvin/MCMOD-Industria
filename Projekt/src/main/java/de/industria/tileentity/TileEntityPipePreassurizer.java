package de.industria.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.industria.blocks.BlockPipePreassurizer;
import de.industria.blocks.BlockPreassurePipe;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.ITEFluidConnective;
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

public class TileEntityPipePreassurizer extends TileEntityPreassurePipe implements ITEFluidConnective {
	
	public final int maxStorage;
	public FluidStack compressedAir;
	
	public TileEntityPipePreassurizer() {
		super(ModTileEntityType.PIPE_PREASSURIZER);
		this.compressedAir = FluidStack.EMPTY;
		this.maxStorage = 1000;
	}
	
	@Override
	public Direction getOtherOutlet(Direction inputDirection) {
		if (inputDirection.getAxis() == this.getBlockState().getValue(BlockStateProperties.FACING).getAxis()) return inputDirection;
		return null;
	}
	
	@Override
	public boolean isPreassurized() {
		return this.preassure > 0;
	}

	public Direction getInputSide() {
		return this.getBlockState().getValue(BlockStateProperties.FACING);
	}
	
	public Direction getOutputSide() {
		return this.getInputSide().getOpposite();
	}
	
	@Override
	public VoxelShape getItemDetectBounds() {
		Direction facing = this.getBlockState().getValue(BlockPipePreassurizer.FACING);
		int extraHeight = (int) (this.preassure * 16);
		boolean flag1 = !((BlockPreassurePipe) ModItems.preassure_pipe).canConnect(getBlockState(), level, worldPosition, getInputSide());
		boolean flag2 = !((BlockPreassurePipe) ModItems.preassure_pipe).canConnect(getBlockState(), level, worldPosition, getOutputSide());
		int extraHeight1 = flag1 ? extraHeight : 0;
		int extraHeight2 = flag2 ? extraHeight : 0;
		if (facing.getAxis().isVertical()) {
			return Block.box(2, -extraHeight1, 2, 14, 16 + extraHeight2, 14);
		} else {
			return VoxelHelper.rotateShape(Block.box(2, 2, -extraHeight1, 14, 14, 16 + extraHeight2), facing);
		}
	}
	
	@Override
	public void tick() {
		
		int power = this.level.getBestNeighborSignal(worldPosition);
		float preassure = power / 15F * 10;
		int neededAir = (int) (preassure * 10);
		if (this.compressedAir.getAmount() >= neededAir) {
			this.preassure = power / 15F * 10;
			if (this.compressedAir.getAmount() >= neededAir && !this.compressedAir.isEmpty() && this.isPreassurized()) this.compressedAir.shrink(neededAir);
		} else {
			this.preassure = 0;
		}
		
		if (!this.level.isClientSide) {
			
			if (this.isPreassurized() && this.level.getGameTime() % 20 == 0) {
				
				// Push
				Direction outletDirection = getOutputSide();
				BlockPos outletPos = this.worldPosition.relative(outletDirection);
				TileEntity nextPipe = this.level.getBlockEntity(outletPos);
				
				boolean flag = true;
				
				if (nextPipe instanceof TileEntityPreassurePipe) {
					
					List<BlockPos> pipeStream = new ArrayList<BlockPos>();
					flag = ((TileEntityPreassurePipe) nextPipe).preassurizePipe(outletDirection, preassure, pipeStream);
					
				}
				
				// Pull
				Direction inputDirection = getInputSide();
				BlockPos inputPos = this.worldPosition.relative(inputDirection);
				nextPipe = this.level.getBlockEntity(inputPos);
				
				if (nextPipe instanceof TileEntityPreassurePipe && flag) {
					
					List<BlockPos> pipeStream = new ArrayList<BlockPos>();
					((TileEntityPreassurePipe) nextPipe).preassurizePipe(inputDirection, -preassure, pipeStream);
					
				}
				
			}
			
		} else {

			float posX = this.worldPosition.getX() + 0.5F;
			float posY = this.worldPosition.getY() + 0.5F;
			float posZ = this.worldPosition.getZ() + 0.5F;
			
			if (isPreassurized()) {
				
				IParticleData particle = ParticleTypes.CLOUD;
				float speed = 0.1F;
				
				float xIn = getInputSide().getStepX() * speed;
				float yIn = getInputSide().getStepY() * speed;
				float zIn = getInputSide().getStepZ() * speed;		
				
				if (this.level.random.nextInt(14) == 0) {
					this.level.addParticle(particle, posX + xIn * 10, posY + yIn * 10, posZ + zIn * 10, -xIn, -yIn, -zIn);
				}
							
			}
			
		}

		super.tick();
		
	}	
	
	@Override
	public boolean preassurizePipe(Direction inputDirection, float preassure, List<BlockPos> pipeStreamList) {

		if (pipeStreamList.contains(this.worldPosition)) return false;
			
		Direction outletDirection = getOtherOutlet(inputDirection);
		
		if (outletDirection == null) return false;
				
		if (isOpenPipe(this.worldPosition.relative(outletDirection), outletDirection)) {
			
			pipeStreamList.add(this.worldPosition);
			return true;
			
		}
		
		return false;
		
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("CompressedAir", this.compressedAir.writeToNBT(new CompoundNBT()));
		compound.putFloat("Preassure", this.preassure);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		this.compressedAir = FluidStack.loadFluidStackFromNBT(nbt.getCompound("CompressedAir"));
		this.preassure = nbt.getFloat("Preassure");
		super.load(state, nbt);
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
		return this.getBlockState().getValue(BlockStateProperties.FACING).getAxis() != side.getAxis();
	}

	@Override
	public void setStorage(FluidStack storage) {
		this.compressedAir = storage;
	}
	
}
