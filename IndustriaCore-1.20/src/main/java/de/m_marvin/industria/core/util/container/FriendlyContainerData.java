package de.m_marvin.industria.core.util.container;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import de.m_marvin.industria.core.util.types.Holder;
import net.minecraft.world.inventory.ContainerData;

public class FriendlyContainerData implements ContainerData {

	protected record DataItem<T>(int index, int len, Class<T> type, Consumer<T> setter, Supplier<T> getter, Holder<T> value) {
		
		public DataItem(int index, int len, Class<T> type, Consumer<T> setter, Supplier<T> getter) {
			this(index, len, type, setter, getter, null);
		}
		
		public DataItem(int index, int len, Class<T> type) {
			this(index, len, type, null, null, new Holder<T>());
			
			if (type() == Boolean.class)
				value().set(type().cast(false));
			else if (type() == Integer.class)
				value().set(type().cast(0));
			else if (type() == Long.class)
				value().set(type().cast(0L));
			else if (type() == Float.class)
				value().set(type().cast(0F));
			else if (type() == Double.class)
				value().set(type().cast(0D));
			else if (type().isInstance(Enum.class))
				value().set(null);
		}
		
		public <R> R get(Class<R> type) {
			if (type() != type)
				throw new IllegalStateException("data container slot type does not match invoked getter");
			if (value() != null)
				return type.cast(value().get());
			else
				return type.cast(getter().get());
		}
		
		public void set(Object value) {
			if (type() != value.getClass())
				throw new IllegalStateException("data container slot type does not match invoked getter");
			if (value() != null)
				value().set(type().cast(value));
			else
				setter().accept(type().cast(value));
		}
		
		public int getRawInt(int index) {
			int subIndex = index - index();
			if (subIndex >= len() || subIndex < 0)
				throw new IndexOutOfBoundsException(String.format("data container slot sub index %s out of bounds %d: %d", subIndex, len(), index));
			
			if (type() == Boolean.class)
				return get(Boolean.class) ? 1 : 0;
			else if (type() == Integer.class)
				return get(Integer.class);
			else if (type() == Long.class)
				return (int) (get(Long.class) >> (subIndex * 32));
			else if (type() == Float.class)
				return Float.floatToRawIntBits(get(Float.class));
			else if (type() == Double.class)
				return (int) (Double.doubleToRawLongBits(get(Double.class)) >> (subIndex * 32));
			else if (type().isInstance(Enum.class))
				return ((Enum<?>) getter().get()).ordinal();

			throw new IllegalStateException("data container slot item of undefined type: " + type().getSimpleName());
		}
		
		public void setRawInt(int index, int value) {
			int subIndex = index - index();
			if (subIndex >= len() || subIndex < 0)
				throw new IndexOutOfBoundsException(String.format("data container slot sub index %s out of bounds %d: %d", subIndex, len(), index));
			
			if (type() == Boolean.class)
				set(Boolean.valueOf(value != 0));
			else if (type() == Integer.class)
				set(Integer.valueOf(value));
			else if (type() == Long.class) {
				long value0 = get(Long.class);
				set(Long.valueOf((value0 & ~(0xFFFFFFFFL << (subIndex * 32))) | (long) value << (subIndex * 32)));
			} else if (type() == Float.class)
				set(Float.intBitsToFloat(value));
			else if (type() == Double.class) {
				long value0 = Double.doubleToRawLongBits(get(Double.class));
				set(Double.longBitsToDouble((value0 & ~(0xFFFFFFFFL << (subIndex * 32))) | (long) value << (subIndex * 32)));
			} else if (type().isInstance(Enum.class))
				set(type().getEnumConstants()[value]);
		}
		
	}
	
	private final List<DataItem<?>> items = new ArrayList<>();
	
	public static FriendlyContainerData empty() {
		return new FriendlyContainerData();
	}
	
	protected FriendlyContainerData nextItem(DataItem<?> item) {
		for (int i = item.index(); i < item.index() + item.len(); i++) {
			if (i < this.items.size())
				this.items.set(i, item);
			else
				this.items.add(item);
		}
		return this;
	}

