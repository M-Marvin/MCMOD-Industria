package de.industria.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IPostMoveHandledTE;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityRLockedCompositeBlock extends TileEntity implements IPostMoveHandledTE {
	
	private long lastMoved;
	private List<BlockPos> storedPositions;
	
	public TileEntityRLockedCompositeBlock() {
		super(ModTileEntityType.LOCKED_COMPOSITE_BLOCK);
		this.storedPositions = new ArrayList<BlockPos>();
	}

	public void movePositions(BlockPos offset) {
		
		List<BlockPos> positionsWithOffset = new ArrayList<BlockPos>();
		for (BlockPos position : this.storedPositions) {
			positionsWithOffset.add(position.add(offset));
		}
		this.storedPositions = positionsWithOffset;
		
	}
	
	public List<BlockPos> getConnectedBlocks() {
		if (this.hasWorld() ? !this.world.isRemote() : false) this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 0);
		return this.storedPositions;
	}
	
	public void setPositions(List<BlockPos> positions) {
		this.storedPositions = positions;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		for (BlockPos pos : this.storedPositions) {
			list.add(NBTUtil.writeBlockPos(pos));
		}
		compound.put("StoredPositions", list);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		ListNBT list = compound.getList("StoredPositions", 10);
		this.storedPositions.clear();
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT posTag = list.getCompound(i);
			this.storedPositions.add(NBTUtil.readBlockPos(posTag));
		}
		super.read(state, compound);
	}

	@Override
	public void handlePostMove(BlockPos pos, BlockPos newPos, boolean multipleCall) {
		
		boolean hasMovedThisTick = lastMoved == this.world.getDayTime();
		this.lastMoved = this.world.getDayTime();
		
		if (!hasMovedThisTick || !multipleCall) {
			
			int offsetX = newPos.getX() - pos.getX();
			int offsetY = newPos.getY() - pos.getY();
			int offsetZ = newPos.getZ() - pos.getZ();
			BlockPos offset = new BlockPos(offsetX, offsetY, offsetZ);
			
			this.movePositions(offset);
			
		}
		
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, this.write(new CompoundNBT()));
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(null, pkt.getNbtCompound());
	}
	
}
