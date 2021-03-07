package de.redtec.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.redtec.RedTec;
import de.redtec.blocks.BlockSignalAntennaConector;
import de.redtec.registys.ModTileEntityType;
import de.redtec.util.RedstoneControlSignal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class TileEntitySignalAntenna extends TileEntity {
	
	private ItemStack chanelItem;
	private List<RedstoneControlSignal> sendedSignals;
	
	public TileEntitySignalAntenna() {
		super(ModTileEntityType.SIGNAL_ANTENNA);
		this.sendedSignals = new ArrayList<RedstoneControlSignal>();
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (this.chanelItem != null) compound.put("ChanelItem", this.chanelItem.write(new CompoundNBT()));
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		this.chanelItem = ItemStack.read(compound.getCompound("ChanelItem"));
		if (this.chanelItem.isEmpty()) this.chanelItem = null;
		super.func_230337_a_(state, compound);
	}
	
	public void setChanelItem(ItemStack chanelItem) {
		this.chanelItem = chanelItem;
	}
	
	public ItemStack getChanelItem() {
		return chanelItem;
	}
	
	public int getRange() {
		
		List<BlockPos> blocks = new ArrayList<BlockPos>();
		scannAt(this.pos.offset(getBlockState().get(BlockSignalAntennaConector.FACING)), blocks, 0);
		int antennaSize = blocks.size();
		
		return (int) (antennaSize / 100F * 26 * 16);
		
	}
	
	public boolean isValidAntennaBlock(BlockState state) {
		
		return	state.getBlock() == RedTec.stacked_redstone_wire ||
				state.getBlock() == RedTec.stacked_redstone_torch ||
				state.getBlock() == Blocks.IRON_BARS;
		
	}
	
	public void reciveSignal(RedstoneControlSignal signal) {
		
		BlockState state = world.getBlockState(pos);
		if (!sendedSignals.contains(signal) && state.getBlock() == RedTec.antenna_conector) {
			sendedSignals.add(signal);
			((BlockSignalAntennaConector) state.getBlock()).sendSignal(world, pos, signal);
		}
		
	}
	
	private void scannAt(BlockPos scannPos, List<BlockPos> blocks, int scannCount) {
		
		if (isValidAntennaBlock(this.world.getBlockState(scannPos)) && !blocks.contains(scannPos) && scannCount < 24) {
			
			blocks.add(scannPos);
			
			for (Direction direction : Direction.values()) {
				
				scannAt(scannPos.offset(direction), blocks, scannCount++);
				
			}
			
		}
		
	}
	
}
