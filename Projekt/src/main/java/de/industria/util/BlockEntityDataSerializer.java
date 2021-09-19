package de.industria.util;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public class BlockEntityDataSerializer {
	
	protected HashMap<Class<?>, DataSerializer> serealizers = new HashMap<Class<?>, DataSerializer>();
	
	public BlockEntityDataSerializer() {
		registerDataType(Boolean.class, (buffer, value) -> buffer.writeBoolean((Boolean) value), (buffer) -> buffer.readBoolean());
		registerDataType(Short.class, (buffer, value) -> buffer.writeShort((Short) value), (buffer) -> buffer.readShort());
		registerDataType(Integer.class, (buffer, value) -> buffer.writeInt((Integer) value), (buffer) -> buffer.readInt());
		registerDataType(Long.class, (buffer, value) -> buffer.writeLong((Long) value), (buffer) -> buffer.readLong());
		registerDataType(Float.class, (buffer, value) -> buffer.writeFloat((Float) value), (buffer) -> buffer.readFloat());
		registerDataType(Double.class, (buffer, value) -> buffer.writeDouble((Double) value), (buffer) -> buffer.readDouble());
		registerDataType(String.class, (buffer, value) -> buffer.writeUtf((String) value), (buffer) -> buffer.readUtf());
		registerDataType(ItemStack.class, (buffer, value) -> buffer.writeItem((ItemStack) value), (buffer) -> buffer.readItem());
		registerDataType(FluidStack.class, (buffer, value) -> ((FluidStack) value).writeToPacket(buffer), (buffer) -> FluidStack.readFromPacket(buffer));
		registerDataType(BlockPos.class, (buffer, value) -> buffer.writeBlockPos((BlockPos) value), (buffer) -> buffer.readBlockPos());
		
	}
	
	public void registerDataType(Class<?> classType, BiConsumer<PacketBuffer, Object> serealizer, Function<PacketBuffer, Object> deserealizer) {
		this.serealizers.put(classType, new DataSerializer(serealizer, deserealizer));
	}
	
	public DataSerializer getSerealizer(Object objectClass) {
		 return this.serealizers.get(objectClass);
	}
	
	public void serealizeData(PacketBuffer buffer, Object data) {
		buffer.writeInt(data.getClass().getName().length());
		buffer.writeUtf(data.getClass().getName());
		DataSerializer serealizer = getSerealizer(data.getClass());
		if (serealizer == null) {
			System.err.println("Recived unregistred Object on BlockEntityDataSerealizer: " + data.getClass().getName());
			return;
		}
		serealizer.serealize(buffer, data);
	}
	
	public Object deserealizeData(PacketBuffer buffer) {
		int nameLength = buffer.readInt();
		String className = buffer.readUtf(nameLength);
		try {
			DataSerializer serealizer = getSerealizer(Class.forName(className));
			return serealizer.deserealize(buffer);
		} catch (ClassNotFoundException e) {
			System.err.println("Recived unregistred Object on BlockEntityDataSerealizer: " + className);
			e.printStackTrace();
			return null;
		}
	}
	
	public static class DataSerializer {
		public DataSerializer(BiConsumer<PacketBuffer, Object> serealizer, Function<PacketBuffer, Object> deserealizer) {
			this.serealizer = serealizer;
			this.deserealizer = deserealizer;
		}
		protected BiConsumer<PacketBuffer, Object> serealizer;
		public void serealize(PacketBuffer buffer, Object value) {
			this.serealizer.accept(buffer, value);;
		}
		protected Function<PacketBuffer, Object> deserealizer;
		public Object deserealize(PacketBuffer buffer) {
			return this.deserealizer.apply(buffer);
		}
	}
	
}
