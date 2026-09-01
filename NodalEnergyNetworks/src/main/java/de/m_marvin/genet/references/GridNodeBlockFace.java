package de.m_marvin.genet.references;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class GridNodeBlockFace extends GridNode<GridNodeBlockFace> {
	
	public static Format<GridNodeBlockFace> FORMAT = Format.create(format -> {
		var blockKey = format.add("block", BlockPos.CODEC, GridNodeBlockFace::getBlock);
		var faceKey = format.add("face", Direction.CODEC, GridNodeBlockFace::getFace);
		return ops -> new GridNodeBlockFace(blockKey.decode(ops), faceKey.decode(ops));
	});
	
	private BlockPos block;
	private Direction face;
	
	public GridNodeBlockFace(BlockPos block, Direction face) {
		this.block = block;
		this.face = face;
	}
	
	public BlockPos getBlock() {
		return block;
	}
	
	public void setBlock(BlockPos block) {
		this.block = block;
	}
	
	public Direction getFace() {
		return face;
	}
	
	public void setFace(Direction face) {
		this.face = face;
	}
	
	@Override
	public Format<GridNodeBlockFace> getFormat() {
		return FORMAT;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
