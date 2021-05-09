package de.industria.tileentity;

import de.industria.blocks.BlockMultiPart;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.gui.ContainerMAirCompressor;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IFluidConnective;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.MachineSoundHelper;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMAirCompressor extends TileEntity implements ITickableTileEntity, IFluidConnective, INamedContainerProvider, ISimpleMachineSound {
	
	public boolean hasPower;
	public boolean isWorking;
	public final int maxStorage;
	public FluidStack compressedAir;
	public int progress1;
	public int tankProgress;
	public int progress2;
	
	public TileEntityMAirCompressor() {
		super(ModTileEntityType.AIR_COMPRESSOR);
		this.maxStorage = 4000;
		this.compressedAir = FluidStack.EMPTY;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(1, 1, 1));
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
			
			this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
			this.isWorking = canWork() && this.hasPower;
			
			if (this.isWorking) {
				
				if (this.tankProgress >= 5) {
					
					this.progress2++;
					if (this.progress2 >= 10) {
						this.progress2 = 0;
						this.tankProgress = 0;
						
						if (this.compressedAir.isEmpty()) {
							this.compressedAir = new FluidStack(ModFluids.COMPRESSED_AIR, 1500);
						} else {
							this.compressedAir.grow(1500);
						}
						
					}
					
				} else {
					
					this.progress1++;
					if (this.progress1 >= 10) {
						this.progress1 = 0;
						this.tankProgress++;
					}
					
				}
				
			}
			
			FluidStack rest = pushFluid(this.compressedAir, world, pos);
			if (rest != compressedAir) compressedAir = rest;
			
		} else {
			
			if (this.isWorking) {

				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.COMPRESSOR_LOOP);
				
				if (this.world.rand.nextInt(10) == 0) {

					IParticleData paricle = ParticleTypes.POOF;
					Direction facing = getBlockState().get(BlockMultiPart.FACING);
					
					float ox = 0;
					float oz = 0;
					
					switch(facing) {
					default:
					case NORTH:
						oz = 1.5F;
						ox = 0.5F;
						break;
					case EAST:
						ox = -0.5F;
						oz = 0.5F;
						break;
					case SOUTH:
						ox = 0.5F;
						oz = -0.5F;
						break;
					case WEST:
						ox = 1.5F;
						oz = 0.5F;
						break;
					}
					
					float oy = 0.5F;
					float width = 1.0F;
					float height = 1.0F;
					
					float x = this.pos.getX() + ox + (world.rand.nextFloat() - 0.5F) * width;
					float y = this.pos.getY() + oy + (world.rand.nextFloat() - 0.5F) * height;
					float z = this.pos.getZ() + oz + (world.rand.nextFloat() - 0.5F) * width;
					this.world.addParticle(paricle, x, y, z, 0, 0, 0);
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}
	
	public boolean canWork() {
		return this.compressedAir.isEmpty() || this.maxStorage - this.compressedAir.getAmount() >= 1500;
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		if (!this.compressedAir.isEmpty()) {
			int transfer = Math.min(amount, this.compressedAir.getAmount());
			if (transfer > 0) {
				Fluid fluid = this.compressedAir.getFluid();
				this.compressedAir.shrink(transfer);
				return new FluidStack(fluid, transfer);
			}
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack insertFluid(FluidStack fluid) {
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
		return BlockMultiPart.getInternPartPos(getBlockState()).equals(new BlockPos(0, 0, 0));
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putInt("Progress1", this.progress1);
		compound.putInt("Progress2", this.progress2);
		compound.putInt("TankProgress", this.tankProgress);
		if (!this.compressedAir.isEmpty()) compound.put("CompressedAir", this.compressedAir.writeToNBT(new CompoundNBT()));
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		this.hasPower = nbt.getBoolean("hasPower");
		this.isWorking = nbt.getBoolean("isWorking");
		this.progress1 = nbt.getInt("Progress1");
		this.progress2 = nbt.getInt("Progress2");
		this.tankProgress = nbt.getInt("TankProgress");
		this.compressedAir = FluidStack.EMPTY;
		if (nbt.contains("CompressedAir")) this.compressedAir = FluidStack.loadFluidStackFromNBT(nbt.getCompound("CompressedAir"));
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

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMAirCompressor(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.air_compressor");
	}
	
}
