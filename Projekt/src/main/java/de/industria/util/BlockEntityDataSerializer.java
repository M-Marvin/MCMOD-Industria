package de.industria.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;

import de.industria.tileentity.TileEntityControllPanel;
import de.industria.util.blockfeatures.ITENetworkDevice.NetworkDeviceIP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fluids.FluidStack;

public class BlockEntityDataSerializer {
	
	protected HashMap<Integer, DataSerializer> serealizers = new HashMap<Integer, DataSerializer>();
	
	public BlockEntityDataSerializer() {
		registerDataType(0, Boolean.class, (buffer, value) -> buffer.writeBoolean((Boolean) value), (buffer) -> buffer.readBoolean());
		registerDataType(1, Short.class, (buffer, value) -> buffer.writeShort((Short) value), (buffer) -> buffer.readShort());
		registerDataType(2, Integer.class, (buffer, value) -> buffer.writeInt((Integer) value), (buffer) -> buffer.readInt());
		registerDataType(3, Long.class, (buffer, value) -> buffer.writeLong((Long) value), (buffer) -> buffer.readLong());
		registerDataType(4, Float.class, (buffer, value) -> buffer.writeFloat((Float) value), (buffer) -> buffer.readFloat());
		registerDataType(5, Double.class, (buffer, value) -> buffer.writeDouble((Double) value), (buffer) -> buffer.readDouble());
		registerDataType(6, String.class, (buffer, value) -> buffer.writeUtf((String) value), (buffer) -> buffer.readUtf());
		registerDataType(7, ItemStack.class, (buffer, value) -> buffer.writeItem((ItemStack) value), (buffer) -> buffer.readItem());
		registerDataType(8, FluidStack.class, (buffer, value) -> ((FluidStack) value).writeToPacket(buffer), (buffer) -> FluidStack.readFromPacket(buffer));
		registerDataType(9, BlockPos.class, (buffer, value) -> buffer.writeBlockPos((BlockPos) value), (buffer) -> buffer.readBlockPos());
		registerDataType(10, ChunkPos.class, (buffer, value) -> buffer.writeLong(((ChunkPos) value).toLong()), (buffer) -> new ChunkPos(buffer.readLong()));
		registerDataType(11, Direction.class, (buffer, value) -> buffer.writeInt(((Direction) value).get3DDataValue()), (buffer) -> Direction.from3DDataValue(buffer.readInt()));
		registerDataType(12, NetworkDeviceIP.class, (buffer, value) -> ((NetworkDeviceIP) value).writeBuf(buffer), (buffer) -> NetworkDeviceIP.readBuf(buffer));
		registerDataType(13, HashMap.class, (buffer, value) -> {
			@SuppressWarnings("unchecked")
			HashMap<Object, Object> map = (HashMap<Object, Object>) value;
			buffer.writeInt(map.size());
			map.entrySet().forEach((entry) -> {
				serealizeData(buffer, entry.getKey());
				serealizeData(buffer, entry.getValue());
			});
		}, (buffer) -> {
			int size = buffer.readInt();
			HashMap<Object, Object> map = new HashMap<Object, Object>();
			for (int i = 0; i < size; i++) {
				Object key = deserealizeData(buffer);
				Object value = deserealizeData(buffer);
				map.put(key, value);
			}
			return map;
		});
		registerDataType(14, ArrayList.class, (buffer, value) -> {
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) value;
			buffer.writeInt(list.size());
			list.forEach((entry) -> serealizeData(buffer, entry));
		}, (buffer) -> {
			int size = buffer.readInt();
			List<Object> list = new ArrayList<Object>();
			for (int i = 0; i < size; i++) {
				list.add(deserealizeData(buffer));
			}
			return list;
		});
		registerDataType(15, TileEntityControllPanel.Pos.class, (buffer, value) -> {
			buffer.writeInt(((TileEntityControllPanel.Pos) value).getX());
			buffer.writeInt(((TileEntityControllPanel.Pos) value).getY());
		}, (buffer) -> {
			int x = buffer.readInt();
			int y = buffer.readInt();
			return new TileEntityControllPanel.Pos(x, y);
		});
		registerDataType(16, NonNullList.class, (buffer, value) -> {
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) value;
			buffer.writeInt(list.size());
			list.forEach((entry) -> serealizeData(buffer, entry));
		}, (buffer) -> {
			int size = buffer.readInt();
			List<Object> list = new ArrayList<Object>();
			for (int i = 0; i < size; i++) {
				list.add(deserealizeData(buffer));
			}
			return NonNullList.of(ItemStack.EMPTY, list.toArray());
		});
		registerDataType(17, TileEntityControllPanel.Pos.class, (buffer, value) -> {
			buffer.writeInt(((TileEntityControllPanel.Pos) value).getX());
			buffer.writeInt(((TileEntityControllPanel.Pos) value).getY());
		}, (buffer) -> {
			int x = buffer.readInt();
			int y = buffer.readInt();
			return new TileEntityControllPanel.Pos(x, y);
		});
	}
	
	public void registerDataType(int index, Class<?> classType, BiConsumer<PacketBuffer, Object> serealizer, Function<PacketBuffer, Object> deserealizer) {
		this.serealizers.put(index, new DataSerializer(classType, serealizer, deserealizer));
	}
	
	public int getSerealizer(Object objectClass) {
		for (Entry<Integer, DataSerializer> entry : this.serealizers.entrySet()) {
			 if (entry.getValue().getClazz() == objectClass.getClass()) return entry.getKey();
		}
		return -1;
	}
	
	public boolean serealizeData(PacketBuffer buffer, Object data) {
		int serealizerId = getSerealizer(data);
		if (serealizerId < 0) {
			System.err.println("Recived unregistred Object on BlockEntityDataSerealizer to serealize: " + data.getClass().getName());
			return false;
		}
		DataSerializer serealizer = this.serealizers.get(new Integer(serealizerId));
		buffer.writeInt(serealizerId);
		serealizer.serealize(buffer, data);
		return true;
	}
	
	public Object deserealizeData(PacketBuffer buffer) {
		int serealizerId = buffer.readInt();
		DataSerializer serealizer = this.serealizers.get(serealizerId);
		if (serealizer == null) {
			System.err.println("Recived unregistred SerealizerId on BlockEntityDataSerealizer to deserealize: " + serealizerId);
			return null;
		}
		return serealizer.deserealize(buffer);
	}
	
	public static class DataSerializer {
		public DataSerializer(Class<?> clazz, BiConsumer<PacketBuffer, Object> serealizer, Function<PacketBuffer, Object> deserealizer) {
			this.serealizer = serealizer;
			this.deserealizer = deserealizer;
			this.clazz = clazz;
		}
		protected BiConsumer<PacketBuffer, Object> serealizer;
		public void serealize(PacketBuffer buffer, Object value) {
			this.serealizer.accept(buffer, value);;
		}
		protected Function<PacketBuffer, Object> deserealizer;
		public Object deserealize(PacketBuffer buffer) {
			return this.deserealizer.apply(buffer);
		}
		protected Class<?> clazz;
		public Class<?> getClazz() {
			return clazz;
		}
	}
	
}
