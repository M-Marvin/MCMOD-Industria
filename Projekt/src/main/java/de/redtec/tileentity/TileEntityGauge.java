package de.redtec.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class TileEntityGauge extends TileEntity implements ITickableTileEntity {
	
	public String name;
	public float value;
	
	public TileEntityGauge(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	public String getName() {
		return name;
	}
	
	public float getValue() {
		return value;
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT compound = new CompoundNBT();
		compound.putString("Name", this.name);
		compound.putFloat("Value", this.value);
		return new SUpdateTileEntityPacket(pos, 0, compound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT compound = pkt.getNbtCompound();
		this.name = compound.getString("Name");
		this.value = compound.getFloat("Value");
	}
	
}
