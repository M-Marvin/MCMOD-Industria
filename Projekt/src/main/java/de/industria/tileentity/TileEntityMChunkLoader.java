package de.industria.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.industria.blocks.BlockMChunkLoader;
import de.industria.gui.ContainerMChunkLoader;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.ITEChunkForceLoading;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMChunkLoader extends TileEntity implements ITEChunkForceLoading, ITickableTileEntity, INamedContainerProvider {
	
	public static final int CHUNK_RANGE = 8;
	
	public boolean hasPower;
	public boolean isWorking;
	public List<ChunkPos> activeRelativeChunks;
	
	@SuppressWarnings("unchecked")
	public TileEntityMChunkLoader() {
		super(ModTileEntityType.CHUNK_LOADER);
		this.activeRelativeChunks = new ArrayList<ChunkPos>();
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityMChunkLoader) tileEntity).hasPower = (Boolean) data[0];
			if (data[1] != null) ((TileEntityMChunkLoader) tileEntity).isWorking = (Boolean) data[1];
			if (data[2] != null) ((TileEntityMChunkLoader) tileEntity).activeRelativeChunks = (List<ChunkPos>) data[2];
		}, () -> hasPower, () -> isWorking, () -> activeRelativeChunks);
	}
	
	@Override
	public List<ChunkPos> getLoadHoldChunks() {
		List<ChunkPos> chunks = new ArrayList<ChunkPos>();
		if (this.isWorking) {
			ChunkPos ownChunk = new ChunkPos(this.worldPosition);
			this.activeRelativeChunks.forEach((chunk) -> {
				chunks.add(new ChunkPos(chunk.x + ownChunk.x, + chunk.z + ownChunk.z));
			});
		}
		return chunks;
	}
	
	public boolean canWork() {
		return activeRelativeChunks.size() > 0;
	}
	
	public boolean setChunkCactive(ChunkPos relativChunk, boolean active) {
		if (active && !this.activeRelativeChunks.contains(relativChunk)) {
			this.activeRelativeChunks.add(relativChunk);
			return true;
		} else if (!active && this.activeRelativeChunks.contains(relativChunk)) {
			this.activeRelativeChunks.remove(relativChunk);
			return true;
		}
		return false;
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
			this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
			this.isWorking = canWork() && this.hasPower;
			
			boolean active = this.getBlockState().getValue(BlockMChunkLoader.ACTIVE);
			if (active != this.isWorking) level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(BlockMChunkLoader.ACTIVE, this.isWorking));
			
		}
		
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		ListNBT chunkList = new ListNBT();
		this.activeRelativeChunks.forEach((chunk) -> {
			ListNBT chunkNBT = new ListNBT();
			chunkNBT.add(IntNBT.valueOf(chunk.x));
			chunkNBT.add(IntNBT.valueOf(chunk.z));
			chunkList.add(chunkNBT);
		});
		compound.put("ActiveChunks", chunkList);
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("isWorking", this.isWorking);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		this.activeRelativeChunks.clear();
		ListNBT chunkList = nbt.getList("ActiveChunks", 9);
		chunkList.forEach((chunkNBT) -> {
			ChunkPos chunk = new ChunkPos(((IntNBT) ((ListNBT) chunkNBT).get(0)).getAsInt(), ((IntNBT) ((ListNBT) chunkNBT).get(1)).getAsInt());
			this.activeRelativeChunks.add(chunk);
		});
		this.hasPower = nbt.getBoolean("hasPower");
		this.isWorking = nbt.getBoolean("isWorking");
		super.load(state, nbt);
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity palyer) {
		return new ContainerMChunkLoader(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.chunk_loader");
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
