package de.industria.tileentity;

import java.util.ArrayList;

import de.industria.blocks.BlockMSteamGenerator;
import de.industria.fluids.FluidSteam;
import de.industria.fluids.util.BlockGasFluid;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IFluidConnective;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.FluidStackStateTagHelper;
import de.industria.util.handler.MachineSoundHelper;
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

public class TileEntityMSteamGenerator extends TileEntity implements IFluidConnective, ITickableTileEntity {
	
	protected TEPart part;
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
		this.part = TEPart.CENTER;
		this.maxFluid = 2000;
		this.maxEnergy = 5000; // Max Watt/tick
		this.wattPerMB = 100;
		this.generatedVoltage = Voltage.NormalVoltage;
		this.steamIn = FluidStack.EMPTY;
		this.steamOut = FluidStack.EMPTY;
	}
	
	public TileEntityMSteamGenerator(TEPart part) {
		this();
		this.part = part;
	}
	
	public TEPart getPart() {
		return part;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putString("Part", this.part.getName());
		if (this.part == TEPart.CENTER) {
			if (!this.steamIn.isEmpty()) compound.put("SteamIn", this.steamIn.writeToNBT(new CompoundNBT()));
			if (!this.steamOut.isEmpty()) compound.put("SteamOut", this.steamOut.writeToNBT(new CompoundNBT()));
			compound.putFloat("GeneratedAmperes", this.generatedAmperes);
			compound.putFloat("Accerlation", this.accerlation);
			compound.putFloat("TurbinRotation", this.turbinRotation);
		}
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.part = TEPart.fromName(compound.getString("Part"));
		if (this.part == TEPart.CENTER) {
			this.steamIn = FluidStack.loadFluidStackFromNBT(compound.getCompound("SteamIn"));
			this.steamOut = FluidStack.loadFluidStackFromNBT(compound.getCompound("SteamOut"));
			this.generatedAmperes = compound.getFloat("GeneratedAmperes");
			this.accerlation = compound.getFloat("Accerlation");
			this.turbinRotation = compound.getFloat("TurbinRotation");
		}
		super.read(state, compound);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.pos.subtract(new BlockPos(3, 3, 3)), this.pos.add(new BlockPos(3, 3, 3)));
	}
	
	public static enum TEPart {
		
		STEAM_IN("steam_in", new BlockPos(1, 2, 0), new BlockPos(1, 2, 1)),
		CENTER("center", new BlockPos(1, 1, 1)),
		ELECTRICITY("electricity", new BlockPos(1, 0, 0), new BlockPos(1, 0, 1));
		
		private String name;
		private BlockPos[] innerPos;
		
		TEPart(String name, BlockPos... innerPos) {
			this.name = name;
			this.innerPos = innerPos;
		}
		
		public String getName() {
			return name;
		}
		
		public BlockPos[] getInnerPos() {
			return innerPos;
		}
		
		public static TEPart fromName(String name) {
			switch (name) {
			case "steam_in": return STEAM_IN;
			case "center": return CENTER;
			case "electricity": return ELECTRICITY;
			default: return CENTER;
			}
		}
		
		public boolean isValidPosition(BlockPos innerPos) {
			for (BlockPos pos : this.innerPos) {
				if (pos.equals(innerPos)) return true;
			}
			return false;
		}
		
		public static boolean hasTileEntity(BlockPos innerPos) {
			return	STEAM_IN.isValidPosition(innerPos) ||
					CENTER.isValidPosition(innerPos) ||
					ELECTRICITY.isValidPosition(innerPos);
		}
		
		public static TEPart fromPosition(BlockPos innerPos) {
			if (STEAM_IN.isValidPosition(innerPos)) return STEAM_IN;
			if (CENTER.isValidPosition(innerPos)) return CENTER;
			if (ELECTRICITY.isValidPosition(innerPos)) return ELECTRICITY;
			return CENTER;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			if (this.part == TEPart.CENTER) {
				
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
					
					Direction direction = this.getBlockState().get(BlockMSteamGenerator.FACING);
					
					if (this.steamOut.getAmount() >= 1000) {
						
						BlockPos pos1 = this.pos.offset(direction, 2);
						BlockPos pos2 = this.pos.offset(direction.getOpposite(), 1);
						
						BlockPos exhaustPos = this.world.rand.nextBoolean() ? pos1 : pos2;
						BlockState replaceState = this.world.getBlockState(exhaustPos);
						
						if (replaceState.isAir()) {
							
							this.world.setBlockState(exhaustPos, this.steamOut.getFluid().getDefaultState().getBlockState());
							this.steamOut.shrink(1000);
							
						} else if (replaceState.getBlock() instanceof BlockGasFluid) {
							
							((BlockGasFluid) replaceState.getBlock()).pushFluid(new ArrayList<BlockPos>(), replaceState, (ServerWorld) this.world, exhaustPos, world.rand);
							replaceState = this.world.getBlockState(exhaustPos);
							
							if (replaceState.isAir()) {
								
								this.world.setBlockState(exhaustPos, this.steamOut.getFluid().getDefaultState().getBlockState());
								this.steamOut.shrink(1000);
								
							}
							
						}
						
					}
					
				}
				
				this.turbinRotation += this.accerlation;
				if (this.turbinRotation >= 360) this.turbinRotation -= 360;
				
			} else if (this.part == TEPart.ELECTRICITY) {
				
				ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(this.world);
				handler.updateNetwork(world, pos);
				
			}
			
		} else if (this.part == TEPart.CENTER) {
			
			if (this.accerlation > 0) {
				
				MachineSoundHelper.startSoundTurbinIfNotRunning(this);
								
			}
			
		}
		
	}
	
	public BlockPos getCenterTE() {
		if (this.part == TEPart.CENTER) return this.pos;
		if (BlockMSteamGenerator.getInternPartPos(this.getBlockState()).getZ() == 1) {
			return this.part == TEPart.ELECTRICITY ? this.pos.up() : this.pos.down();
		} else {
			Direction facing = this.getBlockState().get(BlockMSteamGenerator.FACING).getOpposite();
			return this.part == TEPart.ELECTRICITY ? this.pos.up().offset(facing) : this.pos.down().offset(facing);
		}
	}
	
	public Voltage getVoltage() {
		
		BlockPos centerPos = getCenterTE();
		TileEntity tileEntity = this.world.getTileEntity(centerPos);
		
		if (tileEntity instanceof TileEntityMSteamGenerator) {
			TileEntityMSteamGenerator center = (TileEntityMSteamGenerator) tileEntity;
			return center.generatedAmperes > 0 ? center.generatedVoltage : Voltage.NoLimit;
		}
		
		return Voltage.NoLimit;
		
	}
	public float getGenerateCurrent() {
		
		BlockPos centerPos = getCenterTE();
		TileEntity tileEntity = this.world.getTileEntity(centerPos);
		
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
		
		BlockPos centerPos = getCenterTE();
		TileEntity tileEntity = this.world.getTileEntity(centerPos);
		
		if (tileEntity instanceof TileEntityMSteamGenerator) {
			
			TileEntityMSteamGenerator center = (TileEntityMSteamGenerator) tileEntity;
			
			if (fluid.getFluid() == this.getFluidType()) {
				if (!FluidStackStateTagHelper.makeStateFromStack(fluid).get(FluidSteam.PRESSURIZED)) {

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
		return this.part == TEPart.STEAM_IN;
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
	public FluidStack getStorage() {
		return this.steamIn;
	}

}
