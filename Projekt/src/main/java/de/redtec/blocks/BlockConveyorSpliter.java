package de.redtec.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

public class BlockConveyorSpliter extends BlockConveyorBelt {
	
	public BlockConveyorSpliter() {
		super("conveyor_spliter");
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(RIGHT, BeltState.CLOSE).with(LEFT, BeltState.CLOSE));
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
