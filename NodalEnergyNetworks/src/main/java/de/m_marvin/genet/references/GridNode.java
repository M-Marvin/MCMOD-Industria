package de.m_marvin.genet.references;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import dev.lukebemish.codecextras.structured.CodecInterpreter;
import dev.lukebemish.codecextras.structured.Structure;

public abstract class GridNode<N extends GridNode> {
	
	public stasti
	
	private final Structure<N> structure;
	private final Codec<N> codec;
	
	public GridNode(Structure<N> structure) {
		this.structure = structure;
		this.codec = CodecInterpreter.create().interpret(structure).result().get();
	}
	
	public Structure<N> getStructure() {
		return structure;
	}
	
	public Codec<N> getCodec() {
		return codec;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GridNode<?> other) {
			
			if (other.getStructure().) {
				
				
				
			}
			
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