	public FriendlyContainerData nextIntItem(Supplier<Integer> getter, Consumer<Integer> setter) {
		return nextItem(new DataItem<>(this.items.size(), 1, Integer.class, setter, getter));
	}

	public FriendlyContainerData nextLongItem(Supplier<Long> getter, Consumer<Long> setter) {
		return nextItem(new DataItem<>(this.items.size(), 2, Long.class, setter, getter));
	}

	public FriendlyContainerData nextFloatItem(Supplier<Double> getter, Consumer<Double> setter) {
		return nextItem(new DataItem<>(this.items.size(), 1, Double.class, setter, getter));
	}

	public FriendlyContainerData nextDoubleItem(Supplier<Double> getter, Consumer<Double> setter) {
		return nextItem(new DataItem<>(this.items.size(), 2, Double.class, setter, getter));
	}

	public FriendlyContainerData nextBooleanItem(Supplier<Double> getter, Consumer<Double> setter) {
		return nextItem(new DataItem<>(this.items.size(), 1, Double.class, setter, getter));
	}
	
	public <T extends Enum<T>> FriendlyContainerData nextEnumItem(Supplier<T> getter, Consumer<T> setter, Class<T> clazz) {
		return nextItem(new DataItem<>(this.items.size(), 1, clazz, setter, getter));
	}
	
	public FriendlyContainerData nextIntItemStatic() {
		return nextItem(new DataItem<>(this.items.size(), 1, Integer.class));
	}

	public FriendlyContainerData nextLongItemStatic() {
		return nextItem(new DataItem<>(this.items.size(), 2, Long.class));
	}

	public FriendlyContainerData nextFloatItemStatic() {
		return nextItem(new DataItem<>(this.items.size(), 1, Double.class));
	}

	public FriendlyContainerData nextDoubleItemStatic() {
		return nextItem(new DataItem<>(this.items.size(), 2, Double.class));
	}

	public FriendlyContainerData nextBooleanItemStatic() {
		return nextItem(new DataItem<>(this.items.size(), 1, Boolean.class));
	}

	public <T extends Enum<T>> FriendlyContainerData nextEnumItemStatic(Class<T> clazz) {
		return nextItem(new DataItem<>(this.items.size(), 1, clazz));
	}

	public int getInt(int index) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		return this.items.get(index).get(Integer.class);
	}
	
	public long getLong(int index) {
		if (index + 1 >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		return this.items.get(index).get(Long.class);
	}
	
	public float getFloat(int index) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		return this.items.get(index).get(Float.class);
	}
	
	public double getDouble(int index) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		return this.items.get(index).get(Double.class);
	}
	
	public boolean getBoolean(int index) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		return this.items.get(index).get(Boolean.class);
	}
	
	public <T extends Enum<T>> T getEnum(int index, Class<T> clazz) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		return this.items.get(index).get(clazz);
	}

	public void setInt(int index, int value) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		this.items.get(index).set(value);
	}
	
	public void setLong(int index, long value) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		this.items.get(index).set(value);
	}
	
	public void setFloat(int index, float value) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		this.items.get(index).set(value);
	}
	
	public void setDouble(int index, double value) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		this.items.get(index).set(value);
	}
	
	public void setBoolean(int index, boolean value) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		this.items.get(index).set(value);
	}
	
	public <T extends Enum<T>> void setEnum(int index, T value) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		this.items.get(index).set(value);
	}

	@Override
	public int get(int index) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		return this.items.get(index).getRawInt(index);
	}

	@Override
	public void set(int index, int value) {
		if (index >= this.items.size() || index < 0)
			throw new IndexOutOfBoundsException(String.format("data container slot %d out of boudnds 0-%s", index, this.items.size() - 1));
		this.items.get(index).setRawInt(index, value);
	}

	@Override
	public int getCount() {
		return this.items.size();
	}
	
	public int[] getRawData() {
		return IntStream.range(0, getCount()).map(i -> get(i)).toArray();
	}
	
	public void fromRawData(int[] data) {
		if (data.length != getCount())
			throw new IllegalArgumentException("data array length does not match container slot count");
		for (int i = 0; i < getCount(); i++)
			set(i, data[i]);
	}
	
}
