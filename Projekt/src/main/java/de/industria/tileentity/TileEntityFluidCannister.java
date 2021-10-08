package de.industria.tileentity;

import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.DataWatcher;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidCannister extends TileEntity {
	
	public static final int MAX_CONTENT = 32000;
	protected FluidStack content;
	
	public TileEntityFluidCannister() {
		super(ModTileEntityType.FLUID_CANISTER);
		this.content = FluidStack.EMPTY;
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityFluidCannister) tileEntity).content = (FluidStack) data[0];
		}, () -> this.content);
	}
	
	public FluidStack getContent() {
		return content;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.put("Fluid", this.content.writeToNBT(new CompoundNBT()));
		return super.save(nbt);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		this.content = nbt.contains("Fluid") ? FluidStack.loadFluidStackFromNBT(nbt.getCompound("Fluid")) : FluidStack.EMPTY;
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
	
}
