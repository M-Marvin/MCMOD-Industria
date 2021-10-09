package de.industria.tileentity;

import de.industria.blocks.BlockMultipart;
import de.industria.gui.ContainerMAirCompressor;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.ITEFluidConnective;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
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

public class TileEntityMAirCompressor extends TileEntity implements ITickableTileEntity, ITEFluidConnective, INamedContainerProvider, ITESimpleMachineSound {
	
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
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityMAirCompressor) tileEntity).hasPower = (boolean) data[0];
			if (data[1] != null) ((TileEntityMAirCompressor) tileEntity).isWorking = (boolean) data[1];
			if (data[2] != null) ((TileEntityMAirCompressor) tileEntity).tankProgress = (int) data[2];
			if (data[3] != null) ((TileEntityMAirCompressor) tileEntity).progress1 = (int) data[3];
			if (data[4] != null) ((TileEntityMAirCompressor) tileEntity).progress2 = (int) data[4];
			if (data[5] != null) ((TileEntityMAirCompressor) tileEntity).compressedAir = (FluidStack) data[5];
		}, () -> this.hasPower, () -> this.isWorking, () -> this.tankProgress, () -> this.progress1, () -> this.progress2, () -> this.compressedAir);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition.offset(-2, -2, -2), worldPosition.offset(2, 1, 2));
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide) {
			
			ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
			
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
			
			FluidStack rest = pushFluid(this.compressedAir, level, worldPosition);
			if (rest != compressedAir) compressedAir = rest;
			
		} else {
			
			MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.COMPRESSOR_LOOP);
			
			if (this.isWorking) {
				
				if (this.level.random.nextInt(10) == 0) {

					IParticleData paricle = ParticleTypes.POOF;
					Direction facing = getBlockState().getValue(BlockMultipart.FACING);
					
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
					
					float x = this.worldPosition.getX() + ox + (level.random.nextFloat() - 0.5F) * width;
					float y = this.worldPosition.getY() + oy + (level.random.nextFloat() - 0.5F) * height;
					float z = this.worldPosition.getZ() + oz + (level.random.nextFloat() - 0.5F) * width;
					this.level.addParticle(paricle, x, y, z, 0, 0, 0);
					
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
		return BlockMultipart.getInternPartPos(getBlockState()).equals(new BlockPos(0, 0, 0));
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putInt("Progress1", this.progress1);
		compound.putInt("Progress2", this.progress2);
		compound.putInt("TankProgress", this.tankProgress);
		if (!this.compressedAir.isEmpty()) compound.put("CompressedAir", this.compressedAir.writeToNBT(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		this.hasPower = nbt.getBoolean("hasPower");
		this.isWorking = nbt.getBoolean("isWorking");
		this.progress1 = nbt.getInt("Progress1");
		this.progress2 = nbt.getInt("Progress2");
		this.tankProgress = nbt.getInt("TankProgress");
		this.compressedAir = FluidStack.EMPTY;
		if (nbt.contains("CompressedAir")) this.compressedAir = FluidStack.loadFluidStackFromNBT(nbt.getCompound("CompressedAir"));
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

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMAirCompressor(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.air_compressor");
	}

	@Override
	public void setStorage(FluidStack storage) {
		this.compressedAir = storage;
	}
	
}
