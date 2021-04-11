package de.industria.gui;

import de.industria.tileentity.TileEntityJigsaw;
import de.industria.typeregistys.ModContainerType;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;

public class ContainerJigsaw extends Container {
	
	protected PlayerInventory playerInv;
	protected TileEntityJigsaw tileEntity;
	
	public ContainerJigsaw(int id, PlayerInventory playerInv, PacketBuffer data) {
		this(id, playerInv, (TileEntityJigsaw) getClientTileEntity(data));
	}
	
	public ContainerJigsaw(int id, PlayerInventory playerInv, TileEntityJigsaw tileEntity) {
		super(ModContainerType.JIGSAW, id);
		this.tileEntity = tileEntity;
		this.playerInv = playerInv;
	}
	
	@SuppressWarnings("resource")
	private static TileEntity getClientTileEntity(PacketBuffer data) {
		
		BlockPos pos = data.readBlockPos();
		TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
		CompoundNBT nbt = data.readCompoundTag();
		te.deserializeNBT(nbt);
		return te;
		
	}
	
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(IWorldPosCallable.DUMMY, playerIn, Blocks.CRAFTING_TABLE) && !this.tileEntity.isRemoved();
	}
	
	public TileEntityJigsaw getTileEntity() {
		return tileEntity;
	}
	
}