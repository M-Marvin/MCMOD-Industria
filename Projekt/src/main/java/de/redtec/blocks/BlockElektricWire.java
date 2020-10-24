package de.redtec.blocks;

import de.redtec.util.IElectricConnective;
import de.redtec.util.IElectricWire;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockElektricWire extends BlockWiring implements IElectricWire {
	
	protected final int maximumPower;
	
	public BlockElektricWire(String name, int maximumPower, int size) {
		super(name, Material.WOOL, 0.2F, SoundType.CLOTH, size);
		this.maximumPower = maximumPower;
	}
	
	public int getMaximumPower() {
		return maximumPower;
	}
	
	@Override
	public boolean canConnectTo(BlockState wireState, World worldIn, BlockPos wirePos, BlockPos connectPos, Direction direction) {
		
		BlockState otherState = worldIn.getBlockState(connectPos);
		
		if (otherState.getBlock() instanceof IElectricWire) {
			return true;
		} else if (otherState.getBlock() instanceof IElectricConnective) {
			return ((IElectricConnective) otherState.getBlock()).canConnect(direction.getOpposite(), otherState);
		}
		
		return false;
		
	}
	
}
