package de.m_marvin.genet.nodal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import de.m_marvin.genet.misc.DataResultExtras;
import de.m_marvin.genet.misc.StreamExtras;
import de.m_marvin.unimap.api.BiMap;
import de.m_marvin.unimap.impl.HashBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;

public class Node {
	
	private static final Logger LOGGER = LogManager.getLogger();

	public static record Format(Format parent, Class<?>[] ntype) implements Codec<Object[]> {
		
		private static final Map<Class<?>, Codec<?>> TYPE2CODEC = new HashMap<>();
		
		static {
			// standard types
			registerType(Boolean.class, Codec.BOOL);
			registerType(Byte.class, Codec.BYTE);
			registerType(Short.class, Codec.SHORT);
			registerType(Integer.class, Codec.INT);
			registerType(Long.class, Codec.LONG);
			registerType(Float.class, Codec.FLOAT);
			registerType(Double.class, Codec.DOUBLE);
			registerType(String.class, Codec.STRING);
			
			// minecraft types
			registerType(BlockPos.class, BlockPos.CODEC);
			registerType(Direction.class, Direction.CODEC);
			registerType(Axis.class, Axis.CODEC);
		}
		
		public static <T> void registerType(Class<T> type, Codec<T> codec) {
			Objects.requireNonNull(type, "type can not be null");
			Objects.requireNonNull(codec, "condec can not be null");
			if (TYPE2CODEC.containsKey(type))
				LOGGER.warn("duplicate codec registry for type, using exsting codec: " + type.getName());
			else
				TYPE2CODEC.put(type, codec);
		}
		
		@SuppressWarnings("unchecked")
		public static Codec<Object> codecByType(Class<?> type) {
			Codec<?> codec = TYPE2CODEC.get(type);
			if (codec == null)
				throw new IllegalArgumentException("unknown node format type: " + type.getName());
			return (Codec<Object>) codec;
		}
		
		public static boolean validateType(Class<?> type) {
			return TYPE2CODEC.containsKey(type);
		}
		
		public static final Codec<Format> CODEC = ResourceLocation.CODEC.comapFlatMap(DataResultExtras.unstableNonNullMap(Node::formatByKey), Node::keyOfFormat); 

		public static Format of(Class<?>... ntype) {
			for (var t : ntype)
				if (!validateType(t))
					throw new IllegalArgumentException("type not valid for node format: " + t.getName());
			return new Format(null, Arrays.copyOf(ntype, ntype.length));
		}
		
		public Format sub(Class<?>... ntype) {
			for (var t : ntype)
				if (!validateType(t))
					throw new IllegalArgumentException("type not valid for node format: " + t.getName());
			return new Format(this, Stream.concat(Stream.of(this.ntype), Stream.of(ntype)).toArray(Class<?>[]::new));
		}
		
		public void validate(Object... npos) {
			if (npos.length != this.ntype.length)
				throw new IllegalArgumentException("argument count wrong for node format: " + toString());
			for (int i = 0; i < npos.length; i++)
				if (!this.ntype[i].isInstance(npos[i]))
					throw new IllegalArgumentException("arguments wrong for node format: " + toString());
		}
		
		public Node node(Object... npos) {
			validate(npos);
			return new Node(this, Arrays.copyOf(npos, npos.length));
		}
		
		public Node subnode(Node parent, Object... npos) {
			if (!parent.format().equals(this.parent))
				throw new IllegalArgumentException("parent node does not match parent format");
			return node(Stream.concat(Stream.of(parent.npos()), Stream.of(npos)).toArray(Object[]::new));
		}
		
		public Codec<Node> nodeCodec() {
			return Node.Format.CODEC.dispatch("Format", Node::format, format -> format.xmap(npos -> format.node(npos), Node::npos).fieldOf("NPos"));
		}
		
		@Override
		public <T> DataResult<T> encode(Object[] input, DynamicOps<T> ops, T prefix) {
			return DataResultExtras.flatStream(
						IntStream.range(0, this.ntype.length)
							.mapToObj(i -> codecByType(this.ntype[i]).encodeStart(ops, input[i]))
				)
				.map(ops::createList);
		}

