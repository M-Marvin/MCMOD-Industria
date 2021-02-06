package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.renderer.BlockGaugeItemRenderer;
import de.redtec.tileentity.TileEntityMMultimeter;
import de.redtec.tileentity.TileEntityMMultimeter.DecimalUnit;
import de.redtec.tileentity.TileEntityMMultimeter.MessurementType;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IElectricConnective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMMultimeter extends BlockContainerBase implements IElectricConnective, IAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<TileEntityMMultimeter.MessurementType> UNIT = EnumProperty.create("unit", TileEntityMMultimeter.MessurementType.class);
	
	public BlockMMultimeter() {
		super("multimeter", Material.IRON, 2F, SoundType.METAL);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, UNIT);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(UNIT, MessurementType.VOLT);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (player.isSneaking()) {
			
			MessurementType unit = state.get(UNIT);
			if (unit == MessurementType.VOLT) {
				unit = MessurementType.AMPERE;
			} else if (unit == MessurementType.AMPERE) {
				unit = MessurementType.WATT;
			} else if (unit == MessurementType.WATT) {
				unit = MessurementType.VOLT;
			}
			
			worldIn.setBlockState(pos, state.with(UNIT, unit));
			return ActionResultType.SUCCESS;
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		VoxelShape shape = Block.makeCuboidShape(0, 0, 0, 16, 5, 16);
		shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 14, 0, 16, 16, 16));
		
		switch(state.get(FACING)) {
		case NORTH:
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 5, 2, 16, 16, 16));
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 5, 0, 1, 16, 16));
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(15, 5, 0, 16, 16, 2));
			break;
		case SOUTH:
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 5, 0, 16, 16, 14));
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 5, 0, 1, 16, 16));
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(15, 5, 14, 16, 16, 16));
			break;
		case WEST:
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(2, 5, 0, 16, 16, 16));
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 5, 0, 16, 16, 1));
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 5, 15, 2, 16, 16));
			break;
		case EAST:
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 5, 0, 14, 16, 16));
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(0, 5, 0, 16, 16, 1));
			shape = VoxelShapes.or(shape, Block.makeCuboidShape(14, 5, 15, 16, 16, 16));
			break;
		default:
			break;
		
		}
		
		return shape;
		
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return Voltage.NoLimit;
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		return 0;
	}

	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side != state.get(FACING).getOpposite();
	}

	@Override
	public DeviceType getDeviceType() {
		return DeviceType.SWITCH;
	}
	
	@Override
	public boolean beforNetworkChanges(World world, BlockPos pos, BlockState state, ElectricityNetwork network, int lap) {
		
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof TileEntityMMultimeter) {
			
			float value = state.get(UNIT).getValue(world, pos, network);
			
			DecimalUnit unit = DecimalUnit.getUnitForValue(value);
			value = value / unit.getValue();
			
			((TileEntityMMultimeter) te).updateValue(value, unit);
			
		}
		
		return false;
		
	}
	
	@Override
	public boolean isSwitchClosed(World worldIn, BlockPos pos, BlockState state) {
		
		return state.get(UNIT) != TileEntityMMultimeter.MessurementType.VOLT;
		
	}
	
	@Override
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.multimeter"));
		return info;
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return () -> BlockGaugeItemRenderer::new;
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityMMultimeter();
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}
	
	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntityMMultimeter) {
			switch (((TileEntityMMultimeter) tileEntity).decimalUnit) {
			case MILLI: return 0;
			case NONE: return 1;
			case KILO: return 2;
			case MEGA: return 3;
			}
		}
		return 0;
	}
	
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		TileEntity tileEntity = blockAccess.getTileEntity(pos);
		if (tileEntity instanceof TileEntityMMultimeter) {
			return (int) (((TileEntityMMultimeter) tileEntity).getValue() / 1000 * 15);
		}
		return 0;
	}
}
