package de.redtec.blocks;

import de.redtec.tileentity.TileEntityFluidInput;
import de.redtec.util.IElectricConnective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFluidInput extends BlockContainerBase implements IElectricConnective {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BlockFluidInput() {
		super("fluid_input", Material.IRON, 2F, SoundType.METAL);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityFluidInput();
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NormalVoltage;
	}

	@Override
	public int getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFluidInput) {
			return ((TileEntityFluidInput) te).canSourceFluid() ? 3 : 0;
		}
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, BlockState state) {
		return side != state.get(FACING) && side != state.get(FACING).getOpposite();
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MASCHINE;
	}
	
}
