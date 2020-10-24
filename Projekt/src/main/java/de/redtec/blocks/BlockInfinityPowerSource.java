package de.redtec.blocks;

import java.util.Random;

import de.redtec.tileentity.TileEntitySimpleBlockTicking;
import de.redtec.util.IElectricConnective;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockInfinityPowerSource extends BlockContainerBase implements IElectricConnective {

	public BlockInfinityPowerSource() {
		super("infinity_power_source", Material.ROCK, -1, SoundType.STONE);
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NormalVoltage;
	}

	@Override
	public int getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return -16;
	}

	@Override
	public boolean canConnect(Direction side, BlockState state) {
		return true;
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		this.updateNetwork(worldIn, pos);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntitySimpleBlockTicking();
	}
	
}
