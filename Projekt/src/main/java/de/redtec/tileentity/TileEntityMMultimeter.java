package de.redtec.tileentity;

import de.redtec.blocks.BlockMMultimeter;
import de.redtec.registys.ModTileEntityType;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityMMultimeter extends TileEntityGauge implements ITickableTileEntity {
	
	public float value;
	public DecimalUnit decimalUnit;
	
	public TileEntityMMultimeter() {
		super(ModTileEntityType.MULTIMETER);
		this.decimalUnit = DecimalUnit.NONE;
		this.value = 0;
	}

	@Override
	public void tick() {
		
		if (!world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
		}
		
	}
	
	public void updateValue(float value, DecimalUnit unit) {
		this.value = value;
		this.decimalUnit = unit;
	}
	
	@Override
	public String getUnit() {
		return this.decimalUnit.getUnit() + this.getBlockState().get(BlockMMultimeter.UNIT).getUnit();
	}

	@Override
	public float getValue() {
		return this.value;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		this.value = compound.getFloat("Value");
		this.decimalUnit = DecimalUnit.byUnit(compound.getString("Unit"));
		super.func_230337_a_(state, compound);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putFloat("Value", this.value);
		if (this.decimalUnit != DecimalUnit.NONE) compound.putString("Unit", this.decimalUnit.getUnit());
		return super.write(compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
	public static enum MessurementType implements IStringSerializable {
		
		VOLT("voltage", "V", (world, pos, network) -> network.getVoltage().getVoltage()),
		AMPERE("ampere", "A", (world, pos, network) -> network.getCurrent()),
		WATT("watt", "W", (world, pos, network) -> AMPERE.getValue(world, pos, network) * network.getVoltage().getVoltage());
		
		private MessurementType(String name, String unit, MessureFunction function) {
			this.funcition = function;
			this.unit = unit;
			this.name = name;
		}
		
		protected MessureFunction funcition;
		protected String unit;
		protected String name;
		
		public String getUnit() {
			return unit;
		}
		
		public float getValue(World world, BlockPos pos, ElectricityNetwork network) {
			return this.funcition.messure(world, pos, network);
		}
		
		@FunctionalInterface
		public static interface MessureFunction {
			public float messure(World world, BlockPos pos, ElectricityNetwork network);
		}

		@Override
		public String func_176610_l() {
			return this.name;
		}
		
	}
	
	public static enum DecimalUnit {
		
		MILLI("m", 0.001F),NONE("", 1),KILO("k", 1000),MEGA("M", 1000000);
		
		private DecimalUnit(String unit, float value) {
			this.unit = unit;
			this.value = value;
		}
		
		protected String unit;
		protected float value;
		
		public String getUnit() {
			return unit;
		}
		
		public float getValue() {
			return value;
		}
		
		public static DecimalUnit byUnit(String unit) {
			if (unit.equals("m")) return MILLI;
			if (unit.equals("k")) return KILO;
			if (unit.equals("M")) return MEGA;
			return NONE;
		}

		public static DecimalUnit getUnitForValue(float value) {
			
			if (value < 1 && value != 0) return MILLI;
			if (value >= 1000) return KILO;
			if (value >= 1000000) return MEGA;
			return NONE;
			
		}
		
	}
	
}
