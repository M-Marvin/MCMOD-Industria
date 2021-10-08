package de.industria.tileentity;

import java.util.ArrayList;

import de.industria.blocks.BlockMSteamGenerator;
import de.industria.blocks.BlockMultipart;
import de.industria.fluids.FluidSteam;
import de.industria.fluids.util.BlockGasFluid;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.ITEFluidConnective;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.FluidStackStateTagHelper;
import de.industria.util.handler.MachineSoundHelper;
import de.industria.util.types.MultipartBuild.MultipartBuildLocation;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMSteamGenerator extends TileEntity implements ITEFluidConnective, ITickableTileEntity {
	
	protected FluidStack steamIn;
	protected FluidStack steamOut;
	protected float generatedAmperes;
	public final Voltage generatedVoltage;
	public final int maxEnergy;
	public final int maxFluid;
	public final int wattPerMB;
	
	public float turbinRotation;
	public float accerlation;
	
	public TileEntityMSteamGenerator() {
		super(ModTileEntityType.STEAM_GENERATOR);
		this.maxFluid = 2000;
		this.maxEnergy = 5000; // Max Watt/tick
		this.wattPerMB = 200;
		this.generatedVoltage = Voltage.NormalVoltage;
		this.steamIn = FluidStack.EMPTY;
		this.steamOut = FluidStack.EMPTY;
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityMSteamGenerator) tileEntity).steamIn = (FluidStack) data[0];
			if (data[1] != null) ((TileEntityMSteamGenerator) tileEntity).steamOut = (FluidStack) data[1];
			if (data[2] != null) ((TileEntityMSteamGenerator) tileEntity).generatedAmperes = (float) data[2];
		}, () -> steamIn, () -> steamOut, () -> generatedAmperes);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (!this.steamIn.isEmpty()) compound.put("SteamIn", this.steamIn.writeToNBT(new CompoundNBT()));
		if (!this.steamOut.isEmpty()) compound.put("SteamOut", this.steamOut.writeToNBT(new CompoundNBT()));
		compound.putFloat("GeneratedAmperes", this.generatedAmperes);
		compound.putFloat("Accerlation", this.accerlation);
		compound.putFloat("TurbinRotation", this.turbinRotation);
		compound.put("BuildData", this.buildData.writeNBT(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.steamIn = FluidStack.loadFluidStackFromNBT(compound.getCompound("SteamIn"));
		this.steamOut = FluidStack.loadFluidStackFromNBT(compound.getCompound("SteamOut"));
		this.generatedAmperes = compound.getFloat("GeneratedAmperes");
		this.accerlation = compound.getFloat("Accerlation");
		this.turbinRotation = compound.getFloat("TurbinRotation");
		this.buildData = MultipartBuildLocation.loadNBT(compound.getCompound("BuildData"));
		super.load(state, compound);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.worldPosition.subtract(new BlockPos(3, 3, 3)), this.worldPosition.offset(new BlockPos(3, 3, 3)));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			if (BlockMultipart.getInternPartPos(this.getBlockState()).equals(new BlockPos(0, 0, 0))) {
				
				int capacity = Math.min(this.maxFluid - this.steamOut.getAmount(), this.maxEnergy / wattPerMB);
				int transfer = Math.max(0, Math.min(steamIn.getAmount(), capacity));
				
				if (transfer > 0) {
					
					this.steamIn.shrink(transfer);
					if (this.steamOut.isEmpty()) {
						this.steamOut = new FluidStack(ModFluids.STEAM, transfer);
					} else {
						this.steamOut.grow(transfer);
					}
					
					float speed = transfer / (float) (this.maxEnergy / this.wattPerMB);
					this.accerlation = Math.min(20F, this.accerlation + speed * 0.06F);
					
				} else {
					
					this.accerlation = Math.max(0F, this.accerlation - 0.04F);
					
				}
				
				float capacityEnergy = this.maxEnergy * this.accerlation / 20;
				this.generatedAmperes = capacityEnergy / this.generatedVoltage.getVoltage();
				
				if (!this.steamOut.isEmpty()) {
					
					Direction direction = this.getBlockState().getValue(BlockMSteamGenerator.FACING);
					
					if (this.steamOut.getAmount() >= 1000) {
						
						BlockPos pos1 = this.worldPosition.relative(direction.getClockWise()).relative(direction, 1).above();
						BlockPos pos2 = this.worldPosition.relative(direction.getClockWise()).relative(direction.getOpposite(), 2).above();
						
						BlockPos exhaustPos = this.level.random.nextBoolean() ? pos1 : pos2;
						BlockState replaceState = this.level.getBlockState(exhaustPos);
						
						if (replaceState.isAir()) {
							
							this.level.setBlockAndUpdate(exhaustPos, this.steamOut.getFluid().defaultFluidState().createLegacyBlock());
							this.steamOut.shrink(1000);
							
						} else if (replaceState.getBlock() instanceof BlockGasFluid) {
							
							((BlockGasFluid) replaceState.getBlock()).pushFluid(new ArrayList<BlockPos>(), replaceState, (ServerWorld) this.level, exhaustPos, level.random);
							replaceState = this.level.getBlockState(exhaustPos);
							
							if (replaceState.isAir()) {
								
								this.level.setBlockAndUpdate(exhaustPos, this.steamOut.getFluid().defaultFluidState().createLegacyBlock());
								this.steamOut.shrink(1000);
								
							}
							
						}
						
					}
					
				}
				
				this.turbinRotation += this.accerlation;
				if (this.turbinRotation >= 360) this.turbinRotation -= 360;
				
			} else if (BlockMultipart.getInternPartPos(this.getBlockState()).equals(new BlockPos(1, 0, 0)) || BlockMultipart.getInternPartPos(this.getBlockState()).equals(new BlockPos(1, 0, 1))) {
				
				ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(this.level);
				handler.updateNetwork(level, worldPosition);
				
			}
			
		} else if (BlockMultipart.getInternPartPos(this.getBlockState()).equals(new BlockPos(0, 0, 0))) {
			
			if (this.accerlation > 0) {
				
				MachineSoundHelper.startSoundTurbinIfNotRunning(this);
								
			}
			
		}
		
	}
	
	public Voltage getVoltage() {
		
		TileEntity tileEntity = BlockMultipart.getSCenterTE(this.worldPosition, getBlockState(), level);
		
		if (tileEntity instanceof TileEntityMSteamGenerator) {
			TileEntityMSteamGenerator center = (TileEntityMSteamGenerator) tileEntity;
			return center.generatedAmperes > 0 ? center.generatedVoltage : Voltage.NoLimit;
		}
		
		return Voltage.NoLimit;
		
	}
	public float getGenerateCurrent() {
		
		TileEntity tileEntity = BlockMultipart.getSCenterTE(this.worldPosition, getBlockState(), level);
		
		if (tileEntity instanceof TileEntityMSteamGenerator) {
			TileEntityMSteamGenerator center = (TileEntityMSteamGenerator) tileEntity;
			return center.generatedAmperes;
		}
		
		return 0;
		
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack insertFluid(FluidStack fluid) {

		TileEntity tileEntity = BlockMultipart.getSCenterTE(this.worldPosition, getBlockState(), level);
		
		if (tileEntity instanceof TileEntityMSteamGenerator) {
			
			TileEntityMSteamGenerator center = (TileEntityMSteamGenerator) tileEntity;
			
			if (fluid.getFluid() == this.getFluidType()) {
				if (!FluidStackStateTagHelper.makeStateFromStack(fluid).getValue(FluidSteam.PRESSURIZED)) {

					if (center.steamOut.isEmpty()) {
						int transfer = Math.min(this.maxFluid, fluid.getAmount());
						center.steamOut = new FluidStack(fluid.getFluid(), transfer);
						FluidStack rest = fluid.copy();
						rest.shrink(transfer);
						return rest;
					} else if (center.steamOut.getFluid() == fluid.getFluid()) {
						int capacity = this.maxFluid - center.steamOut.getAmount();
						int transfer = Math.min(capacity, fluid.getAmount());
						center.steamOut.grow(transfer);
						FluidStack rest = fluid.copy();
						rest.shrink(transfer);
						return rest;
					}
					
				}
			} else {
				return fluid;
			}
			
			if (center.steamIn.isEmpty()) {
				int transfer = Math.min(this.maxFluid, fluid.getAmount());
				center.steamIn = new FluidStack(fluid.getFluid(), transfer);
				FluidStack rest = fluid.copy();
				rest.shrink(transfer);
				return rest;
			} else if (center.steamIn.getFluid() == fluid.getFluid()) {
				int capacity = this.maxFluid - center.steamIn.getAmount();
				int transfer = Math.min(capacity, fluid.getAmount());
				center.steamIn.grow(transfer);
				FluidStack rest = fluid.copy();
				rest.shrink(transfer);
				return rest;
			}
			
		}
		
		return fluid;
	}
	
	@Override
	public Fluid getFluidType() {
		return ModFluids.STEAM;
	}
	
	@Override
	public boolean canConnect(Direction side) {
		return BlockMultipart.getInternPartPos(this.getBlockState()).equals(new BlockPos(1, 2, 1)) || BlockMultipart.getInternPartPos(this.getBlockState()).equals(new BlockPos(1, 2, 0));
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
	public FluidStack getStorage() {
		return this.steamIn;
	}

	@Override
	public void setStorage(FluidStack storage) {
		this.steamIn = storage;
	}

	public MultipartBuildLocation buildData = MultipartBuildLocation.EMPTY;
	public void storeBuildData(MultipartBuildLocation buildData) {
		this.buildData = buildData;
	}

	public MultipartBuildLocation getBuildData() {
		return this.buildData;
	}
	
}
