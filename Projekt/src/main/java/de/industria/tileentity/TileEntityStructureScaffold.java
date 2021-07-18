package de.industria.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IPostMoveHandledTE;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class TileEntityStructureScaffold extends TileEntityInventoryBase implements ITickableTileEntity, IPostMoveHandledTE {
	
	public TileEntityStructureScaffold() {
		super(ModTileEntityType.STRUCTURE_SCAFFOLD, 6);
	}
	
	public boolean setCladding(Direction d, ItemStack cladding) {
		this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		int slotId = d.get3DDataValue();
		if (this.getItem(slotId).isEmpty()) {
			ItemStack stack = cladding.copy();
			stack.setCount(1);
			this.setItem(slotId, stack);
			return true;
		}
		return false;
	}
	
	public ItemStack removeCladding(Direction d) {
		this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		int slotId = d.get3DDataValue();
		if (!this.getItem(slotId).isEmpty()) {
			ItemStack cladding = this.getItem(slotId);
			this.setItem(slotId, ItemStack.EMPTY);
			return cladding;
		}
		return ItemStack.EMPTY;
	}
	
	public ItemStack getCladding(Direction d) {
		return this.getItem(d.get3DDataValue());
	}
	
	public Direction[] getCladdingSides() {
		List<Direction> sides = new ArrayList<Direction>();
		for (Direction d : Direction.values()) {
			if (!this.getCladding(d).isEmpty()) sides.add(d);
		}
		return sides.toArray(new Direction[] {});
	}
	
	@Override
	public void tick() {
		if (!this.level.isClientSide()) {
			if (this.level.getGameTime() % 30 == 0) this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		super.deserializeNBT(nbt);
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
	public void handlePostMove(BlockPos pos, BlockPos newPos, boolean multipleCall) {
		this.level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 2);
	}
	
}
