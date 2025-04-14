package de.m_marvin.industria.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sun.misc.Unsafe;

/**
 * Utility method to perform low level native memory operations, bypassing any access restrictions.<br>
 * <b>AVOID USING ANY OF THESE METHODS IF POSSIBLE</b>
 */
public class TheUnsafeUtility {
	
	private TheUnsafeUtility() {}
	
	/* UNSAFE INSTANCE FOR LOW LEVEL MEMORY ACCESS */
	private static final Unsafe theUnsafe;
	
	/* AQUIRE UNSAFE INSTANCE AT STARTUP */
	static {
		try {
			Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);
			theUnsafe = (Unsafe) theUnsafeField.get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("Your java seems to be to safe :(", e);
		}
	}
	
	/* INTERNAL CLASS/FIELD CACHE TO IMPROVE PERFORMANCE */
	private static final Map<Class<?>, Set<Field>> fieldCache = new ConcurrentHashMap<Class<?>, Set<Field>>();
	
	/**
	 * Iterates over the supplied class and all its super-classes and interfaces<br>
	 * to compile a list of all non-static fields that this class has and make them accessible.
	 * 
	 * @param clazz The class to scan for fields
	 * @return A set of all non-static fields that instances of the class have
	 */
	public static Set<Field> getAllClassFields(Class<?> clazz) {
		Set<Field> fields = fieldCache.get(clazz);
		if (fields == null) {
			fields = Stream.of(clazz.getDeclaredFields())
				.filter(f -> !Modifier.isStatic(f.getModifiers())).collect(Collectors.toSet());
			for (Field field : fields)
				field.setAccessible(true);
			for (Class<?> i : clazz.getInterfaces())
				fields.addAll(getAllClassFields(i));
			Class<?> s = clazz.getSuperclass();
			if (s != null)
				fields.addAll(getAllClassFields(s));
			fieldCache.put(clazz, fields);
		}
		return fields;
	}
	
	/**
	 * Copies all fields values from the source instance to the target instance.<br>
	 * The fields to copy are determined by scanning the supplied class type for all its fields, including super class and interface fields.<br>
	 * Both supplied objects are not allowed to be below the supplied class type in the hierarchy. (both have to be children or instances of that type)<br>
	 *
	 * @param highestType The highest class type to scan for fields to transfer, fields of sub-classes of this type are NOT transfered.
	 * @param source The source instance from which the fields are read
	 * @param target The target instance to which the fields are written
	 */
	public static void securedCopyClassFields(Class<?> highestType, Object source, Object target) {
		Objects.requireNonNull(source);
		Objects.requireNonNull(target);
		if (!highestType.isInstance(source))
			throw new IllegalArgumentException("supplied source object " + source.toString() + " is not of type " + highestType.getName());
		if (!highestType.isInstance(target))
			throw new IllegalArgumentException("supplied target object " + target.toString() + " is not of type " + highestType.getName());
		if (source == target)
			throw new IllegalArgumentException("supplied source and target are the same instance!");
		unsafeCopyClassFields(highestType, source, target);
	}
	
	/**
	 * Copies all fields values from the source instance to the target instance.<br>
	 * The fields to copy are determined by scanning the supplied class type for all its fields, including super class and interface fields.<br>
	 * Both supplied objects are not allowed to be below the supplied class type in the hierarchy. (both have to be children or instances of that type)<br>
	 * 
	 * <br><b>WARNING, USE {@link TheUnsafeUtility#securedCopyClassFields(Class, Object, Object)} WHEN EVER POSIBLE!<br>
	 * THIS METHOD SKIPS ALL SAFETY CHECKS AND PERFORMS THE UNSAFE OPERATION IMIDIENTLY,<br>POTENTIALLY CAUSING AN CRITICAL MEMORY CORRUPTION AND JVM CRASH</b>
	 * 
	 * @param highestType The highest class type to scan for fields to transfer, fields of sub-classes of this type are NOT transfered.
	 * @param source The source instance from which the fields are read
	 * @param target The target instance to which the fields are written
	 */
	public static void unsafeCopyClassFields(Class<?> highestType, Object source, Object target) {
		Set<Field> fields = getAllClassFields(highestType);
		for (Field field : fields) {
			LowLevelType type = LowLevelType.of(field.getType());
			long offset = theUnsafe.objectFieldOffset(field);
			Object value = type.read(source, offset);
			type.write(target, offset, value);
		}
	}
	
	/**
	 * An unprotected direct memory operation to read a field value of a specific type
	 */
	@FunctionalInterface
	protected static interface UnsafeRead {
		public Object read(Object instance, long offset);
	}
	
	/**
	 * An unprotected direct memory operation to write a field value of a specific type
	 */
	@FunctionalInterface
	protected static interface UnsafeWrite {
		public void write(Object instance, long offset, Object value);
	}
	
	/**
	 * Represents the low level type of an field.<br>
	 * 
	 */
	public static enum LowLevelType  {
		UNSAFE_BOOL(boolean.class, 	theUnsafe::getBoolean, 	(o, l, b) -> theUnsafe.putBoolean(o, l, (boolean) b)),
		UNSAFE_BYTE(byte.class, 	theUnsafe::getByte,		(o, l, b) -> theUnsafe.putByte(o, l, (byte) b)),
		UNSAFE_SHORT(short.class,	theUnsafe::getShort,	(o, l, b) -> theUnsafe.putShort(o, l, (short) b)),
		UNSAFE_INT(int.class,		theUnsafe::getInt,		(o, l, b) -> theUnsafe.putInt(o, l, (int) b)),
		UNSAFE_LONG(long.class,		theUnsafe::getLong,		(o, l, b) -> theUnsafe.putLong(o, l, (long) b)),
		UNSAFE_FLOAT(float.class,	theUnsafe::getFloat,	(o, l, b) -> theUnsafe.putFloat(o, l, (float) b)),
		UNSAFE_DOUBLE(double.class,	theUnsafe::getDouble,	(o, l, b) -> theUnsafe.putDouble(o, l, (double) b)),
		UNSAFE_CHAR(char.class,		theUnsafe::getChar,		(o, l, b) -> theUnsafe.putChar(o, l, (char) b)),
		UNSAFE_OBJECT(Object.class,	theUnsafe::getObject,	(o, l, b) -> theUnsafe.putObject(o, l, b));
		
		private final Class<?> type;
		private final UnsafeRead readOp;
		private final UnsafeWrite writeOp;
		
		private LowLevelType(Class<?> type, UnsafeRead read, UnsafeWrite write) {
			this.type = type;
			this.readOp = read;
			this.writeOp = write;
		}
		
		public static LowLevelType of(Class<?> type) {
			if (!type.isPrimitive())
				return UNSAFE_OBJECT;
			for (LowLevelType t : values())
				if (t.type == type) return t;
			return UNSAFE_OBJECT;
		}
		
		public Object read(Object instance, long offset) {
			return this.readOp.read(instance, offset);
		}
		
		public void write(Object instance, long offset, Object value) {
			this.writeOp.write(instance, offset, value);
		}
		
	}
	
	/**
	 * Reads the supplied field from the supplied instance by direct unsafe memory access, bypassing any restrictions such as final or private modifiers.
	 * 
	 * @param field The field to read from the instance
	 * @param instance The instance to read from
	 * @return The value of the field, including null if the field had the value null
	 */
	public static <T> T securedReadField(Field field, Object instance) {
		Objects.requireNonNull(field);
		Objects.requireNonNull(instance);
		if (!getAllClassFields(instance.getClass()).contains(field))
			throw new UnsupportedOperationException("unable to acces field " + field + " at object " + instance);
		return unsafeReadField(field, instance);
	}
	
	/**
	 * Reads the supplied field from the supplied instance by direct unsafe memory access, bypassing any restrictions such as final or private modifiers.<br>
	 * 
	 * <br><b>WARNING, USE {@link TheUnsafeUtility#securedReadField(Field, Object)} WHEN EVER POSIBLE!<br>
	 * THIS METHOD SKIPS ALL SAFETY CHECKS AND PERFORMS THE UNSAFE OPERATION IMIDIENTLY,<br>POTENTIALLY CAUSING AN CRITICAL MEMORY CORRUPTION AND JVM CRASH</b>
	 * 
	 * @param field The field to read from the instance
	 * @param instance The instance to read from
	 * @return The value of the field, including null if the field had the value null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unsafeReadField(Field field, Object instance) {
		LowLevelType type = LowLevelType.of(field.getType());
		long offset = theUnsafe.objectFieldOffset(field);
		return (T) type.read(instance, offset);
	}

	/**
	 * Writes the supplied field to the supplied instance by direct unsafe memory access, bypassing any restrictions such as final or private modifiers.
	 * 
	 * @param field The field to write to the instance
	 * @param instance The instance to write to
	 * @param value The value of the field
	 */
	public static <T> void securedWriteField(Field field, Object instance, T value) {
		Objects.requireNonNull(field);
		Objects.requireNonNull(instance);
		if (!getAllClassFields(instance.getClass()).contains(field))
			throw new UnsupportedOperationException("unable to acces field " + field + " at object " + instance);
		if (field.getType().isPrimitive()) {
			if (value == null)
				throw new NullPointerException("primitive field can not be set to null!");
			if (value.getClass() != field.getType())
				throw new IllegalArgumentException("primitive type " + field.getType().getName() + " can not be set to " + value.getClass().getName());
		} else {
			if (!field.getType().isAssignableFrom(value.getClass()))
				throw new IllegalArgumentException("object type " + field.getType().getName() + " can not be set to " + value.getClass());
		}
		unsafeWriteField(field, instance, value);
	}
	
	/**
	 * Writes the supplied field to the supplied instance by direct unsafe memory access, bypassing any restrictions such as final or private modifiers.
	 * 
	 * <br><b>WARNING, USE {@link TheUnsafeUtility#securedWriteField(Field, Object, Object)} WHEN EVER POSIBLE!<br>
	 * THIS METHOD SKIPS ALL SAFETY CHECKS AND PERFORMS THE UNSAFE OPERATION IMIDIENTLY,<br>POTENTIALLY CAUSING AN CRITICAL MEMORY CORRUPTION AND JVM CRASH</b>
	 * 
	 * @param field The field to write to the instance
	 * @param instance The instance to write to
	 * @param value The value of the field
	 */
	public static <T> void unsafeWriteField(Field field, Object instance, T value) {
		LowLevelType type = LowLevelType.of(field.getType());
		long offset = theUnsafe.objectFieldOffset(field);
		type.write(instance, offset, value);
	}
	
	/**
	 * Allocates a new instance of the supplied type without calling its constructor.<br>
	 * This means all the fields are initialized to null or their primitive default value!<br>
	 * 
	 * @param <T> Type of the instance to create
	 * @param type The class of the instance to create
	 * @return The created instance
	 * @throws InstantiationException 
	 */
	public static <T> T securedAllocateNewInstance(Class<T> type) throws InstantiationException {
		Objects.requireNonNull(type);
		if (type == String.class)
			throw new IllegalArgumentException("String class can not be instantiated!");
		if (type.isPrimitive()) // In theory, primitives can be instantiated as their respective class version, but it does not make any sense to do that.
			throw new IllegalArgumentException("primitive types can not be instantiated!");
		return unsafeAllocateNewInstance(type);
	}
	
	/**
	 * Allocates a new instance of the supplied type without calling its constructor.<br>
	 * This means all the fields are initialized to null or their primitive default value!<br>
	 * 	 * 
	 * <br><b>WARNING, USE {@link TheUnsafeUtility#securedAllocateNewInstance(Class)} WHEN EVER POSIBLE!<br>
	 * THIS METHOD SKIPS ALL SAFETY CHECKS AND PERFORMS THE UNSAFE OPERATION IMIDIENTLY,<br>POTENTIALLY CAUSING AN CRITICAL MEMORY CORRUPTION AND JVM CRASH</b>
	 * 
	 * @param <T> Type of the instance to create
	 * @param type The class of the instance to create
	 * @return The created instance
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unsafeAllocateNewInstance(Class<T> type) throws InstantiationException {
		return (T) theUnsafe.allocateInstance(type);
	}
	
}
