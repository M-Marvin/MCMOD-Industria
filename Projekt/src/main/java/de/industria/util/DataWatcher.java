package de.industria.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;

import de.industria.Industria;
import de.industria.packet.SUpdateBlockEntitys;
import de.industria.util.handler.UtilHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class DataWatcher {
	
	protected static BlockEntityDataSerializer dataSerealizer = new BlockEntityDataSerializer();
	protected static HashMap<TileEntityType<?>, BiConsumer<TileEntity, Object[]>> blockEntityUpdateHandlerMap = new HashMap<TileEntityType<?>, BiConsumer<TileEntity, Object[]>>();
	protected static List<ChunkPos> requestUpdates = new ArrayList<ChunkPos>();
	protected static List<ObservedBlockEntity> observedBlockEntitys = new ArrayList<DataWatcher.ObservedBlockEntity>();
	protected static List<ObservedBlockEntity> blockEntitysToUpdate = new ArrayList<DataWatcher.ObservedBlockEntity>();
	protected static List<ObservedBlockEntity> blockEntitysToRemove = new ArrayList<DataWatcher.ObservedBlockEntity>();
	
	/**
	 * Returns the BlockEntityDataSerealizer instance, that is used to serialize the data of the BlockEntitys.
	 * @return
	 */
	public static BlockEntityDataSerializer getDataSerealizer() {
		return dataSerealizer;
	}
	
	/**
	 * Checks every BlockEntitys to detect changes, and marks them to be updated or get removed.
	 * Also detects requested updates from the client side, when a chunk has loaded.
	 * @return true if one or more BlockEntitys needs to be updated or removed
	 */
	protected static boolean observe() {
		try {
			observedBlockEntitys.forEach((observedBlockEntity) -> {
				boolean requested = requestUpdates.contains(new ChunkPos(observedBlockEntity.getBlockEntity().getBlockPos()));
				if (requested) {
					observedBlockEntity.markDirty();
					blockEntitysToUpdate.add(observedBlockEntity);
					return;
				}
				ObserverResult result = observedBlockEntity.observe();
				switch(result) {
				case CLEAN:
					return;
				case DIRTY:
					blockEntitysToUpdate.add(observedBlockEntity);
					return;
				case REMOVED:
					blockEntitysToRemove.add(observedBlockEntity);
					return;
				}
			});
			requestUpdates.clear();
		} catch (ConcurrentModificationException e) {
			System.err.println("ConcurrentModificationException on accessing observed BlockEntity list, i dont know why ...");
		}
		return !blockEntitysToUpdate.isEmpty() || !blockEntitysToRemove.isEmpty();
	}
	
	/**
	 * Update all BlockEnitys that are marked from the observer() method.
	 * @param network The NetowrkConnection to send the updates
	 */
	protected static void updateObserved(SimpleChannel network) {
		blockEntitysToRemove.forEach((blockEntity) -> {
			observedBlockEntitys.remove(blockEntity);
		});
		blockEntitysToRemove.clear();
		
		if (!blockEntitysToUpdate.isEmpty()) network.send(PacketDistributor.ALL.noArg(), new SUpdateBlockEntitys(blockEntitysToUpdate));	// TODO	
		blockEntitysToUpdate.clear();
	}
	
	//########################################-^Server Update Handling^-##########################################//
	
	/**
	 * Method needs to be called every WorldTick at client and server side (and with the correct clientSide parameter);
	 * @param clientSide needs to be true when and only when the method is called on the client world tick event, false otherwise.
	 */
	public static void updateBlockEntitys(boolean clientSide) {
		if (clientSide) {
			handleUpdates();
		} else {
			if (observe()) {
				updateObserved(Industria.NETWORK);
			}
		}
	}
	
	/**
	 * Adds the given Chunk to the requested update list, which is used to detect updates of BlockEntitys requested from the client chunkloading.
	 * @param chunk The chunk to add to the list
	 */
	public static void requestChunkUpdate(ChunkPos chunk) {
		if (requestUpdates.size() > 90) requestUpdates.clear();
		requestUpdates.add(chunk);
	}
	public static void requestChunkUpdate() {
		observedBlockEntitys.forEach((observed) -> observed.markDirty());
	}
	
	/**
	 * Adds a update on the client side to the pending updates list
	 * @param data
	 */
	public static void handleUpdate(List<SendeableBlockEntityData> data) {
		pendingUpdates.addAll(data);
	}

	/**
	 * Adds the given TileEntity to the DataWatcher, and also registers its de-/serealizers.
	 * @param tileEntity The tileentity to add to the DataWatcher.
	 * @param updateHandler The deserealizer to register, if not already registred.
	 * @param observedData The serealizer to register, if not already registred.
	 */
	@SafeVarargs
	public static void registerBlockEntity(TileEntity tileEntity, BiConsumer<TileEntity, Object[]> updateHandler, Supplier<Object>... observedData) {
		TileEntityType<?> type = tileEntity.getType();
		if (!blockEntityUpdateHandlerMap.containsKey(type)) blockEntityUpdateHandlerMap.put(type, updateHandler);
		ObservedBlockEntity observeable = new ObservedBlockEntity(tileEntity);
		observeable.registerData(observedData);
		observedBlockEntitys.add(observeable);
	}
	
	//########################################-vClient Update Handlingv-##########################################//
	
	protected static List<SendeableBlockEntityData> pendingUpdates = new ArrayList<DataWatcher.SendeableBlockEntityData>();
	
	/**
	 * Handles all received updates.
	 */
	@SuppressWarnings("resource")
	protected static void handleUpdates() {
		ClientWorld world = Minecraft.getInstance().level;
		pendingUpdates.forEach((updateData) -> {
			if (world.isLoaded(updateData.targetPosition)) {
				TileEntity tileEntity = world.getBlockEntity(updateData.targetPosition);
				if (tileEntity != null ? tileEntity.getType() == updateData.targetBlockEntity : false) {
					BiConsumer<TileEntity, Object[]> updater = blockEntityUpdateHandlerMap.get(updateData.targetBlockEntity);
					if (updater == null) {
						System.err.println("Recived update for unregistred TileEntityType: " + updateData.targetBlockEntity.getRegistryName());
					} else {
						updater.accept(tileEntity, updateData.updateData);
					}
				}
			}
		});
		pendingUpdates.clear();
	}
	
	//########################################-Util-##########################################//
	
	/**
	 * Represents the Client side data of an BlockEntity.
	 */
	public static class SendeableBlockEntityData {
		public SendeableBlockEntityData() {}
		public BlockPos targetPosition;
		public DimensionType targetDimension;
		public TileEntityType<?> targetBlockEntity;
		public Object[] updateData;
		
		@SuppressWarnings("deprecation")
		public SendeableBlockEntityData readBuf(PacketBuffer buf) {
			this.targetPosition = buf.readBlockPos();
			this.targetBlockEntity = Registry.BLOCK_ENTITY_TYPE.get(buf.readResourceLocation());
			int dataCount = buf.readInt();
			this.updateData = new Object[dataCount];
			for (int i = 0; i < dataCount; i++) {
				int dataIndex = buf.readInt();
				if (dataIndex < 0) continue;
				this.updateData[dataIndex] = getDataSerealizer().deserealizeData(buf);
			}
			return this;
		}
	}
	
	/**
	 * Represents the Server side data of an BlockEntity.
	 * It contains the code that determines if date needs to be send and serializes them.
	 */
	public static class ObservedBlockEntity {
		protected TileEntity blockEntity;
		protected Supplier<Object>[] observedData;
		protected HashMap<Integer, Byte[]> lastData;
		protected boolean[] dataToSend;
		
		public ObservedBlockEntity(TileEntity tileEntity) {
			this.blockEntity = tileEntity;
		}
		public void markDirty() {
			for (int i = 0; i < this.dataToSend.length; i++) this.dataToSend[i] = true;
		}
		@SuppressWarnings("unchecked")
		public void registerData(Supplier<Object>... observedData) {
			this.observedData = observedData;
			this.lastData = new HashMap<Integer, Byte[]>();
			this.dataToSend = new boolean[this.observedData.length];
		}
		public TileEntity getBlockEntity() {
			return blockEntity;
		}
		public Object[] getObservedData() {
			return observedData;
		}
		
		/**
		 * Checks if some of the registred data has changed, and marks them as "needs to be updated"
		 * @return DIRTY if the data needs to be updated, CLEAN if nothing has changed and REMOVED if the BlockEntity has been removed, or if the chunk has unloaded.
		 */
		public ObserverResult observe() {
			if (this.blockEntity.isRemoved() || !this.blockEntity.hasLevel() || !this.blockEntity.getLevel().blockEntityList.contains(this.blockEntity) || this.blockEntity.getLevel().isClientSide()) {
				return ObserverResult.REMOVED;
			} else {
				boolean dataToSend = false;
				for (int i = 0; i < this.observedData.length; i++) {
					PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
					if (!getDataSerealizer().serealizeData(buf, this.observedData[i].get())) {
						System.err.println("Failure on serealizing " + this.blockEntity.getType().getRegistryName() + " at " + this.blockEntity.getBlockPos() + "!");
						continue;
					}
					Byte[] currentValue = ArrayUtils.toObject(UtilHelper.makeArrayFromBuffer(buf));
					Byte[] lastValue = this.lastData.getOrDefault(i, null);
					
					if ((lastValue == null ? true : !Arrays.equals(lastValue, currentValue))) {
						this.lastData.put(i, currentValue);
						this.dataToSend[i] = true;
						dataToSend = true;
					} else {
						this.dataToSend[i] = false;
					}
				}
				return !dataToSend ? ObserverResult.CLEAN : ObserverResult.DIRTY;
			}
		}
		
		public void writeBuf(PacketBuffer buf) {
			buf.writeBlockPos(this.blockEntity.getBlockPos());
			buf.writeResourceLocation(this.blockEntity.getType().getRegistryName());
			buf.writeInt(this.dataToSend.length);
			for (int index = 0; index < this.dataToSend.length; index++) {
				Byte[] lastValue = this.lastData.get(index);
				if (this.dataToSend[index] && lastValue != null) {
					buf.writeInt(index);
					buf.writeBytes(ArrayUtils.toPrimitive(lastValue));
				} else {
					buf.writeInt(-1);
				}
			}
		}
	}
	
	public static enum ObserverResult {
		CLEAN,DIRTY,REMOVED;
	}
	
}
