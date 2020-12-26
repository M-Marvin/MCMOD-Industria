package de.redtec.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class TileEntityGauge extends TileEntity {
	
	// Client only
	public float currentGaugeValue;

	public TileEntityGauge(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	public abstract String getUnit();
	
	public abstract float getValue();
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = super.serializeNBT();
		if (this.world.isRemote()) nbt.putFloat("currentGaugeValue", this.currentGaugeValue);
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains("currentGaugeValue")) this.currentGaugeValue = nbt.getFloat("currentGaugeValue");
		super.deserializeNBT(nbt);
	}
	
}
