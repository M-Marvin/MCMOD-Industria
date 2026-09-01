package de.m_marvin.genet.references;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.lukebemish.codecextras.structured.Structure;
import net.minecraft.core.BlockPos;

public record TypeChain(TypeChain.Format format, Object[] chain) {
	
	public static record Format(Link<?>[] chain) implements Codec<TypeChain> {
		
		public record Link<T>(Class<T> type, Codec<T> codec, T value) {
			
			
			
		}
		
		public static Format withLength(int links) {
			return new Format(new Link[links]);
		}
		
		public <T> Format nextType(Class<T> type, Codec<T> codec, T value) {
			for (int i = 0; i < this.chain.length; i++) {
				if (this.chain[i] == null) {
					this.chain[i] = new Link<T>(type, codec, value);
					return this;
				}
			}
			throw new IllegalStateException("type chain format is already completed");
		}
		
		public TypeChain defaultChain() {
			Object[] valueChain = new Object[this.chain.length];
			for (int i = 0; i < this.chain.length; i++)
				valueChain[i] = this.chain[i].value();
			return new TypeChain(this, valueChain);
		}
		
		public TypeChain emptyChain() {
			return new TypeChain(this, new Object[this.chain.length]);
		}

		@Override
		public <T> DataResult<T> encode(TypeChain input, DynamicOps<T> ops, T prefix) {
			if (!input.format().equals(this))
				throw new IllegalArgumentException("type chain is not of this format");
			for (int i = 0; i < this.chain.length; i++) {
				this.chain[i].encode(this.chain[i].type().cast(input.chain[i]));
			}
			
		}
		
		@Override
		public <T> DataResult<Pair<TypeChain, T>> decode(DynamicOps<T> ops, T input) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public void clear() {
		for (int i = 0; i < this.chain.length; i++)
			this.chain[i] = null;
	}
	
	public <T> TypeChain nextValue(T value) {
		for (int i = 0; i < this.chain.length; i++) {
			if (this.chain[i] == null) {
				if (!this.format.chain()[i].type().isInstance(value))
					throw new IllegalArgumentException("type does not match type chain foramt: " + value.getClass().getSimpleName() + " != " + this.format.chain()[i].type().getSimpleName());
				this.chain[i] = value;
				return this;
			}
		}
		throw new IllegalStateException("type chain is alrady completed");
	}
	
	
	
}
