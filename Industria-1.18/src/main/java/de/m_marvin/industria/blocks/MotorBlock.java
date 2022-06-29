package de.m_marvin.industria.blocks;

import java.util.stream.Stream;

import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.ITE;

import de.m_marvin.industria.blockentities.MotorBlockEntity;
import de.m_marvin.industria.registries.ModBlockEntities;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MotorBlock extends DirectionalKineticBlock implements ITE<MotorBlockEntity> {
	
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
	
	public MotorBlock(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}
	
	// https://github.com/mrh0/createaddition/blob/67958eb55fac0654200fe355c2bc4fc5859de3e4/src/main/java/com/mrh0/createaddition/index/CABlocks.java#L150
	// https://github.com/Creators-of-Create/Create/blob/mc1.18/dev/src/main/java/com/simibubi/create/content/contraptions/components/motor/CreativeMotorTileEntity.java
	
	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING).getAxis();
	}
	
	@Override
	public Class<MotorBlockEntity> getTileEntityClass() {
		return MotorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends MotorBlockEntity> getTileEntityType() {
		return ModBlockEntities.MOTOR.get();
	}
	
}
