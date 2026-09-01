package de.m_marvin.genet.references;

import com.mojang.serialization.Codec;

public record TypeChainFormat(Link<?>[] chain) {
	
	public record Link<T>(Class<T> type, Codec<T> codec) {
		
	}
	
	public static TypeChainFormat withLength(int links) {
		return new TypeChainFormat(new Link[links]);
	}
	
	public <T> TypeChainFormat nextType(Class<T> type, Codec<T> codec) {
		for (int i = 0; i < this.chain.length; i++) {
			if (this.chain[i] == null) {
				this.chain[i] = new Link<T>(type, codec);
				return this;
			}
		}
		throw new IllegalStateException("node format is already completed");
	}
	
	public void serialize() {
		
		
		
	}
	
}
