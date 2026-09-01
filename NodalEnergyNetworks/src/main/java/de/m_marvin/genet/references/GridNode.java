package de.m_marvin.genet.references;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.print.attribute.standard.MediaSize.Other;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;

import de.m_marvin.genet.references.GridNode.Format.Builder.PartialFormat;
import dev.lukebemish.codecextras.structured.CodecInterpreter;
import dev.lukebemish.codecextras.structured.Structure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public abstract class GridNode<N extends GridNode> {
	
	public static class Format<C> {
		
		public static class Key<C, V> {
			
			private final String name;
			private final Codec<V> codec;
			private final Function<C, V> getter;
			
			public Key(String name, Codec<V> codec, Function<C, V> getter) {
				this.name = name;
				this.codec = codec;
				this.getter = getter;
			}
			
			public <T> V decode(DynamicOps<T> ops) {
				var result = codec.parse(ops, ops.empty());
				return result.getOrThrow();
			}
			
			public <T> void encode(DynamicOps<T> ops, C container) {
				this.codec.encodeStart(ops, get(container));
			}
			
			public String getName() {
				return name;
			}
			
			public V get(C container) {
				return this.getter.apply(container);
			}
			
			@Override
			public boolean equals(Object obj) {
				if (obj instanceof Key other) {
					return this.name.equals(other.name) && this.codec.equals(other.codec);
				}
				return false;
			}
			
			@Override
			public int hashCode() {
				return Objects.hashCode(this.name);
			}
			
		}
		
		private final Function<DynamicOps<?>, C> decode;
		private final List<Key<C, ?>> keys;
		
		private Format(Function<DynamicOps<?>, C> decode, List<Key<C, ?>> keys) {
			this.decode = decode;
			this.keys = keys;
		}
		
		public C decode(DynamicOps<?> ops) {
			return this.decode.apply(ops);
		}
		
		public void encode(DynamicOps<?> ops, C value) {
			for (Key<C, ?> key : this.keys)
				key.encode(null, value);
		}
		
		public List<Key<C, ?>> getKeys() {
			return keys;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Format other) {
				if (this.getKeys().size() != other.getKeys().size())
					return false;
				return this.keys.equals(other.keys);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(this.keys);
		}
		
		public static <T> Format<T> create(Builder<T> builder) {
			PartialFormat<T> format = new PartialFormat<T>(new ArrayList<GridNode.Format.Key<T, ?>>());
			return format.build(builder.build(format));
		}
		
		@FunctionalInterface
		public static interface Builder<T> {

			public record PartialFormat<T>(List<Key<T, ?>> keys) {
				
				public <V> Key<T, V> add(String name, Codec<V> codec, Function<T, V> getter) {
					Key<T, V> key = new Key<T, V>(name, codec, getter);
					this.keys.add(key);
					return key;
				}
				
				public Format<T> build(Function<DynamicOps<?>, T> decode) {
					this.keys.sort((a, b) -> a.getName().compareTo(b.getName()));
					return new Format<T>(decode, Collections.unmodifiableList(this.keys));
				}
				
			}
			
			public Function<DynamicOps<?>, T> build(PartialFormat<T> format);
			
		}
		
	}
	
//	public static Format<GridNodeBlockFace> TEST = Format.create(format -> {
//		var blockKey = format.add("block", BlockPos.CODEC, GridNodeBlockFace::getBlock);
//		var faceKey = format.add("face", Direction.CODEC, GridNodeBlockFace::getFace);
//		return ops -> new GridNodeBlockFace(blockKey.decode(ops), faceKey.decode(ops));
//	});
//	
//	private final Structure<N> structure;
//	private final Codec<N> codec;
	
//	public GridNode(Structure<N> structure) {
//		this.structure = structure;
//		this.codec = CodecInterpreter.create().interpret(structure).result().get();
//	}
//	
//	public Structure<N> getStructure() {
//		return structure;
//	}
//	
//	public Codec<N> getCodec() {
//		return codec;
//	}
	
	public abstract Format<N> getFormat();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GridNode<?> other) {
			if (!other.getFormat().equals(this.getFormat()))
				return false;
			
			for (var key : getFormat().getKeys())
				if (key.get(this).equals(obj))
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
		
	}
	
//	@Override
//	public abstract boolean equals(Object obj);
//	
//	@Override
//	public abstract int hashCode();
	
	@Override
	public abstract String toString();
	
}
