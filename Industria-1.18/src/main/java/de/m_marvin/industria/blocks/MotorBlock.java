package de.m_marvin.industria.blocks;

import java.util.stream.Stream;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MotorBlock extends Block {
	
	public static VoxelShape BLOCK_SHAPE = Stream.of(
			Block.box(3, 3, 1, 13, 13, 16),
			Block.box(12, 2, 0, 14, 4, 16),
			Block.box(12, 12, 0, 14, 14, 16),
			Block.box(2, 2, 0, 4, 4, 16),
			Block.box(2, 12, 0, 4, 14, 16),
			Block.box(1, 0, 6, 15, 3, 13)
			).reduce((v1, v2) -> Shapes.or(v1, v2)).get();
	public static VoxelShape BLOCK_SHAPE_VERTICAL = Stream.of(
			Block.box(3, 0, 3, 13, 15, 13),
			Block.box(12, 0, 2, 14, 16, 4),
			Block.box(12, 0, 12, 14, 16, 14),
			Block.box(2, 0, 12, 4, 16, 14),
			Block.box(2, 0, 2, 4, 16, 4)
			).reduce((v1, v2) -> Shapes.or(v1, v2)).get();
	
	public MotorBlock(Properties p_49795_) {
		super(p_49795_);
		// TODO Auto-generated constructor stub
		
	}
	
}
