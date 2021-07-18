package de.industria.tileentity;

import de.industria.items.ItemFuse;
import de.industria.typeregistys.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMFuseBox extends TileEntity implements ITickableTileEntity {
	
	public ItemStack fuse;
	
	public TileEntityMFuseBox() {
		super(ModTileEntityType.FUSE_BOX);
		this.fuse = ItemStack.EMPTY;
	}

	public boolean canSwitch() {
		return true;
	}
	
	public boolean insertFuse(ItemStack fuseStack) {
		
		if (!fuseStack.isEmpty() ? fuseStack.getCount() > 0 && fuseStack.getItem() instanceof ItemFuse : false) {
			
			this.fuse = fuseStack.copy();
			this.fuse.setCount(1);
			return true;
			
		}
		
		return false;
		
	}
	
	public ItemStack removeFuse() {
		
		if (!this.fuse.isEmpty()) {
			
			ItemStack fuseStack = this.fuse.copy();
			this.fuse = ItemStack.EMPTY;
			return fuseStack;
			
		}
		
		return ItemStack.EMPTY;
		
	}
	
	public ItemStack getFuse() {
		return fuse;
	}
	
	public int getMaxCurrent() {
		
		if (!fuse.isEmpty() ? fuse.getCount() > 0 && fuse.getItem() instanceof ItemFuse : false) {
			
			return ((ItemFuse) this.fuse.getItem()).getMaxCurrent();
			
		}
		
		return 0;
		
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (!this.fuse.isEmpty()) compound.put("Fuse", this.fuse.save(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.fuse = ItemStack.of(compound.getCompound("Fuse"));
		super.load(state, compound);
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
	public void tick() {
		if (!level.isClientSide()) {
			
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			
		}
	}

}
