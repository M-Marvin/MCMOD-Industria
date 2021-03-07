package de.redtec.blocks;

import de.redtec.tileentity.TileEntityMotor;
import de.redtec.util.IElectricConnective;
import de.redtec.util.VoxelHelper;
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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMotor extends BlockContainerBase implements IElectricConnective {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BlockMotor() {
		super("motor", Material.IRON, 3F, SoundType.METAL);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = Block.makeCuboidShape(3, 3, 3, 13, 13, 16);
		shape = VoxelShapes.or(Block.makeCuboidShape(5, 5, 1, 11, 11, 2), Block.makeCuboidShape(4, 4, 0, 12, 12, 1), shape);
		return VoxelHelper.rotateShape(shape, state.get(FACING));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		super.fillStateContainer(builder);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMotor();
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		// TODO
		return Voltage.LowVoltage;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side != state.get(FACING);
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.MASCHINE;
	}

}
