package de.industria.util.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.industria.util.blockfeatures.IChunkForceLoading;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class ChunkLoadHandler extends WorldSavedData {
	
	protected static ChunkLoadHandler clientInstance;
	protected boolean isServerInstace;
	protected World world;
	
	/** List of chunks and a list the block positions of the loaders in this chunk **/
	protected HashMap<ChunkPos, List<BlockPos>> chunkLoaderMap = new HashMap<ChunkPos, List<BlockPos>>();
	/** List of chunks force loaded with chunk loading blocks, and the force laoding state from the vanilla /forceload command **/
	protected HashMap<ChunkPos, Boolean> loadHoldChunks = new HashMap<ChunkPos, Boolean>();
	
	public ChunkLoadHandler() {
		super("chunkLoader");
	}
	
	public ChunkLoadHandler(boolean serverInstance) {
		super("chunkLoader");
		this.isServerInstace = serverInstance;
	}
	
	public boolean isServerInstace() {
		return isServerInstace;
	}
	
	public static ChunkLoadHandler getHandlerForWorld(IWorld world) {
		
		if (!world.isRemote()) {
			DimensionSavedDataManager storage = ((ServerWorld) world).getSavedData();
			ChunkLoadHandler handler = storage.getOrCreate(ChunkLoadHandler::new, "chunkLoader");
			handler.world = (World) world;
			return handler;
		} else {
			if (clientInstance == null) clientInstance = new ChunkLoadHandler(false);
			return clientInstance;
		}
		
	}
	
	/**
	 * Updates the maps containing the states of the force loaded chunks
	 */
	public void updateChunkForceLoads() {
		
		if (this.world.getGameTime() % 40 == 0) {
			
			this.world.loadedTileEntityList.forEach((tileEntity) -> {
				if (tileEntity instanceof IChunkForceLoading) {
					BlockPos loaderPos = tileEntity.getPos();
					ChunkPos chunkPos = new ChunkPos(loaderPos);
					List<BlockPos> loaderMap = this.chunkLoaderMap.getOrDefault(chunkPos, new ArrayList<BlockPos>());
					if (!loaderMap.contains(loaderPos)) {
						loaderMap.add(loaderPos);
						this.chunkLoaderMap.put(chunkPos, loaderMap);
					}
				}
			});
			
			List<ChunkPos> removedChunks = new ArrayList<ChunkPos>();
			this.chunkLoaderMap.forEach((chunkPos, loaderMap) -> {
				List<BlockPos> removeList = new ArrayList<BlockPos>();
				loaderMap.forEach((loaderPos) -> {
					TileEntity tileEntity = this.world.getTileEntity(loaderPos);
					if (!(tileEntity instanceof IChunkForceLoading)) removeList.add(loaderPos);
				});
				loaderMap.removeAll(removeList);
				if (loaderMap.size() == 0) removedChunks.add(chunkPos);
			});
			removedChunks.forEach((chunkToRemove) -> this.chunkLoaderMap.remove(chunkToRemove));
			
			this.chunkLoaderMap.forEach((chunkPos, loaderMap) -> {
				loaderMap.forEach((loaderPos) -> {
					TileEntity loader = this.world.getTileEntity(loaderPos);
					if (loader instanceof IChunkForceLoading) {
						List<ChunkPos> forcedChunks = ((IChunkForceLoading) loader).getLoadHoldChunks();
						forcedChunks.forEach((loadChunkPos) -> {
							this.setForceLoadState(loadChunkPos, true);
						});
					}
				});
			});
			
			List<ChunkPos> removeList = new ArrayList<ChunkPos>();
			this.loadHoldChunks.forEach((chunkPos, vanillaState) -> {
				if (listLoadersForChunk(chunkPos).size() == 0) {
					this.setForceLoadState(chunkPos, false);
					removeList.add(chunkPos);
				}
			});
			removeList.forEach((chunkToRemove) -> this.loadHoldChunks.remove(chunkToRemove));
			
			this.markDirty();
			
		}
		
		
	}
	
	protected void setForceLoadState(ChunkPos chunkPos, boolean forceLoad) {
		if (forceLoad && !this.loadHoldChunks.containsKey(chunkPos)) {
			this.loadHoldChunks.put(chunkPos, ((ServerWorld) world).getForcedChunks().contains(chunkPos.asLong()));
			((ServerWorld) this.world).forceChunk(chunkPos.x, chunkPos.z, true);
		} else if (!forceLoad) {
			boolean vanillaLoaded = this.loadHoldChunks.getOrDefault(chunkPos, false);
			((ServerWorld) this.world).forceChunk(chunkPos.x, chunkPos.z, vanillaLoaded);
		}
	}
	
	/**
	 * Returns true if the chunk is force loaded from vanilla behavior
	 * 
	 * @param chunkPos the position of the chunk to check
	 * @return true if the chunk is force loaded from vanilla behavior
	 */
	public boolean getVanillaLoad(ChunkPos chunkPos) {
		return this.loadHoldChunks.getOrDefault(chunkPos, false);
	}
	
	/**
	 * Returns true if the chunk is force loaded from one or more tileentitys
	 * 
	 * @param chunkPos the position of the chunk to check
	 * @return true if the chunk is force loaded from one or more tileentitys
	 */
	public boolean isChunkBlockLoaded(ChunkPos chunkPos) {
		return this.loadHoldChunks.containsKey(chunkPos);
	}
	
	/**
	 * Lists all loader positions for the given chunk
	 * 
	 * @param chunkPos the chunk to list the loaders
	 * @return a list of block positions for the loaders
	 */
	public List<BlockPos> listLoadersForChunk(ChunkPos chunkPos) {
		List<BlockPos> loaders = new ArrayList<BlockPos>();
		this.chunkLoaderMap.values().forEach((map) -> map.forEach((loaderPos) -> {
			TileEntity loader = this.world.getTileEntity(loaderPos);
			if (loader instanceof IChunkForceLoading) {
				List<ChunkPos> forcingChunks = ((IChunkForceLoading) loader).getLoadHoldChunks();
				if (forcingChunks.contains(chunkPos)) loaders.add(loaderPos);
			}
		}));
		return loaders;
	}
	
	@Override
	public void read(CompoundNBT nbt) {
		
		this.chunkLoaderMap.clear();
		ListNBT nbtChunkLoaderMap = nbt.getList("ChunkLoaderMap", 10);
		nbtChunkLoaderMap.forEach((nbtEntry) -> {
			ListNBT nbtLoaderMap = ((CompoundNBT) nbtEntry).getList("ChunkLoaders", 4);
			List<BlockPos> loaderMap = new ArrayList<BlockPos>();
			nbtLoaderMap.forEach((nbtLoader) -> {
				loaderMap.add(BlockPos.fromLong(((LongNBT) nbtLoader).getLong()));
			});
			int[] nbtChunkPos = ((CompoundNBT) nbtEntry).getIntArray("ChunkPos");
			ChunkPos chunkPos = new ChunkPos(nbtChunkPos[0], nbtChunkPos[1]);
			this.chunkLoaderMap.put(chunkPos, loaderMap);
		});
		
		this.loadHoldChunks.clear();
		ListNBT nbtLoadHoldChunks = nbt.getList("BlockLoadedChunks", 10);
		nbtLoadHoldChunks.forEach((nbtEntry) -> {
			int[] nbtChunkPos = ((CompoundNBT) nbtEntry).getIntArray("ChunkPos");
			ChunkPos chunkPos = new ChunkPos(nbtChunkPos[0], nbtChunkPos[1]);
			boolean vanillaState = ((CompoundNBT) nbtEntry).getBoolean("VanillaForceLoaded");
			this.loadHoldChunks.put(chunkPos, vanillaState);
		});
		
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		
		ListNBT nbtChunkLoaderMap = new ListNBT();
		this.chunkLoaderMap.forEach((chunkPos, loaderMap) -> {
			CompoundNBT nbtEntry = new CompoundNBT();
			ListNBT nbtLoaderMap = new ListNBT();
			loaderMap.forEach((loader) -> nbtLoaderMap.add(LongNBT.valueOf(loader.toLong())));
			nbtEntry.put("ChunkLoaders", nbtLoaderMap);
			nbtEntry.putIntArray("ChunkPos", new int[] {chunkPos.x, chunkPos.z});
			nbtChunkLoaderMap.add(nbtEntry);
		});
		compound.put("ChunkLoaderMap", nbtChunkLoaderMap);
		
		ListNBT nbtLoadHoldChunks = new ListNBT();
		this.loadHoldChunks.forEach((chunkPos, vanillaState) -> {
			CompoundNBT nbtEntry = new CompoundNBT();
			nbtEntry.putBoolean("VanillaForceLoaded", vanillaState);
			nbtEntry.putIntArray("ChunkPos", new int[] {chunkPos.x, chunkPos.z});
			nbtLoadHoldChunks.add(nbtEntry);
		});
		compound.put("BlockLoadedChunks", nbtLoadHoldChunks);
		
		return compound;
	}
	
}
