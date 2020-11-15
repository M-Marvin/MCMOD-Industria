package de.redtec.tileentity;

import de.redtec.blocks.BlockFluidInput;
import de.redtec.dynamicsounds.ISimpleMachineSound;
import de.redtec.dynamicsounds.SoundMachine;
import de.redtec.registys.ModSoundEvents;
import de.redtec.registys.ModTileEntityType;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.FluidTankHelper;
import de.redtec.util.IFluidConnective;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidInput extends TileEntity implements IFluidConnective, ITickableTileEntity, ISimpleMachineSound {
	
	private int progress;
	private final int maxFluid;
	private FluidStack fluid;
	
	public TileEntityFluidInput() {
		super(ModTileEntityType.FLUID_INPUT);
		this.maxFluid = 1500;
		this.fluid = FluidStack.EMPTY;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (!this.fluid.isEmpty()) compound.put("Fluid", this.fluid.writeToNBT(new CompoundNBT()));
		compound.putInt("progress", this.progress);
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compund) {
		this.fluid = FluidStack.loadFluidStackFromNBT(compund.getCompound("Fluid"));
		this.progress = compund.getInt("progress");
		super.func_230337_a_(state, compund);
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		if (!this.fluid.isEmpty()) {
			int transfer = Math.min(this.fluid.getAmount(), amount);
			this.fluid.shrink(transfer);
			return new FluidStack(this.fluid.getFluid(), transfer);
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		return fluid;
	}
	
	@Override
	public Fluid getFluidType() {
		return this.fluid.getFluid();
	}

	@Override
	public void tick() {
		
		if (!this.world.isRemote) {
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(world);
			handler.updateNetwork(world, pos);
			ElectricityNetwork network = handler.getNetwork(pos);
			
			if (network.canMachinesRun() && canSourceFluid() && this.progress++ >= 10) {
				
				this.progress = 0;
				
				BlockPos tankBeginPos = this.pos.offset(this.getBlockState().get(BlockFluidInput.FACING));
				BlockPos sourcePos = new FluidTankHelper(this.world, tankBeginPos).extractFluidFromTank();
				FluidState sourceFluid = this.world.getFluidState(sourcePos);
				FluidStack fluid = new FluidStack(sourceFluid.getFluid(), 1000);

				this.world.setBlockState(sourcePos, Blocks.AIR.getDefaultState());
				
				if (this.fluid.isEmpty()) {
					this.fluid = fluid;
				} else {
					this.fluid.grow(fluid.getAmount());
				}
				
			}
			
			if (!this.fluid.isEmpty()) {
				
				this.fluid = this.pushFluid(this.fluid, world, pos);
				
			}
			
		} else {
			
			if (this.isSoundRunning()) {
				
				SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
				
				if (this.sound == null ? true : !soundHandler.isPlaying(sound)) {
					
					this.sound = new SoundMachine(this, ModSoundEvents.PUMP_LOOP);
					soundHandler.play(this.sound);
					
				}
				
			}
			
			
		}
		
	}

	private SoundMachine sound;
	
	@Override
	public boolean isSoundRunning() {

		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(world);
		ElectricityNetwork network = handler.getNetwork(pos);
		
		return network.canMachinesRun() && canSourceFluid();
		
	}
	
	public boolean canSourceFluid() {
		
		BlockPos tankBeginPos = this.pos.offset(this.getBlockState().get(BlockFluidInput.FACING));
		BlockPos sourceBlock = new FluidTankHelper(this.world, tankBeginPos).extractFluidFromTank();
		
		if (sourceBlock != null) {
			
			FluidState sourceFluid = this.world.getFluidState(sourceBlock);
			FluidStack fluid = new FluidStack(sourceFluid.getFluid(), 1000);
			
			return this.fluid.isEmpty() || (this.maxFluid - this.fluid.getAmount() >= 1000 && this.fluid.getFluid() == fluid.getFluid());
			
		}
		
		return false;
		
	}

	@Override
	public boolean canConnect(Direction side) {
		return side == this.getBlockState().get(BlockFluidInput.FACING).getOpposite();
	}

}
