package de.redtec.blocks;

import java.util.Random;

import de.redtec.tileentity.TileEntitySimpleBlockTicking;
import de.redtec.util.IElectricConnective;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockPanelLamp extends BlockContainerBase implements IElectricConnective {
	
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BlockPanelLamp() {
		super("panel_lamp", Material.IRON, 1F, SoundType.METAL);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT);
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return state.get(LIT) ? 30 : 0;
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NormalVoltage; // TODO
	}

	@Override
	public int getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 1;
	}

	@Override
	public boolean canConnect(Direction side, BlockState state) {
		return side == state.get(FACING).getOpposite();
	}
	
	@Override
	public void onNetworkChanges(World worldIn, BlockPos pos, BlockState state, ElectricityNetwork network) {
		
		boolean active = state.get(LIT);
		boolean powered = network.canMachinesRun();
		
		if (active != powered) worldIn.setBlockState(pos, state.with(LIT, powered));
		
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
