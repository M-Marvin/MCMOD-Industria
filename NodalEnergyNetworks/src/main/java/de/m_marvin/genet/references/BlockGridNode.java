package de.m_marvin.genet.references;

import dev.lukebemish.codecextras.structured.Structure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BlockGridNode extends GridNode<BlockGridNode> {
	
	private BlockPos block;
	private Direction face;
	
	public BlockGridNode(BlockPos block, Direction face) {
		super(Structure.record(structure -> {
			var blockKey = structure.add("block", null, BlockGridNode::getBlock);
			var faceKey = structure.add("face", null, BlockGridNode::getFace);
			return container -> new BlockGridNode(blockKey.apply(container), faceKey.apply(container));
		}));
		
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
