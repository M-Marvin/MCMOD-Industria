package de.industria.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.ITEPostMoveHandled;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityRLockedCompositeBlock extends TileEntity implements ITEPostMoveHandled {
	
	private long lastMoved;
	private List<BlockPos> storedPositions;
	
	public TileEntityRLockedCompositeBlock() {
		super(ModTileEntityType.LOCKED_COMPOSITE_BLOCK);
		this.storedPositions = new ArrayList<BlockPos>();
	}

	public void movePositions(BlockPos offset) {
		
		List<BlockPos> positionsWithOffset = new ArrayList<BlockPos>();
		for (BlockPos position : this.storedPositions) {
			positionsWithOffset.add(position.offset(offset));
		}
		this.storedPositions = positionsWithOffset;
		
	}
	
	public List<BlockPos> getConnectedBlocks() {
		if (this.hasLevel() ? !this.level.isClientSide() : false) this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 0);
		return this.storedPositions;
	}
	
	public void setPositions(List<BlockPos> positions) {
		this.storedPositions = positions;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		for (BlockPos pos : this.storedPositions) {
			list.add(NBTUtil.writeBlockPos(pos));
		}
		compound.put("StoredPositions", list);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		ListNBT list = compound.getList("StoredPositions", 10);
		this.storedPositions.clear();
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT posTag = list.getCompound(i);
			this.storedPositions.add(NBTUtil.readBlockPos(posTag));
		}
		super.load(state, compound);
	}

	@Override
	public void handlePostMove(BlockPos pos, BlockPos newPos, boolean multipleCall) {
		
		boolean hasMovedThisTick = lastMoved == this.level.getDayTime();
		this.lastMoved = this.level.getDayTime();
		
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
		return new SUpdateTileEntityPacket(worldPosition, 0, this.save(new CompoundNBT()));
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.load(null, pkt.getTag());
	}
	
}
