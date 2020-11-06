package de.redtec.tileentity;

import de.redtec.blocks.BlockMSteamGenerator;
import de.redtec.util.IElectricConnective.Voltage;
import de.redtec.util.IFluidConnective;
import de.redtec.util.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMSteamGenerator extends TileEntity implements IFluidConnective, ITickableTileEntity {
	
	private final int maxFluid;
	private TEPart part;
	private FluidStack steamIn;
	private FluidStack steamOut;
	private int generatedAmperes;
	private Voltage generatedVoltage;
	private final int maxEnergy;
	
	public TileEntityMSteamGenerator() {
		super(ModTileEntityType.STEAM_GENERATOR);
		this.part = TEPart.CENTER;
		this.maxFluid = 2000;
		this.maxEnergy = 32000; // TODO HV -> 1000V * 32A = 32000W
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
			
		}
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		this.part = TEPart.fromName(compound.getString("Part"));
		if (this.part == TEPart.CENTER) {
			this.steamIn = FluidStack.loadFluidStackFromNBT(compound.getCompound("SteamIn"));
			this.steamOut = FluidStack.loadFluidStackFromNBT(compound.getCompound("SteamOut"));
			
		}
		super.func_230337_a_(state, compound);
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
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			if (this.part == TEPart.CENTER) {
				
				int capacity = this.maxFluid - this.steamOut.getAmount();
				int transfer = Math.min(steamIn.getAmount(), capacity);
				
				if (!this.steamIn.isEmpty() && transfer > 0 && this.generatedAmperes * this.generatedVoltage.getVoltage() <= this.maxEnergy) {
					
					this.steamIn.shrink(transfer);
					if (this.steamOut.isEmpty()) {
						this.steamOut = new FluidStack(Fluids.WATER, transfer);
					} else {
						this.steamOut.grow(transfer);
					}
					
					int generatedEnergy = transfer; // TODO
					int generatedVoltageI = generatedEnergy * 2;
					this.generatedVoltage = Voltage.byVoltageInt(generatedVoltageI);
					this.generatedAmperes = generatedEnergy / this.generatedVoltage.getVoltage();
					
				} else {
					this.generatedAmperes = 0;
					this.generatedVoltage = Voltage.NoLimit;
				}
				
				if (!this.steamOut.isEmpty()) {
					
					// TODO Exhaust Uncompressed Stem
					
				}
				
			}
			
		}
		
	}
	
	public BlockPos getCenterTE() {
		if (BlockMSteamGenerator.getInternPartPos(this.getBlockState()).getZ() == 1) {
			return this.part == TEPart.ELECTRICITY ? this.pos.up() : this.pos.down();
		} else {
			Direction facing = this.getBlockState().get(BlockMSteamGenerator.FACING).getOpposite();
			return this.part == TEPart.ELECTRICITY ? this.pos.up().offset(facing) : this.pos.down().offset(facing);
		}
	}
	
	public Voltage getVoltage() {
		return this.generatedVoltage;
	}
	public int getGenerateCurrent() {
		return this.generatedAmperes;
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
		return Fluids.LAVA; // TODO
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
	
}
