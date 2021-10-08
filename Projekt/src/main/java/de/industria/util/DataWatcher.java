package de.industria.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;

import com.rits.cloning.Cloner;

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
	protected static Cloner dataCloner = new Cloner();
	protected static HashMap<TileEntityType<?>, BiConsumer<TileEntity, Object[]>> blockEntityUpdateHandlerMap = new HashMap<TileEntityType<?>, BiConsumer<TileEntity, Object[]>>();
	protected static List<ObservedBlockEntity> observedBlockEntitys = new ArrayList<DataWatcher.ObservedBlockEntity>();
	protected static List<ObservedBlockEntity> blockEntitysToUpdate = new ArrayList<DataWatcher.ObservedBlockEntity>();
	protected static List<ObservedBlockEntity> blockEntitysToRemove = new ArrayList<DataWatcher.ObservedBlockEntity>();
	protected static List<ChunkPos> requestUpdates = new ArrayList<ChunkPos>();
	
	public static BlockEntityDataSerializer getDataSerealizer() {
		return dataSerealizer;
	}
	
	public static Cloner getDataCloner() {
		return dataCloner;
	}
	
	public static boolean observe() {
		observedBlockEntitys.forEach((observedBlockEntity) -> {
			boolean requested = requestUpdates.contains(new ChunkPos(observedBlockEntity.getBlockEntity().getBlockPos()));
			ObserverResult result = observedBlockEntity.observe();
			if (result == ObserverResult.CLEAN && requested) result = ObserverResult.DIRTY;
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
		return !blockEntitysToUpdate.isEmpty() || !blockEntitysToRemove.isEmpty();
	}
	
	public static void updateObserved(SimpleChannel network) {
		blockEntitysToRemove.forEach((blockEntity) -> {
			observedBlockEntitys.remove(blockEntity);
		});
		blockEntitysToRemove.clear();
		
		if (!blockEntitysToUpdate.isEmpty()) network.send(PacketDistributor.ALL.noArg(), new SUpdateBlockEntitys(blockEntitysToUpdate));	// TODO	
		blockEntitysToUpdate.clear();
	}
	
	public static void requestChunkUpdate(ChunkPos chunk) {
		requestUpdates.add(chunk);
	}
	
	//########################################-^Server Update Handling^-##########################################//
	
	public static void updateBlockEntitys(boolean clientSide) {
		if (clientSide) {
			handleUpdates();
		} else {
			if (observe()) {
				updateObserved(Industria.NETWORK);
			}
		}
	}
	
	public static void handleUpdate(List<SendeableBlockEntityData> data) {
		pendingUpdates.addAll(data);
	}
	
	//########################################-vClient Update Handlingv-##########################################//
	
	protected static List<SendeableBlockEntityData> pendingUpdates = new ArrayList<DataWatcher.SendeableBlockEntityData>();
	
	@SuppressWarnings("resource")
	public static void handleUpdates() {
		ClientWorld world = Minecraft.getInstance().level;
		List<SendeableBlockEntityData> completedPackages = new ArrayList<DataWatcher.SendeableBlockEntityData>();
		pendingUpdates.forEach((updateData) -> {
			if (world.isLoaded(updateData.targetPosition)) {
				TileEntity tileEntity = world.getBlockEntity(updateData.targetPosition);
				if (tileEntity != null ? tileEntity.getType() == updateData.targetBlockEntity : false) {
					BiConsumer<TileEntity, Object[]> updater = blockEntityUpdateHandlerMap.get(updateData.targetBlockEntity);
					if (updater == null) {
						System.err.println("Recived update for unregistred TileEntityType: " + updateData.targetBlockEntity.getRegistryName());
						return;
					}
					updater.accept(tileEntity, updateData.updateData);
				}
				completedPackages.add(updateData);
			}
		});
		pendingUpdates.removeAll(completedPackages);
	}
	
	//########################################-Util-##########################################//
	
	@SafeVarargs
	public static void registerBlockEntity(TileEntity tileEntity, BiConsumer<TileEntity, Object[]> updateHandler, Supplier<Object>... observedData) {
		TileEntityType<?> type = tileEntity.getType();
		if (!blockEntityUpdateHandlerMap.containsKey(type)) blockEntityUpdateHandlerMap.put(type, updateHandler);
		ObservedBlockEntity observeable = new ObservedBlockEntity(tileEntity);
		observeable.registerData(observedData);
		observedBlockEntitys.add(observeable);
	}
	
	//##################################################################################//
	
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
		public ObserverResult observe() {
			if (this.blockEntity.isRemoved() || !this.blockEntity.hasLevel() || !this.blockEntity.getLevel().blockEntityList.contains(this.blockEntity) || this.blockEntity.getLevel().isClientSide()) {
				return ObserverResult.REMOVED;
			} else {
				boolean dataToSend = false;
				for (int i = 0; i < this.observedData.length; i++) {
					byte[] currentValue = null;
					if (this.observedData[i].get() != null) {
						PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
						if (!getDataSerealizer().serealizeData(buf, this.observedData[i].get())) {
							System.err.println("Failure on serealizing " + this.blockEntity.getType().getRegistryName() + " at " + this.blockEntity.getBlockPos() + "!");
						}
						currentValue = UtilHelper.makeArrayFromBuffer(buf);
					}
					byte[] lastValue = ArrayUtils.toPrimitive(this.lastData.getOrDefault(i, null));
					
					if ((lastValue == null ? currentValue != null : !Arrays.equals(lastValue, currentValue))) {
						this.lastData.put(i, ArrayUtils.toObject(currentValue));
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
			for (Entry<Integer, Byte[]> entry : this.lastData.entrySet()) {
				int index = entry.getKey();
				if (this.dataToSend[index]) {
					buf.writeInt(index);
					buf.writeBytes(ArrayUtils.toPrimitive(entry.getValue()));
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
