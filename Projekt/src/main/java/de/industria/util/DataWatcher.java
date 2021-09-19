package de.industria.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import de.industria.Industria;
import de.industria.packet.SUpdateBlockEntitys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class DataWatcher {
	
	protected static BlockEntityDataSerializer dataSerealizer = new BlockEntityDataSerializer();
	protected static HashMap<TileEntityType<?>, BiConsumer<TileEntity, Object[]>> blockEntityUpdateHandlerMap = new HashMap<TileEntityType<?>, BiConsumer<TileEntity, Object[]>>();
	protected static List<ObservedBlockEntity> observedBlockEntitys = new ArrayList<DataWatcher.ObservedBlockEntity>();
	protected static List<ObservedBlockEntity> blockEntitysToUpdate = new ArrayList<DataWatcher.ObservedBlockEntity>();
	protected static List<ObservedBlockEntity> blockEntitysToRemove = new ArrayList<DataWatcher.ObservedBlockEntity>();
	
	public static BlockEntityDataSerializer getDataSerealizer() {
		return dataSerealizer;
	}
	
	public static boolean observe() {
		observedBlockEntitys.forEach((observedBlockEntity) -> {
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
		return !blockEntitysToUpdate.isEmpty() || !blockEntitysToRemove.isEmpty();
	}
	
	public static void updateObserved(SimpleChannel network) {
		blockEntitysToRemove.forEach((blockEntity) -> {
			observedBlockEntitys.remove(blockEntity);
		});
		blockEntitysToRemove.clear();
		
		List<SendeableBlockEntityData> sendeableData = new ArrayList<DataWatcher.SendeableBlockEntityData>();
		for (ObservedBlockEntity blockEntity : blockEntitysToUpdate) {
			TileEntity tileEntity = blockEntity.getBlockEntity();
			Supplier<Object>[] updateDataSup = blockEntity.getDataToSend();
			sendeableData.add(new SendeableBlockEntityData(tileEntity, updateDataSup));
		}
		blockEntitysToUpdate.clear();
		if (!sendeableData.isEmpty()) network.send(PacketDistributor.ALL.noArg(), new SUpdateBlockEntitys(sendeableData));		
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
	
	public static class SendeableBlockEntityData {
		public SendeableBlockEntityData(TileEntity tileEntity, Supplier<Object>[] updateDataSup) {
			this.updateData = new Object[updateDataSup.length];
			for (int i = 0; i < this.updateData.length; i++) this.updateData[i] = updateDataSup[i] != null ? updateDataSup[i].get() : null;
			this.targetPosition = tileEntity.getBlockPos();
			this.targetBlockEntity = tileEntity.getType();
		}
		public SendeableBlockEntityData() {}
		public BlockPos targetPosition;
		public DimensionType targetDimension;
		public TileEntityType<?> targetBlockEntity;
		public Object[] updateData;
		public void writeBuf(PacketBuffer buf) {
			buf.writeBlockPos(this.targetPosition);
			buf.writeResourceLocation(this.targetBlockEntity.getRegistryName());
			buf.writeInt(this.updateData.length);
			for (int i = 0; i < this.updateData.length; i++) {
				if (this.updateData[i] != null) {
					buf.writeBoolean(true);
					getDataSerealizer().serealizeData(buf, this.updateData[i]);
				} else {
					buf.writeBoolean(false);
				}
			}
		}
		@SuppressWarnings("deprecation")
		public SendeableBlockEntityData readBuf(PacketBuffer buf) {
			this.targetPosition = buf.readBlockPos();
			this.targetBlockEntity = Registry.BLOCK_ENTITY_TYPE.get(buf.readResourceLocation());
			int dataCount = buf.readInt();
			this.updateData = new Object[dataCount];
			for (int i = 0; i < dataCount; i++) {
				if (buf.readBoolean()) {
					this.updateData[i] = getDataSerealizer().deserealizeData(buf);
				} else {
					this.updateData[i] = null;
				}
			}
			return this;
		}
	}
	
	public static class ObservedBlockEntity {
		protected TileEntity blockEntity;
		protected Supplier<Object>[] observedData;
		protected Object[] lastData;
		protected Supplier<Object>[] dataToSend;
		
		public ObservedBlockEntity(TileEntity tileEntity) {
			this.blockEntity = tileEntity;
		}
		@SuppressWarnings("unchecked")
		public void registerData(Supplier<Object>... observedData) {
			this.observedData = observedData;
			this.lastData = new Object[this.observedData.length];
			this.dataToSend = new Supplier[this.observedData.length];
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
					Object currentValue = this.observedData[i].get();
					Object lastValue = this.lastData[i];
					if ((lastValue == null ? currentValue != null : !lastValue.equals(currentValue))) {
						this.dataToSend[i] = this.observedData[i];
						this.lastData[i] = currentValue;
						dataToSend = true;
					} else {
						this.dataToSend[i] = null;
					}
				}
				return !dataToSend ? ObserverResult.CLEAN : ObserverResult.DIRTY;
			}
		}
		public Supplier<Object>[] getDataToSend() {
			return dataToSend;
		}
	}

	public static enum ObserverResult {
		CLEAN,DIRTY,REMOVED;
	}
	
}