		@Override
		public <T> DataResult<Pair<Object[], T>> decode(DynamicOps<T> ops, T input) {
			return ops.getStream(input).flatMap(ts ->
					DataResultExtras.flatStream(
							StreamExtras.costream(ts, Stream.of(this.ntype))
								.map(pair -> codecByType(pair.getSecond()).decode(ops, pair.getFirst()))
					)
					.map(nps -> nps.map(Pair::getFirst).toArray())
				)
				.map(node -> new Pair<>(node, input));
		}
		
		@Override
		public final String toString() {
			StringBuffer s = new StringBuffer();
			for (var t : this.ntype)
				s.append('/').append(t.getSimpleName());
			return String.format("NodeFormat[%s]", s.toString());
		}
		
		@Override
		public final boolean equals(Object obj) {
			if (obj instanceof Format other)
				return Arrays.equals(this.ntype, other.ntype);
			return false;
		}
		
		@Override
		public final int hashCode() {
			return Arrays.hashCode(this.ntype);
		}
		
	}
	
	private static final BiMap<ResourceLocation, Format> REG2FORMAT = new HashBiMap<>();
	
	public static Format registerFormat(String modid, String name, Format format) {
		return registerFormat(ResourceLocation.tryBuild(modid, name), format);
	}
	
	public static Format registerFormat(ResourceLocation name, Format format) {
		Objects.requireNonNull(name, "name can not be null");
		Objects.requireNonNull(format, "format can not be null");
		Format dup = REG2FORMAT.get(name);
		if (dup != null) {
			if (dup.equals(format))
				LOGGER.error("duplicate format registry, incompatible types: " + name + " : " + format.toString() + " <!> " + dup.toString());
			else
				LOGGER.warn("duplicate format registry, using existing format: " + name + " : " + dup.toString());
			return dup;
		} else {
			REG2FORMAT.put(name, format);
			return format;
		}
	}
	
	public static Format formatByKey(ResourceLocation name) {
		Format format = REG2FORMAT.get(name);
		if (format == null)
			throw new IllegalArgumentException("unknown format: " + name);
		return format;
	}
	
	public static ResourceLocation keyOfFormat(Format format) {
		ResourceLocation name = REG2FORMAT.getKey(format);
		if (name == null)
			throw new IllegalArgumentException("unregistered format, no key found: " + format.toString());
		return name;
	}
	
	public static final Codec<Node> CODEC = Node.Format.CODEC.dispatch("Format", Node::format, format -> format.xmap(npos -> format.node(npos), Node::npos).fieldOf("NPos"));
	
	private final Format format;
	private final Object[] npos;
	
	private Node(Format format, Object[] npos) {
		this.format = format;
		this.npos = npos;
	}
	
	public Format format() {
		return format;
	}
	
	public Object[] npos() {
		return npos;
	}
	
	public <T> T get(int n, Class<T> type) {
		if (type != this.format.ntype[n])
			throw new IllegalArgumentException("type does not match node format: " + n + " " + type.getSimpleName() + " <!> " + this.format.toString());
		return type.cast(this.npos[n]);
	}
	
	public Node with(int n, Object... npos) {
		if (n + npos.length > this.format.ntype.length)
			throw new IllegalArgumentException("argument count exceeds node format: " + this.format.toString());
		for (int i = 0; n < npos.length; i++)
			if (npos[i].getClass() != this.format.ntype[i + n])
				throw new IllegalArgumentException("argument does not match node format: " + (i + n) + " " + npos[i].getClass().getSimpleName() + " <!> " + this.format.toString());
		Object[] npos2 = Arrays.copyOf(this.npos, this.npos.length);
		for (int i = 0; n < npos.length; i++)
			npos2[i + n] = npos[i];
		return new Node(this.format, npos2);
	}
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (var t : this.npos)
			s.append('/').append(t.toString());
		return String.format("Node[%s]", s.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node other) {
			if (!this.format.equals(other.format))
				return false;
			if (!Arrays.equals(this.npos, other.npos))
				return false;
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.format, Arrays.hashCode(this.npos));
	}
	
}
