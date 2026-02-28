package de.m_marvin.industria.core.util.types;

import java.util.NoSuchElementException;

import it.unimi.dsi.fastutil.ints.IntIterator;

public class Tiler implements IntIterator {
	
	private final int tile;
	private final int tiles;
	private final int begin;
	private final int end;
	private int returned = 0;
	
	public Tiler(int start, int total, int tile) {
		this.tile = tile;
		this.begin = Math.min(tile - start, total);
		this.end = (total - this.begin) % this.tile;
		this.tiles = (total - this.begin) / tile;
	}
	
	@Override
	public boolean hasNext() {
		return returned <= tiles + (this.end > 0 ? 1 : 0);
	}

	@Override
	public int nextInt() {
		if (!hasNext())
			throw new NoSuchElementException();
		else
			return this.returned++ == 0 ? this.begin : this.returned - 1 > this.tiles ? this.end : this.tile;
	}
	
}
